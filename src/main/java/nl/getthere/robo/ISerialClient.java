package nl.getthere.robo;

public interface ISerialClient {

	void setRightSpeed(int val);

	void setLeftSpeed(int val);

	void fireRocket();

	void setServoDirection(int degrees);

	void requestGunDistance();

	void fullStop();

	int getBodyDistance();

	int getGunDistance();
}
