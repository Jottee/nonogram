package GUI;

import communication.SPI2;
import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.List;

public class MainFrame2 extends JFrame {


    private List<JTextField> rowJTFs = new ArrayList<>();
    private List<JTextField> colJTFs = new ArrayList<>();
    private List<List<Integer>> rowIntArLi = new ArrayList<>();
    private List<List<Integer>> colIntArLi = new ArrayList<>();
    private int maxRowSize = 0;
    private int maxColSize = 0;
    private int rowElements = 0;
    private int colElements = 0;
    private Map<Integer, JPanel> nonogramField = new HashMap<>();
    private SPI2 spi;
    private byte[] out;

    public static int PPW = 1300;
    public static int PPH = 680;

    /**
     * Constructor of the frame, calls createGUI which initializes the elements of the GUI
     */
    private MainFrame2() {
        super("Nonogram Puzzle Solver");
        spi = new SPI2(this);
        new Thread(spi).start();
    }

    /**
     * Initialize the elements of the GUI
     */
    private void createGUI() {
        //Create the two main panels
        final JPanel puzzlePanel = new JPanel();
        final JPanel optionsPanel = new JPanel();

        //add panels and set options for the panels
        getContentPane().setLayout(new GridBagLayout());
        
        GridBagConstraints constr = new GridBagConstraints();
        //constr.fill = constr.HORIZONTAL;
        //constr.anchor = constr.FIRST_LINE_START;
        constr.ipady = 0;
        constr.ipadx = 50;
        constr.weightx = 1;
        constr.weighty = 0.0;
        constr.gridwidth = 1;
        constr.gridheight = 1;
        constr.gridx = 0;
        constr.gridy = 0;
        constr.fill = constr.BOTH;
        getContentPane().add(optionsPanel, constr);

        //constr.fill = constr.HORIZONTAL;
        //constr.fill = constr.VERTICAL;
        constr.ipady = 0;
        constr.weightx = 1;
        constr.weighty = 1;
        constr.gridwidth = 1;
        constr.gridheight = 1;
        constr.gridx = 0;
        constr.gridy = 1;
        constr.fill = constr.BOTH;
        getContentPane().add(puzzlePanel, constr);
        optionsPanel.setBackground(Color.gray);
        puzzlePanel.setBackground(Color.lightGray);
        optionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        puzzlePanel.setLayout(new BorderLayout());

//        //TEST TODO
//        final JPanel panel = new JPanel();
//        panel.setBackground(Color.orange);
//        panel.setBorder(BorderFactory.createLineBorder(Color.red));
//        panel.setPreferredSize(new Dimension(800, 600));

        //add new panel with GridBagLayout
        final JPanel testGBag = new JPanel(new GridBagLayout());
        testGBag.setBackground(Color.lightGray);
        testGBag.setPreferredSize(new Dimension(1200, 600));
        final JScrollPane scroll = new JScrollPane(testGBag);
        puzzlePanel.add(scroll, BorderLayout.CENTER);
        updatePuzzleField(puzzlePanel, testGBag, scroll, 5, 5);

        //Create a simple text input (for testing at the moment) and add actionlistener
        final JTextField rowInput = new JTextField(10);
        optionsPanel.add(rowInput);
        final JTextField colInput = new JTextField(10);
        optionsPanel.add(colInput);

        //Create ok button
        final JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    int rows = Integer.parseInt(rowInput.getText());
                    int cols = Integer.parseInt(colInput.getText());
                    if (rows <= 50 && cols <= 50) {
                        updatePuzzleField(puzzlePanel, testGBag, scroll, rows, cols);
                    } else {
                        JOptionPane.showMessageDialog(null, "Can be no more than 50");
                    }

                    System.out.println("Clicked OK");
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Not a number");
                }
            }
        });
        optionsPanel.add(okButton);

        //Create button to generate list
        final JButton generateButton = new JButton("Generate List");
        generateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                inputFieldsToIntArLi();
            }
        });
        optionsPanel.add(generateButton);

        //Create button to generate byte array
        final JButton generateBytesButton = new JButton("Generate Byte Array");
        generateBytesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                byte[] rowBytes = generateByteArray(rowIntArLi, maxRowSize, rowElements);
                byte[] colBytes = generateByteArray(colIntArLi, maxColSize, colElements);
                out = new byte[4 + rowBytes.length + colBytes.length];
                out[0] = (byte) rowElements;
                out[1] = (byte) maxRowSize;
                out[2] = (byte) colElements;
                out[3] = (byte) maxColSize;
                System.arraycopy(rowBytes, 0, out, 4, rowBytes.length);
                System.arraycopy(colBytes, 0, out, 4 + (out[0] * out[1]), colBytes.length);
                printGeneratedByteArray(out);
            }
        });
        optionsPanel.add(generateBytesButton);

        //Create send buttone
        final JButton sendButton = new JButton("Send the bytes");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                spi.sendByteArray(out);
            }
        });
        optionsPanel.add(sendButton);

        //Change some variables for the frame
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        setSize(1366, 768);

        //TODO Only for testing:
        byte[] in = new byte[]{45, 32, 9, 17};
        drawNonogram(in);

    }

    /**
     * Using the variables extracted from the input fields, generate a byte array that can be send to the fpga
     *
     * @param intArLi  the list from which the array will be generate
     * @param maxSize  max row/column size
     * @param elements amount of rows/columns
     * @return the generated byte array
     */
    private byte[] generateByteArray(List<List<Integer>> intArLi, int maxSize, int elements) {
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

    /**
     * Makes the byte array readable for humans
     *
     * @param bytes
     */
    public void printGeneratedByteArray(byte[] bytes) {
        String div = "---------------------------------------------------------------------------------------------";
        String hdiv = "\n=============================================================================================";
        int rowStart = 4;
        int colStart = 4 + (bytes[0] * bytes[1]);
        for (int i = 0; i < bytes.length; i++) {
            if (i == 0) {
                System.out.println(hdiv +
                        "\nTHE GENERATED BYTE ARRAY" + hdiv +
                        "\nRows: " + bytes[i] +
                        "\nMax elements per row: " + bytes[i + 1] +
                        "\nColumns: " + bytes[i + 2] +
                        "\nMax elements per column: " + bytes[i + 3] + hdiv + "\n" + div +
                        "\n\t\tpos.\tbyte\tval\tbuffer\n" + div
                );
            } else if (i == rowStart) {
                System.out.println(div + "\n\t\tRow list: " + rowIntArLi + "\n" + div);
            } else if (i == colStart) {
                System.out.println(div + "\n\t\tColumn list: " + colIntArLi + "\n" + div);
            }
            boolean buffer = false;
            if ((i >= rowStart) && (i < colStart)) {
                buffer = (bytes[i] == 0) && ((i - rowStart) % maxRowSize != 0);
            } else if ((i >= 4 + (bytes[0] * bytes[1]))) {
                buffer = (bytes[i] == 0) && ((i - colStart) % maxColSize != 0);
            }

            String s = String.format("%8s", Integer.toBinaryString(bytes[i] & 0xFF)).replace(' ', '0');
            System.out.println("byte:\t" + i + "\t" + s + "\t" + bytes[i] + "\t" + buffer + "");
        }
    }

    /**
     * Simple print byte array function
     * @param bytes to print
     */
    public void printReceivedByteArray(byte[] bytes) {
        String div = "---------------------------------------------------------------------------------------------";
        String hdiv = "\n=============================================================================================";
        for (int i = 0; i < bytes.length; i++) {
            if (i == 0) {
                System.out.println(hdiv + "\nTHE RECEIVED BYTE ARRAY" + hdiv + "\n" + div +
                        "\n\t\tpos.\tbyte\tval\n" + div
                );
            }
            String s = String.format("%8s", Integer.toBinaryString(bytes[i] & 0xFF)).replace(' ', '0');
            System.out.println("byte:\t" + i + "\t" + s + "\t" + bytes[i]);
        }
    }

    /**
     * Reads the input fields of the GUI and puts them into lists
     */
    private void inputFieldsToIntArLi() {
        System.out.println("Generating lists...");
        rowIntArLi.clear();
        colIntArLi.clear();
        maxRowSize = 0;
        maxColSize = 0;

        for (JTextField toBIntRow : rowJTFs) {
            List<Integer> intsOnOneRow = new ArrayList<Integer>();
            String rowString = toBIntRow.getText();
            rowString = rowString.trim();
            String[] splitRow = rowString.split("\\s+");
            ArrayList<String> splitRowList = new ArrayList<>(Arrays.asList(splitRow));
            for (int j = 0; j < splitRowList.size(); j++) {
                if (splitRowList.get(j).equals("")) {
                    splitRowList.remove(j);
                }
            }
            if (splitRowList.size() != 0) {
                for (int i = 0; i < splitRowList.size(); i++) {
                    if (splitRowList.size() > maxRowSize) {
                        maxRowSize = splitRowList.size();
                    }
                    try {
                        intsOnOneRow.add(Integer.parseInt(splitRowList.get(i)));
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Not a number in the row");
                    }
                }
            } else {
                if (maxRowSize == 0) {
                    maxRowSize = 1;
                }
                intsOnOneRow.add(0);
            }
            rowIntArLi.add(intsOnOneRow);

        }

        for (JTextField toBIntCol : colJTFs) {
            List<Integer> intsOnOneCol = new ArrayList<Integer>();
            String colString = toBIntCol.getText();
            colString = colString.trim();
            String[] splitCol = colString.split("\\s+");
            ArrayList<String> splitColList = new ArrayList<>(Arrays.asList(splitCol));
            for (int j = 0; j < splitColList.size(); j++) {
                if (splitColList.get(j).equals("")) {
                    splitColList.remove(j);
                }
            }
            if (splitColList.size() != 0) {
                for (int i = 0; i < splitColList.size(); i++) {
                    if (splitColList.size() > maxColSize) {
                        maxColSize = splitColList.size();
                    }
                    try {
                        intsOnOneCol.add(Integer.parseInt(splitColList.get(i)));
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Not a number in the col");
                    }
                }
            } else {
                if (maxColSize == 0) {
                    maxColSize = 1;
                }

                intsOnOneCol.add(0);
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
     * @param rows     amount of rows
     * @param cols     amount of cols
     */
    private void updatePuzzleField(JPanel puzzlePanel, JPanel testGBag, JScrollPane scroll, int rows, int cols) {

        puzzlePanel.removeAll();
        scroll.removeAll();
        testGBag = new JPanel(new GridBagLayout());
        testGBag.setBackground(Color.lightGray);
        testGBag.setPreferredSize(new Dimension(cols * 30 + 350, rows * 30 + 330));
        System.out.println((cols * 30 + 200) + " " + (rows * 30 + 250));
        scroll = new JScrollPane(testGBag);
        puzzlePanel.add(scroll, BorderLayout.CENTER);

//        puzzlePanel.removeAll();
//        testGBag = new JPanel(new GridBagLayout());
//        testGBag.setBackground(Color.lightGray);
//        puzzlePanel.add(testGBag);

        rowJTFs.clear();
        colJTFs.clear();
        
        //set dimensions and settings of top left panel
        GridBagConstraints cTL = new GridBagConstraints();
        final JPanel tLPanel = new JPanel();
        tLPanel.setBackground(Color.lightGray);
        cTL.weightx = 0;
        cTL.weighty = 0;
        cTL.gridwidth = 1;
        cTL.gridheight = 1;
        cTL.gridx = 0;
        cTL.gridy = 0;
        cTL.fill = cTL.BOTH;
        testGBag.add(tLPanel, cTL);

        //set dimensions and settings of mid panel
        GridBagConstraints cMid = new GridBagConstraints();
        final JPanel midPanel = new JPanel();
        midPanel.setBackground(Color.lightGray);
        cMid.ipady = rows * 17;
        cMid.ipadx = cols * 17;
        cMid.weightx = 0;
        cMid.weighty = 0;
        cMid.gridwidth = 1;
        cMid.gridheight = 1;
        cMid.gridx = 1;
        cMid.gridy = 1;
        cMid.fill = cMid.BOTH;
        midPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
        testGBag.add(midPanel, cMid);

        //set dimensions and settings of col panel
        GridBagConstraints cCol = new GridBagConstraints();
        final JPanel colPanel = new JPanel();
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

        //set dimensions and settings for row panel
        GridBagConstraints cRow = new GridBagConstraints();
        final JPanel rowPanel = new JPanel();
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

        //Add the input elements to the panels
        rowPanel.setLayout(new GridBagLayout());
        colPanel.setLayout(new GridBagLayout());
        JTextField jtf;
        cRow.ipady = 15;
        cRow.ipadx = 170;
        for (int i = 0; i < rows; i++) {
            jtf = new JTextField();
            cRow.gridy = i;
            jtf.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.gray));
            rowPanel.add(jtf, cRow);
            rowJTFs.add(jtf);
        }
        cCol.ipady = 130;
        cCol.ipadx = 30;
        for (int i = 0; i < cols; i++) {
            jtf = new JTextField();
            cCol.gridx = i;
            jtf.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.gray));
            colPanel.add(jtf, cCol);
            colJTFs.add(jtf);
        }

        //create the puzzle field
        midPanel.setLayout(new GridLayout(rows, cols));
        for (int i = 0; i < (rows * cols); i++) {
            final JPanel gridPanel = new JPanel();
            gridPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
            Random rand = new Random();
            float r = rand.nextFloat();
            float g = rand.nextFloat();
            float b = rand.nextFloat();
            Color clr = new Color(r, g, b);
            gridPanel.setBackground(clr);
            nonogramField.put(i, gridPanel);
            midPanel.add(gridPanel);
        }

        pack();
        setVisible(true);
        setSize(1366, 768);
    }

    /**
     * Given a byte array interpret the data and update the game board.
     * @param in data received from fpga
     */
    public void drawNonogram(byte[] in) {

        int pos = 0;
        byte bit = 0;
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j <= 6; j++) {
                if (pos < nonogramField.size()) {
                    bit = (byte) (in[i] >> j & 1);
                    if (bit == 0) {
                        nonogramField.get(pos).setBackground(Color.white);
                    } else if (bit == 1) {
                        nonogramField.get(pos).setBackground(Color.black);
                    }
                    pos++;
                }
            }
        }

        pack();
        setVisible(true);
        setSize(1366, 768);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainFrame2 fr = new MainFrame2();
                //Initialize the screen
                fr.createGUI();
            }
        });

    }

}

