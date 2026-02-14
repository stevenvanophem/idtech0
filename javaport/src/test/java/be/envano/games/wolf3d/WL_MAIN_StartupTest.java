package be.envano.games.wolf3d;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WL_MAIN_StartupTest {

    @Test
    void startupOrderMatchesWlMainCUntilDemoLoopStub() {
        WL_MAIN.resetTrace();

        assertThrows(UnsupportedOperationException.class, () -> WL_MAIN.main(new String[0]));

        assertEquals(
                List.of("CheckForEpisodes", "Patch386", "InitGame", "DemoLoop"),
                WL_MAIN.getStartupTrace()
        );
    }
}
