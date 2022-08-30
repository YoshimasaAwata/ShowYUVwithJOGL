package com.example;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ShowYUVImage extends BufferedImage {
    private int y_size;
    private int uv_size;
    private byte[] y;
    private byte[] u;
    private byte[] v;
    private int[] argb;

    private FileInputStream inFile = null;
    private boolean dataAvailable = false;

    public boolean isDataAvailable() {
        return dataAvailable;
    }

    public ShowYUVImage(int width, int height) {
        super(width, height, BufferedImage.TYPE_INT_ARGB);

        y_size = width * height;
        uv_size = (width / 2) * (height / 2);

        y = new byte[y_size];
        u = new byte[uv_size];
        v = new byte[uv_size];
        argb = new int[y_size];
    }

    protected void readYUV() {
        if (inFile != null) {
            try {
                int readNum = inFile.read(y);
                readNum = inFile.read(u);
                readNum = inFile.read(v);
                dataAvailable = (readNum == uv_size);
            } catch (IOException e) {
                dataAvailable = false;
            }
        }
    }

    public boolean setFile(File yuvFile) {
        try {
            inFile = new FileInputStream(yuvFile);
        } catch (Exception e) {
            return false;
        }
        readYUV();
        return dataAvailable;
    }

    protected int clip(int n) {
        return (n <= 0) ? 0 : ((n >= 255) ? 255 : n);
    }

    protected void transYUV2RGB() {
        int height = getHeight();
        int width = getWidth();
        for (int h = 0; h < height; h++) {
            int y_pos = h * width;
            int uv_pos = (h / 2) * (width / 2);

            for (int w = 0; w < width; w++) {
                int yi = y[y_pos + w] & 0x0FF;
                int ui = u[uv_pos + (w / 2)] & 0x0FF;
                int vi = v[uv_pos + (w / 2)] & 0x0FF;

                double y16 = yi - 16.0;
                double u128 = ui - 128.0;
                double v128 = vi - 128.0;

                int r = (int) ((1.164 * y16) + (0.0 * u128) + (1.596 * v128));
                int g = (int) ((1.164 * y16) + (-0.392 * u128) + (-0.813 * v128));
                int b = (int) ((1.164 * y16) + (2.017 * u128) + (0.0 * v128));
                r = clip(r);
                g = clip(g);
                b = clip(b);
                argb[y_pos + w] = (0xFF << 24) | (r << 16) | (g << 8) | b;
            }
        }

        return;
    }

    public void setNextFrame() {
        if (dataAvailable) {
            transYUV2RGB();
            setRGB(0, 0, getWidth(), getHeight(), argb, 0, getWidth());
            readYUV();
        }
    }
}
