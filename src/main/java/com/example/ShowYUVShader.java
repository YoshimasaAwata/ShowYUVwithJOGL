package com.example;

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

}
