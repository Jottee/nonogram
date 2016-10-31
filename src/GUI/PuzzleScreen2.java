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
        puzzlePanel.setBackground(Color.red);
        optionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        //test gbag
        JPanel testGBag = new JPanel(new GridBagLayout());
        testGBag.setBackground(Color.pink);
        optionsPanel.add(testGBag);

        JLabel gBagLabel = new JLabel("This be a gbag!");
        testGBag.add(gBagLabel);

        JButton button;
        GridBagConstraints c = new GridBagConstraints();

//        button = new JButton("Button 1");
//        c.weightx = 0.5;
//        c.fill = GridBagConstraints.HORIZONTAL;
//        c.gridx = 0;
//        c.gridy = 0;
//        testGBag.add(button, c);
//
//        button = new JButton("Button 2");
//        c.fill = GridBagConstraints.HORIZONTAL;
//        c.weightx = 0.5;
//        c.gridx = 1;
//        c.gridy = 0;
//        testGBag.add(button, c);
//
//        button = new JButton("Button 3");
//        c.fill = GridBagConstraints.HORIZONTAL;
//        c.weightx = 0.5;
//        c.gridx = 2;
//        c.gridy = 0;
//        testGBag.add(button, c);

        JPanel midPanel = new JPanel();
        midPanel.setBackground(Color.blue);
        //c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 100;      //make this component tall
        c.ipadx = 120;
        c.weightx = 0.0;
        c.gridwidth = 5;
        c.gridheight = 6;
        c.gridx = 1;
        c.gridy = 1;
        testGBag.add(midPanel, c);

//        button = new JButton("5");
//        c.fill = GridBagConstraints.HORIZONTAL;
//        c.ipady = 0;       //reset to default
//        c.weighty = 1.0;   //request any extra vertical space
//        c.anchor = GridBagConstraints.PAGE_END; //bottom of space
//        c.insets = new Insets(10,0,0,0);  //top padding
//        c.gridx = 6;       //aligned with button 2
//        c.gridwidth = 2;   //2 columns wide
//        c.gridy = 2;       //third row
//        testGBag.add(button, c);

    }

    public static void main(String[] args) {
        JFrame fr = new PuzzleScreen2();
        fr.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        fr.pack();
        fr.setVisible(true);
        fr.setSize(1366, 768);
    }
}
