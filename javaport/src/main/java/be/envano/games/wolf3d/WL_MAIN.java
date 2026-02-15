package be.envano.games.wolf3d;

public final class WL_MAIN {

    private static final String[] JHParmStrings = {"no386", ""};
    private static final int SC_M = 50;
    private static final int STARTFONT = 1;
    private static final int MAPSIZE = 64;
    private static final int PORTTILESWIDE = 20;
    private static final int PORTTILESHIGH = 13;
    private static final int UPDATEWIDE = PORTTILESWIDE;
    private static final int UPDATEHIGH = PORTTILESHIGH;
    private static final int SCREENWIDTH = 80;
    private static final int TILEWIDTH = 4;
    private static final int PAGE1START = 0;
    private static final int PAGE2START = 0;

    private static boolean IsA386;
    private static boolean virtualreality;
    private static boolean NoWait;
    private static int viewsize = 15;
    private static int displayofs;
    private static int bufferofs;
    private static long mminfo_mainmem = 640000L;
    private static final int[] farmapylookup = new int[MAPSIZE];
    private static final int[] nearmapylookup = new int[MAPSIZE];
    private static final byte[] tilemap = new byte[MAPSIZE * MAPSIZE];
    private static final int[] uwidthtable = new int[PORTTILESHIGH];
    private static final int[] blockstarts = new int[UPDATEWIDE * UPDATEHIGH];
    private static final byte[] update = new byte[UPDATEWIDE * UPDATEHIGH];
    private static int updateptr;

    private WL_MAIN() {
    }

    // Correlates to: original/WOLFSRC/WL_MAIN.C:1586 (void main(void))
    public static void main(String[] args) {
        C_RUNTIME.setMainArgs(args == null ? new String[0] : args);

        WL_MENU.CheckForEpisodes();
        Patch386();
        InitGame();
        DemoLoop();
        Quit("Demo loop exited???");
    }

    /**
     * Correlates to {@code original/WOLFSRC/WL_MAIN.C:241} ({@code void Patch386(void)}).
     * <p>
     * Historical purpose: on DOS-era x86 systems, detect 386-class CPUs and patch a division
     * routine to use faster 32-bit instructions.
     * <p>
     * Porting note: this is hardware-era optimization logic (not core game behavior). In the
     * Java port, preserve call flow and flags for fidelity; low-level runtime patching is expected
     * to become a no-op or platform-layer stub.
     */
    public static void Patch386() {
        int i;

        for (i = 1; i < C_RUNTIME.argc(); i++) {
            if (ID_US.US_CheckParm(C_RUNTIME.argv(i), JHParmStrings) == 0) {
                IsA386 = false;
                return;
            }
        }

        if (CheckIs386()) {
            IsA386 = true;
            jabhack2();
        } else {
            IsA386 = false;
        }
    }

    /**
     * Correlates to {@code original/WOLFSRC/WL_MAIN.C:1145} ({@code void InitGame(void)}).
     * <p>
     * This is startup scaffolding with strict call ordering. Unported dependencies are stubbed
     * and will be replaced module-by-module.
     */
    public static void InitGame() {
        if (MS_CheckParm("virtual")) {
            virtualreality = true;
        } else {
            virtualreality = false;
        }

        ID_MM.MM_Startup();
        SignonScreen();

        ID_VH_H.VW_Startup();
        ID_IN.IN_Startup();
        ID_PM.PM_Startup();
        ID_PM.PM_UnlockMainMem();
        ID_SD.SD_Startup();
        ID_CA.CA_Startup();
        ID_US.US_Startup();

        // Correlates to WL_MAIN.C non-SPEAR low-memory branch.
        if (mminfo_mainmem < 235000L) {
            // TODO: Port ERRORSCREEN cache/blit path.
            ShutdownId();
            Quit("Not enough memory");
            return;
        }

        InitDigiMap();
        InitLookupTablesAndUpdateBlocks();
        ReadConfig();

        if (Keyboard(SC_M)) {
            DoJukebox();
        } else if (!virtualreality) {
            IntroScreen();
        }

        CA_CacheGrChunk(STARTFONT);
        MM_SetLock();

        LoadLatchMem();
        BuildTables();
        SetupWalls();

        NewViewSize(viewsize);

        InitRedShifts();
        if (!virtualreality) {
            FinishSignon();
        }

        displayofs = PAGE1START;
        bufferofs = PAGE2START;

        if (virtualreality) {
            NoWait = true;
            VR_Interrupt();
        }
    }

