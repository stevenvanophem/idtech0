package be.envano.games.wolf3d;

public final class ID_SD {

    /**
     * Correlates to {@code original/WOLFSRC/ID_SD.C:77} ({@code longword TimeCount;}).
     */
    private static long TimeCount;

    private ID_SD() {
    }

    public static long GetTimeCount() {
        return TimeCount;
    }

    public static void SetTimeCount(long value) {
        TimeCount = value;
    }

    /**
     * Correlates to callsites using {@code EXTRN TimeCount:WORD} in asm modules.
     */
    public static int GetTimeCountWord() {
        return (int) (TimeCount & 0xffffL);
    }

    public static void AdvanceTimeCount(long delta) {
        TimeCount += delta;
    }

    // Correlates to: original/WOLFSRC/ID_SD.C (SD_Startup)
    public static void SD_Startup() {
        // Correlates to original/WOLFSRC/ID_SD.C:1922 (LocalTime = TimeCount = alTimeCount = 0;).
        TimeCount = 0;
    }

    // Correlates to: original/WOLFSRC/ID_SD.C (SD_Shutdown)
    public static void SD_Shutdown() {
    }
}
