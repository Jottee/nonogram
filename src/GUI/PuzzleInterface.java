package GUI;

import javax.swing.*;
import java.awt.*;

public class PuzzleInterface extends JFrame {

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
        puzzleFrame.setVisible(true);
    }

    public static void addMainPanels(JFrame puzzleFrame) {
        puzzleFrame.getContentPane().add(optionsPanel);
        puzzleFrame.getContentPane().add(puzzlePanel);
        optionsPanel.setBackground(Color.green);
        puzzlePanel.setBackground(Color.white);
        int a = 10;
        int b = 10;
        JPanel[][] panelHolder = new JPanel[3][3];
        puzzlePanel.setLayout(new GridLayout(3, 3));
        createPuzzle(puzzlePanel, a, b);
    }

    public static void createPuzzle(JPanel puzzlePnl, int rows, int cols) {
        puzzlePnl.setLayout(new GridLayout(rows, cols));
        for (int i = 0; i < (rows * cols); i++) {
            final JLabel label = new JLabel("");
            label.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
            puzzlePnl.add(label);
        }

    }

//    public static void createPzzl(JPanel puzzlePnl, int rows, int cols) {
//        puzzlePnl.setLayout(new GridLayout(rows, cols));
//        puzzlePnl.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
//        final int borderWidth = 2;
//        for (int row = 0; row < rows; row++) {
//            for (int col = 0; col < cols; col++) {
//                final JLabel label = new JLabel("");
//                if (row == 0) {
//                    if (col == 0) {
//                        // Top left corner, draw all sides
//                        label.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
//                    } else {
//                        // Top edge, draw all sides except left edge
//                        label.setBorder(BorderFactory.createMatteBorder(borderWidth,
//                                0,
//                                borderWidth,
//                                borderWidth,
//                                Color.BLACK));
//                    }
//                } else {
//                    if (col == 0) {
//                        // Left-hand edge, draw all sides except top
//                        label.setBorder(BorderFactory.createMatteBorder(0,
//                                borderWidth,
//                                borderWidth,
//                                borderWidth,
//                                Color.BLACK));
//                    } else {
//                        // Neither top edge nor left edge, skip both top and left lines
//                        label.setBorder(BorderFactory.createMatteBorder(0,
//                                0,
//                                borderWidth,
//                                borderWidth,
//                                Color.BLACK));
//                    }
//                }
//                puzzlePnl.add(label);
//            }
//        }
//    }

    /**
     * Sets some attributes for the main frame
     *
     * @param fr the frame
     * @param w  width of frame
     * @param h  height of frame
     */
    public static void setPuzzleFrameAttr(JFrame fr, int w, int h) {
        fr.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        fr.pack();
        fr.getContentPane().setLayout(new GridLayout(1, 2));
        fr.setVisible(true);
        fr.setLocationRelativeTo(null);
        fr.setSize(w, h);
    }
}