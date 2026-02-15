package be.envano.games.wolf3d;

public final class ASM_RUNTIME {

    private static int AX;

    private ASM_RUNTIME() {
    }

    /**
     * Assembly intent bridge for {@code mov ax,<value>}.
     */
    public static void MOV_AX(int value) {
        AX = value & 0xffff;
    }

    /**
     * Assembly/BIOS intent bridge for {@code int 0x10}.
     * <p>
     * Correlates to inline asm usage in {@code original/WOLFSRC/ID_VL.C}.
     */
    public static void INT_10h() {
        // TODO: Replace with platform backend behavior (FFM/native API mapping).
        // Current intent: BIOS video service with AX function/mode selected by MOV_AX.
    }

    /**
     * Convenience wrapper for mode switches; not used where strict instruction-shape is required.
     */
    public static void BIOS_SetVideoMode(int mode) {
        MOV_AX(mode);
        INT_10h();
    }

    /**
     * Assembly intent bridge for x86 direction flag clear ({@code cld}).
     */
    public static void CLD() {
        // TODO: Keep as no-op unless a backend needs explicit state tracking.
    }

    /**
     * Assembly intent bridge for byte output to an I/O port ({@code outportb}).
     */
    public static void OUTPORTB(int port, int value) {
        // TODO: Replace with platform backend behavior where applicable.
    }

    /**
     * Assembly intent bridge for word output to an I/O port ({@code outport}).
     */
    public static void OUTPORT(int port, int value) {
        // TODO: Replace with platform backend behavior where applicable.
    }

    /**
     * Assembly intent bridge for byte input from an I/O port ({@code inportb}).
     */
    public static int INPORTB(int port) {
        // TODO: Replace with platform backend behavior where applicable.
        return 0;
    }
}
