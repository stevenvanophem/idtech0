package be.envano.games.wolf3d;

public final class ID_US {

    private ID_US() {
    }

    // Correlates to: original/WOLFSRC/ID_US_1.C (US_Startup)
    public static void US_Startup() {
    }

    // Correlates to: original/WOLFSRC/ID_US_1.C (US_Shutdown)
    public static void US_Shutdown() {
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_US_1.C:237} ({@code int US_CheckParm(char *parm,char **strings)}).
     * <p>
     * Checks whether a command-line argument matches one of the provided option names and returns
     * the index of the first match, or {@code -1} if none match.
     * <p>
     * Matching behavior follows the original C logic:
     * - skips leading non-alphabetic characters in the argument (for example '-', '/', '\')
     * - compares case-insensitively
     * - matches by prefix against each option string
     */
    public static int US_CheckParm(String parm, String[] strings) {
        char cp;
        char cs;
        int i;
        int pIndex;
        int sIndex;
        int parmIndex;

        parmIndex = 0;
        while (parmIndex < parm.length() && !C_RUNTIME.isalpha(parm.charAt(parmIndex))) {
            if (parmIndex >= parm.length()) {
                break;
            }
            parmIndex++;
        }

        for (i = 0; i < strings.length && strings[i] != null && !strings[i].isEmpty(); i++) {
            sIndex = 0;
            pIndex = 0;
            cs = 0;
            cp = 0;

            for (; cs == cp; ) {
                cs = sIndex < strings[i].length() ? strings[i].charAt(sIndex++) : 0;
                if (cs == 0) {
                    return i;
                }
                cp = (parmIndex + pIndex) < parm.length() ? parm.charAt(parmIndex + pIndex) : 0;
                pIndex++;

                if (C_RUNTIME.isupper(cs)) {
                    cs = C_RUNTIME.tolower(cs);
                }
                if (C_RUNTIME.isupper(cp)) {
                    cp = C_RUNTIME.tolower(cp);
                }
            }
        }
        return -1;
    }

}
