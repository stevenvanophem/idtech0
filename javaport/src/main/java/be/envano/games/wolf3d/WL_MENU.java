package be.envano.games.wolf3d;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class WL_MENU {

    private static String extension = "";
    private static String configname = "CONFIG.";
    private static String SaveName = "SAVEGAM?.";
    private static String PageFileName = "VSWAP.";
    private static String audioname = "AUDIO.";
    private static String demoname = "DEMO.";
    private static String helpfilename = "HELPART.";
    private static String endfilename = "ENDART.";
    private static final int[] EpisodeSelect = {1, 0, 0, 0, 0, 0};

    private WL_MENU() {
    }

    // Correlates to: original/WOLFSRC/WL_MENU.C:3882 (void CheckForEpisodes(void))
    public static void CheckForEpisodes() {
        // WL1 baseline: match the non-SPEAR fallback branch in WL_MENU.C.
        if (findFirst("*.WL1")) {
            extension = "WL1";
        } else {
            WL_MAIN.Quit("NO WOLFENSTEIN 3-D DATA FILES to be found!");
        }

        configname += extension;
        SaveName += extension;
        PageFileName += extension;
        audioname += extension;
        demoname += extension;
        helpfilename += extension;
        endfilename += extension;
    }

    private static boolean findFirst(String glob) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Path.of("."), glob)) {
            for (Path ignored : stream) {
                return true;
            }
            return false;
        } catch (IOException ignored) {
            return false;
        }
    }
}
