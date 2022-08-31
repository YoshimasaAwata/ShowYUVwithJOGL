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
            "    gl_Position.xyz = position;\n" +
            "    gl_Position.w = 1.0;\n" +
            "    uv = vertexUV;\n" +
            "}";
    static final String FRAGMENT_SOURCE = "#version 330 core\n" +
            "in vec2 uv;\n" +
            "out vec4 color;\n" +
            "uniform sampler2D textureSampler;\n" +
            "void main(void)\n" +
            "{\n" +
            "    color = texture(textureSampler, uv);\n" +
            "}";

    static final String ERROR_COMPILE = "シェーダーコンパイル失敗";
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
        // 頂点シェーダを作成、コンパイル
        vertexShaderID = CompileShader(gl, VERTEX_SOURCE, GL3.GL_VERTEX_SHADER);

        // フラグメントシェーダを作成、コンパイル
        fragmentShaderID = CompileShader(gl, FRAGMENT_SOURCE, GL3.GL_FRAGMENT_SHADER);

        // プログラムをリンク
        programID = gl.glCreateProgram();
        gl.glAttachShader(programID, vertexShaderID);
        gl.glAttachShader(programID, fragmentShaderID);
        gl.glBindAttribLocation(programID, POSITION_ID, "position");
        gl.glBindAttribLocation(programID, TEXTURE_ID, "vertexUV");
        gl.glLinkProgram(programID);

        // プログラムをチェック
        int[] result = new int[3];
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

    /*
     * 渡されたソースコードをコンパイルしshaderIDを返す
     */
    protected int CompileShader(GL3 gl, String source, int shaderType) throws GLException {
        String[] sources = new String[] { source };
        int[] sourceLengths = new int[] { sources[0].length() };
        int shaderID = gl.glCreateShader(shaderType);
        gl.glShaderSource(shaderID, sources.length, sources, sourceLengths, 0);
        gl.glCompileShader(shaderID);

        // コンパイル結果を取得
        int[] result = new int[3];
        gl.glGetShaderiv(shaderID, GL3.GL_COMPILE_STATUS, result, 0);
        if (result[0] == GL3.GL_FALSE) {
            int[] length = new int[1];
            byte[] infoLog = new byte[65536];
            gl.glGetShaderInfoLog(shaderID, infoLog.length, length, 0, infoLog, 0);
            System.err.println(new String(infoLog));
            DeleteAllObjects(gl);
            throw new GLException(ERROR_COMPILE);
        }

        return shaderID;
    }

    private void DeleteAllObjects(GL3 gl) {
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