    public static void DemoLoop() {
        throw new UnsupportedOperationException("TODO: Port DemoLoop from WL_MAIN.C");
    }

    // Correlates to: original/WOLFSRC/WL_MAIN.C:557 (void ShutdownId(void))
    public static void ShutdownId() {
        ID_US.US_Shutdown();
        ID_SD.SD_Shutdown();
        ID_PM.PM_Shutdown();
        ID_IN.IN_Shutdown();
        ID_VH_H.VW_Shutdown();
        ID_CA.CA_Shutdown();
        ID_MM.MM_Shutdown();
    }

    public static void Quit(String error) {
        throw new IllegalStateException(error == null ? "Quit called" : error);
    }

    private static boolean CheckIs386() {
        // TODO: Port hardware detection behavior for modern platform layer.
        return false;
    }

    private static void jabhack2() {
        // TODO: Port/replace assembly optimization hook.
    }

    // Correlates to: original/WOLFSRC/WL_MAIN.C:814 (boolean MS_CheckParm(char far *check))
    private static boolean MS_CheckParm(String check) {
        int i;
        String parm;
        for (i = 1; i < C_RUNTIME.argc(); i++) {
            parm = C_RUNTIME.argv(i);

            while (!parm.isEmpty() && !C_RUNTIME.isalpha(parm.charAt(0))) {
                parm = parm.substring(1);
            }
            if (C_RUNTIME.stricmp(check, parm) == 0) {
                return true;
            }
        }
        return false;
    }

    private static void SignonScreen() {
    }

    private static void InitDigiMap() {
    }

    /**
     * Correlates to {@code original/WOLFSRC/WL_MAIN.C:1190-1204}.
     * <p>
     * Initializes precomputed lookup tables used by map addressing and screen update bookkeeping:
     * - {@code nearmapylookup}: row offsets into the contiguous {@code tilemap} buffer
     * - {@code farmapylookup}: row offsets used by map segment addressing
     * - {@code uwidthtable}: row starts for the update region width
     * - {@code blockstarts}: screen-space block origins used by the update system
     * - {@code updateptr}: reset to the start of the update buffer
     */
    private static void InitLookupTablesAndUpdateBlocks() {
        int i;
        int x;
        int y;
        int blockstartIndex;

        // Correlates to WL_MAIN.C:1190-1193.
        for (i = 0; i < MAPSIZE; i++) {
            nearmapylookup[i] = MAPSIZE * i;
            farmapylookup[i] = i * 64;
        }

        // Correlates to WL_MAIN.C:1196-1197.
        for (i = 0; i < PORTTILESHIGH; i++) {
            uwidthtable[i] = UPDATEWIDE * i;
        }

        // Correlates to WL_MAIN.C:1199-1202.
        blockstartIndex = 0;
        for (y = 0; y < UPDATEHIGH; y++) {
            for (x = 0; x < UPDATEWIDE; x++) {
                blockstarts[blockstartIndex++] = SCREENWIDTH * 16 * y + x * TILEWIDTH;
            }
        }

        // Correlates to WL_MAIN.C:1204.
        updateptr = 0;
    }

    private static void ReadConfig() {
    }

    private static boolean Keyboard(int scancode) {
        return false;
    }

    private static void DoJukebox() {
    }

    private static void IntroScreen() {
    }

    private static void CA_CacheGrChunk(int chunk) {
    }

    private static void MM_SetLock() {
    }

    private static void LoadLatchMem() {
    }

    private static void BuildTables() {
    }

    private static void SetupWalls() {
    }

    private static void NewViewSize(int size) {
    }

    private static void InitRedShifts() {
    }

    private static void FinishSignon() {
    }

    private static void VR_Interrupt() {
    }
}
