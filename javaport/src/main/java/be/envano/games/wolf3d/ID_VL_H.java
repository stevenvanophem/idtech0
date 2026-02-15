package be.envano.games.wolf3d;

public final class ID_VL_H {

    private static final int SC_INDEX = 0x3C4;
    private static final int SC_MAPMASK = 2;

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
