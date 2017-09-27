package nl.getthere.robo;

import java.util.Arrays;

import nl.getthere.robo.aangeleverd.mock.RoboMock;

public class RoboJar {

	private ICameraClient cameraClient = null;
	private ISerialClient serialClient = null;

	public RoboJar(boolean mockSerialClient) {
		if (mockSerialClient) {
			System.out.println("Running in mock mode");
			RoboMock mock = new RoboMock();
			this.cameraClient = mock;
			this.serialClient = mock;
		} else {
			System.out.println("Connect naar de camera client");
			this.cameraClient = new CameraClient(); //Connect to localhost
			System.out.println("Connect naar de serial client");
			this.serialClient = new SerialClient();
		}
	}

	public void go() {
		System.out.println("Search and destrory!");
		System.out.println("Camera connectie: " + Arrays.toString(this.getCameraClient().getPosition()));
		System.out.println("Get body distance: " + this.getSerialClient().getBodyDistance());
	}

	public static void main(String[] args) {

		new RoboJar(args.length > 0).go();
	}

	//Getters
	public ICameraClient getCameraClient() {
		return cameraClient;
	}

	public ISerialClient getSerialClient() {
		return serialClient;
	}
}
