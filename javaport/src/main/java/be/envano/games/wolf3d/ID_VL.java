package be.envano.games.wolf3d;

public final class ID_VL {

    private static final String[] ParmStrings = {"HIDDENCARD", ""};
    private static final int SCREENSEG = 0xA000;
    private static final int SC_INDEX = 0x3C4;
    private static final int SC_MAPMASK = 2;
    private static final int SC_MEMMODE = 4;
    private static final int CRTC_INDEX = 0x3D4;
    private static final int CRTC_OFFSET = 19;
    private static final int CRTC_UNDERLINE = 20;
    private static final int CRTC_MODE = 23;
    private static final int GC_INDEX = 0x3CE;
    private static final int GC_MODE = 5;
    private static final int GC_MISCELLANEOUS = 6;
    private static final int MAXSCANLINES = 200;
    private static int linewidth;
    private static final int[] ylookup = new int[MAXSCANLINES];

    private ID_VL() {
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:71} ({@code void VL_Startup(void)}).
     */
    public static void VL_Startup() {
        int i;
        int videocard;

        ASM_RUNTIME.CLD();

        videocard = VL_VideoID();
        for (i = 1; i < C_RUNTIME.argc(); i++) {
            if (ID_US.US_CheckParm(C_RUNTIME.argv(i), ParmStrings) == 0) {
                videocard = 5;
                break;
            }
        }

        if (videocard != 5) {
            WL_MAIN.Quit("Improper video card!  If you really have a VGA card that I am not \n"
                    + "detecting, use the -HIDDENCARD command line parameter!");
        }
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C} ({@code void VL_Shutdown(void)}).
     */
    public static void VL_Shutdown() {
        VL_SetTextMode();
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:112} ({@code void VL_SetVGAPlaneMode(void)}).
     */
    public static void VL_SetVGAPlaneMode() {
        ASM_RUNTIME.MOV_AX(0x13);
        ASM_RUNTIME.INT(0x10);
        VL_DePlaneVGA();
        ID_VL_H.VGAMAPMASK(15);
        VL_SetLineWidth(40);
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:128} ({@code void VL_SetTextMode(void)}).
     */
    public static void VL_SetTextMode() {
        ASM_RUNTIME.MOV_AX(0x03);
        ASM_RUNTIME.INT(0x10);
    }

    private static int VL_VideoID() {
        // TODO: Port real video-card detection behavior.
        return 5;
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:192} ({@code void VL_DePlaneVGA(void)}).
     */
    private static void VL_DePlaneVGA() {
        //
        // change CPU addressing to non linear mode
        //

        //
        // turn off chain 4 and odd/even
        //
        ASM_RUNTIME.OUTPORTB(SC_INDEX, SC_MEMMODE);
        ASM_RUNTIME.OUTPORTB(SC_INDEX + 1, (ASM_RUNTIME.INPORTB(SC_INDEX + 1) & ~8) | 4);

        ASM_RUNTIME.OUTPORTB(SC_INDEX, SC_MAPMASK);

        //
        // turn off odd/even and set write mode 0
        //
        ASM_RUNTIME.OUTPORTB(GC_INDEX, GC_MODE);
        ASM_RUNTIME.OUTPORTB(GC_INDEX + 1, ASM_RUNTIME.INPORTB(GC_INDEX + 1) & ~0x13);

        //
        // turn off chain
        //
        ASM_RUNTIME.OUTPORTB(GC_INDEX, GC_MISCELLANEOUS);
        ASM_RUNTIME.OUTPORTB(GC_INDEX + 1, ASM_RUNTIME.INPORTB(GC_INDEX + 1) & ~2);

        //
        // clear the entire buffer space, because int 10h only did 16 k / plane
        //
        VL_ClearVideo(0);

        //
        // change CRTC scanning from doubleword to byte mode, allowing >64k scans
        //
        ASM_RUNTIME.OUTPORTB(CRTC_INDEX, CRTC_UNDERLINE);
        ASM_RUNTIME.OUTPORTB(CRTC_INDEX + 1, ASM_RUNTIME.INPORTB(CRTC_INDEX + 1) & ~0x40);

        ASM_RUNTIME.OUTPORTB(CRTC_INDEX, CRTC_MODE);
        ASM_RUNTIME.OUTPORTB(CRTC_INDEX + 1, ASM_RUNTIME.INPORTB(CRTC_INDEX + 1) | 0x40);
    }

    private static void VL_ClearVideo(int color) {
        ASM_RUNTIME.MOV_DX(GC_INDEX);
        ASM_RUNTIME.MOV_AL(GC_MODE);
        ASM_RUNTIME.OUT_DX_AL();
        ASM_RUNTIME.INC_DX();
        ASM_RUNTIME.IN_AL_DX();
        ASM_RUNTIME.AND_AL(0xfc);
        ASM_RUNTIME.OUT_DX_AL();

        ASM_RUNTIME.MOV_DX(SC_INDEX);
        ASM_RUNTIME.MOV_AX(SC_MAPMASK + 15 * 256);
        ASM_RUNTIME.OUT_DX_AX();

        ASM_RUNTIME.MOV_AX(SCREENSEG);
        ASM_RUNTIME.MOV_ES_AX();
        ASM_RUNTIME.MOV_AL(color);
        ASM_RUNTIME.MOV_AH_AL();
        ASM_RUNTIME.MOV_CX(0x8000);
        ASM_RUNTIME.XOR_DI_DI();
        ASM_RUNTIME.REP_STOSW();
    }

    private static void VL_SetLineWidth(int width) {
        int i;
        int offset;

        //
        // set wide virtual screen
        //
        ASM_RUNTIME.OUTPORT(CRTC_INDEX, CRTC_OFFSET + width * 256);

        //
        // set up lookup tables
        //
        linewidth = width * 2;

        offset = 0;

        for (i = 0; i < MAXSCANLINES; i++) {
            ylookup[i] = offset;
            offset += linewidth;
        }
    }
}
