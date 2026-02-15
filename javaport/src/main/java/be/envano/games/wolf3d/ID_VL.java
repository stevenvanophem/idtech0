package be.envano.games.wolf3d;

import java.util.Arrays;

public final class ID_VL {

    private static final String[] ParmStrings = {"HIDDENCARD", ""};
    private static int bufferofs;
    private static int displayofs;
    private static int pelpan;
    private static int screenseg = ID_VL_H.SCREENSEG;
    private static int linewidth;
    private static final int[] ylookup = new int[ID_VL_H.MAXSCANLINES];
    private static boolean fastpalette;
    private static final byte[] palette1 = new byte[256 * 3];
    private static final byte[] palette2 = new byte[256 * 3];
    private static boolean screenfaded;
    private static int bordercolor;
    private static final byte[] pixmasks = {1, 2, 4, 8};
    private static final byte[] leftmasks = {15, 14, 12, 8};
    private static final byte[] rightmasks = {1, 3, 7, 15};

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
     * Correlates to {@code original/WOLFSRC/ID_VL.C:371} ({@code void VL_SetPalette (byte far *palette)}).
     */
    public static void VL_SetPalette(byte[] palette) {
        ASM_RUNTIME.MOV_DX(ID_VL_H.PEL_WRITE_ADR);
        ASM_RUNTIME.MOV_AL(0);
        ASM_RUNTIME.OUT_DX_AL();
        ASM_RUNTIME.MOV_DX(ID_VL_H.PEL_DATA);
        ASM_RUNTIME.LDS_SI(palette);

        ASM_RUNTIME.TEST_IMM8((fastpalette ? 1 : 0), 1);
        if (ASM_RUNTIME.JZ()) {
            ASM_RUNTIME.MOV_CX(256);
            do {
                ASM_RUNTIME.LODSB();
                ASM_RUNTIME.OUT_DX_AL();
                ASM_RUNTIME.LODSB();
                ASM_RUNTIME.OUT_DX_AL();
                ASM_RUNTIME.LODSB();
                ASM_RUNTIME.OUT_DX_AL();
            } while (ASM_RUNTIME.LOOP());
        } else {
            ASM_RUNTIME.MOV_CX(768);
            ASM_RUNTIME.REP_OUTSB();
        }

        ASM_RUNTIME.MOV_AX_SS();
        ASM_RUNTIME.MOV_DS_AX();
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:424} ({@code void VL_GetPalette (byte far *palette)}).
     */
    public static void VL_GetPalette(byte[] palette) {
        int i;

        ASM_RUNTIME.OUTPORTB(ID_VL_H.PEL_READ_ADR, 0);
        for (i = 0; i < 768; i++) {
            palette[i] = (byte) ASM_RUNTIME.INPORTB(ID_VL_H.PEL_DATA);
        }
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:535} ({@code void VL_TestPaletteSet (void)}).
     */
    public static void VL_TestPaletteSet() {
        int i;

        for (i = 0; i < 768; i++) {
            palette1[i] = (byte) i;
        }

        fastpalette = true;
        VL_SetPalette(palette1);
        VL_GetPalette(palette2);
        if (!Arrays.equals(palette1, palette2)) {
            fastpalette = false;
        }
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:444}
     * ({@code void VL_FadeOut (int start, int end, int red, int green, int blue, int steps)}).
     */
    public static void VL_FadeOut(int start, int end, int red, int green, int blue, int steps) {
        int i;
        int j;
        int orig;
        int delta;
        int origptr;
        int newptr;

        VL_WaitVBL(1);
        VL_GetPalette(palette1);
        System.arraycopy(palette1, 0, palette2, 0, 768);

        for (i = 0; i < steps; i++) {
            origptr = start * 3;
            newptr = start * 3;
            for (j = start; j <= end; j++) {
                orig = palette1[origptr++] & 0xff;
                delta = red - orig;
                palette2[newptr++] = (byte) (orig + delta * i / steps);
                orig = palette1[origptr++] & 0xff;
                delta = green - orig;
                palette2[newptr++] = (byte) (orig + delta * i / steps);
                orig = palette1[origptr++] & 0xff;
                delta = blue - orig;
                palette2[newptr++] = (byte) (orig + delta * i / steps);
            }

            VL_WaitVBL(1);
            VL_SetPalette(palette2);
        }

        VL_FillPalette(red, green, blue);

        screenfaded = true;
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:495}
     * ({@code void VL_FadeIn (int start, int end, byte far *palette, int steps)}).
     */
    public static void VL_FadeIn(int start, int end, byte[] palette, int steps) {
        int i;
        int j;
        int delta;

        VL_WaitVBL(1);
        VL_GetPalette(palette1);
        System.arraycopy(palette1, 0, palette2, 0, palette1.length);

        start *= 3;
        end = end * 3 + 2;

        for (i = 0; i < steps; i++) {
            for (j = start; j <= end; j++) {
                delta = (palette[j] & 0xff) - (palette1[j] & 0xff);
                palette2[j] = (byte) ((palette1[j] & 0xff) + delta * i / steps);
            }

            VL_WaitVBL(1);
            VL_SetPalette(palette2);
        }

        VL_SetPalette(palette);
        screenfaded = false;
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:558} ({@code void VL_ColorBorder (int color)}).
     */
    public static void VL_ColorBorder(int color) {
        ASM_RUNTIME.MOV_AH(0x10);
        ASM_RUNTIME.MOV_AL(1);
        ASM_RUNTIME.MOV_BH(color);
        ASM_RUNTIME.INT(0x10);
        bordercolor = color;
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:583} ({@code void VL_Plot (int x, int y, int color)}).
     */
    public static void VL_Plot(int x, int y, int color) {
        int mask;

        mask = pixmasks[x & 3] & 0xff;
        ID_VL_H.VGAMAPMASK(mask);
        ASM_RUNTIME.WRITE_VIDEO_BYTE(screenseg, bufferofs + (ylookup[y] + (x >> 2)), color);
        ID_VL_H.VGAMAPMASK(15);
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
