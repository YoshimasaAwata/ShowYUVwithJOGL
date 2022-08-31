package com.example;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;

public class ShowYUVPanel extends GLJPanel {
    private ShowYUVImage img;

    public ShowYUVPanel(int width, int height) {
        GLProfile profile = GLProfile.get(GLProfile.GL3);
        GLCapabilities cap = new GLCapabilities(profile);
        setRequestedGLCapabilities(cap);

        img = new ShowYUVImage(width, height);
        setPreferredSize(new Dimension(width, height));
    }

    public boolean setFile(File yuvFile) {
        return img.setFile(yuvFile);
    }

    public boolean isDataAvailable() {
        return img.isDataAvailable();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (img.setNextFrame()) {
            g.drawImage(img, 0, 0, this);
        }
    }
}
