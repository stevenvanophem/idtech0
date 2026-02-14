package be.envano.games.wolf3d;

public final class WL_MAIN {

    private static final String[] JHParmStrings = {"no386", ""};
    private static final int SC_M = 50;
    private static final int STARTFONT = 1;
    private static final int PAGE1START = 0;
    private static final int PAGE2START = 0;

    private static int _argc;
    private static String[] _argv = new String[0];
    private static boolean IsA386;
    private static boolean virtualreality;
    private static boolean NoWait;
    private static int viewsize = 15;
    private static int displayofs;
    private static int bufferofs;
    private static long mminfo_mainmem = 640000L;

    private WL_MAIN() {
    }

    // Correlates to: original/WOLFSRC/WL_MAIN.C:1586 (void main(void))
    public static void main(String[] args) {
        setArgv(args == null ? new String[0] : args);

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

        for (i = 1; i < _argc; i++) {
            if (ID_US.US_CheckParm(_argv[i], JHParmStrings) == 0) {
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

        ID_VW.VW_Startup();
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
        ID_VW.VW_Shutdown();
        ID_CA.CA_Shutdown();
        ID_MM.MM_Shutdown();
    }

    public static void Quit(String error) {
        throw new IllegalStateException(error == null ? "Quit called" : error);
    }

    private static void setArgv(String[] javaArgs) {
        String[] args = javaArgs == null ? new String[0] : javaArgs;
        _argv = new String[args.length + 1];
        _argv[0] = "java";
        System.arraycopy(args, 0, _argv, 1, args.length);
        _argc = _argv.length;
    }

    private static String skipNonAlpha(String value) {
        if (value == null) {
            return "";
        }
        int idx = 0;
        while (idx < value.length() && !Character.isAlphabetic(value.charAt(idx))) {
            idx++;
        }
        return value.substring(idx);
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
        for (i = 1; i < _argc; i++) {
            String parm = skipNonAlpha(_argv[i]);
            if (check.equalsIgnoreCase(parm)) {
                return true;
            }
        }
        return false;
    }

    private static void SignonScreen() {
    }

    private static void InitDigiMap() {
    }

    private static void InitLookupTablesAndUpdateBlocks() {
        // TODO: Port nearmapylookup/farmapylookup/uwidthtable/blockstarts setup.
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
