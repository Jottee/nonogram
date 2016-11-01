package GUI;

import javax.swing.*;
import java.awt.*;

public class PuzzleScreen2 extends JFrame {

    /**
     * Constructor of the frame, calls createGUI which initializes the elements of the GUI
     */
    public PuzzleScreen2(int rows, int cols) {
        super("Nonogram Puzzle Solver");
        createGUI(rows, cols);
    }

    /**
     * Initialize the elements of the GUI
     */
    public void createGUI(int rows, int cols) {
        //Create the two main panels
        JPanel puzzlePanel = new JPanel();
        JPanel optionsPanel = new JPanel();

        //add panels and set options for the panels
        getContentPane().setLayout(new GridLayout(1, 2));
        getContentPane().add(optionsPanel);
        getContentPane().add(puzzlePanel);
        optionsPanel.setBackground(Color.gray);
        puzzlePanel.setBackground(Color.lightGray);
        optionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        //Create a simple text input (for testing at the moment) and add actionlistener
        JTextField rowInput = new JTextField(20);
        rowInput.addActionListener(new DisplayTextAL());
        optionsPanel.add(rowInput);

        //add new panel with GridBagLayout
        JPanel testGBag = new JPanel(new GridBagLayout());
        testGBag.setBackground(Color.lightGray);
        puzzlePanel.add(testGBag);

        createPuzzleField(testGBag, rows, cols);
    }

    /**
     * Creates a field of the nonogram with specified size
     * @param panel panel that the puzzle will be on
     * @param rows amount of rows
     * @param cols amount of cols
     */
    public void createPuzzleField(JPanel panel, int rows, int cols) {
        //set dimensions and settings of mid panel
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
        midPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
        panel.add(midPanel, cMid);

        //set dimensions and settings of top panel
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
        topPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.black));
        panel.add(topPanel, cTop);


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
        leftPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.black));
        panel.add(leftPanel, cLeft);

        leftPanel.setLayout(new GridBagLayout());
        topPanel.setLayout(new GridBagLayout());
        JTextField jtf;
        cLeft.ipady = 2;
        cLeft.ipadx = 170;
        for (int i = 0; i < rows; i ++) {
            jtf = new JTextField();
            cLeft.gridy = i;
            jtf.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.gray));
            leftPanel.add(jtf, cLeft);
        }
        JTextArea jta;
        cTop.ipady = 100;
        cTop.ipadx = 17;
        for (int i = 0; i < cols; i ++) {
            jta = new JTextArea();
            cTop.gridx = i;
            jta.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.gray));
            topPanel.add(jta, cTop);
        }

        midPanel.setLayout(new GridLayout(rows, cols));
        for (int i = 0; i < (rows * cols); i++) {
            final JLabel label = new JLabel("");
            label.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
            midPanel.add(label);
        }
    }

    public static void main(String[] args) {
        //Initialize the screen
        JFrame fr = new PuzzleScreen2(5, 10);
        //Change some variables for the frame
        fr.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        fr.pack();
        fr.setVisible(true);
        fr.setSize(1366, 768);
    }
}
