package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PuzzleScreen3 extends JFrame {


    private List<JTextField> rowJTFs = new ArrayList();
    private List<JTextField> colJTFs = new ArrayList();
    private List<List<Integer>> rowIntArLi = new ArrayList();
    private List<List<Integer>> colIntArLi = new ArrayList();
    private int totalElements = 0;
    private int maxRowSize = 0;
    private int maxColSize = 0;
    private int rowElements = 0;
    private int colElements = 0;
    
    /**
     * Constructor of the frame, calls createGUI which initializes the elements of the GUI
     */
    public PuzzleScreen3() {
        super("Nonogram Puzzle Solver");
    }

    /**
     * Initialize the elements of the GUI
     */
    public void createGUI() {
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

        //add new panel with GridBagLayout
        JPanel testGBag = new JPanel(new GridBagLayout());
        testGBag.setBackground(Color.lightGray);
        puzzlePanel.add(testGBag);
        updatePuzzleField(puzzlePanel, testGBag, 5, 5);

        //Create a simple text input (for testing at the moment) and add actionlistener
        JTextField rowInput = new JTextField(10);
        optionsPanel.add(rowInput);
        JTextField colInput = new JTextField(10);
        optionsPanel.add(colInput);
        
        //Create ok button
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    int rows = Integer.parseInt(rowInput.getText());
                    int cols = Integer.parseInt(colInput.getText());
                    updatePuzzleField(puzzlePanel, testGBag, rows, cols);
                    System.out.println("Clicked OK");
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Not a number");
                }
            }
        });
        optionsPanel.add(okButton, BorderLayout.CENTER);
        
        //Create button to generate array
        JButton generateButton = new JButton("Generate List");
        generateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                inputFieldsToIntArLi();
            }
        });
        optionsPanel.add(generateButton);

        //Create button to generate array
        JButton generateBytesButton = new JButton("Generate Byte Array");
        generateBytesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                byte[] rowBytes = generateByteArray(rowIntArLi, maxRowSize, rowElements);
                byte[] colBytes = generateByteArray(colIntArLi, maxColSize, colElements);
                byte[] out = new byte[4 + rowBytes.length + colBytes.length];
                out[0] = (byte)rowElements;
                out[1] = (byte)maxRowSize;
                out[2] = (byte)colElements;
                out[3] = (byte)maxColSize;
                System.arraycopy(rowBytes, 0, out, 4, rowBytes.length);
                System.arraycopy(colBytes, 0, out, 4 + (out[0] * out[1]), colBytes.length);
                printByteArray(out);
            }
        });
        optionsPanel.add(generateBytesButton);

        //Change some variables for the frame
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        setSize(1366, 768);


    }

    public byte[] generateByteArray(List<List<Integer>> intArLi, int maxSize, int elements) {
        byte[] bytes = new byte[elements * maxSize];
        //System.out.println("list = " + intArLi + " max size = " + maxSize);
        int c = 0;
        for (List<Integer> intsOnOne : intArLi) {
            for (Integer i : intsOnOne) {
                byte[] intAsBytes = ByteBuffer.allocate(4).putInt(i).array();
                //String s1 = String.format("%8s", Integer.toBinaryString(intAsBytes[3] & 0xFF)).replace(' ', '0');
                //System.out.println("byte:\t" + c  + "\t" + s1 + "\t" + intAsBytes[3] + "\t(i = " + i + ")");
                bytes[c] = intAsBytes[3];
                c++;
            }
            if (intsOnOne.size() < maxSize) {
                int size = intsOnOne.size();
                for (int j = size; j < maxSize; j++) {
                    //System.out.println("byte;\t" + c  + "\t00000000\t0\t(buffer)");
                    bytes[c] = 0x00;
                    c++;
                }
            }
        }
        return bytes;
    }

    public void printByteArray(byte[] bytes) {
        String div = "------------------------------------------------------";
        String hdiv = "\n======================================================";
        int rowStart = 4;
        int colStart = 4 + (bytes[0] * bytes[1]);
        for (int i = 0; i < bytes.length; i++) {
            if (i == 0) {
                System.out.println(hdiv +
                        "\nTHE BYTE ARRAY" + hdiv +
                        "\nRows: " + bytes[i] +
                        "\nMax elements per row: " + bytes[i + 1] +
                        "\nColumns: " + bytes[i + 2] +
                        "\nMax elements per column: " + bytes[i + 3] + hdiv +
                        "\n\t\tpos.\tbyte\tval\tbuffer\n" + div
                );
            } else if (i == rowStart) {
                System.out.println(div + "\n\t\tRow list: " + rowIntArLi + "\n" + div);
            } else if (i == colStart) {
                System.out.println(div + "\n\t\tColumn list: " + colIntArLi + "\n" + div);
            }
            boolean buffer = false;
            if ((i >= rowStart) && (i < colStart)) {
                //System.out.println("rowbuf");
                //System.out.println(bytes[i] %maxRowSize);
                buffer = (bytes[i] == 0) && ((i - rowStart) % maxRowSize != 0);
            } else if ((i >= 4 + (bytes[0] * bytes[1]))) {
                //System.out.println("colbuf");
                buffer = (bytes[i] == 0) && ((i - colStart) % maxColSize != 0);
            }

            String s = String.format("%8s", Integer.toBinaryString(bytes[i] & 0xFF)).replace(' ', '0');
            System.out.println("byte:\t" + i  + "\t" + s + "\t" + bytes[i] + "\t" + buffer + "");
        }
    }

    public void inputFieldsToIntArLi() {
        System.out.println("Generating lists...");
        totalElements = 0;
        rowIntArLi.clear();
        colIntArLi.clear();
        maxRowSize = 0;
        maxColSize = 0;

        for (JTextField toBIntRow : rowJTFs) {
            List intsOnOneRow = new ArrayList<Integer>();
            String[] splitRow = toBIntRow.getText().split("\\s+");
            for (int i = 0; i < splitRow.length; i++) {
                if (splitRow.length > maxRowSize) {
                    maxRowSize = splitRow.length;
                }
                try {
                    intsOnOneRow.add(Integer.parseInt(splitRow[i]));
                    totalElements++;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Not a number in the row");
                }
            }
            rowIntArLi.add(intsOnOneRow);
        }

        for (JTextField toBIntCol : colJTFs) {
            List intsOnOneCol = new ArrayList<Integer>();
            String[] splitCol = toBIntCol.getText().split("\\s+");
            for (int i = 0; i < splitCol.length; i++) {
                if (splitCol.length > maxColSize) {
                    maxColSize = splitCol.length;
                }
                try {
                    intsOnOneCol.add(Integer.parseInt(splitCol[i]));
                    totalElements++;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Not a number in the col");
                }
            }
            colIntArLi.add(intsOnOneCol);
        }

        rowElements = rowIntArLi.size();
        colElements = colIntArLi.size();

        System.out.println("Lists generated.");

        //System.out.println(colIntArLi);
        //System.out.println(rowIntArLi);
    }

    /**
     * Creates a field of the nonogram with specified size
     *
     * @param testGBag panel that the puzzle will be on
     * @param rows  amount of rows
     * @param cols  amount of cols
     */
    public void updatePuzzleField(JPanel puzzlePanel, JPanel testGBag, int rows, int cols) {

        puzzlePanel.removeAll();
        testGBag = new JPanel(new GridBagLayout());
        testGBag.setBackground(Color.lightGray);
        puzzlePanel.add(testGBag);
        
        rowJTFs.clear();
        colJTFs.clear();

        //set dimensions and settings of mid panel
        GridBagConstraints cMid = new GridBagConstraints();
        JPanel midPanel = new JPanel();
        midPanel.setBackground(Color.white);
        cMid.ipady = rows * 17;
        cMid.ipadx = cols * 17;
        cMid.weightx = 0.0;
        cMid.weighty = 0.0;
        cMid.gridwidth = 1;
        cMid.gridheight = 1;
        cMid.gridx = 1;
        cMid.gridy = 1;
        cMid.fill = cMid.BOTH;
        midPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
        testGBag.add(midPanel, cMid);

        //set dimensions and settings of col panel
        GridBagConstraints cCol = new GridBagConstraints();
        JPanel colPanel = new JPanel();
        colPanel.setBackground(Color.orange);
        cCol.ipady = 0;
        cCol.ipadx = 0;
        cCol.weightx = 0.0;
        cCol.weighty = 0.0;
        cCol.gridwidth = 1;
        cCol.gridheight = 1;
        cCol.gridx = 1;
        cCol.gridy = 0;
        colPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.black));
        testGBag.add(colPanel, cCol);


        GridBagConstraints cRow = new GridBagConstraints();
        JPanel rowPanel = new JPanel();
        rowPanel.setBackground(Color.yellow);
        cRow.ipady = 0;
        cRow.ipadx = 0;
        cRow.weightx = 0.0;
        cRow.weighty = 0.0;
        cRow.gridwidth = 1;
        cRow.gridheight = 1;
        cRow.gridx = 0;
        cRow.gridy = 1;
        rowPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.black));
        testGBag.add(rowPanel, cRow);

        rowPanel.setLayout(new GridBagLayout());
        colPanel.setLayout(new GridBagLayout());
        JTextField jtf;
        cRow.ipady = 2;
        cRow.ipadx = 170;
        for (int i = 0; i < rows; i++) {
            jtf = new JTextField();
            cRow.gridy = i;
            jtf.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.gray));
            rowPanel.add(jtf, cRow);
            rowJTFs.add(jtf);
        }
        cCol.ipady = 100;
        cCol.ipadx = 17;
        for (int i = 0; i < cols; i++) {
            jtf = new JTextField();
            cCol.gridx = i;
            jtf.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.gray));
            colPanel.add(jtf, cCol);
            colJTFs.add(jtf);
        }

        midPanel.setLayout(new GridLayout(rows, cols));
        for (int i = 0; i < (rows * cols); i++) {
            final JLabel label = new JLabel("");
            label.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
            midPanel.add(label);
        }

        //System.out.println(rowJTFs.size());
        //System.out.println(colJTFs.size());

        pack();
        setVisible(true);
        setSize(1366, 768);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PuzzleScreen3 fr = new PuzzleScreen3();
                //Initialize the screen
                fr.createGUI();
            }
        });
    }

}

