package communication;

import GUI.MainFrame;
import GUI.MainFrame2;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.util.Console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class SPI implements Runnable {

    public static final byte DUMMY_BYTE = (byte)0;

    private static SpiDevice spi = null;
    private static final Console console = new Console();
    private boolean isWaiting = true;
    private boolean isReceiving = false;
    private byte[] check = new byte[]{DUMMY_BYTE, DUMMY_BYTE};
    private List<Byte> in = new ArrayList<>();
    private MainFrame fr;

    public SPI(MainFrame fr) {
        this.fr = fr;
    }

    public static void main(String[] args) {



        byte[] bytes = new byte[4];
        bytes[0] = (byte) 0xff;
        bytes[1] = (byte) 0x0f;
        bytes[2] = (byte) 0x11;
        bytes[3] = (byte) 0xe4;

        for (int i = 0; i < 4; i++) {
            try {
                byte[] in = spi.write(bytes[i]);
                for (int j = 0; j < in.length; j++) {
                    System.out.println(String.format("%8s", Integer.toBinaryString(bytes[i] & 0xFF)).replace(' ', '0'));
                }
                sleep(1000);
            } catch (IOException e) {
                console.println("IOException while writing bytes \n" + e);
            } catch (InterruptedException e) {
                console.println("InterruptedException while sleeping \n" + e);
            }
        }
    }

    public void run() {
        console.promptForExit();

        try {
            spi = SpiFactory.getInstance(SpiChannel.CS0, SpiDevice.DEFAULT_SPI_SPEED, SpiDevice.DEFAULT_SPI_MODE);
        } catch (IOException e) {
            console.println("IOException while creating device \n" + e);
        }

        console.println("isWaiting = true\t\tisReceiving = false\nCurrently waiting for user input");

        while (console.isRunning()) {
            if (isWaiting) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    console.println("InterruptedException while sleeping \n" + e);
                }
            } else if (!isWaiting){
                byte[] rec;
                if (!isReceiving) {
                    try {
                        rec = spi.write(check);
                        if (rec.length >= 1 && rec[0] == 0xff) {
                            isReceiving = true;
                            console.println("isWaiting = false\t\tisReceiving = true\n" +
                                    "Currently receiving data and waiting for FPGA to send 0xff once all data is sent");
                        }
                        Thread.sleep(1000);
                    } catch (IOException e) {
                        console.println("IOException while writing bytes \n" + e);
                    } catch (InterruptedException e1) {
                        console.println("InterruptedException while sleeping \n" + e1);
                    }
                } else if (isReceiving) {
                    try {
                        rec = spi.write(DUMMY_BYTE);
                        if (rec.length >= 1 && rec[0] == 0xff) {
                            byte[] draw = new byte[in.size()];
                            for (int i = 0; i < in.size(); i++) {
                                draw[i] = in.get(i);
                            }
                            fr.drawNonogram(draw);
                            fr.printReceivedByteArray(draw);
                            isReceiving = false;
                            isWaiting = true;

                            console.println("isWaiting = true\t\tisReceiving = false\n" +
                                    "Currently waiting for user input");
                        } else {
                            in.add(rec[0]);
                        }
                    } catch (IOException e) {
                        console.println("IOException while writing bytes \n" + e);
                    }
                }
            }
        }
    }

    public void sendByteArray(byte[] out) {

        try {
            spi.write(out, 0, out.length);
        } catch (IOException e) {
            console.println("IOException while writing bytes \n" + e);
        }

        isWaiting = false;

        console.println("isWaiting = false\t\tisReceiving = false\n" +
                "Currently waiting for FPGA to send 0xff after it finishes computing");
    }
}
