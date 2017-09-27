package nl.getthere.robo;

public class Position {

	private int x;
	private int y;
	private int direction;

	public Position(int[] input) {
		this.x = input[0];
		this.y = input[1];
		this.direction = input[2];
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getDirection() {
		return direction;
	}

	@Override
	public String toString() {
		return x + ", " + y + ", " + direction;
	}
}
