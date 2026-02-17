package be.envano.games.wolf3d;

/**
 * Port scaffold for original/WOLFSRC/ID_CA.C.
 */
public final class ID_CA {

    // C source: original/WOLFSRC/ID_CA.C:56
    static int mapon;
    // C source: original/WOLFSRC/ID_CA.C:64
    static byte ca_levelbit;
    // C source: original/WOLFSRC/ID_CA.C:64
    static byte ca_levelnum;

    private ID_CA() {
    }

    // C source: original/WOLFSRC/ID_CA.C:1083
    static void CA_Startup() {
        // Deferred compile-time block: original/WOLFSRC/ID_CA.C:1085-1088 (#ifdef PROFILE).

        // C call site: original/WOLFSRC/ID_CA.C:1090
        CAL_SetupMapFile();
        // C call site: original/WOLFSRC/ID_CA.C:1091
        CAL_SetupGrFile();
        // C call site: original/WOLFSRC/ID_CA.C:1092
        CAL_SetupAudioFile();

        // C line: original/WOLFSRC/ID_CA.C:1094
        mapon = -1;
        // C line: original/WOLFSRC/ID_CA.C:1095
        ca_levelbit = 1;
        // C line: original/WOLFSRC/ID_CA.C:1096
        ca_levelnum = 0;
    }

    // C source: original/WOLFSRC/ID_CA.C:942
    static void CAL_SetupMapFile() {
        // Deferred until this method is reached in strict traversal.
    }

    // C source: original/WOLFSRC/ID_CA.C:861
    static void CAL_SetupGrFile() {
        // Deferred until this method is reached in strict traversal.
    }

    // C source: original/WOLFSRC/ID_CA.C:1026
    static void CAL_SetupAudioFile() {
        // Deferred until this method is reached in strict traversal.
    }
}
