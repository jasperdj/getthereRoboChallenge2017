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
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author woelen
 */
public class SerialClient implements ISerialClient  {
	InputStream in;
	OutputStream out;
	RobotData robotData;

	class RobotData {
		private AtomicInteger bodyDistance;
		private AtomicInteger gunDistance;

		public RobotData() {
			bodyDistance.set(0);
			gunDistance.set(0);
		}

		public AtomicInteger getBodyDistance() {
			return bodyDistance;
		}

		public void setBodyDistance(AtomicInteger bodyDistance) {
			this.bodyDistance = bodyDistance;
		}

		public AtomicInteger getGunDistance() {
			return gunDistance;
		}

		public void setGunDistance(AtomicInteger gunDistance) {
			this.gunDistance = gunDistance;
		}
	}

	public SerialClient() {
		RobotData robotData = new RobotData();

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

					(new Thread(new SerialReader(in, robotData))).start();

				} else {
					System.out.println("Error: Only serial ports are handled by this example.");
				}
			}
		} catch (Exception e) {}

	}

	public static class SerialReader implements Runnable {

		InputStream in;
		RobotData robotData;

		public SerialReader(InputStream in, RobotData robotData) {
			this.in = in;
			this.robotData = robotData;
		}

		public void run() {
			byte[] buffer = new byte[1024];
			int len = -1;
			try {
				while ((len = this.in.read(buffer)) > -1) {
					System.out.print(new String(buffer, 0, len));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void write(Byte data) {
		try {
			out.write(data);
		} catch (IOException e) {
			writeAgain(data, 3);
		}
	}

	private void writeAgain(Byte data, int again) {
		try {
			out.write(data);
		} catch (IOException e) {
			if (again > 0) {
				writeAgain(data, again-1);
			}
		}
	}
	
	@Override
	public void setRightSpeed(int val) {
		if (val > 0) {
			write((byte) 0x03);
		} else {
			write((byte) 0x04);
		}
		write((byte) Math.abs(val));
	}

	@Override
	public void setLeftSpeed(int val) {
		if (val > 0) {
			write((byte) 0x01);
		} else {
			write((byte) 0x02);
		}
		write((byte) Math.abs(val));

	}

	@Override
	public void fireRocket()  {
		write((byte) 0x05);
	}

	@Override
	public void setServoDirection(int degrees)  {
		if (degrees < 0 || degrees > 180) {
			throw new IllegalArgumentException();
		}
		write((byte) 0x06);
		write((byte) degrees);
	}

	@Override
	public void requestGunDistance() {
		//	const byte cPing = 0x07;     -  afstand bepalen sonor op de server
		write((byte) 0x07);
	}

	@Override
	public void fullStop()  {
		write((byte) 0x08);
	}

	@Override
	public int getBodyDistance()  {
		//		A0    -  daarna twee bytes met afstand   | deze waarde ontvang je 60 ms
		return -1;
	}

	@Override
	public int getGunDistance()  {
		//		B0   -   daarna twee bytes met afstand    | deze waarde ontvang je na commando 0x07
		return -1;
	}
}
