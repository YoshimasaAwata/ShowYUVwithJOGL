package com.example;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;

import javax.swing.JPanel;

public class ShowYUVPanel extends JPanel {
    private ShowYUVImage img;

    public ShowYUVPanel(int width, int height) {
        img = new ShowYUVImage(width, height);
        setPreferredSize(new Dimension(width, height));
    }

    public boolean setFile(File yuvFile) {
        return img.setFile(yuvFile);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (img.setNextFrame()) {
            g.drawImage(img, 0, 0, this);
        }
    }
}
