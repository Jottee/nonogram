package GUI;

import javax.swing.*;
import java.awt.*;

public class PuzzleScreen {

    public static JPanel puzzlePanel = new JPanel();
    public static JPanel optionsPanel = new JPanel();

    /**
     * Main method to create window
     */
    public static void main(String[] args) {
        //create frame and set attributes
        JFrame puzzleFrame = new JFrame("Nonogram Puzzle Solver");
        setPuzzleFrameAttr(puzzleFrame, 1366, 768);
        addMainPanels(puzzleFrame);
    }

    public static void addMainPanels(JFrame puzzleFrame) {
        puzzleFrame.getContentPane().add(optionsPanel);
        puzzleFrame.getContentPane().add(puzzlePanel);
        optionsPanel.setBackground(Color.green);
        puzzlePanel.setBackground(Color.red);
    }

    /**
     * Sets some attributes for the main frame
     * @param fr the frame
     * @param w width of frame
     * @param h height of frame
     */
    public static void setPuzzleFrameAttr(JFrame fr, int w, int h) {
        fr.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        fr.pack();
        fr. getContentPane().setLayout(new GridLayout(1, 2));
        fr.setVisible(true);
        fr.setLocationRelativeTo(null);
        fr.setSize(w, h);
    }
}