package nl.getthere.robo;

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
public class SerialClient implements ISerialClient {

	public SerialClient() {
		//		System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");"
		//TODO: connect
	}

	@Override
	public void setRightSpeed(int val) {
		//const byte cMRF = 0x03;    -  Motor rechts naar voren ( daarna byte met snelheid )
		//const byte cMRB = 0x04;    -  Motor rechts naar achter (daarna byte met snelheid )

	}

	@Override
	public void setLeftSpeed(int val) {
		//	const byte cMLF = 0x01;    -  Motor links naar voren  ( daarna byte met snelheid ) 
		//	const byte cMLB = 0x02;    -  Motor links naar achter ( daarna byte met snelheid )

	}

	@Override
	public void fireRocket() {
		//const byte cFire = 0x05;      - Afvuren van raket
	}

	@Override
	public void setServoDirection(int degrees) {
		//	const byte cServo = 0x06;  -  Aansturen van servo  (daarna byte met servo graden ) 0 t/m 180
	}

	@Override
	public void requestGunDistance() {
		//	const byte cPing = 0x07;     -  afstand bepalen sonor op de server
	}

	@Override
	public void fullStop() {
		//	const byte cStop = 0x08;    - Full Stop
	}

	@Override
	public int getBodyDistance() {
		//		A0    -  daarna twee bytes met afstand   | deze waarde ontvang je 60 ms
		return -1;
	}

	@Override
	public int getGunDistance() {
		//		B0   -   daarna twee bytes met afstand    | deze waarde ontvang je na commando 0x07
		return -1;
	}
}
