package be.envano.games.wolf3d;

/**
 * C runtime compatibility shim.
 * Holds process-level runtime globals used by the original codebase.
 */
public final class C_RUNTIME {

    // C runtime globals used throughout the original code.
    public static int _argc;
    public static String[] _argv = new String[0];

    private C_RUNTIME() {
    }

    public static void initArgv(String[] args) {
        _argv = args == null ? new String[0] : args;
        _argc = _argv.length;
    }
}
