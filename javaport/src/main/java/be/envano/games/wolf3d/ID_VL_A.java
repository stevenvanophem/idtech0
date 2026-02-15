package be.envano.games.wolf3d;

public final class ID_VL_A {

    private static int TimeCount;
    private static final int MDA = 1;
    private static final int CGA = 2;
    private static final int EGA = 3;
    private static final int MCGA = 4;
    private static final int VGA = 5;
    private static final int HGC = 0x80;
    private static final int HGC_PLUS = 0x81;
    private static final int IN_COLOR = 0x82;
    private static final int MDA_DISPLAY = 1;
    private static final int CGA_DISPLAY = 2;
    private static final int EGA_COLOR_DISPLAY = 3;
    private static final int PS2_MONO_DISPLAY = 4;
    private static final int PS2_COLOR_DISPLAY = 5;
    private static final int[] EGA_DISPLAYS = {
            CGA_DISPLAY, EGA_COLOR_DISPLAY, MDA_DISPLAY, CGA_DISPLAY, EGA_COLOR_DISPLAY, MDA_DISPLAY
    };
    private static final int[] DCC_TABLE = {
            0, 0,
            MDA, MDA_DISPLAY,
            CGA, CGA_DISPLAY,
            0, 0,
            EGA, EGA_COLOR_DISPLAY,
            EGA, MDA_DISPLAY,
            0, 0,
            VGA, PS2_MONO_DISPLAY,
            VGA, PS2_COLOR_DISPLAY,
            0, 0,
            MCGA, EGA_COLOR_DISPLAY,
            MCGA, PS2_MONO_DISPLAY,
            MCGA, PS2_COLOR_DISPLAY
    };

