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
    private static final int MAINCOLOR = 0x6c;
    private static final int EMSCOLOR = 0x6c;
    private static final int XMSCOLOR = 0x6c;
    private static final int FILLCOLOR = 14;

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

    /**
     * Correlates to {@code original/WOLFSRC/WL_MENU.C:2882} ({@code void IntroScreen(void)}).
     */
    public static void IntroScreen(long mainMemBytes,
                                   boolean emsPresent, int emsPagesAvail,
                                   boolean xmsPresent, int xmsPagesAvail,
                                   boolean mousePresent,
                                   boolean joy0Present, boolean joy1Present,
                                   boolean adLibPresent,
                                   boolean soundBlasterPresent,
                                   boolean soundSourcePresent) {
        long memory;
        long emshere;
        long xmshere;
        int i;
        int[] ems = {100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};
        int[] xms = {100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};
        int[] main = {32, 64, 96, 128, 160, 192, 224, 256, 288, 320};

        memory = (1023L + mainMemBytes) / 1024L;
        for (i = 0; i < 10; i++) {
            if (memory >= main[i]) {
                ID_VH_H.VW_Bar(49, 163 - 8 * i, 6, 5, MAINCOLOR - i);
            }
        }

        if (emsPresent) {
            emshere = 4L * emsPagesAvail;
            for (i = 0; i < 10; i++) {
                if (emshere >= ems[i]) {
                    ID_VH_H.VW_Bar(89, 163 - 8 * i, 6, 5, EMSCOLOR - i);
                }
            }
        }

        if (xmsPresent) {
            xmshere = 4L * xmsPagesAvail;
            for (i = 0; i < 10; i++) {
                if (xmshere >= xms[i]) {
                    ID_VH_H.VW_Bar(129, 163 - 8 * i, 6, 5, XMSCOLOR - i);
                }
            }
        }

        if (mousePresent) {
            ID_VH_H.VW_Bar(164, 82, 12, 2, FILLCOLOR);
        }

        if (joy0Present || joy1Present) {
            ID_VH_H.VW_Bar(164, 105, 12, 2, FILLCOLOR);
        }

        if (adLibPresent && !soundBlasterPresent) {
            ID_VH_H.VW_Bar(164, 128, 12, 2, FILLCOLOR);
        }

        if (soundBlasterPresent) {
            ID_VH_H.VW_Bar(164, 151, 12, 2, FILLCOLOR);
        }

        if (soundSourcePresent) {
            ID_VH_H.VW_Bar(164, 174, 12, 2, FILLCOLOR);
        }
    }
}
