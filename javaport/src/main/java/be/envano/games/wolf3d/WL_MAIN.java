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
    private static final int INTROSONG = 0;
    private static final int TITLEPIC = 0;
    private static final int CREDITSPIC = 0;
    private static final int TickBase = 70;
    private static final int ex_abort = 1;
    private static final String[] DemoParmStrings = {"baby", "easy", "normal", "hard", ""};
    private static final long FOCALLENGTH = 0x5700L;
    private static final int STATUSLINES = 40;
    private static final double HEIGHTRATIO = 0.50;

    private static boolean IsA386;
    private static boolean startgame;
    private static boolean loadedgame;
    private static boolean virtualreality;
    private static boolean NoWait;
    private static int WindowX;
    private static int WindowW;
    private static int PrintY;
    private static int fontcolor;
    private static int backcolor;
    private static int screenofs;
    private static int viewwidth;
    private static int viewheight;
    private static int centerx;
    private static int shootdelta;
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
    private static int LastDemo;
    private static int playstate;
    private static boolean tedlevel;
    private static int tedlevelnum;
    // Correlates to original/WOLFSRC/ID_HEADS.H extern signon/introscn linkage.
    private static final byte[] introscn = new byte[320 * 200];
    // Correlates to original/WOLFSRC/ID_VH.H extern gamepal.
    private static final byte[] gamepal = new byte[256 * 3];

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

    /**
     * Correlates to {@code original/WOLFSRC/WL_MAIN.C:1411} ({@code void DemoLoop(void)}).
     */
    public static void DemoLoop() {
        int i;
        int level;

        if (tedlevel) {
            NoWait = true;
            NewGame(1, 0);

            for (i = 1; i < C_RUNTIME.argc(); i++) {
                level = ID_US.US_CheckParm(C_RUNTIME.argv(i), DemoParmStrings);
                if (level != -1) {
                    SetGameDifficulty(level);
                    break;
                }
            }

            SetGameEpisodeAndMapFromTed(tedlevelnum);
            GameLoop();
            Quit(null);
            return;
        }

        if (!NoWait) {
            NonShareware();
        }

        StartCPMusic(INTROSONG);

        if (!NoWait) {
            PG13();
        }

        while (1 != 0) {
            while (!NoWait) {
                MM_SortMem();
                CA_CacheScreen(TITLEPIC);
                VW_UpdateScreen();
                ID_VL.VL_FadeIn(0, 255, gamepal, 30);

                if (IN_UserInput(TickBase * 15)) {
                    break;
                }
                ID_VL.VL_FadeOut(0, 255, 0, 0, 0, 30);

                CA_CacheScreen(CREDITSPIC);
                VW_UpdateScreen();
                ID_VL.VL_FadeIn(0, 255, gamepal, 30);
                if (IN_UserInput(TickBase * 10)) {
                    break;
                }
                ID_VL.VL_FadeOut(0, 255, 0, 0, 0, 30);

                DrawHighScores();
                VW_UpdateScreen();
                ID_VL.VL_FadeIn(0, 255, gamepal, 30);
                if (IN_UserInput(TickBase * 10)) {
                    break;
                }

                PlayDemo((LastDemo++) % 4);

                if (playstate == ex_abort) {
                    break;
                }
                StartCPMusic(INTROSONG);
            }

            ID_VL.VL_FadeOut(0, 255, 0, 0, 0, 30);
            if (Keyboard(15) && MS_CheckParm("goobers")) {
                RecordDemo();
            } else {
                US_ControlPanel(0);
            }

            if (startgame || loadedgame) {
                GameLoop();
                ID_VL.VL_FadeOut(0, 255, 0, 0, 0, 30);
                StartCPMusic(INTROSONG);
            }
        }
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

    /**
     * Correlates to {@code original/WOLFSRC/WL_MAIN.C:727} ({@code void SignonScreen(void)}).
     */
    private static void SignonScreen() {
        int segstart;
        int seglength;

        ID_VL.VL_SetVGAPlaneMode();
        ID_VL.VL_TestPaletteSet();
        ID_VL.VL_SetPalette(gamepal);

        if (!virtualreality) {
            ID_VH_H.VW_SetScreen(0x8000, 0);
            ID_VH.VL_MungePic(introscn, 320, 200);
            ID_VL.VL_MemToScreen(introscn, 320, 200, 0, 0);
            ID_VH_H.VW_SetScreen(0, 0);
        }

        segstart = 0;
        seglength = 64000 / 16;
        // TODO: Port FP_SEG/FP_OFF linked-resource reclaim behavior.
        MML_UseSpace(segstart, seglength);
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
        WL_MENU.IntroScreen(
                mminfo_mainmem,
                false, 0,
                false, 0,
                false,
                false, false,
                false,
                false,
                false
        );
    }

    private static void CA_CacheGrChunk(int chunk) {
    }

    private static void CA_CacheScreen(int screen) {
    }

    private static void StartCPMusic(int music) {
    }

    private static void MM_SortMem() {
    }

    private static void VW_UpdateScreen() {
        ID_VL.VL_Present();
    }

    private static boolean IN_UserInput(int delay) {
        return false;
    }

    private static void DrawHighScores() {
    }

    private static void PlayDemo(int index) {
    }

    private static void RecordDemo() {
    }

    private static void US_ControlPanel(int arg) {
    }

    private static void GameLoop() {
    }

    private static void NewGame(int difficulty, int episode) {
    }

    private static void SetGameDifficulty(int level) {
        // TODO: Port gamestate difficulty assignment.
    }

    private static void SetGameEpisodeAndMapFromTed(int levelnum) {
        // TODO: Port WL_MAIN.C tedlevel episode/map mapping.
    }

    private static void NonShareware() {
    }

    private static void PG13() {
    }

    private static void MM_SetLock() {
    }

    private static void LoadLatchMem() {
    }

    private static void BuildTables() {
    }

    private static void SetupWalls() {
    }

    /**
     * Correlates to {@code original/WOLFSRC/WL_MAIN.C:1309} ({@code void ShowViewSize(int width)}).
     */
    private static void ShowViewSize(int width) {
        int oldwidth;
        int oldheight;

        oldwidth = viewwidth;
        oldheight = viewheight;

        viewwidth = width * 16;
        viewheight = (int) (width * 16 * HEIGHTRATIO);
        DrawPlayBorder();

        viewheight = oldheight;
        viewwidth = oldwidth;
    }

    /**
     * Correlates to {@code original/WOLFSRC/WL_MAIN.C:1278}
     * ({@code boolean SetViewSize(unsigned width, unsigned height)}).
     */
    private static boolean SetViewSize(int width, int height) {
        viewwidth = width & ~15;
        viewheight = height & ~1;
        centerx = viewwidth / 2 - 1;
        shootdelta = viewwidth / 10;
        screenofs = ((200 - STATUSLINES - viewheight) / 2 * SCREENWIDTH + (320 - viewwidth) / 8);

        CalcProjection(FOCALLENGTH);
        SetupScaling(viewwidth * 1.5);
        return true;
    }

    /**
     * Correlates to {@code original/WOLFSRC/WL_MAIN.C:1325} ({@code void NewViewSize(int width)}).
     */
    private static void NewViewSize(int size) {
        CA_UpLevel();
        MM_SortMem();
        viewsize = size;
        SetViewSize(size * 16, (int) (size * 16 * HEIGHTRATIO));
        CA_DownLevel();
    }

    private static void CalcProjection(long focalLength) {
    }

    private static void SetupScaling(double view) {
    }

    private static void DrawPlayBorder() {
    }

    private static void CA_UpLevel() {
    }

    private static void CA_DownLevel() {
    }

    private static void InitRedShifts() {
    }

    /**
     * Correlates to {@code original/WOLFSRC/WL_MAIN.C:765} ({@code void FinishSignon(void)}).
     */
    private static void FinishSignon() {
        ID_VH_H.VW_Bar(0, 189, 300, 11, PEEKB_A000_0());
        WindowX = 0;
        WindowW = 320;
        PrintY = 190;

        SETFONTCOLOR(14, 4);
        US_CPrint("Press a key");

        if (!NoWait) {
            IN_Ack();
        }

        ID_VH_H.VW_Bar(0, 189, 300, 11, PEEKB_A000_0());

        PrintY = 190;
        SETFONTCOLOR(10, 4);
        US_CPrint("Working...");

        SETFONTCOLOR(0, 15);
    }

    private static void VR_Interrupt() {
    }

    private static int PEEKB_A000_0() {
        return ASM_RUNTIME.DEBUG_READ_VRAM_PLANE_BYTE(0, 0);
    }

    private static void SETFONTCOLOR(int f, int b) {
        fontcolor = f;
        backcolor = b;
    }

    private static void US_CPrint(String s) {
        // TODO: Port text rendering and windowed print behavior.
    }

    private static void IN_Ack() {
        // TODO: Port input acknowledge/wait behavior.
    }

    private static void MML_UseSpace(int segstart, int seglength) {
        // TODO: Port memory-manager free-space reclamation behavior.
    }

}
