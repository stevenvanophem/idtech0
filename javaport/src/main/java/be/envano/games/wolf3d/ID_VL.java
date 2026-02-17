package be.envano.games.wolf3d;

/**
 * Port scaffold for original/WOLFSRC/ID_VL.C.
 */
public final class ID_VL {

    private ID_VL() {
    }

    // C source: original/WOLFSRC/ID_VL.C:115
    static void VL_SetVGAPlaneMode() {
        ID_SDL.SDL_SetVideoMode_Windowed();

        // Deferred for later fidelity passes:
        // C call site: original/WOLFSRC/ID_VL.C:120 -> VL_DePlaneVGA()
        // C call site: original/WOLFSRC/ID_VL.C:121 -> VGAMAPMASK(15)
        // C call site: original/WOLFSRC/ID_VL.C:122 -> VL_SetLineWidth(40)
    }

    static boolean SDL_RunEventLoopTick() {
        return ID_SDL.SDL_RunEventLoopTick();
    }
}
