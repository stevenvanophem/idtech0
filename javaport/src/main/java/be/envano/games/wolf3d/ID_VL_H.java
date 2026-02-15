package be.envano.games.wolf3d;

public final class ID_VL_H {

    public static final int SC_INDEX = 0x3C4;
    public static final int SC_MAPMASK = 2;
    public static final int SC_MEMMODE = 4;
    public static final int CRTC_INDEX = 0x3D4;
    public static final int CRTC_OVERFLOW = 7;
    public static final int CRTC_MAXSCANLINE = 9;
    public static final int CRTC_OFFSET = 19;
    public static final int CRTC_UNDERLINE = 20;
    public static final int CRTC_MODE = 23;
    public static final int CRTC_LINECOMPARE = 24;
    public static final int GC_INDEX = 0x3CE;
    public static final int GC_MODE = 5;
    public static final int GC_MISCELLANEOUS = 6;
    public static final int PEL_WRITE_ADR = 0x3C8;
    public static final int PEL_READ_ADR = 0x3C7;
    public static final int PEL_DATA = 0x3C9;
    public static final int SCREENSEG = 0xA000;
    public static final int STATUS_REGISTER_1 = 0x3DA;
    public static final int MAXSCANLINES = 200;

    private ID_VL_H() {
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.H:113}
     * ({@code #define VGAMAPMASK(x) ...}).
     */
    public static void VGAMAPMASK(int x) {
        ASM_RUNTIME.CLI();
        ASM_RUNTIME.MOV_DX(SC_INDEX);
        ASM_RUNTIME.MOV_AL(SC_MAPMASK);
        ASM_RUNTIME.MOV_AH(x);
        ASM_RUNTIME.OUT_DX_AX();
        ASM_RUNTIME.STI();
    }
}
