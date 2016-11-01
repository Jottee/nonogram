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
            console.println("IOException while creating device \n" + e);
        }

        byte[] bytes = new byte[4];
        bytes[0] = (byte) 0xff;
        bytes[1] = (byte) 0x0f;
        bytes[2] = (byte) 0x11;
        bytes[3] = (byte) 0xe4;

        for (int i = 0; i < 4; i++) {
            try {
                spi.write(bytes[i]);
                Thread.sleep(1000);
            } catch (IOException e) {
               console.println("IOException while writing bytes \n" + e);
            } catch (InterruptedException e) {
                console.println("InterruptedException while sleeping \n" + e);
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
