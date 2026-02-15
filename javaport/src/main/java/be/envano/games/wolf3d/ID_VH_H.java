package be.envano.games.wolf3d;

public final class ID_VH_H {

    private ID_VH_H() {
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VH.H:96}
     * ({@code #define VW_Startup VL_Startup}).
     */
    public static void VW_Startup() {
        ID_VL.VL_Startup();
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VH.H:97}
     * ({@code #define VW_Shutdown VL_Shutdown}).
     */
    public static void VW_Shutdown() {
        ID_VL.VL_Shutdown();
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VH.H:99}
     * ({@code #define VW_SetScreen VL_SetScreen}).
     */
    public static void VW_SetScreen(int crtc, int pelpan) {
        ID_VL.VL_SetScreen(crtc, pelpan);
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VH.H:100}
     * ({@code #define VW_Bar VL_Bar}).
     */
    public static void VW_Bar(int x, int y, int width, int height, int color) {
        ID_VL.VL_Bar(x, y, width, height, color);
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VH.H:108}
     * ({@code #define VW_WaitVBL VL_WaitVBL}).
     */
    public static void VW_WaitVBL(int vbls) {
        ID_VL.VL_WaitVBL(vbls);
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VH.H:102}
     * ({@code #define VW_Hlin(x,z,y,c) VL_Hlin(x,y,(z)-(x)+1,c)}).
     */
    public static void VW_Hlin(int x1, int x2, int y, int color) {
        ID_VL.VL_Hlin(x1, y, (x2) - (x1) + 1, color);
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VH.H:103}
     * ({@code #define VW_Vlin(y,z,x,c) VL_Vlin(x,y,(z)-(y)+1,c)}).
     */
    public static void VW_Vlin(int y1, int y2, int x, int color) {
        ID_VL.VL_Vlin(x, y1, (y2) - (y1) + 1, color);
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VH.H:106}
     * ({@code #define VW_SetLineWidth VL_SetLineWidth}).
     */
    public static void VW_SetLineWidth(int width) {
        ID_VL.VL_SetLineWidth_PUBLIC(width);
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VH.H:107}
     * ({@code #define VW_ColorBorder VL_ColorBorder}).
     */
    public static void VW_ColorBorder(int color) {
        ID_VL.VL_ColorBorder(color);
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VH.H:109}
     * ({@code #define VW_FadeIn() VL_FadeIn(0,255,&gamepal,30);}).
     */
    public static void VW_FadeIn(byte[] gamepal) {
        ID_VL.VL_FadeIn(0, 255, gamepal, 30);
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VH.H:110}
     * ({@code #define VW_FadeOut() VL_FadeOut(0,255,0,0,0,30);}).
     */
    public static void VW_FadeOut() {
        ID_VL.VL_FadeOut(0, 255, 0, 0, 0, 30);
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VH.H:111}
     * ({@code #define VW_ScreenToScreen VL_ScreenToScreen}).
     */
    public static void VW_ScreenToScreen(int source, int dest, int wide, int height) {
        ID_VL.VL_ScreenToScreen(source, dest, wide, height);
    }
}
