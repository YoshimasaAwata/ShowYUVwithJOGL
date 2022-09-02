package com.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    private IntBuffer textureIDY = Buffers.newDirectIntBuffer(1);
    private IntBuffer textureIDU = Buffers.newDirectIntBuffer(1);
    private IntBuffer textureIDV = Buffers.newDirectIntBuffer(1);

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

    public void init(GL3 gl) {
        gl.glGenTextures(1, textureIDY);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, textureIDY.get(0));
        gl.glPixelStorei(GL3.GL_UNPACK_ALIGNMENT, 1);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER,
                GL3.GL_LINEAR);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER,
                GL3.GL_LINEAR);

        gl.glGenTextures(1, textureIDU);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, textureIDU.get(0));
        gl.glPixelStorei(GL3.GL_UNPACK_ALIGNMENT, 1);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER,
                GL3.GL_LINEAR);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER,
                GL3.GL_LINEAR);

        gl.glGenTextures(1, textureIDV);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, textureIDV.get(0));
        gl.glPixelStorei(GL3.GL_UNPACK_ALIGNMENT, 1);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER,
                GL3.GL_LINEAR);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER,
                GL3.GL_LINEAR);
    }

    public void setNextTexture(GL3 gl, ShowYUVShader shader) {
        if (dataAvailable) {
            int samplerLocationY = gl.glGetUniformLocation(shader.getProgramID(), "textureSamplerY");
            gl.glUniform1i(samplerLocationY, 0);
            gl.glActiveTexture(GL3.GL_TEXTURE0);
            gl.glBindTexture(GL3.GL_TEXTURE_2D, textureIDY.get(0));
            gl.glTexImage2D(
                    GL3.GL_TEXTURE_2D,
                    0,
                    GL3.GL_RED,
                    tex_width,
                    tex_height,
                    0,
                    GL3.GL_RED,
                    GL3.GL_UNSIGNED_BYTE,
                    Buffers.newDirectByteBuffer(y));

            int samplerLocationU = gl.glGetUniformLocation(shader.getProgramID(), "textureSamplerU");
            gl.glUniform1i(samplerLocationU, 1);
            gl.glActiveTexture(GL3.GL_TEXTURE1);
            gl.glBindTexture(GL3.GL_TEXTURE_2D, textureIDU.get(0));
            gl.glTexImage2D(
                    GL3.GL_TEXTURE_2D,
                    0,
                    GL3.GL_RED,
                    (tex_width / 2),
                    (tex_height / 2),
                    0,
                    GL3.GL_RED,
                    GL3.GL_UNSIGNED_BYTE,
                    Buffers.newDirectByteBuffer(u));

            int samplerLocationV = gl.glGetUniformLocation(shader.getProgramID(), "textureSamplerV");
            gl.glUniform1i(samplerLocationV, 2);
            gl.glActiveTexture(GL3.GL_TEXTURE2);
            gl.glBindTexture(GL3.GL_TEXTURE_2D, textureIDV.get(0));
            gl.glTexImage2D(
                    GL3.GL_TEXTURE_2D,
                    0,
                    GL3.GL_RED,
                    (tex_width / 2),
                    (tex_height / 2),
                    0,
                    GL3.GL_RED,
                    GL3.GL_UNSIGNED_BYTE,
                    Buffers.newDirectByteBuffer(v));

            readYUV();
        }
    }

    public void dispose(GL3 gl) {
        gl.glDeleteTextures(1, textureIDY);
        gl.glDeleteTextures(1, textureIDU);
        gl.glDeleteTextures(1, textureIDV);
    }
}
