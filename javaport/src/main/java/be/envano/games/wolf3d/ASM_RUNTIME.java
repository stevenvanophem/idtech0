package be.envano.games.wolf3d;

public final class ASM_RUNTIME {

    private static int AX;
    private static int DX;
    private static int AL;
    private static int AH;
    private static int ES;
    private static int CX;
    private static int DI;

    private ASM_RUNTIME() {
    }

    /**
     * Assembly intent bridge for {@code mov ax,<value>}.
     */
    public static void MOV_AX(int value) {
        AX = value & 0xffff;
        AL = AX & 0xff;
        AH = (AX >> 8) & 0xff;
    }

    /**
     * Assembly intent bridge for {@code mov dx,<value>}.
     */
    public static void MOV_DX(int value) {
        DX = value & 0xffff;
    }

    /**
     * Assembly intent bridge for {@code mov al,<value>}.
     */
    public static void MOV_AL(int value) {
        AL = value & 0xff;
        AX = (AH << 8) | AL;
    }

    /**
     * Assembly intent bridge for {@code mov ah,<value>}.
     */
    public static void MOV_AH(int value) {
        AH = value & 0xff;
        AX = (AH << 8) | AL;
    }

    /**
     * Assembly intent bridge for {@code mov ah,al}.
     */
    public static void MOV_AH_AL() {
        AH = AL;
        AX = (AH << 8) | AL;
    }

    /**
     * Assembly intent bridge for {@code mov es,ax}.
     */
    public static void MOV_ES_AX() {
        ES = AX & 0xffff;
    }

    /**
     * Assembly intent bridge for {@code mov cx,<value>}.
     */
    public static void MOV_CX(int value) {
        CX = value & 0xffff;
    }

    /**
     * Assembly intent bridge for {@code xor di,di}.
     */
    public static void XOR_DI_DI() {
        DI = 0;
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

    /**
     * Assembly intent bridge for {@code out dx,al}.
     */
    public static void OUT_DX_AL() {
        OUTPORTB(DX, AL);
    }

    /**
     * Assembly intent bridge for {@code out dx,ax}.
     */
    public static void OUT_DX_AX() {
        OUTPORT(DX, AX);
    }

    /**
     * Assembly intent bridge for {@code inc dx}.
     */
    public static void INC_DX() {
        DX = (DX + 1) & 0xffff;
    }

    /**
     * Assembly intent bridge for {@code in al,dx}.
     */
    public static void IN_AL_DX() {
        AL = INPORTB(DX) & 0xff;
        AX = (AH << 8) | AL;
    }

    /**
     * Assembly intent bridge for {@code and al,<mask>}.
     */
    public static void AND_AL(int mask) {
        AL = AL & (mask & 0xff);
        AX = (AH << 8) | AL;
    }

    /**
     * Assembly intent bridge for {@code rep stosw} using current register values.
     */
    public static void REP_STOSW() {
        // TODO: Replace with platform backend behavior where applicable.
        // Uses ES:DI destination, AX word value, CX word count.
    }
}
