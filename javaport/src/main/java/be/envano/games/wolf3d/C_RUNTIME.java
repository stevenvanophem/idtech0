package be.envano.games.wolf3d;

public final class C_RUNTIME {

    private static int _argc;
    private static String[] _argv = new String[0];

    private C_RUNTIME() {
    }

    /**
     * Port bridge: emulates C runtime globals {@code _argc} and {@code _argv}.
     * <p>
     * Not a direct function from original source; this centralizes runtime argument state.
     */
    public static void setMainArgs(String[] javaArgs) {
        String[] args = javaArgs == null ? new String[0] : javaArgs;
        _argv = new String[args.length + 1];
        _argv[0] = "java";
        System.arraycopy(args, 0, _argv, 1, args.length);
        _argc = _argv.length;
    }

    public static int argc() {
        return _argc;
    }

    public static String argv(int i) {
        return _argv[i];
    }

    /**
     * C-style alpha classification helper ({@code isalpha} equivalent for ASCII letters).
     */
    public static boolean isalpha(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    /**
     * C-style case-insensitive string compare (ASCII-focused), similar to {@code stricmp}.
     * Returns 0 when equal, negative/positive on lexical difference.
     */
    public static int stricmp(String a, String b) {
        String left = a == null ? "" : a;
        String right = b == null ? "" : b;
        int i = 0;
        int min = Math.min(left.length(), right.length());

        while (i < min) {
            char ca = toLowerAscii(left.charAt(i));
            char cb = toLowerAscii(right.charAt(i));
            if (ca != cb) {
                return ca - cb;
            }
            i++;
        }
        return left.length() - right.length();
    }

    private static char toLowerAscii(char c) {
        if (c >= 'A' && c <= 'Z') {
            return (char) (c + ('a' - 'A'));
        }
        return c;
    }
}
