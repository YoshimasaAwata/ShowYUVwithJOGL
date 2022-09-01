package com.example;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLException;

public class ShowYUVShader {
    static final String VERTEX_SOURCE = "#version 330 core\n" +
            "in vec3 position;\n" +
            "in vec2 vertexUV;\n" +
            "out vec2 uv;\n" +
            "void main(void)\n" +
            "{\n" +
            " gl_Position.xyz = position;\n" +
            " gl_Position.w = 1.0;\n" +
            " uv = vertexUV;\n" +
            "}";
    static final String FRAGMENT_SOURCE = "#version 330 core\n" +
            "in vec2 uv;\n" +
            "out vec4 color;\n" +
            "uniform sampler2D textureSampler;\n" +
            "void main(void)\n" +
            "{\n" +
            // " color = texture(textureSampler, uv);\n" +
            " color = vec4(1.0, 0.0, 0.0, 1.0);\n" +
            "}";

    static final String ERROR_VERTEX = "バーテックスシェーダー作成失敗";
    static final String ERROR_FRAGMENT = "フラグメントシェーダー作成失敗";
    static final String ERROR_PROGRAM = "プログラムのリンク失敗";

    static final int POSITION_ID = 0;
    static final int TEXTURE_ID = 1;

    private int vertexShaderID = -1;
    private int fragmentShaderID = -1;
    private int programID = -1;

    public int getProgramID() {
        return programID;
    }

    public ShowYUVShader(GL3 gl) throws GLException {
        int[] result = new int[3];

        // 頂点シェーダを作成、コンパイル
        String[] vertexSource = new String[] { VERTEX_SOURCE };
        int[] vertexSourceLengths = new int[] { vertexSource[0].length() };
        vertexShaderID = gl.glCreateShader(GL3.GL_VERTEX_SHADER);
        gl.glShaderSource(vertexShaderID, vertexSource.length, vertexSource,
                vertexSourceLengths, 0);
        gl.glCompileShader(vertexShaderID);
        // コンパイル結果を取得
        gl.glGetShaderiv(vertexShaderID, GL3.GL_COMPILE_STATUS, result, 0);
        if (result[0] == GL3.GL_FALSE) {
            int[] length = new int[1];
            byte[] infoLog = new byte[65536];
            gl.glGetShaderInfoLog(vertexShaderID, infoLog.length, length, 0, infoLog, 0);
            System.err.println(new String(infoLog));
            DeleteAllObjects(gl);
            throw new GLException(ERROR_VERTEX);
        }

        // フラグメントシェーダを作成、コンパイル
        String[] fragmentSource = new String[] { FRAGMENT_SOURCE };
        int[] fragmentSourceLengths = new int[] { fragmentSource[0].length() };
        fragmentShaderID = gl.glCreateShader(GL3.GL_FRAGMENT_SHADER);
        gl.glShaderSource(fragmentShaderID, fragmentSource.length, fragmentSource,
                fragmentSourceLengths, 0);
        gl.glCompileShader(fragmentShaderID);
        // コンパイル結果を取得
        gl.glGetShaderiv(fragmentShaderID, GL3.GL_COMPILE_STATUS, result, 0);
        if (result[0] == GL3.GL_FALSE) {
            int[] length = new int[1];
            byte[] infoLog = new byte[65536];
            gl.glGetShaderInfoLog(fragmentShaderID, infoLog.length, length, 0, infoLog,
                    0);
            System.err.println(new String(infoLog));
            DeleteAllObjects(gl);
            throw new GLException(ERROR_FRAGMENT);
        }

        // プログラムをリンク
        programID = gl.glCreateProgram();
        gl.glAttachShader(programID, vertexShaderID);
        gl.glAttachShader(programID, fragmentShaderID);
        gl.glBindAttribLocation(programID, POSITION_ID, "position");
        gl.glBindAttribLocation(programID, TEXTURE_ID, "vertexUV");
        gl.glLinkProgram(programID);

        // プログラムをチェック
        gl.glGetShaderiv(programID, GL3.GL_LINK_STATUS, result, 0);
        if (result[0] == GL3.GL_FALSE) {
            int[] length = new int[1];
            byte[] infoLog = new byte[65536];
            gl.glGetProgramInfoLog(programID, infoLog.length, length, 0, infoLog, 0);
            System.err.println(new String(infoLog));
            DeleteAllObjects(gl);
            throw new GLException(ERROR_PROGRAM);
        }
    }

    public void DeleteAllObjects(GL3 gl) {
        if (fragmentShaderID > 0) {
            if (programID > 0) {
                gl.glDetachShader(programID, fragmentShaderID);
            }
            gl.glDeleteShader(fragmentShaderID);
            fragmentShaderID = -1;
        }
        if (vertexShaderID > 0) {
            if (programID > 0) {
                gl.glDetachShader(programID, vertexShaderID);
            }
            gl.glDeleteShader(vertexShaderID);
            vertexShaderID = -1;
        }
        if (programID > 0) {
            gl.glDeleteProgram(programID);
            programID = -1;
        }
    }
}
