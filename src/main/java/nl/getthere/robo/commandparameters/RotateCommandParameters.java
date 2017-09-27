package nl.getthere.robo.commandparameters;

public class RotateCommandParameters implements CommandParameters {

	private int targetRadial;

	public RotateCommandParameters(int targetRadial) {
		this.targetRadial = targetRadial;
	}
	
	public int getTargetRadial() {
		return targetRadial;
	}
	@Override
	public String toString() {
		return ""+targetRadial;
	}
}
