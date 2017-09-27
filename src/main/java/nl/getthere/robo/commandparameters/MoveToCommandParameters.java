package nl.getthere.robo.commandparameters;

public class MoveToCommandParameters implements CommandParameters {

	private int targetX;
	private int targetY;

	public MoveToCommandParameters(int targetX, int targetY) {
		this.targetX = targetX;
		this.targetY = targetY;
	}

	public int getTargetX() {
		return targetX;
	}

	public int getTargetY() {
		return targetY;
	}

	@Override
	public String toString() {
		return targetX + ", " + targetY;
	}
}
