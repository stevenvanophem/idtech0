package be.envano.games.wolf3d;

/**
 * Port scaffold for original/WOLFSRC/WL_MAIN.C.
 */
public final class WL_MAIN {

    // C source: original/WOLFSRC/WL_MAIN.C:67
    static boolean virtualreality;
    // C source usage: original/WOLFSRC/WL_MAIN.C:733
    static final Object gamepal = new Object();
    // C source usage: original/WOLFSRC/WL_MAIN.C:738-739
    static final Object introscn = new Object();

    private WL_MAIN() {
    }

    // C source: original/WOLFSRC/WL_MAIN.C:1586
    public static void main(String[] args) {
        C_RUNTIME.initArgv(args);
        // C call site: original/WOLFSRC/WL_MAIN.C:1606
        CheckForEpisodes();
        // C call site: original/WOLFSRC/WL_MAIN.C:1608
        Patch386();
        // C call site: original/WOLFSRC/WL_MAIN.C:1610
        InitGame();
        // C call site: original/WOLFSRC/WL_MAIN.C:1612
        DemoLoop();
        // C call site: original/WOLFSRC/WL_MAIN.C:1614
        Quit("Demo loop exited???");
    }

    // C source: original/WOLFSRC/WL_MAIN.C:1606
    static void CheckForEpisodes() {
        // Traversal placeholder: skipped branch for now; tracked in .idea/ai/ledger.md.
    }

    // C source: original/WOLFSRC/WL_MAIN.C:241
    static void Patch386() {
        // Traversal placeholder: skipped branch for now; tracked in .idea/ai/ledger.md.
    }

    // C source: original/WOLFSRC/WL_MAIN.C:1145
    static void InitGame() {
        // C source control flow: original/WOLFSRC/WL_MAIN.C:1150-1153
        // C call site: original/WOLFSRC/WL_MAIN.C:1150
        if (MS_CheckParm("virtual")) {
            // C line: original/WOLFSRC/WL_MAIN.C:1151
            virtualreality = true;
        } else {
            // C line: original/WOLFSRC/WL_MAIN.C:1153
            virtualreality = false;
        }

        // Window milestone path only:
        // MM_Startup();
        // C call site: original/WOLFSRC/WL_MAIN.C:1157
        SignonScreen();
        // C call site: original/WOLFSRC/WL_MAIN.C:1159 (VW_Startup -> VL_Startup)
        VW_Startup();

        // Deferred until after first window milestone:
        // IN_Startup();
        // PM_Startup();
        // PM_UnlockMainMem();
        // SD_Startup();
        // CA_Startup();
        // US_Startup();
        // ...
    }

    // C source: original/WOLFSRC/WL_MAIN.C:1411
    static void DemoLoop() {
        throw new UnsupportedOperationException("TODO port WL_MAIN.C::DemoLoop");
    }

    // C source: original/WOLFSRC/WL_MAIN.C:1346
    static void Quit(String error) {
        throw new UnsupportedOperationException("TODO port WL_MAIN.C::Quit");
    }

    // C source: original/WOLFSRC/WL_MAIN.C:727
    static void SignonScreen() {
        // C call site: original/WOLFSRC/WL_MAIN.C:731
        ID_VL.VL_SetVGAPlaneMode();
        // C call site: original/WOLFSRC/WL_MAIN.C:732
        VL_TestPaletteSet();
        // C call site: original/WOLFSRC/WL_MAIN.C:733
        VL_SetPalette(gamepal);

        // C source control flow: original/WOLFSRC/WL_MAIN.C:735
        if (!virtualreality) {
            // C call site: original/WOLFSRC/WL_MAIN.C:737
            VW_SetScreen(0x8000, 0);
            // C call site: original/WOLFSRC/WL_MAIN.C:738
            VL_MungePic(introscn, 320, 200);
            // C call site: original/WOLFSRC/WL_MAIN.C:739
            VL_MemToScreen(introscn, 320, 200, 0, 0);
            // C call site: original/WOLFSRC/WL_MAIN.C:740
            VW_SetScreen(0, 0);
        }

        // Deferred until after first window milestone:
        // C lines: original/WOLFSRC/WL_MAIN.C:746-753 (signon memory reclaim via FP_SEG/FP_OFF/MML_UseSpace)
    }

    // C source: original/WOLFSRC/ID_VH.H:96 (VW_Startup -> VL_Startup)
    static void VW_Startup() {
        throw new UnsupportedOperationException("TODO port ID_VL.C::VL_Startup");
    }

    // C source: original/WOLFSRC/WL_MAIN.C:819
    static boolean MS_CheckParm(String check) {
        int i;
        String parm;

        for (i = 1; i < C_RUNTIME._argc; i++) {
            parm = C_RUNTIME._argv[i];
            int p = 0;

            // C lines: original/WOLFSRC/WL_MAIN.C:827-830
            while (p < parm.length() && !Character.isLetter(parm.charAt(p))) {
                p++;
            }

            // C line: original/WOLFSRC/WL_MAIN.C:832
            if (parm.substring(p).equalsIgnoreCase(check)) {
                return true;
            }
        }

        // C line: original/WOLFSRC/WL_MAIN.C:836
        return false;
    }

    // C source: original/WOLFSRC/ID_VL.C:546
    static void VL_TestPaletteSet() {
        // Deferred for window milestone: VGA palette test path is not required to show an SDL window.
        // See .idea/ai/ledger.md for deferred line tracking.
    }

    // C source: original/WOLFSRC/ID_VL.C:371
    static void VL_SetPalette(Object palette) {
        throw new UnsupportedOperationException("TODO port ID_VL.C::VL_SetPalette");
    }

    // C source: original/WOLFSRC/ID_VL.C:36 (called through VW macro layer from WL_MAIN.C)
    static void VW_SetScreen(int crtc, int pelpan) {
        throw new UnsupportedOperationException("TODO port ID_VL.C::VL_SetScreen");
    }

    // C source call site: original/WOLFSRC/WL_MAIN.C:738
    static void VL_MungePic(Object pic, int width, int height) {
        throw new UnsupportedOperationException("TODO port WL_MAIN.C signon prep (VL_MungePic)");
    }

    // C source: original/WOLFSRC/ID_VL.C:791
    static void VL_MemToScreen(Object source, int width, int height, int x, int y) {
        throw new UnsupportedOperationException("TODO port ID_VL.C::VL_MemToScreen");
    }
}
