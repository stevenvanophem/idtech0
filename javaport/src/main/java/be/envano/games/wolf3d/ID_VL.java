package be.envano.games.wolf3d;

public final class ID_VL {

    private static final String[] ParmStrings = {"HIDDENCARD", ""};

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
        ASM_RUNTIME.INT_10h();
        VL_DePlaneVGA();
        VGAMAPMASK(15);
        VL_SetLineWidth(40);
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL.C:128} ({@code void VL_SetTextMode(void)}).
     */
    public static void VL_SetTextMode() {
        ASM_RUNTIME.MOV_AX(0x03);
        ASM_RUNTIME.INT_10h();
    }

    private static int VL_VideoID() {
        // TODO: Port real video-card detection behavior.
        return 5;
    }

    private static void VL_DePlaneVGA() {
        // TODO: Port non-linear VGA plane setup from ID_VL.C.
    }

    private static void VGAMAPMASK(int mask) {
        // TODO: Port VGA map-mask register write.
    }

    private static void VL_SetLineWidth(int width) {
        // TODO: Port line-width setup and lookup update.
    }
}
