package com.example;

import java.awt.Dimension;
import java.io.File;
import java.nio.IntBuffer;

import javax.swing.JOptionPane;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;

public class ShowYUVPanel extends GLJPanel {
    private final float[] TEXTURE_DATA = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    private final float[] VERTEX_DATA = {
            -0.9f, -0.9f, 0.0f,
            -0.9f, 0.9f, 0.0f,
            0.9f, 0.9f, 0.0f,
            0.9f, -0.9f, 0.0f,
    };

    private ShowYUVShader shader;
    private ShowYUVImage img;
    private IntBuffer vertexBuffer = Buffers.newDirectIntBuffer(1);
    private IntBuffer textureBuffer = Buffers.newDirectIntBuffer(1);

    public ShowYUVPanel(int width, int height) {
        GLProfile profile = GLProfile.get(GLProfile.GL3);
        GLCapabilities cap = new GLCapabilities(profile);
        setRequestedGLCapabilities(cap);

        // img = new ShowYUVImage(width, height);
        setPreferredSize(new Dimension(width, height));
    }

    public boolean setFile(File yuvFile) {
        return img.setFile(yuvFile);
    }

    public boolean isDataAvailable() {
        return img.isDataAvailable();
    }

    public void init(GL3 gl) {
        gl.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);

        try {
            shader = new ShowYUVShader(gl);
            // texture.init(gl);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            shader = null;
        }

        gl.glGenBuffers(1, vertexBuffer);
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vertexBuffer.get(0));
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, (VERTEX_DATA.length * 4),
                Buffers.newDirectFloatBuffer(VERTEX_DATA), GL3.GL_STATIC_DRAW);

        gl.glGenBuffers(1, textureBuffer);
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, textureBuffer.get(0));
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, (TEXTURE_DATA.length * 4),
                Buffers.newDirectFloatBuffer(TEXTURE_DATA), GL3.GL_STATIC_DRAW);
    }

    public void display(GL3 gl) {
        if (shader != null) {
            gl.glUseProgram(shader.getProgramID());

            // texture.setNextTexture(gl, shader);

            gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

            gl.glEnableVertexAttribArray(ShowYUVShader.POSITION_ID);
            gl.glEnableVertexAttribArray(ShowYUVShader.TEXTURE_ID);

            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vertexBuffer.get(0));
            gl.glVertexAttribPointer(
                    ShowYUVShader.POSITION_ID, // 属性0：0に特に理由はありません。
                    3, // サイズ
                    GL3.GL_FLOAT, // タイプ
                    false, // 正規化？
                    0, // ストライド
                    0 // 配列バッファオフセット
            );

            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, textureBuffer.get(0));
            gl.glVertexAttribPointer(
                    ShowYUVShader.TEXTURE_ID, // 属性1：1に特に理由はありません。
                    2, // サイズ
                    GL3.GL_FLOAT, // タイプ
                    false, // 正規化？
                    0, // ストライド
                    0 // 配列バッファオフセット
            );

            gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, 4);

            gl.glDisableVertexAttribArray(ShowYUVShader.TEXTURE_ID);
            gl.glDisableVertexAttribArray(ShowYUVShader.POSITION_ID);
        }
    }

    public void dispose(GL3 gl) {
        gl.glDeleteBuffers(1, textureBuffer);
        gl.glDeleteBuffers(1, vertexBuffer);

        if (shader != null) {
            shader.DeleteAllObjects(gl);
        }

        // texture.dispose(gl);
    }

}
