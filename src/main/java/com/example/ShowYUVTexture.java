package com.example;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;

public class ShowYUVTexture {
    private int tex_width;
    private int tex_height;
    private int y_size;
    private int uv_size;
    private byte[] y;
    private byte[] u;
    private byte[] v;
    private byte[] rgb;

    private IntBuffer textureID = Buffers.newDirectIntBuffer(1);

    private FileInputStream inFile = null;
    private boolean dataAvailable = false;

    public boolean isDataAvailable() {
        return dataAvailable;
    }

    public ShowYUVTexture(int width, int height) {
        tex_width = width;
        tex_height = height;
        y_size = width * height;
        uv_size = (width / 2) * (height / 2);

        y = new byte[y_size];
        u = new byte[uv_size];
        v = new byte[uv_size];
        rgb = new byte[y_size * 3];
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

    protected byte clip(int n) {
        int clipped = (n <= 0) ? 0 : ((n >= 0x0FF) ? 0x0FF : n);
        return (byte) clipped;
    }

    protected void transYUV2RGB() {
        for (int h = 0; h < tex_height; h++) {
            int y_pos = h * tex_width;
            int uv_pos = (h / 2) * (tex_width / 2);

            for (int w = 0; w < tex_width; w++) {
                int yi = y[y_pos + w] & 0x0FF;
                int ui = u[uv_pos + (w / 2)] & 0x0FF;
                int vi = v[uv_pos + (w / 2)] & 0x0FF;

                double y16 = yi - 16.0;
                double u128 = ui - 128.0;
                double v128 = vi - 128.0;

                int r = (int) ((1.164 * y16) + (0.0 * u128) + (1.596 * v128));
                int g = (int) ((1.164 * y16) + (-0.392 * u128) + (-0.813 * v128));
                int b = (int) ((1.164 * y16) + (2.017 * u128) + (0.0 * v128));
                int pos = (y_pos + w) * 3;
                rgb[pos] = clip(r);
                rgb[pos + 1] = clip(g);
                rgb[pos + 2] = clip(b);
            }
        }

        return;
    }

    public void init(GL3 gl) {
        gl.glGenTextures(1, textureID);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, textureID.get(0));
        gl.glPixelStorei(GL3.GL_UNPACK_ALIGNMENT, 1);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER,
                GL3.GL_LINEAR);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER,
                GL3.GL_LINEAR);
    }

    public void setNextTexture(GL3 gl, ShowYUVShader shader) {
        if (dataAvailable) {
            transYUV2RGB();

            int samplerLocation = gl.glGetUniformLocation(shader.getProgramID(), "textureSampler");
            gl.glUniform1i(samplerLocation, 0);
            gl.glActiveTexture(GL3.GL_TEXTURE0);
            gl.glBindTexture(GL3.GL_TEXTURE_2D, textureID.get(0));
            gl.glTexImage2D(
                    GL3.GL_TEXTURE_2D,
                    0,
                    GL3.GL_RGB,
                    tex_width,
                    tex_height,
                    0,
                    GL3.GL_RGB,
                    GL3.GL_UNSIGNED_BYTE,
                    Buffers.newDirectByteBuffer(rgb));

            readYUV();
        }
    }

    public void dispose(GL3 gl) {
        gl.glDeleteTextures(1, textureID);
    }
}
