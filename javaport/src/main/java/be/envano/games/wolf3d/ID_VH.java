package be.envano.games.wolf3d;

public final class ID_VH {

    private ID_VH() {
    }

    /**
     * Correlates to {@code original/WOLFSRC/ID_VH.C:170} ({@code void VL_MungePic (byte far *source, unsigned width, unsigned height)}).
     */
    public static void VL_MungePic(byte[] source, int width, int height) {
        int x;
        int y;
        int plane;
        int size;
        int pwidth;
        byte[] temp;
        int dest;
        int srcline;

        size = width * height;

        if ((width & 3) != 0) {
            WL_MAIN.Quit("VL_MungePic: Not divisable by 4!");
            return;
        }

        temp = new byte[size];
        System.arraycopy(source, 0, temp, 0, size);

        dest = 0;
        pwidth = width / 4;

        for (plane = 0; plane < 4; plane++) {
            srcline = 0;
            for (y = 0; y < height; y++) {
                for (x = 0; x < pwidth; x++) {
                    source[dest++] = temp[srcline + x * 4 + plane];
                }
                srcline += width;
            }
        }
    }
}