    private ID_VL_A() {
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL_A.ASM} ({@code PUBLIC VL_VideoID}).
     */
    public static int VL_VideoID() {
        VideoDetectState state;
        int video;

        state = new VideoDetectState();
        state.cgaFlag = true;
        state.egaFlag = true;
        state.monoFlag = true;

        FindPS2(state);
        if (state.egaFlag) {
            FindEGA(state);
        }
        if (state.cgaFlag) {
            FindCGA(state);
        }
        if (state.monoFlag) {
            FindMono(state);
        }

        FindActive(state);
        video = state.video0Type;
        if (video == 0) {
            // TODO: Remove fallback when BIOS/port backend provides real detection values.
            return 5;
        }
        return video;
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL_A.ASM:30} ({@code PROC VL_WaitVBL num:WORD}).
     */
    public static void VL_WaitVBL(int vbls) {
        while (vbls != 0) {
            while ((ASM_RUNTIME.INPORTB(ID_VL_H.STATUS_REGISTER_1) & 8) != 0) {
                // waitnosync
            }

            while ((ASM_RUNTIME.INPORTB(ID_VL_H.STATUS_REGISTER_1) & 8) == 0) {
                // waitsync
            }

            vbls--;
        }
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL_A.ASM:67} ({@code PROC VL_SetCRTC crtc:WORD}).
     */
    public static void VL_SetCRTC(int crtc) {
        ASM_RUNTIME.CLI();

        while ((ASM_RUNTIME.INPORTB(ID_VL_H.STATUS_REGISTER_1) & 1) != 0) {
            // waitdisplay
        }

        ASM_RUNTIME.OUTPORTB(ID_VL_H.CRTC_INDEX, ID_VL_H.CRTC_STARTHIGH);
        ASM_RUNTIME.OUTPORTB(ID_VL_H.CRTC_INDEX + 1, (crtc >> 8) & 0xff);
        ASM_RUNTIME.OUTPORTB(ID_VL_H.CRTC_INDEX, ID_VL_H.CRTC_STARTLOW);
        ASM_RUNTIME.OUTPORTB(ID_VL_H.CRTC_INDEX + 1, crtc & 0xff);

        ASM_RUNTIME.STI();
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL_A.ASM:121} ({@code PROC VL_SetScreen crtc:WORD, pel:WORD}).
     */
    public static void VL_SetScreen(int crtc, int pel) {
        int cx;
        int al;

        cx = TimeCount;
        cx += 2;

        while ((ASM_RUNTIME.INPORTB(ID_VL_H.STATUS_REGISTER_1) & 1) != 0) {
            // waitdisplay
        }

        while (true) {
            ASM_RUNTIME.STI();
            ASM_RUNTIME.CLI();

            if (TimeCount >= cx) {
                break;
            }

            al = ASM_RUNTIME.INPORTB(ID_VL_H.STATUS_REGISTER_1);
            if ((al & 8) != 0) {
                while ((ASM_RUNTIME.INPORTB(ID_VL_H.STATUS_REGISTER_1) & 1) != 0) {
                    // waitdisplay
                }
                continue;
            }
            if ((al & 1) == 0) {
                continue;
            }

            al = ASM_RUNTIME.INPORTB(ID_VL_H.STATUS_REGISTER_1);
            if ((al & 8) != 0) {
                while ((ASM_RUNTIME.INPORTB(ID_VL_H.STATUS_REGISTER_1) & 1) != 0) {
                    // waitdisplay
                }
                continue;
            }
            if ((al & 1) == 0) {
                continue;
            }

            al = ASM_RUNTIME.INPORTB(ID_VL_H.STATUS_REGISTER_1);
            if ((al & 8) != 0) {
                while ((ASM_RUNTIME.INPORTB(ID_VL_H.STATUS_REGISTER_1) & 1) != 0) {
                    // waitdisplay
                }
                continue;
            }
            if ((al & 1) == 0) {
                continue;
            }

            al = ASM_RUNTIME.INPORTB(ID_VL_H.STATUS_REGISTER_1);
            if ((al & 8) != 0) {
                while ((ASM_RUNTIME.INPORTB(ID_VL_H.STATUS_REGISTER_1) & 1) != 0) {
                    // waitdisplay
                }
                continue;
            }
            if ((al & 1) == 0) {
                continue;
            }

            al = ASM_RUNTIME.INPORTB(ID_VL_H.STATUS_REGISTER_1);
            if ((al & 8) != 0) {
                while ((ASM_RUNTIME.INPORTB(ID_VL_H.STATUS_REGISTER_1) & 1) != 0) {
                    // waitdisplay
                }
                continue;
            }
            if ((al & 1) == 0) {
                continue;
            }

            break;
        }

        ASM_RUNTIME.OUTPORTB(ID_VL_H.CRTC_INDEX, ID_VL_H.CRTC_STARTHIGH);
        ASM_RUNTIME.OUTPORTB(ID_VL_H.CRTC_INDEX + 1, (crtc >> 8) & 0xff);
        ASM_RUNTIME.OUTPORTB(ID_VL_H.CRTC_INDEX, ID_VL_H.CRTC_STARTLOW);
        ASM_RUNTIME.OUTPORTB(ID_VL_H.CRTC_INDEX + 1, crtc & 0xff);

        ASM_RUNTIME.OUTPORTB(ID_VL_H.ATR_INDEX, ID_VL_H.ATR_PELPAN | 0x20);
        ASM_RUNTIME.OUTPORTB(ID_VL_H.ATR_INDEX, pel & 0xff);

        ASM_RUNTIME.STI();
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VL_A.ASM:237}
     * ({@code PROC VL_ScreenToScreen source:WORD, dest:WORD, wide:WORD, height:WORD}).
     */
    public static void VL_ScreenToScreen(int source, int dest, int width, int height) {
        int linedelta;
        int row;
        int src;
        int dst;
        int gcMode;

        ASM_RUNTIME.PUSHF();
        ASM_RUNTIME.CLI();

        ASM_RUNTIME.MOV_DX(ID_VL_H.SC_INDEX);
        ASM_RUNTIME.MOV_AX(ID_VL_H.SC_MAPMASK + 15 * 256);
        ASM_RUNTIME.OUT_DX_AX();
        ASM_RUNTIME.MOV_DX(ID_VL_H.GC_INDEX);
        ASM_RUNTIME.MOV_AL(ID_VL_H.GC_MODE);
        ASM_RUNTIME.OUT_DX_AL();
        ASM_RUNTIME.INC_DX();
        ASM_RUNTIME.IN_AL_DX();
        gcMode = ASM_RUNTIME.INPORTB(ID_VL_H.GC_INDEX + 1) & 0xff;
        gcMode &= ~3;
        gcMode |= 1;
        ASM_RUNTIME.OUTPORTB(ID_VL_H.GC_INDEX + 1, gcMode);

        ASM_RUNTIME.POPF();

        src = source;
        dst = dest;
        linedelta = ID_VL.GetLineWidthForAsm() - width;
        for (row = 0; row < height; row++) {
            ASM_RUNTIME.COPY_VIDEO_TO_VIDEO(ID_VL_H.SCREENSEG, src, dst, width);
            src += width;
            src += linedelta;
            dst += width;
            dst += linedelta;
        }

        gcMode = ASM_RUNTIME.INPORTB(ID_VL_H.GC_INDEX + 1) & 0xff;
        gcMode &= ~3;
        ASM_RUNTIME.OUTPORTB(ID_VL_H.GC_INDEX + 1, gcMode);

        ASM_RUNTIME.MOV_AX_SS();
        ASM_RUNTIME.MOV_DS_AX();
    }

    private static void FindPS2(VideoDetectState state) {
        int bx;
        int ch;
        int cl;

        ASM_RUNTIME.MOV_AX(0x1A00);
        ASM_RUNTIME.INT(0x10);

        if (ASM_RUNTIME.AL() != 0x1A) {
            return;
        }

        bx = ASM_RUNTIME.BX();
        ch = (bx >> 8) & 0xff;
        cl = bx & 0xff;

        if (ch != 0) {
            SetDevice1FromDcc(state, ch);
        }

        SetDevice0FromDcc(state, cl);

        state.cgaFlag = false;
        state.egaFlag = false;
        state.monoFlag = false;

        if (state.video0Type == MDA) {
            state.video0Type = 0;
            state.display0Type = 0;
            state.monoFlag = true;
        } else if (state.video1Type == MDA) {
            state.video1Type = 0;
            state.display1Type = 0;
            state.monoFlag = true;
        }
    }

