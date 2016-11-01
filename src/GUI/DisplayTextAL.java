package GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class DisplayTextAL implements ActionListener{

    public void actionPerformed(ActionEvent evt) {
        String disp = "eyo " + evt.getActionCommand();
        JOptionPane.showMessageDialog(null, disp);
    }
}
