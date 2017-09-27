package nl.getthere.robo;

public class RoboJar {

	private CameraClient cameraClient = new CameraClient();
	private SerialClient serialClient = null;

	public RoboJar() {
		System.out.println("Connect naar de camera client");
		this.cameraClient = new CameraClient(); //Connect to localhost
		System.out.println("Connect naar de serial client");
		this.serialClient = new SerialClient();
	}

	public void go() {
		System.out.println("Search and destrory!");
		System.out.println("Camera connectie: "+this.getCameraClient().getCoords());
		System.out.println("Get body distance: "+ this.getSerialClient().getBodyDistance());
	}

	public static void main(String[] args) {
		new RoboJar().go();
	}

	//Getters
	public CameraClient getCameraClient() {
		return cameraClient;
	}

	public SerialClient getSerialClient() {
		return serialClient;
	}
}
