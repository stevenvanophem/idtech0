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
}
