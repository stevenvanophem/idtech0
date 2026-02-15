package be.envano.games.wolf3d;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
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
    private static boolean windowBootstrapAttempted;
    private static boolean windowCreated;
    private static long windowHandle;
    private static MethodHandle getDCHandle;
    private static MethodHandle releaseDCHandle;
    private static MethodHandle stretchDIBitsHandle;
    private static MethodHandle peekMessageWHandle;
    private static MethodHandle translateMessageHandle;
    private static MethodHandle dispatchMessageWHandle;
    private static final byte[] currentPalette = new byte[256 * 3];

    private ID_VL() {
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:71} ({@code void VL_Startup(void)}).
     */
    public static void VL_Startup() {
        int i;
        int videocard;

        ASM_RUNTIME.CLD();

        videocard = ID_VL_A.VL_VideoID();
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
        VL_CreateWindow();
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
     * Public wrapper for header macro ownership parity (`VW_SetLineWidth` -> `VL_SetLineWidth`).
     */
    public static void VL_SetLineWidth_PUBLIC(int width) {
        VL_SetLineWidth(width);
    }

    static int GetLineWidthForAsm() {
        return linewidth;
    }

    static long GetWindowHandleForPlatform() {
        return windowHandle;
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
     * Correlates to {@code original/WOLFSRC/ID_VL_A.ASM:67} ({@code PROC VL_SetCRTC crtc:WORD}).
     */
    public static void VL_SetCRTC(int crtc) {
        ID_VL_A.VL_SetCRTC(crtc);
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL_A.ASM:121} ({@code PROC VL_SetScreen crtc:WORD, pel:WORD}).
     */
    public static void VL_SetScreen(int crtc, int pel) {
        ID_VL_A.VL_SetScreen(crtc, pel);
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:309} ({@code void VL_FillPalette (int red, int green, int blue)}).
     */
    public static void VL_FillPalette(int red, int green, int blue) {
        int i;
        int c;

        ASM_RUNTIME.OUTPORTB(ID_VL_H.PEL_WRITE_ADR, 0);
        for (i = 0; i < 256; i++) {
            ASM_RUNTIME.OUTPORTB(ID_VL_H.PEL_DATA, red);
            ASM_RUNTIME.OUTPORTB(ID_VL_H.PEL_DATA, green);
            ASM_RUNTIME.OUTPORTB(ID_VL_H.PEL_DATA, blue);
        }
        for (c = 0; c < 256; c++) {
            currentPalette[c * 3] = (byte) red;
            currentPalette[c * 3 + 1] = (byte) green;
            currentPalette[c * 3 + 2] = (byte) blue;
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
        currentPalette[color * 3] = (byte) red;
        currentPalette[color * 3 + 1] = (byte) green;
        currentPalette[color * 3 + 2] = (byte) blue;
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
        int i;

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

        for (i = 0; i < 768 && i < palette.length; i++) {
            currentPalette[i] = palette[i];
        }
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
     * Correlates to {@code original/WOLFSRC/ID_VL.C:598} ({@code void VL_Hlin (unsigned x, unsigned y, unsigned width, unsigned color)}).
     */
    public static void VL_Hlin(int x, int y, int width, int color) {
        int xbyte;
        int dest;
        int leftmask;
        int rightmask;
        int midbytes;

        xbyte = x >> 2;
        leftmask = leftmasks[x & 3] & 0xff;
        rightmask = rightmasks[(x + width - 1) & 3] & 0xff;
        midbytes = ((x + width + 3) >> 2) - xbyte - 2;

        dest = bufferofs + ylookup[y] + xbyte;

        if (midbytes < 0) {
            ID_VL_H.VGAMAPMASK(leftmask & rightmask);
            ASM_RUNTIME.WRITE_VIDEO_BYTE(screenseg, dest, color);
            ID_VL_H.VGAMAPMASK(15);
            return;
        }

        ID_VL_H.VGAMAPMASK(leftmask);
        ASM_RUNTIME.WRITE_VIDEO_BYTE(screenseg, dest, color);
        dest++;

        ID_VL_H.VGAMAPMASK(15);
        ASM_RUNTIME.FILL_VIDEO_BYTES(screenseg, dest, color, midbytes);
        dest += midbytes;

        ID_VL_H.VGAMAPMASK(rightmask);
        ASM_RUNTIME.WRITE_VIDEO_BYTE(screenseg, dest, color);

        ID_VL_H.VGAMAPMASK(15);
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:643} ({@code void VL_Vlin (int x, int y, int height, int color)}).
     */
    public static void VL_Vlin(int x, int y, int height, int color) {
        int dest;
        int mask;

        mask = pixmasks[x & 3] & 0xff;
        ID_VL_H.VGAMAPMASK(mask);

        dest = bufferofs + ylookup[y] + (x >> 2);

        while (height-- != 0) {
            ASM_RUNTIME.WRITE_VIDEO_BYTE(screenseg, dest, color);
            dest += linewidth;
        }

        ID_VL_H.VGAMAPMASK(15);
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:692} ({@code void VL_Bar (int x, int y, int width, int height, int color)}).
     */
    public static void VL_Bar(int x, int y, int width, int height, int color) {
        int dest;
        int leftmask;
        int rightmask;
        int midbytes;
        int linedelta;

        leftmask = leftmasks[x & 3] & 0xff;
        rightmask = rightmasks[(x + width - 1) & 3] & 0xff;
        midbytes = ((x + width + 3) >> 2) - (x >> 2) - 2;
        linedelta = linewidth - (midbytes + 1);

        dest = bufferofs + ylookup[y] + (x >> 2);

        if (midbytes < 0) {
            ID_VL_H.VGAMAPMASK(leftmask & rightmask);
            while (height-- != 0) {
                ASM_RUNTIME.WRITE_VIDEO_BYTE(screenseg, dest, color);
                dest += linewidth;
            }
            ID_VL_H.VGAMAPMASK(15);
            return;
        }

        while (height-- != 0) {
            ID_VL_H.VGAMAPMASK(leftmask);
            ASM_RUNTIME.WRITE_VIDEO_BYTE(screenseg, dest, color);
            dest++;

            ID_VL_H.VGAMAPMASK(15);
            ASM_RUNTIME.FILL_VIDEO_BYTES(screenseg, dest, color, midbytes);
            dest += midbytes;

            ID_VL_H.VGAMAPMASK(rightmask);
            ASM_RUNTIME.WRITE_VIDEO_BYTE(screenseg, dest, color);

            dest += linedelta;
        }

        ID_VL_H.VGAMAPMASK(15);
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:752} ({@code void VL_MemToLatch (byte far *source, int width, int height, unsigned dest)}).
     */
    public static void VL_MemToLatch(byte[] source, int width, int height, int dest) {
        int count;
        int plane;
        int mask;
        int sourceIndex;

        count = ((width + 3) / 4) * height;
        mask = 1;
        sourceIndex = 0;
        for (plane = 0; plane < 4; plane++) {
            ID_VL_H.VGAMAPMASK(mask);
            mask <<= 1;

            ASM_RUNTIME.MOV_CX(count);
            ASM_RUNTIME.MOV_AX(ID_VL_H.SCREENSEG);
            ASM_RUNTIME.MOV_ES_AX();
            ASM_RUNTIME.COPY_BYTES_TO_VIDEO(screenseg, dest, source, sourceIndex, count);
            ASM_RUNTIME.MOV_AX_SS();
            ASM_RUNTIME.MOV_DS_AX();

            sourceIndex += count;
        }
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:791} ({@code void VL_MemToScreen (byte far *source, int width, int height, int x, int y)}).
     */
    public static void VL_MemToScreen(byte[] source, int width, int height, int x, int y) {
        int screen;
        int dest;
        int mask;
        int plane;
        int sourceIndex;

        width >>= 2;
        dest = bufferofs + ylookup[y] + (x >> 2);
        mask = 1 << (x & 3);
        sourceIndex = 0;

        for (plane = 0; plane < 4; plane++) {
            ID_VL_H.VGAMAPMASK(mask);
            mask <<= 1;
            if (mask == 16) {
                mask = 1;
            }

            screen = dest;
            for (y = 0; y < height; y++, screen += linewidth, sourceIndex += width) {
                ASM_RUNTIME.COPY_BYTES_TO_VIDEO(screenseg, screen, source, sourceIndex, width);
            }
        }
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:826} ({@code void VL_MaskedToScreen (byte far *source, int width, int height, int x, int y)}).
     */
    public static void VL_MaskedToScreen(byte[] source, int width, int height, int x, int y) {
        int screen;
        int dest;
        int mask;
        int plane;
        int sourceIndex;

        width >>= 2;
        dest = bufferofs + ylookup[y] + (x >> 2);
        // mask = 1 << (x&3);
        // maskptr = source;
        mask = 0;
        sourceIndex = 0;

        for (plane = 0; plane < 4; plane++) {
            ID_VL_H.VGAMAPMASK(mask);
            mask <<= 1;
            if (mask == 16) {
                mask = 1;
            }

            screen = dest;
            for (y = 0; y < height; y++, screen += linewidth, sourceIndex += width) {
                ASM_RUNTIME.COPY_BYTES_TO_VIDEO(screenseg, screen, source, sourceIndex, width);
            }
        }
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:861} ({@code void VL_LatchToScreen (unsigned source, int width, int height, int x, int y)}).
     */
    public static void VL_LatchToScreen(int source, int width, int height, int x, int y) {
        int src;
        int dest;
        int linedelta;
        int row;

        ID_VL_H.VGAWRITEMODE(1);
        ID_VL_H.VGAMAPMASK(15);

        dest = bufferofs + ylookup[y] + (x >> 2);
        src = source;
        linedelta = linewidth - width;
        for (row = 0; row < height; row++) {
            ASM_RUNTIME.COPY_VIDEO_TO_VIDEO(screenseg, src, dest, width);
            src += width;
            dest += width;
            dest += linedelta;
        }

        ASM_RUNTIME.MOV_AX_SS();
        ASM_RUNTIME.MOV_DS_AX();

        ID_VL_H.VGAWRITEMODE(0);
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL_A.ASM:237}
     * ({@code PROC VL_ScreenToScreen source:WORD, dest:WORD, wide:WORD, height:WORD}).
     */
    public static void VL_ScreenToScreen(int source, int dest, int width, int height) {
        ID_VL_A.VL_ScreenToScreen(source, dest, width, height);
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:960} ({@code void VL_DrawTile8String (char *str, char far *tile8ptr, int printx, int printy)}).
     */
    public static void VL_DrawTile8String(String str, byte[] tile8ptr, int printx, int printy) {
        int i;
        int strIndex;
        int dest;
        int screen;
        int src;
        int ch;

        dest = bufferofs + ylookup[printy] + (printx >> 2);

        strIndex = 0;
        while (strIndex < str.length() && str.charAt(strIndex) != 0) {
            ch = str.charAt(strIndex) & 0xff;
            src = ch << 6;

            ID_VL_H.VGAMAPMASK(1);
            screen = dest;
            for (i = 0; i < 8; i++, screen += linewidth) {
                ASM_RUNTIME.COPY_BYTES_TO_VIDEO(screenseg, screen, tile8ptr, src, 2);
                src += 2;
            }
            ID_VL_H.VGAMAPMASK(2);
            screen = dest;
            for (i = 0; i < 8; i++, screen += linewidth) {
                ASM_RUNTIME.COPY_BYTES_TO_VIDEO(screenseg, screen, tile8ptr, src, 2);
                src += 2;
            }
            ID_VL_H.VGAMAPMASK(4);
            screen = dest;
            for (i = 0; i < 8; i++, screen += linewidth) {
                ASM_RUNTIME.COPY_BYTES_TO_VIDEO(screenseg, screen, tile8ptr, src, 2);
                src += 2;
            }
            ID_VL_H.VGAMAPMASK(8);
            screen = dest;
            for (i = 0; i < 8; i++, screen += linewidth) {
                ASM_RUNTIME.COPY_BYTES_TO_VIDEO(screenseg, screen, tile8ptr, src, 2);
                src += 2;
            }

            strIndex++;
            printx += 8;
            dest += 2;
        }
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:1005} ({@code void VL_DrawLatch8String (char *str, unsigned tile8ptr, int printx, int printy)}).
     */
    public static void VL_DrawLatch8String(String str, int tile8ptr, int printx, int printy) {
        int i;
        int strIndex;
        int src;
        int dest;
        int lineDest;
        int ch;

        dest = bufferofs + ylookup[printy] + (printx >> 2);

        ID_VL_H.VGAWRITEMODE(1);
        ID_VL_H.VGAMAPMASK(15);

        strIndex = 0;
        while (strIndex < str.length() && str.charAt(strIndex) != 0) {
            ch = str.charAt(strIndex) & 0xff;
            src = tile8ptr + (ch << 4);

            lineDest = dest;
            for (i = 0; i < 8; i++) {
                ASM_RUNTIME.COPY_VIDEO_TO_VIDEO(screenseg, src + i * 2, lineDest, 2);
                lineDest += linewidth;
            }

            ASM_RUNTIME.MOV_AX_SS();
            ASM_RUNTIME.MOV_DS_AX();

            strIndex++;
            printx += 8;
            dest += 2;
        }

        ID_VL_H.VGAWRITEMODE(0);
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:1071} ({@code void VL_SizeTile8String (char *str, int *width, int *height)}).
     */
    public static void VL_SizeTile8String(String str, int[] width, int[] height) {
        int i;

        i = 0;
        while (i < str.length() && str.charAt(i) != 0) {
            i++;
        }

        height[0] = 8;
        width[0] = 8 * i;
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL_A.ASM:30} ({@code PROC VL_WaitVBL num:WORD}).
     */
    public static void VL_WaitVBL(int vbls) {
        ID_VL_A.VL_WaitVBL(vbls);
    }

    public static void VL_Present() {
        final int WINDOW_WIDTH = 960;
        final int WINDOW_HEIGHT = 600;
        final int BI_RGB = 0;
        final int DIB_RGB_COLORS = 0;
        final int SRCCOPY = 0x00CC0020;
        final int SCREEN_WIDTH = 320;
        final int SCREEN_HEIGHT = 200;
        final int SCREEN_STRIDE = 80;
        int x;
        int y;
        int srcOffset;
        int plane;
        int paletteIndex;
        int pr;
        int pg;
        int pb;
        int color;
        long pixelOffset;
        MemorySegment hwnd;
        MemorySegment hdc;

        if (!windowCreated || stretchDIBitsHandle == null || getDCHandle == null || releaseDCHandle == null) {
            return;
        }
        VL_PumpEvents();

        try (Arena arena = Arena.ofConfined()) {
            hwnd = MemorySegment.ofAddress(windowHandle);
            hdc = (MemorySegment) getDCHandle.invoke(hwnd);
            if (hdc == MemorySegment.NULL) {
                return;
            }

            MemorySegment pixels = arena.allocate((long) SCREEN_WIDTH * SCREEN_HEIGHT * 4, 4);
            MemorySegment bmi = arena.allocate(44, 4);

            // BITMAPINFOHEADER
            bmi.set(ValueLayout.JAVA_INT, 0, 40);
            bmi.set(ValueLayout.JAVA_INT, 4, SCREEN_WIDTH);
            bmi.set(ValueLayout.JAVA_INT, 8, -SCREEN_HEIGHT);
            bmi.set(ValueLayout.JAVA_SHORT, 12, (short) 1);
            bmi.set(ValueLayout.JAVA_SHORT, 14, (short) 32);
            bmi.set(ValueLayout.JAVA_INT, 16, BI_RGB);
            bmi.set(ValueLayout.JAVA_INT, 20, SCREEN_WIDTH * SCREEN_HEIGHT * 4);
            bmi.set(ValueLayout.JAVA_INT, 24, 0);
            bmi.set(ValueLayout.JAVA_INT, 28, 0);
            bmi.set(ValueLayout.JAVA_INT, 32, 0);
            bmi.set(ValueLayout.JAVA_INT, 36, 0);

            pixelOffset = 0;
            for (y = 0; y < SCREEN_HEIGHT; y++) {
                for (x = 0; x < SCREEN_WIDTH; x++) {
                    srcOffset = (displayofs + y * SCREEN_STRIDE + (x >> 2)) & 0xffff;
                    plane = x & 3;
                    paletteIndex = ASM_RUNTIME.DEBUG_READ_VRAM_PLANE_BYTE(plane, srcOffset) & 0xff;

                    pr = (currentPalette[paletteIndex * 3] & 0xff) << 2;
                    pg = (currentPalette[paletteIndex * 3 + 1] & 0xff) << 2;
                    pb = (currentPalette[paletteIndex * 3 + 2] & 0xff) << 2;
                    if (pr > 255) {
                        pr = 255;
                    }
                    if (pg > 255) {
                        pg = 255;
                    }
                    if (pb > 255) {
                        pb = 255;
                    }

                    color = (pr << 16) | (pg << 8) | pb;
                    pixels.set(ValueLayout.JAVA_INT, pixelOffset, color);
                    pixelOffset += 4;
                }
            }

            stretchDIBitsHandle.invoke(
                    hdc,
                    0, 0, WINDOW_WIDTH, WINDOW_HEIGHT,
                    0, 0, SCREEN_WIDTH, SCREEN_HEIGHT,
                    pixels,
                    bmi,
                    DIB_RGB_COLORS,
                    SRCCOPY
            );
            releaseDCHandle.invoke(hwnd, hdc);
        } catch (Throwable t) {
            throw new IllegalStateException("Win32 present failed", t);
        }
    }

    private static void VL_PumpEvents() {
        final int PM_REMOVE = 0x0001;
        final int WM_QUIT = 0x0012;
        final long MSG_MESSAGE_OFFSET = 8L;
        MemorySegment msg;
        int hasMessage;
        int message;

        if (peekMessageWHandle == null || translateMessageHandle == null || dispatchMessageWHandle == null) {
            return;
        }

        try (Arena arena = Arena.ofConfined()) {
            msg = arena.allocate(48, 8);
            while (true) {
                hasMessage = (int) peekMessageWHandle.invoke(
                        msg,
                        MemorySegment.NULL,
                        0,
                        0,
                        PM_REMOVE
                );
                if (hasMessage == 0) {
                    break;
                }

                message = msg.get(ValueLayout.JAVA_INT, MSG_MESSAGE_OFFSET);
                if (message == WM_QUIT) {
                    windowCreated = false;
                    break;
                }

                translateMessageHandle.invoke(msg);
                dispatchMessageWHandle.invoke(msg);
            }
        } catch (Throwable t) {
            throw new IllegalStateException("Win32 message pump failed", t);
        }
    }

    private static void VL_CreateWindow() {
        final int WS_OVERLAPPEDWINDOW = 0x00CF0000;
        final int WS_VISIBLE = 0x10000000;
        final int CW_USEDEFAULT = 0x80000000;
        final int SW_SHOW = 5;
        final int WINDOW_WIDTH = 960;
        final int WINDOW_HEIGHT = 600;
        String os;
        Linker linker;
        SymbolLookup user32;
        SymbolLookup gdi32;
        MethodHandle createWindowExW;
        MethodHandle showWindow;
        MethodHandle updateWindow;
        MemorySegment hwnd;

        if (windowBootstrapAttempted) {
            return;
        }
        windowBootstrapAttempted = true;

        os = System.getProperty("os.name", "");
        if (!os.toLowerCase().contains("win")) {
            return;
        }

        try (Arena arena = Arena.ofConfined()) {
            linker = Linker.nativeLinker();
            user32 = SymbolLookup.libraryLookup("user32", Arena.global());
            gdi32 = SymbolLookup.libraryLookup("gdi32", Arena.global());

            createWindowExW = linker.downcallHandle(
                    user32.find("CreateWindowExW").orElseThrow(),
                    FunctionDescriptor.of(
                            ValueLayout.ADDRESS,
                            ValueLayout.JAVA_INT,
                            ValueLayout.ADDRESS,
                            ValueLayout.ADDRESS,
                            ValueLayout.JAVA_INT,
                            ValueLayout.JAVA_INT,
                            ValueLayout.JAVA_INT,
                            ValueLayout.JAVA_INT,
                            ValueLayout.JAVA_INT,
                            ValueLayout.ADDRESS,
                            ValueLayout.ADDRESS,
                            ValueLayout.ADDRESS,
                            ValueLayout.ADDRESS
                    )
            );
            showWindow = linker.downcallHandle(
                    user32.find("ShowWindow").orElseThrow(),
                    FunctionDescriptor.of(
                            ValueLayout.JAVA_INT,
                            ValueLayout.ADDRESS,
                            ValueLayout.JAVA_INT
                    )
            );
            updateWindow = linker.downcallHandle(
                    user32.find("UpdateWindow").orElseThrow(),
                    FunctionDescriptor.of(
                            ValueLayout.JAVA_INT,
                            ValueLayout.ADDRESS
                    )
            );
            getDCHandle = linker.downcallHandle(
                    user32.find("GetDC").orElseThrow(),
                    FunctionDescriptor.of(
                            ValueLayout.ADDRESS,
                            ValueLayout.ADDRESS
                    )
            );
            releaseDCHandle = linker.downcallHandle(
                    user32.find("ReleaseDC").orElseThrow(),
                    FunctionDescriptor.of(
                            ValueLayout.JAVA_INT,
                            ValueLayout.ADDRESS,
                            ValueLayout.ADDRESS
                    )
            );
            peekMessageWHandle = linker.downcallHandle(
                    user32.find("PeekMessageW").orElseThrow(),
                    FunctionDescriptor.of(
                            ValueLayout.JAVA_INT,
                            ValueLayout.ADDRESS,
                            ValueLayout.ADDRESS,
                            ValueLayout.JAVA_INT,
                            ValueLayout.JAVA_INT,
                            ValueLayout.JAVA_INT
                    )
            );
            translateMessageHandle = linker.downcallHandle(
                    user32.find("TranslateMessage").orElseThrow(),
                    FunctionDescriptor.of(
                            ValueLayout.JAVA_INT,
                            ValueLayout.ADDRESS
                    )
            );
            dispatchMessageWHandle = linker.downcallHandle(
                    user32.find("DispatchMessageW").orElseThrow(),
                    FunctionDescriptor.of(
                            ValueLayout.JAVA_LONG,
                            ValueLayout.ADDRESS
                    )
            );
            stretchDIBitsHandle = linker.downcallHandle(
                    gdi32.find("StretchDIBits").orElseThrow(),
                    FunctionDescriptor.of(
                            ValueLayout.JAVA_INT,
                            ValueLayout.ADDRESS,
                            ValueLayout.JAVA_INT,
                            ValueLayout.JAVA_INT,
                            ValueLayout.JAVA_INT,
                            ValueLayout.JAVA_INT,
                            ValueLayout.JAVA_INT,
                            ValueLayout.JAVA_INT,
                            ValueLayout.JAVA_INT,
                            ValueLayout.JAVA_INT,
                            ValueLayout.ADDRESS,
                            ValueLayout.ADDRESS,
                            ValueLayout.JAVA_INT,
                            ValueLayout.JAVA_INT
                    )
            );

            hwnd = (MemorySegment) createWindowExW.invoke(
                    0,
                    AllocateWideString(arena, "STATIC"),
                    AllocateWideString(arena, "Wolf3D"),
                    WS_OVERLAPPEDWINDOW | WS_VISIBLE,
                    CW_USEDEFAULT,
                    CW_USEDEFAULT,
                    WINDOW_WIDTH,
                    WINDOW_HEIGHT,
                    MemorySegment.NULL,
                    MemorySegment.NULL,
                    MemorySegment.NULL,
                    MemorySegment.NULL
            );
            if (hwnd == MemorySegment.NULL) {
                throw new IllegalStateException("CreateWindowExW failed");
            }

            showWindow.invoke(hwnd, SW_SHOW);
            updateWindow.invoke(hwnd);
            windowHandle = hwnd.address();
            windowCreated = true;
        } catch (Throwable t) {
            throw new IllegalStateException("Win32 window bootstrap failed", t);
        }

        if (!windowCreated) {
            throw new IllegalStateException("Win32 window bootstrap did not create a window");
        }
    }

    private static MemorySegment AllocateWideString(Arena arena, String value) {
        int i;
        MemorySegment segment;

        segment = arena.allocate(
                (value.length() + 1L) * ValueLayout.JAVA_CHAR.byteSize(),
                ValueLayout.JAVA_CHAR.byteAlignment()
        );
        for (i = 0; i < value.length(); i++) {
            segment.set(ValueLayout.JAVA_CHAR, i * ValueLayout.JAVA_CHAR.byteSize(), value.charAt(i));
        }
        segment.set(ValueLayout.JAVA_CHAR, value.length() * ValueLayout.JAVA_CHAR.byteSize(), '\0');
        return segment;
    }
}
