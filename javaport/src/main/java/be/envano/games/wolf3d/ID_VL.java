package be.envano.games.wolf3d;

public final class ID_VL {

    private static final String[] ParmStrings = {"HIDDENCARD", ""};
    private static int linewidth;
    private static final int[] ylookup = new int[ID_VL_H.MAXSCANLINES];

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
        ASM_RUNTIME.OUTPORTB(ID_VL_H.SC_INDEX, ID_VL_H.SC_MEMMODE);
        ASM_RUNTIME.OUTPORTB(ID_VL_H.SC_INDEX + 1, (ASM_RUNTIME.INPORTB(ID_VL_H.SC_INDEX + 1) & ~8) | 4);

        ASM_RUNTIME.OUTPORTB(ID_VL_H.SC_INDEX, ID_VL_H.SC_MAPMASK);

        //
        // turn off odd/even and set write mode 0
        //
        ASM_RUNTIME.OUTPORTB(ID_VL_H.GC_INDEX, ID_VL_H.GC_MODE);
        ASM_RUNTIME.OUTPORTB(ID_VL_H.GC_INDEX + 1, ASM_RUNTIME.INPORTB(ID_VL_H.GC_INDEX + 1) & ~0x13);

        //
        // turn off chain
        //
        ASM_RUNTIME.OUTPORTB(ID_VL_H.GC_INDEX, ID_VL_H.GC_MISCELLANEOUS);
        ASM_RUNTIME.OUTPORTB(ID_VL_H.GC_INDEX + 1, ASM_RUNTIME.INPORTB(ID_VL_H.GC_INDEX + 1) & ~2);

        //
        // clear the entire buffer space, because int 10h only did 16 k / plane
        //
        VL_ClearVideo(0);

        //
        // change CRTC scanning from doubleword to byte mode, allowing >64k scans
        //
        ASM_RUNTIME.OUTPORTB(ID_VL_H.CRTC_INDEX, ID_VL_H.CRTC_UNDERLINE);
        ASM_RUNTIME.OUTPORTB(ID_VL_H.CRTC_INDEX + 1, ASM_RUNTIME.INPORTB(ID_VL_H.CRTC_INDEX + 1) & ~0x40);

        ASM_RUNTIME.OUTPORTB(ID_VL_H.CRTC_INDEX, ID_VL_H.CRTC_MODE);
        ASM_RUNTIME.OUTPORTB(ID_VL_H.CRTC_INDEX + 1, ASM_RUNTIME.INPORTB(ID_VL_H.CRTC_INDEX + 1) | 0x40);
    }

    private static void VL_ClearVideo(int color) {
        ASM_RUNTIME.MOV_DX(ID_VL_H.GC_INDEX);
        ASM_RUNTIME.MOV_AL(ID_VL_H.GC_MODE);
        ASM_RUNTIME.OUT_DX_AL();
        ASM_RUNTIME.INC_DX();
        ASM_RUNTIME.IN_AL_DX();
        ASM_RUNTIME.AND_AL(0xfc);
        ASM_RUNTIME.OUT_DX_AL();

        ASM_RUNTIME.MOV_DX(ID_VL_H.SC_INDEX);
        ASM_RUNTIME.MOV_AX(ID_VL_H.SC_MAPMASK + 15 * 256);
        ASM_RUNTIME.OUT_DX_AX();

        ASM_RUNTIME.MOV_AX(ID_VL_H.SCREENSEG);
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
        ASM_RUNTIME.OUTPORT(ID_VL_H.CRTC_INDEX, ID_VL_H.CRTC_OFFSET + width * 256);

        //
        // set up lookup tables
        //
        linewidth = width * 2;

        offset = 0;

        for (i = 0; i < ID_VL_H.MAXSCANLINES; i++) {
            ylookup[i] = offset;
            offset += linewidth;
        }
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:277} ({@code void VL_SetSplitScreen (int linenum)}).
     */
    public static void VL_SetSplitScreen(int linenum) {
        VL_WaitVBL(1);
        linenum = linenum * 2 - 1;
        ASM_RUNTIME.OUTPORTB(ID_VL_H.CRTC_INDEX, ID_VL_H.CRTC_LINECOMPARE);
        ASM_RUNTIME.OUTPORTB(ID_VL_H.CRTC_INDEX + 1, linenum % 256);
        ASM_RUNTIME.OUTPORTB(ID_VL_H.CRTC_INDEX, ID_VL_H.CRTC_OVERFLOW);
        ASM_RUNTIME.OUTPORTB(ID_VL_H.CRTC_INDEX + 1, 1 + 16 * (linenum / 256));
        ASM_RUNTIME.OUTPORTB(ID_VL_H.CRTC_INDEX, ID_VL_H.CRTC_MAXSCANLINE);
        ASM_RUNTIME.OUTPORTB(ID_VL_H.CRTC_INDEX + 1,
                ASM_RUNTIME.INPORTB(ID_VL_H.CRTC_INDEX + 1) & (255 - 64));
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:309} ({@code void VL_FillPalette (int red, int green, int blue)}).
     */
    public static void VL_FillPalette(int red, int green, int blue) {
        int i;

        ASM_RUNTIME.OUTPORTB(ID_VL_H.PEL_WRITE_ADR, 0);
        for (i = 0; i < 256; i++) {
            ASM_RUNTIME.OUTPORTB(ID_VL_H.PEL_DATA, red);
            ASM_RUNTIME.OUTPORTB(ID_VL_H.PEL_DATA, green);
            ASM_RUNTIME.OUTPORTB(ID_VL_H.PEL_DATA, blue);
        }
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:332} ({@code void VL_SetColor (int color, int red, int green, int blue)}).
     */
    public static void VL_SetColor(int color, int red, int green, int blue) {
        ASM_RUNTIME.OUTPORTB(ID_VL_H.PEL_WRITE_ADR, color);
        ASM_RUNTIME.OUTPORTB(ID_VL_H.PEL_DATA, red);
        ASM_RUNTIME.OUTPORTB(ID_VL_H.PEL_DATA, green);
        ASM_RUNTIME.OUTPORTB(ID_VL_H.PEL_DATA, blue);
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:350} ({@code void VL_GetColor (int color, int *red, int *green, int *blue)}).
     */
    public static void VL_GetColor(int color, int[] red, int[] green, int[] blue) {
        ASM_RUNTIME.OUTPORTB(ID_VL_H.PEL_READ_ADR, color);
        red[0] = ASM_RUNTIME.INPORTB(ID_VL_H.PEL_DATA);
        green[0] = ASM_RUNTIME.INPORTB(ID_VL_H.PEL_DATA);
        blue[0] = ASM_RUNTIME.INPORTB(ID_VL_H.PEL_DATA);
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL_A.ASM:30} ({@code PROC VL_WaitVBL num:WORD}).
     */
    public static void VL_WaitVBL(int vbls) {
        while (vbls != 0) {
            while ((ASM_RUNTIME.INPORTB(ID_VL_H.STATUS_REGISTER_1) & 8) != 0) {
                // Wait for non-sync.
            }

            while ((ASM_RUNTIME.INPORTB(ID_VL_H.STATUS_REGISTER_1) & 8) == 0) {
                // Wait for sync.
            }

            vbls--;
        }
    }
}
