package be.envano.games.wolf3d;

public final class ASM_RUNTIME {

    private static final byte[] VRAM = new byte[0x10000];
    private static final int[] PORT8 = new int[0x10000];
    private static int AX;
    private static int BX;
    private static int DX;
    private static int AL;
    private static int AH;
    private static int BL;
    private static int BH;
    private static int ES;
    private static int DS;
    private static int SS;
    private static int CX;
    private static int DI;
    private static int SI;
    private static boolean ZF;
    private static byte[] DS_BYTES;

    private ASM_RUNTIME() {
    }

    /**
     * Assembly intent bridge for {@code cli}.
     */
    public static void CLI() {
        // TODO: Platform/backend interrupt-disable intent if needed.
    }

    /**
     * Assembly intent bridge for {@code sti}.
     */
    public static void STI() {
        // TODO: Platform/backend interrupt-enable intent if needed.
    }

    /**
     * Assembly intent bridge for {@code pushf}.
     */
    public static void PUSHF() {
        // TODO: Track flags stack when backend requires it.
    }

    /**
     * Assembly intent bridge for {@code popf}.
     */
    public static void POPF() {
        // TODO: Track flags stack when backend requires it.
    }

    /**
     * Assembly intent bridge for short timing no-op used as {@code jmp $+2}.
     */
    public static void JMP_SHORT_2() {
        // TODO: Keep as no-op unless backend timing simulation is introduced.
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
     * Assembly intent bridge for {@code mov bh,<value>}.
     */
    public static void MOV_BH(int value) {
        BH = value & 0xff;
        BX = (BH << 8) | BL;
    }

    /**
     * Assembly intent bridge for {@code mov bl,<value>}.
     */
    public static void MOV_BL(int value) {
        BL = value & 0xff;
        BX = (BH << 8) | BL;
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

    public static int AX() {
        return AX & 0xffff;
    }

    public static int BX() {
        return BX & 0xffff;
    }

    public static int CX() {
        return CX & 0xffff;
    }

    public static int DX() {
        return DX & 0xffff;
    }

    public static int AL() {
        return AL & 0xff;
    }

    public static int AH() {
        return AH & 0xff;
    }

    public static int BL() {
        return BL & 0xff;
    }

    public static int BH() {
        return BH & 0xff;
    }

    public static int CL() {
        return CX & 0xff;
    }

    public static int CH() {
        return (CX >> 8) & 0xff;
    }

    /**
     * Assembly intent bridge for {@code lds si,[source]} using a Java byte array as backing memory.
     */
    public static void LDS_SI(byte[] source) {
        DS_BYTES = source;
        SI = 0;
    }

    /**
     * Assembly intent bridge for {@code xor di,di}.
     */
    public static void XOR_DI_DI() {
        DI = 0;
    }

    /**
     * Assembly/BIOS intent bridge for {@code int <number>}.
     * <p>
     * Correlates to inline asm usage in {@code original/WOLFSRC/ID_VL.C}.
     */
    public static void INT(int number) {
        // TODO: Replace with platform backend behavior (FFM/native API mapping).
        // Current intent: BIOS/interrupt service selected by interrupt number and registers.
    }

    /**
     * Convenience wrapper for {@code int 0x10}.
     */
    public static void INT_10h() {
        INT(0x10);
    }

    /**
     * Convenience wrapper for mode switches; not used where strict instruction-shape is required.
     */
    public static void BIOS_SetVideoMode(int mode) {
        MOV_AX(mode);
        INT(0x10);
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
        PORT8[port & 0xffff] = value & 0xff;
    }

    /**
     * Assembly intent bridge for word output to an I/O port ({@code outport}).
     */
    public static void OUTPORT(int port, int value) {
        OUTPORTB(port, value & 0xff);
        OUTPORTB((port + 1) & 0xffff, (value >> 8) & 0xff);
    }

    /**
     * Assembly intent bridge for byte input from an I/O port ({@code inportb}).
     */
    public static int INPORTB(int port) {
        return PORT8[port & 0xffff] & 0xff;
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
     * Assembly intent bridge for {@code or al,<mask>}.
     */
    public static void OR_AL(int mask) {
        AL = AL | (mask & 0xff);
        AX = (AH << 8) | AL;
    }

    /**
     * Assembly intent bridge for {@code rep stosw} using current register values.
     */
    public static void REP_STOSW() {
        int count;
        int base;

        count = CX & 0xffff;
        base = DI & 0xffff;

        while (count != 0) {
            WRITE_VIDEO_BYTE(ES, base, AX & 0xff);
            WRITE_VIDEO_BYTE(ES, (base + 1) & 0xffff, (AX >> 8) & 0xff);
            base = (base + 2) & 0xffff;
            count--;
        }

        DI = base & 0xffff;
        CX = 0;
    }

    /**
     * Assembly intent bridge for {@code lodsb} from DS:SI.
     */
    public static void LODSB() {
        if (DS_BYTES == null || SI < 0 || SI >= DS_BYTES.length) {
            AL = 0;
        } else {
            AL = DS_BYTES[SI] & 0xff;
        }
        SI++;
        AX = (AH << 8) | AL;
    }

    /**
     * Assembly intent bridge for {@code rep outsb} from DS:SI to port DX.
     */
    public static void REP_OUTSB() {
        while (CX != 0) {
            LODSB();
            OUT_DX_AL();
            CX = (CX - 1) & 0xffff;
        }
    }

    /**
     * Assembly intent bridge for {@code test value,mask}.
     */
    public static void TEST_IMM8(int value, int mask) {
        ZF = ((value & mask) & 0xff) == 0;
    }

    /**
     * Assembly intent bridge for {@code jz}.
     */
    public static boolean JZ() {
        return ZF;
    }

    /**
     * Assembly intent bridge for {@code loop label} condition.
     */
    public static boolean LOOP() {
        CX = (CX - 1) & 0xffff;
        return CX != 0;
    }

    /**
     * Assembly intent bridge for {@code mov ax,ss}.
     */
    public static void MOV_AX_SS() {
        AX = SS & 0xffff;
        AL = AX & 0xff;
        AH = (AX >> 8) & 0xff;
    }

    /**
     * Assembly intent bridge for {@code mov ds,ax}.
     */
    public static void MOV_DS_AX() {
        DS = AX & 0xffff;
    }

    /**
     * Assembly intent bridge for writing a byte to planar video memory.
     */
    public static void WRITE_VIDEO_BYTE(int segment, int offset, int value) {
        int index;

        if (segment != 0xA000) {
            return;
        }

        index = offset & 0xffff;
        VRAM[index] = (byte) (value & 0xff);
    }

    /**
     * Assembly intent bridge for filling contiguous bytes in planar video memory.
     */
    public static void FILL_VIDEO_BYTES(int segment, int offset, int value, int count) {
        int i;
        int start;

        if (segment != 0xA000 || count <= 0) {
            return;
        }

        start = offset & 0xffff;
        for (i = 0; i < count; i++) {
            VRAM[(start + i) & 0xffff] = (byte) (value & 0xff);
        }
    }

    /**
     * Assembly intent bridge for copying bytes from Java memory to video memory.
     */
    public static void COPY_BYTES_TO_VIDEO(int segment, int destOffset, byte[] source, int sourceOffset, int count) {
        int i;
        int dst;
        int src;

        if (segment != 0xA000 || source == null || count <= 0) {
            return;
        }

        dst = destOffset & 0xffff;
        src = sourceOffset;
        for (i = 0; i < count; i++) {
            if (src < 0 || src >= source.length) {
                break;
            }
            VRAM[(dst + i) & 0xffff] = source[src++];
        }
    }

    /**
     * Assembly intent bridge for copying bytes within video memory.
     */
    public static void COPY_VIDEO_TO_VIDEO(int segment, int sourceOffset, int destOffset, int count) {
        int i;
        int src;
        int dst;

        if (segment != 0xA000 || count <= 0) {
            return;
        }

        src = sourceOffset & 0xffff;
        dst = destOffset & 0xffff;

        if (src < dst && src + count > dst) {
            for (i = count - 1; i >= 0; i--) {
                VRAM[(dst + i) & 0xffff] = VRAM[(src + i) & 0xffff];
            }
            return;
        }

        for (i = 0; i < count; i++) {
            VRAM[(dst + i) & 0xffff] = VRAM[(src + i) & 0xffff];
        }
    }
}
