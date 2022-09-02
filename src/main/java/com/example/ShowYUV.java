package com.example;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * YUVファイルを表示
 *
 */
public class ShowYUV extends JFrame implements ActionListener, GLEventListener {
    static final int CIF_WIDTH = 352;
    static final int CIF_HEIGHT = 288;

    static final String TITLE = "ShowYUV";
    static final String FILE_NAME = "File: ";
    static final String FILE_OPEN = "File...";
    static final String PLAY = "Play";
    static final String DIALOG_TITLE = "YUVファイル選択";

    private ShowYUVPanel panel;
    private JLabel fileNameLabel;
    private JButton fileOpenButton;
    private JButton playButton;

    private FPSAnimator animator;

    public ShowYUV(String title) throws HeadlessException {
        super(title);

        /* 描画領域 */
        JPanel borderPanel = new JPanel();
        borderPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        panel = new ShowYUVPanel(CIF_WIDTH, CIF_HEIGHT);
        panel.addGLEventListener(this);
        borderPanel.add(panel);

        add(borderPanel);

        /* ボタン領域 */
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        fileNameLabel = new JLabel(FILE_NAME);
        fileOpenButton = new JButton(FILE_OPEN);
        playButton = new JButton(PLAY);
        playButton.setEnabled(false);
        fileOpenButton.addActionListener(this);
        playButton.addActionListener(this);
        buttonPanel.add(fileNameLabel);
        buttonPanel.add(Box.createGlue());
        buttonPanel.add(fileOpenButton);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(playButton);

        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        animator = new FPSAnimator(panel, 15);
    }

    public static void main(String[] args) {
        ShowYUV frame = new ShowYUV(TITLE);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == fileOpenButton) {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("YUVファイル(*.yuv)", "yuv");
            chooser.setFileFilter(filter);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setDialogTitle(DIALOG_TITLE);
            int rc = chooser.showOpenDialog(this);
            if (rc == JFileChooser.APPROVE_OPTION) {
                File yuvFile = chooser.getSelectedFile();
                if (panel.setFile(yuvFile)) {
                    fileNameLabel.setText(FILE_NAME + yuvFile.getPath());
                    playButton.setEnabled(true);
                    panel.repaint();
                }
            }
        } else if (e.getSource() == playButton) {
            playButton.setEnabled(false);
            fileOpenButton.setEnabled(false);
            animator.start();
        }
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        panel.display(gl);
        if (panel.isDataAvailable() == false) {
            animator.stop();
            fileOpenButton.setEnabled(true);
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        panel.dispose(gl);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        panel.init(gl);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        // TODO Auto-generated method stub

    }
}
