package nl.getthere.robo.commandparameters;

import java.util.Optional;

public class LookAroundCommandParameters implements CommandParameters {

	private int initialDirection;
	private Optional<Integer> minDirection = Optional.empty();
	private Optional<Integer> maxDirection = Optional.empty();
	private Optional<Integer> lastScanDirection = Optional.empty();
	private boolean rotateClockWise = true;

	public LookAroundCommandParameters(int initialDirection, boolean rotateClockWise) {
		this.initialDirection = initialDirection;
		this.rotateClockWise = rotateClockWise;
	}

	public LookAroundCommandParameters(int initialDirection, int minDirection, int maxDirection) {
		this.initialDirection = initialDirection;
		this.minDirection = Optional.of(minDirection);
		this.maxDirection = Optional.of(maxDirection);
	}

	@Override
	public String toString() {
		String returnString = "" + this.getInitialDirection();
		if (this.getMinDirection().isPresent()) {
			returnString += ", minDir: " + this.getMinDirection().get();
		}
		if (this.getMaxDirection().isPresent()) {
			returnString += "x maxDir: " + this.getMaxDirection().get();
		}
		return returnString;
	}

	//Getters
	public int getInitialDirection() {
		return initialDirection;
	}

	public Optional<Integer> getMinDirection() {
		return minDirection;
	}

	public Optional<Integer> getMaxDirection() {
		return maxDirection;
	}

	public Optional<Integer> getLastScanDirection() {
		return lastScanDirection;
	}

	public boolean getRotateClockWise() {
		return rotateClockWise;
	}

	//Setters
	public void setLastScanDirection(int lastScanDirection) {
		this.lastScanDirection = Optional.of(lastScanDirection);
	}
}
