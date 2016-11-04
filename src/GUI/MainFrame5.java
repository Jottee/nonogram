package GUI;

import communication.SPI2;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.List;
import java.util.concurrent.RunnableFuture;

public class MainFrame5 extends JFrame {


    private List<List<JTextField>> rowJTFs = new ArrayList<>();
    private List<List<JTextField>> colJTFs = new ArrayList<>();
    private List<List<Integer>> rowIntArLi = new ArrayList<>();
    private List<List<Integer>> colIntArLi = new ArrayList<>();
    private int maxRowSize = 0;
    private int maxColSize = 0;
    private int rowElements = 0;
    private int colElements = 0;
    private Map<Integer, JPanel> nonogramField = new HashMap<>();
    private SPI2 spi;
    private byte[] out;
    private int currentRowInputs = 0;
    private int currentColInputs = 0;

    public enum Input {ROW, COLUMN}

    ;

    public static int PPW = 350;
    public static int PPH = 360;
    public static int MAX_ALLOWED_COLUMNS_ROWS = 50;
    public static int MAX_ALLOWED_INPUTS = Math.floorDiv(MAX_ALLOWED_COLUMNS_ROWS, 2);

    /**
     * Constructor of the frame, calls createGUI which initializes the elements of the GUI
     */
    private MainFrame5() {
        super("Nonogram Puzzle Solver");
        //spi = new SPI2(this);
        //new Thread(spi).start();
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

        //add new panel with GridBagLayout
        final JPanel testGBag = new JPanel(new GridBagLayout());
        testGBag.setBackground(Color.lightGray);
        testGBag.setPreferredSize(new Dimension(1200, 600));
        final JScrollPane scroll = new JScrollPane(testGBag);
        puzzlePanel.add(scroll, BorderLayout.CENTER);
        updatePuzzleField(puzzlePanel, testGBag, scroll, 5, 5);

        //Create a simple text Input (for testing at the moment) and add actionlistener
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
                    if (rows <= MAX_ALLOWED_COLUMNS_ROWS && cols <= MAX_ALLOWED_COLUMNS_ROWS) {
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
                inputFieldsToIntArLi(Input.ROW);
                inputFieldsToIntArLi(Input.COLUMN);
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
     * Using the variables extracted from the Input fields, generate a byte array that can be send to the fpga
     *
     * @param intArLi  the list from which the array will be generate
     * @param maxSize  max row/column size
     * @param elements amount of rows/columns
     * @return the generated byte array
     */
    private byte[] generateByteArray(List<List<Integer>> intArLi, int maxSize, int elements) {
        byte[] bytes = new byte[elements * maxSize];
        System.out.println("elesize = " + elements + " " + maxSize);
        //System.out.println("list = " + intArLi + " max size = " + maxSize);
        int c = 0;
        for (List<Integer> intsOnOne : intArLi) {
            for (Integer i : intsOnOne) {
                byte[] intAsBytes = ByteBuffer.allocate(4).putInt(i).array();
                String s1 = String.format("%8s", Integer.toBinaryString(intAsBytes[3] & 0xFF)).replace(' ', '0');
                System.out.println("byte:\t" + c + "\t" + s1 + "\t" + intAsBytes[3] + "\t(i = " + i + ")");
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
     *
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
     * Reads the Input fields of the GUI and puts them into lists
     */
    private void inputFieldsToIntArLi(Input in) {
        System.out.println("Generating list...");
        List<List<JTextField>> inputList = new ArrayList<>();
        if (in == Input.ROW) {
            rowIntArLi.clear();
            inputList = rowJTFs;
        } else if (in == Input.COLUMN) {
            colIntArLi.clear();
            inputList = colJTFs;
        }


        for (List<JTextField> JTFs : inputList) { //for every row
            List<Integer> intsPer = new ArrayList<>();
            for (int i = 0; i < JTFs.size(); i++) {  //for every Input on that row
                JTextField jtf = JTFs.get(i);
                if (jtf.isEditable()) {
                    String str = jtf.getText();
                    str = str.trim();
                    String[] split = str.split("\\s+");
                    ArrayList<String> splitList = new ArrayList<>(Arrays.asList(split));
                    for (int j = 0; j < splitList.size(); j++) {
                        if (splitList.get(j).equals("")) {
                            splitList.remove(j);
                        }
                    }
                    if (splitList.size() != 0) {
                        if (splitList.size() == 1) {
                            try {
                                intsPer.add(Integer.parseInt(splitList.get(0)));
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(null, "Not a number in the thingy");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Only one number per Input field allowed!");
                        }
                    } else {
                        intsPer.add(0);
                    }
                }
            }
            if (in == Input.ROW) {
                rowIntArLi.add(intsPer);
                if (intsPer.size() > maxRowSize) {
                    maxRowSize = intsPer.size();
                }
            } else if (in == Input.COLUMN) {
                colIntArLi.add(intsPer);
                if (intsPer.size() > maxColSize) {
                    maxColSize = intsPer.size();
                }
            }

        }

        rowElements = rowIntArLi.size();
        colElements = colIntArLi.size();

        System.out.println("List generated.");

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
        testGBag.setPreferredSize(new Dimension(cols * 30 + PPW, rows * 30 + PPH));
        System.out.println((cols * 30 + 200) + " " + (rows * 30 + 250));
        scroll = new JScrollPane(testGBag);
        puzzlePanel.add(scroll, BorderLayout.CENTER);

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

        //set dimensions and settings for row panel666666666666
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

        generateInputFields(rows, cols, colPanel, rowPanel);


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

    private void generateInputFields(int rows, int cols, JPanel colPanel, JPanel rowPanel) {
        GridBagConstraints cCol = new GridBagConstraints();
        GridBagConstraints cRow = new GridBagConstraints();
        rowPanel.setLayout(new GridBagLayout());
        colPanel.setLayout(new GridBagLayout());
        JTextField jtf;

        //Add the Input elements to the panels
        currentRowInputs = 1;
        cRow.ipady = 15;
        for (int i = 0; i < rows; i++) { //for every row
            List<JTextField> singleRowJTFs = new ArrayList<>();
            jtf = new JTextField(3);
            cRow.gridy = i;
            cRow.gridx = MAX_ALLOWED_INPUTS - 1;
            jtf.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.gray));

            jtf.getDocument().addDocumentListener(
                    new TextFieldUpdateListener(this, jtf, rows, cols, colPanel, rowPanel));

            rowPanel.add(jtf, cRow);
            singleRowJTFs.add(jtf);
            rowJTFs.add(singleRowJTFs);
        }

        currentColInputs = 1;
        cCol.ipady = 15;
        for (int i = 0; i < cols; i++) {
            List<JTextField> singleColJTFs = new ArrayList<>();
            jtf = new JTextField(3);
            cCol.gridx = i;
            cCol.gridy = MAX_ALLOWED_INPUTS - 1;
            jtf.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.gray));

            jtf.getDocument().addDocumentListener(
                    new TextFieldUpdateListener(this, jtf, rows, cols, colPanel, rowPanel));

            colPanel.add(jtf, cCol);
            singleColJTFs.add(jtf);
            colJTFs.add(singleColJTFs);
        }


    }

    public void insertUpdateInputFields(JTextField jtf, int rows, int cols, JPanel colPanel, JPanel rowPanel) {


        List<JTextField> JTFs = new ArrayList<>();
        int index = 0;
        int indexOfJTF = 0;
        for (List<JTextField> singleRowJTFs : rowJTFs) {
            if (singleRowJTFs.contains(jtf)) {
                JTFs = singleRowJTFs;
                index = rowJTFs.indexOf(singleRowJTFs);
                indexOfJTF = singleRowJTFs.indexOf(jtf);
            }
        }
        if (currentRowInputs <= MAX_ALLOWED_INPUTS && jtf.getText().length() == 1) {
            if (countEnabled(JTFs) == JTFs.size()) {
                //Add the Input elements to the panels
                GridBagConstraints cRow = new GridBagConstraints();
                cRow.ipady = 15;
                for (int i = 0; i < rows; i++) { //for every row
                    JTextField jtfNew = new JTextField(3);
                    cRow.gridy = i;
                    cRow.gridx = MAX_ALLOWED_INPUTS - (1 + currentRowInputs);
                    jtfNew.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.gray));

                    jtfNew.getDocument().addDocumentListener(
                            new TextFieldUpdateListener(this, jtfNew, rows, cols, colPanel, rowPanel));

                    rowPanel.add(jtfNew, cRow);

                    if (i != index) {
                        jtfNew.setEditable(false);
                    }
                    rowJTFs.get(i).add(jtfNew);
                }
                currentRowInputs++;
            } else {
                rowJTFs.get(index).get(indexOfJTF + 1).setEditable(true);
            }
        }


        //Change some variables for the frame
        pack();
        setVisible(true);
        setSize(1366, 768);

    }

    public void removeUpdateInputFields(JTextField jtf, int rows, int cols, JPanel colPanel, JPanel rowPanel) {

        List<JTextField> JTFs = new ArrayList<>();
        int index = 0;
        int indexOfJTF = 0;
        for (List<JTextField> singleRowJTFs : rowJTFs) {
            if (singleRowJTFs.contains(jtf)) {
                JTFs = singleRowJTFs;
                index = rowJTFs.indexOf(singleRowJTFs);
                indexOfJTF = singleRowJTFs.indexOf(jtf);
            }
        }
        if (currentRowInputs > 0 && jtf.getText().equals("")) {
            System.out.println("hurray");
            if (onlyOneFullyEnabled(rowJTFs) && countEnabled(JTFs) == JTFs.size() && indexOfJTF > JTFs.size() - 2) {
                System.out.println("for the köning");
                for (List<JTextField> singleRowJTFs : rowJTFs) {
                    rowPanel.remove(singleRowJTFs.get(singleRowJTFs.size() - 1));
                    singleRowJTFs.remove(singleRowJTFs.size() - 1);

                }
            } else if (indexOfJTF <= JTFs.size() - 2) {
                shiftAndRemove(indexOfJTF, JTFs, jtf);
                System.out.println("we have a new emperor!");

            } else {
                System.out.println("vor die queen!");
                JTFs.get(indexOfJTF + 1).setEditable(false);
            }
        }

        //Change some variables for the frame
        pack();
        setVisible(true);
        setSize(1366, 768);
    }

    public void shiftAndRemove(int indexOfJTF, List<JTextField> JTFs, JTextField jtf) {
        Runnable doShiftAndRemove = new Runnable() {
            public void run() {

//                try {
//                    Thread.sleep(1000);
//                    System.out.println("slept");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                if (countEmptyTiles(JTFs) > 1) {
                    System.out.println(countEmptyTiles(JTFs));
                    System.out.println("he ded!");
                    for (int i = JTFs.size() - 1; i >= 0; i--) {
                        if (JTFs.get(i).isEditable()) {
                            System.out.println("the emperor is dead!");
                            JTFs.get(i).setEditable(false);
                            break;
                        }
                    }
                    for (int j = indexOfJTF; j < JTFs.size() - 1; j++) {
                        JTFs.get(j).setText(JTFs.get(j + 1).getText());
                    }
                }
            }
        };
        SwingUtilities.invokeLater(doShiftAndRemove);
    }

    public int countEmptyTiles(List<JTextField> list) {
        int result = 0;
        for (JTextField jtf : list) {
            if (jtf.isEditable() && jtf.getText().equals("")) {
                result++;
            }
        }
        return result;
    }


    public int countEnabled(List<JTextField> list) {
        int result = 0;

        for (JTextField jtf : list) {
            if (jtf.isEditable()) {
                result++;
            }
        }
        return result;
    }

    public boolean onlyOneFullyEnabled(List<List<JTextField>> list) {
        int c = 0;

        for (List<JTextField> JFTs : list) {
            if (JFTs.get(JFTs.size() - 1).isEditable()) {
                c++;
            }
        }
        return !(c > 1);
    }


    /**
     * Given a byte array interpret the data and update the game board.
     *
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
                MainFrame5 fr = new MainFrame5();
                //Initialize the screen
                fr.createGUI();
            }
        });

    }

}

