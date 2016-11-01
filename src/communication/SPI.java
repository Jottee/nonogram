package communication;

import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.util.Console;

import java.io.IOException;

public class SPI {

    public static SpiDevice spi = null;
    protected static final Console console = new Console();

    public static void main(String[] args) {

        console.promptForExit();

        try {
            spi = SpiFactory.getInstance(SpiChannel.CS0, SpiDevice.DEFAULT_SPI_SPEED, SpiDevice.DEFAULT_SPI_MODE);
        } catch (IOException e) {
            System.out.println("IOException while creating device \n" + e);
        }

        byte byte1 = (byte)0xff;
        for (int i = 0; i < 4; i++) {
            try {
                spi.write(byte1);
            } catch (IOException e) {
                System.out.println("IOException while writing bytes \n" + e);
            }
        }

//        while (console.isRunning()) {
//            read();
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                System.out.println("InterruptedException while sleeping \n" + e);
//            }
//        }
    }



}
