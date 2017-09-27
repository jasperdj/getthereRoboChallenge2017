package nl.getthere.robo.aangeleverd;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 *
 * @author woelen
 */
public class RoboClnt implements AutoCloseable {
	public static final int ROBO_PORT = 11111;

	String host = "127.0.0.1";
	Socket socket;
	DataOutputStream out;
	BufferedInputStream in;

	public RoboClnt() {
		socket = null;
		connect();
	}

	public RoboClnt(String host) {
		socket = null;
		this.host = host;
		connect();
	}

	private void connect() {
		if (socket != null) {
			// Already connected, do nothing.
			return;
		}

		try {
			socket = new Socket(host, ROBO_PORT);
			socket.setSoTimeout(30000);
			out = new DataOutputStream(socket.getOutputStream());
			in = new BufferedInputStream(socket.getInputStream());
		} catch (IOException ex) {
			ex.printStackTrace();
			close();
		}
	}

	public void close() {
		if (socket != null) {
			try {
				socket.close();
			} catch (Exception ign) {
			}
		}
		socket = null;
	}

	private int ntohl(byte[] b, int offset) {
		int res = 0;
		for (int i = 0; i < 4; i++) {
			res <<= 8;
			res |= b[i + offset] & 0xff;
		}
		return res;
	}

	public int[] getCoords() {
		if (socket == null) {
			return null;
		}

		int[] val = null;
		try {
			byte[] buf = new byte[16];
			out.writeBytes("X\000");

			int i = 0;
			for (; i < 16; i++) {
				int c = in.read();
				if (c < 0) {
					break;
				}
				buf[i] = (byte) c;
			}

			if (i == 16) {
				// We could read all bytes, all is OK and we
				// construct output for the calling client.
				val = new int[4];
				val[0] = ntohl(buf, 0);
				val[1] = ntohl(buf, 4);
				val[2] = -ntohl(buf, 8);
				val[3] = ntohl(buf, 12);
			}
			return val;
		} catch (IOException ex) {
			// Nothing to be done here
			ex.printStackTrace();
		}
		System.err.println("return null");
		return null;
	}

	public static void main(String[] args) throws InterruptedException {
		System.out.println("Running RoboClnt");
		try (RoboClnt r = new RoboClnt("192.168.1.33")) {
			while (true) {
				int[] val = r.getCoords();
				for (int v : val) {
					System.out.print(v + " ");
				}
				System.out.println();
				Thread.sleep(100);
			}
		}
	}
}
