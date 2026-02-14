package be.envano.games.wolf3d;

import java.util.ArrayList;
import java.util.List;

public final class WL_MAIN {

    private static int _argc;
    private static String[] _argv = new String[0];
    private static final List<String> STARTUP_TRACE = new ArrayList<>();

    private WL_MAIN() {
    }

    public static void main(String[] args) {
        _argv = args == null ? new String[0] : args;
        _argc = _argv.length;

        CheckForEpisodes();
        Patch386();
        InitGame();
        DemoLoop();
        Quit("Demo loop exited???");
    }

    public static void CheckForEpisodes() {
        STARTUP_TRACE.add("CheckForEpisodes");
        // TODO: Port from WL_MAIN.C for selected WL1 variant.
    }

    public static void Patch386() {
        STARTUP_TRACE.add("Patch386");
        // TODO: Port from WL_MAIN.C; likely no-op in modern Java.
    }

    public static void InitGame() {
        STARTUP_TRACE.add("InitGame");
        // TODO: Port from WL_MAIN.C and linked init modules.
    }

    public static void DemoLoop() {
        STARTUP_TRACE.add("DemoLoop");
        throw new UnsupportedOperationException("TODO: Port DemoLoop from WL_MAIN.C");
    }

    public static void Quit(String error) {
        STARTUP_TRACE.add("Quit");
        throw new IllegalStateException(error == null ? "Quit called" : error);
    }

    public static void resetTrace() {
        STARTUP_TRACE.clear();
    }

    public static List<String> getStartupTrace() {
        return List.copyOf(STARTUP_TRACE);
    }

    public static int get_argc() {
        return _argc;
    }

    public static String[] get_argv() {
        return _argv.clone();
    }
}
