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
        String normalizedParm = skipNonAlpha(parm);
        int i;
        for (i = 0; i < strings.length; i++) {
            String s = strings[i];
            if (s == null || s.isEmpty()) {
                break;
            }
            if (startsWithIgnoreCase(normalizedParm, s)) {
                return i;
            }
        }
        return -1;
    }

    private static String skipNonAlpha(String value) {
        if (value == null) {
            return "";
        }
        int idx = 0;
        while (idx < value.length() && !Character.isAlphabetic(value.charAt(idx))) {
            idx++;
        }
        return value.substring(idx);
    }

    private static boolean startsWithIgnoreCase(String source, String prefix) {
        if (source.length() < prefix.length()) {
            return false;
        }
        return source.regionMatches(true, 0, prefix, 0, prefix.length());
    }
}
