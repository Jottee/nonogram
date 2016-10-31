package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PuzzleScreen2 extends JFrame {

    public PuzzleScreen2() {
        createGUI();
    }

    public void createGUI() {
        //create frame and panels
        JFrame puzzleFrame = new JFrame("Nonogram Puzzle Solver");

        JPanel puzzlePanel = new JPanel();
        JPanel optionsPanel = new JPanel();

        JTextField rowInput = new JTextField(20);
        rowInput.addActionListener(new DisplayTextAL());
        optionsPanel.add(rowInput);

        //frame init (pack and render)
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new GridLayout(1, 2));
        setLocationRelativeTo(null);

        getContentPane().add(optionsPanel);
        getContentPane().add(puzzlePanel);
        optionsPanel.setBackground(Color.green);
        puzzlePanel.setBackground(Color.lightGray);
        optionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        //test gbag
        JPanel testGBag = new JPanel(new GridBagLayout());
        testGBag.setBackground(Color.lightGray);
        puzzlePanel.add(testGBag);

        createPuzzleField(testGBag, 15, 25);
    }

    public void createPuzzleField(JPanel testGBag, int rows, int cols) {
        GridBagConstraints cMid = new GridBagConstraints();
        JPanel midPanel = new JPanel();
        midPanel.setBackground(Color.white);
        cMid.ipady = rows * 17;
        cMid.ipadx =  cols * 17;
        cMid.weightx = 0.0;
        cMid.weighty = 0.0;
        cMid.gridwidth = 1;
        cMid.gridheight = 1;
        cMid.gridx = 1;
        cMid.gridy = 1;
        cMid.fill = cMid.BOTH;
        testGBag.add(midPanel, cMid);

        GridBagConstraints cTop = new GridBagConstraints();
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.orange);
        cTop.ipady = 0;
        cTop.ipadx = 0;
        cTop.weightx = 0.0;
        cTop.weighty = 0.0;
        cTop.gridwidth = 1;
        cTop.gridheight = 1;
        cTop.gridx = 1;
        cTop.gridy = 0;
        testGBag.add(topPanel, cTop);

        GridBagConstraints cLeft = new GridBagConstraints();
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.yellow);
        cLeft.ipady = 0;
        cLeft.ipadx = 0;
        cLeft.weightx = 0.0;
        cLeft.weighty = 0.0;
        cLeft.gridwidth = 1;
        cLeft.gridheight = 1;
        cLeft.gridx = 0;
        cLeft.gridy = 1;
        testGBag.add(leftPanel, cLeft);

        leftPanel.setLayout(new GridBagLayout());
        topPanel.setLayout(new GridBagLayout());
        JTextField jtf;
        cLeft.ipady = 0;
        cLeft.ipadx = 170;
        for (int i = 0; i < rows; i ++) {
            jtf = new JTextField();
            cLeft.gridy = i;
            leftPanel.add(jtf, cLeft);
        }
        cTop.ipady = 100;
        cTop.ipadx = 14;
        for (int i = 0; i < cols; i ++) {
            jtf = new JTextField();
            cTop.gridx = i;
            topPanel.add(jtf, cTop);
        }



    }

    public static void main(String[] args) {
        JFrame fr = new PuzzleScreen2();
        fr.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        fr.pack();
        fr.setVisible(true);
        fr.setSize(1366, 768);
    }
}
