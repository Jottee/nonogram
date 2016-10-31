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
        //create frame and panels
        JFrame puzzleFrame = new JFrame("Nonogram Puzzle Solver");
        addMainPanels(puzzleFrame);

        //add components
        JLabel rowLabel = new JLabel("Amount of rows");
        optionsPanel.add(rowLabel);
        JTextField rowInput = new JTextField(20);
        optionsPanel.add(rowInput);

        JLabel colLabel = new JLabel("Amount of rows");
        optionsPanel.add(colLabel);
        JTextField colInput = new JTextField(20);
        optionsPanel.add(colInput);

        //frame init (pack and render)
        puzzleFrame.pack();
        setPuzzleFrameAttr(puzzleFrame, 1366, 768);
        puzzleFrame.setVisible(true);
    }

    /**
     * adds the two main panels to the frame, one for the options and one for displaying the puzzle
     */
    public static void addMainPanels(JFrame fr) {
        fr.getContentPane().add(optionsPanel);
        fr.getContentPane().add(puzzlePanel);
        optionsPanel.setBackground(Color.green);
        puzzlePanel.setBackground(Color.red);
        optionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    }

    /**
     * Sets some attributes for the main frame
     * @param fr the frame
     * @param w width of frame
     * @param h height of frame
     */
    public static void setPuzzleFrameAttr(JFrame fr, int w, int h) {
        fr.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        fr.getContentPane().setLayout(new GridLayout(1, 2));
        fr.setLocationRelativeTo(null);
        fr.setSize(w, h);
    }
}