    private static void FindEGA(VideoDetectState state) {
        int index;
        int display;

        ASM_RUNTIME.MOV_BL(0x10);
        ASM_RUNTIME.MOV_AH(0x12);
        ASM_RUNTIME.INT(0x10);

        if (ASM_RUNTIME.BL() == 0x10) {
            return;
        }

        index = (ASM_RUNTIME.CL() >> 1) & 0xff;
        if (index >= EGA_DISPLAYS.length) {
            index = EGA_DISPLAYS.length - 1;
        }
        display = EGA_DISPLAYS[index];

        FoundDevice(state, EGA, display);

        if (display == MDA_DISPLAY) {
            state.monoFlag = false;
        } else {
            state.cgaFlag = false;
        }
    }

    private static void FindCGA(VideoDetectState state) {
        if (!Find6845(0x3D4)) {
            return;
        }

        FoundDevice(state, CGA, CGA_DISPLAY);
    }

    private static void FindMono(VideoDetectState state) {
        int al;
        int ah;
        int cx;
        int dl;

        if (!Find6845(0x3B4)) {
            return;
        }

        al = ASM_RUNTIME.INPORTB(0x3BA) & 0xff;
        al &= 0x80;
        ah = al;

        cx = 0x8000;
        while (cx-- != 0) {
            al = ASM_RUNTIME.INPORTB(0x3BA) & 0xff;
            al &= 0x80;
            if (ah != al) {
                break;
            }
        }

        if (ah == al) {
            FoundDevice(state, MDA, MDA_DISPLAY);
            return;
        }

        al = ASM_RUNTIME.INPORTB(0x3BA) & 0xff;
        dl = al & 0x70;

        if (dl == 0x10) {
            FoundDevice(state, HGC_PLUS, MDA_DISPLAY);
            return;
        }

        if (dl == 0x50) {
            FoundDevice(state, IN_COLOR, EGA_COLOR_DISPLAY);
            return;
        }

        FoundDevice(state, HGC, MDA_DISPLAY);
    }

    private static boolean Find6845(int port) {
        int dx;
        int original;
        int returned;
        int cx;

        dx = port;
        ASM_RUNTIME.OUTPORTB(dx, 0x0F);
        dx++;
        original = ASM_RUNTIME.INPORTB(dx) & 0xff;
        ASM_RUNTIME.OUTPORTB(dx, 0x66);

        cx = 0x100;
        while (cx-- != 0) {
            // wait for 6845 to respond
        }

        returned = ASM_RUNTIME.INPORTB(dx) & 0xff;
        ASM_RUNTIME.OUTPORTB(dx, original);

        return returned == 0x66;
    }

    private static void FindActive(VideoDetectState state) {
        int mode;
        int v0;
        int d0;

        if (state.video1Type == 0) {
            return;
        }
        if (state.video0Type >= 4) {
            return;
        }
        if (state.video1Type >= 4) {
            return;
        }

        ASM_RUNTIME.MOV_AH(0x0F);
        ASM_RUNTIME.INT(0x10);
        mode = ASM_RUNTIME.AL() & 7;

        if (mode != 7) {
            if (state.display0Type != MDA_DISPLAY) {
                return;
            }
        } else if (state.display0Type == MDA_DISPLAY) {
            return;
        }

        v0 = state.video0Type;
        d0 = state.display0Type;
        state.video0Type = state.video1Type;
        state.display0Type = state.display1Type;
        state.video1Type = v0;
        state.display1Type = d0;
    }

    private static void FoundDevice(VideoDetectState state, int videoType, int displayType) {
        if (state.video0Type == 0) {
            state.video0Type = videoType;
            state.display0Type = displayType;
        } else {
            state.video1Type = videoType;
            state.display1Type = displayType;
        }
    }

    private static void SetDevice0FromDcc(VideoDetectState state, int dcc) {
        int index;

        index = dcc * 2;
        if (index < 0 || index + 1 >= DCC_TABLE.length) {
            state.video0Type = 0;
            state.display0Type = 0;
            return;
        }

        state.video0Type = DCC_TABLE[index];
        state.display0Type = DCC_TABLE[index + 1];
    }

    private static void SetDevice1FromDcc(VideoDetectState state, int dcc) {
        int index;

        index = dcc * 2;
        if (index < 0 || index + 1 >= DCC_TABLE.length) {
            state.video1Type = 0;
            state.display1Type = 0;
            return;
        }

        state.video1Type = DCC_TABLE[index];
        state.display1Type = DCC_TABLE[index + 1];
    }

    private static final class VideoDetectState {
        private int video0Type;
        private int display0Type;
        private int video1Type;
        private int display1Type;
        private boolean egaFlag;
        private boolean cgaFlag;
        private boolean monoFlag;
    }
}
