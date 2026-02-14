package be.envano.games.wolf3d;

public final class WL_MAIN {

    private static final String[] JHParmStrings = {"no386", ""};

    private static int _argc;
    private static String[] _argv = new String[0];
    private static boolean IsA386;

    private WL_MAIN() {
    }

    // Correlates to: original/WOLFSRC/WL_MAIN.C:1586 (void main(void))
    public static void main(String[] args) {
        setArgv(args == null ? new String[0] : args);

        WL_MENU.CheckForEpisodes();
        Patch386();
        InitGame();
        DemoLoop();
        Quit("Demo loop exited???");
    }

    // Correlates to: original/WOLFSRC/WL_MAIN.C:241 (void Patch386(void))
    public static void Patch386() {
        int i;

        for (i = 1; i < _argc; i++) {
            if (US_CheckParm(_argv[i], JHParmStrings) == 0) {
                IsA386 = false;
                return;
            }
        }

        if (CheckIs386()) {
            IsA386 = true;
            jabhack2();
        } else {
            IsA386 = false;
        }
    }

    public static void InitGame() {
        // TODO: Port from WL_MAIN.C and linked init modules.
    }

    public static void DemoLoop() {
        throw new UnsupportedOperationException("TODO: Port DemoLoop from WL_MAIN.C");
    }

    public static void Quit(String error) {
        throw new IllegalStateException(error == null ? "Quit called" : error);
    }

    private static void setArgv(String[] javaArgs) {
        String[] args = javaArgs == null ? new String[0] : javaArgs;
        _argv = new String[args.length + 1];
        _argv[0] = "java";
        System.arraycopy(args, 0, _argv, 1, args.length);
        _argc = _argv.length;
    }

    private static int US_CheckParm(String parm, String[] strings) {
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

    private static boolean CheckIs386() {
        // TODO: Port hardware detection behavior for modern platform layer.
        return false;
    }

    private static void jabhack2() {
        // TODO: Port/replace assembly optimization hook.
    }
}
