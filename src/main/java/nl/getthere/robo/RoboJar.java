package nl.getthere.robo;

public class RoboJar {

	private CameraClient cameraClient = new CameraClient(); //Connect to localhost
	private SerialClient serialClient = new SerialClient();

	public void go() {
		//TODO: search and destroy! 
	}

	public static void main(String[] args) {
		new RoboJar().go();
	}
}
