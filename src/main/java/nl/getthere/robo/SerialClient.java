package nl.getthere.robo;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import jdk.internal.util.xml.impl.Input;
import nl.getthere.robo.aangeleverd.mock.TwoWaySerialComm;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 *
 * @author woelen
 */
public class SerialClient  {
	InputStream in;
	OutputStream out;

	public SerialClient() {

		try {
			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier("/dev/ttyS80");
			if (portIdentifier.isCurrentlyOwned()) {
				System.out.println("Error: Port is currently in use");
			} else {
				int timeout = 2000;
				CommPort commPort = portIdentifier.open(this.getClass().getName(), timeout);
				if (commPort instanceof SerialPort) {
					SerialPort serialPort = (SerialPort) commPort;
					serialPort.setSerialPortParams(115200, 8, 1, 0);
					this.in = serialPort.getInputStream();
					this.out = serialPort.getOutputStream();

					(new Thread(new TwoWaySerialComm.SerialReader(in))).start();

				} else {
					System.out.println("Error: Only serial ports are handled by this example.");
				}
			}
		} catch (Exception e) {}

	}

	@Override
	public void setRightSpeed(int val) throws IOException {
		if (val > 0) {
			out.write((byte) 0x03);
		} else {
			out.write((byte) 0x04);
		}
		out.write((byte) Math.abs(val));
	}

	@Override
	public void setLeftSpeed(int val) throws IOException {
		if (val > 0) {
			out.write((byte) 0x01);
		} else {
			out.write((byte) 0x02);
		}
		out.write((byte) Math.abs(val));

	}

	@Override
	public void fireRocket() throws IOException  {
		out.write((byte) 0x05);
	}

	@Override
	public void setServoDirection(int degrees) throws IOException  {
		if (degrees < 0 || degrees > 180) {
			throw new IllegalArgumentException();
		}
		out.write((byte) 0x06);
		out.write((byte) degrees);
	}

	@Override
	public void requestGunDistance() throws IOException {
		//	const byte cPing = 0x07;     -  afstand bepalen sonor op de server
	}

	@Override
	public void fullStop() throws IOException  {
		out.write((byte) 0x08);
	}

	@Override
	public int getBodyDistance() throws IOException  {
		//		A0    -  daarna twee bytes met afstand   | deze waarde ontvang je 60 ms
		return -1;
	}

	@Override
	public int getGunDistance() throws IOException  {
		//		B0   -   daarna twee bytes met afstand    | deze waarde ontvang je na commando 0x07
		return -1;
	}
}
