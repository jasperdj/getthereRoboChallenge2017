package nl.getthere.robo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

import javax.crypto.spec.OAEPParameterSpec;

import nl.getthere.robo.aangeleverd.mock.RoboMock;
import nl.getthere.robo.commandparameters.CommandParameters;
import nl.getthere.robo.commandparameters.LookAroundCommandParameters;
import nl.getthere.robo.commandparameters.MoveToCommandParameters;
import nl.getthere.robo.commandparameters.RotateCommandParameters;

public class RoboJar {

	private ICameraClient cameraClient = null;
	private ISerialClient serialClient = null;

	private Stack<CommandWithParams> commandStack = new Stack<>();

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
		System.out.println("Search and destroy!");
		System.out.println("Camera connectie: " + this.getCameraClient().getPosition());
		System.out.println("Get body distance: " + this.getSerialClient().getBodyDistance());
		this.getCommandStack().add(new CommandWithParams(Command.MOVE_TO, new MoveToCommandParameters(0, 0)));

		mainLoop: while (!this.getCommandStack().isEmpty()) {
			switch (this.getCommandStack().peek().getCommand()) {
				case MOVE_TO:
					this.handleMoveToCommand();
					break;
				//case ROTATE:
				//	this.handleRotateCommand();
				//	break;
				case LOOK_AROUND:
					this.handleLookAroundCommand();
				default:
					throw new RuntimeException("Don't know how to run command " + this.getCommandStack().peek().getCommand());
			}
		}

		System.out.println("Command stack is empty. Exit...");
	}

	private void handleLookAroundCommand() {
		LookAroundCommandParameters parameters = (LookAroundCommandParameters) this.getCommandStack().peek().getParameters();

		lookAroundLoop: while (true) {
			//Determine direction
			Position position = this.getCameraClient().getPosition();
			System.out.println("Moving from '" + position + "' to '" + parameters + "'");

			//Check if we need to scan or rotate
			if (parameters.getLastScanDirection().isPresent() && Math.abs(parameters.getLastScanDirection().get() - position.getDirection()) > 100) {
				//Scan
			} else {
				//Rotate further using the rotateClockWise param
			}
		}
	}

	/*
	private void handleRotateCommand() {
		RotateCommandParameters parameters = (RotateCommandParameters) this.getCommandStack().peek().getParameters();

		rotateLoop: while (true) {
			Position position = this.getCameraClient().getPosition();
			System.out.println("Rotating from '" + position + "' to '" + parameters + "'");

			//Check if the radial is quite on target
			if (Math.abs(parameters.getTargetRadial() - position.getDirection()) < 50) {
				System.out.println("Rotate success to '" + parameters + "' (current position '" + position + "')");
				this.getCommandStack().pop();
				break rotateLoop;
			}

			//Rotate
			boolean rotateClockWise = false;
			int rotateSpeed = 15;
			if (rotateClockWise) {
				this.getSerialClient().setLeftSpeed(rotateSpeed);
				this.getSerialClient().setRightSpeed(-rotateSpeed);
			} else {
				this.getSerialClient().setLeftSpeed(-rotateSpeed);
				this.getSerialClient().setRightSpeed(rotateSpeed);
			}

			//Sleep so that the robot can move
			try {
				Thread.sleep(500);
			} catch (InterruptedException ign) {
			}
		}
	}*/

	private void handleMoveToCommand() {
		MoveToCommandParameters parameters = (MoveToCommandParameters) this.getCommandStack().peek().getParameters();

		moveLoop: while (true) {
			//Determine direction
			Position position = this.getCameraClient().getPosition();
			System.out.println("Moving from '" + position + "' to '" + parameters + "'");

			//Are we there yet?
			boolean onXTarget = Math.abs(position.getX() - parameters.getTargetX()) < 20;
			boolean onYTarget = Math.abs(position.getY() - parameters.getTargetY()) < 20;
			if (onXTarget && onYTarget) {
				System.out.println("Target position ('" + parameters + "') reached ('" + position + "')");
				//Target position reached
				this.getCommandStack().pop();
				break moveLoop;
			}

			//Determine direction to travel to
			int targetRadial = getTargetRadial(position, parameters.getTargetX(), parameters.getTargetY());
			boolean needToRotate = Math.abs(targetRadial - position.getDirection()) > 50;
			if (needToRotate) {
				boolean rotateClockWise = false; //TODO welke kant op roteren?
				int rotateSpeed = 15; //TODO: welke speed roteren?
				
				System.out.println("Need to rotate to " + targetRadial+", "+(rotateClockWise ? "clockWise" : "coounterClockWise")+", at speed "+rotateSpeed);
				//Do rotation
				if (rotateClockWise) {
					this.getSerialClient().setLeftSpeed(rotateSpeed);
					this.getSerialClient().setRightSpeed(-rotateSpeed);
				} else {
					this.getSerialClient().setLeftSpeed(-rotateSpeed);
					this.getSerialClient().setRightSpeed(rotateSpeed);
				}

				//Sleep so that the robot can move
				try {
					Thread.sleep(500);
				} catch (InterruptedException ign) {
				}
			} else {
				//Move to position
				int targetSpeed = 200;

				//If close by, move slower
				if ((Math.abs(position.getX() - parameters.getTargetX()) < 50) || Math.abs(position.getY() - parameters.getTargetY()) < 50) {
					targetSpeed = 100;
				}
				System.out.println("Move forward at speed " + targetSpeed);
				this.getSerialClient().setLeftSpeed(targetSpeed);
				this.getSerialClient().setRightSpeed(targetSpeed);

				//Sleep so that the robot can move
				try {
					Thread.sleep(500);
				} catch (InterruptedException ign) {
				}
			}
		}
	}

	public int getTargetRadial(Position position, int x, int y) {
		int dx = x - position.getX();
		int dy = y - position.getY();
		double rads = 0.0;
		if (dx == 0 && dy > 0) {
			rads = Math.PI * 1000;
		} else if (dx == 0 && dy < 0) {
			rads = Math.PI * -1000;
		} else {
			rads = Math.atan(1.0 * dy / dx);
		}
		return Math.round((float) rads * 1000);
	}

	public static void main(String[] args) {
		new RoboJar(args.length > 0 && args[0].equals("mock")).go();
	}

	//Getters
	public ICameraClient getCameraClient() {
		return cameraClient;
	}

	public ISerialClient getSerialClient() {
		return serialClient;
	}

	public Stack<CommandWithParams> getCommandStack() {
		return commandStack;
	}

	//Inner classes
	private class CommandWithParams {
		private Command command;
		private CommandParameters parameters;

		public CommandWithParams(Command command) {
			this.command = command;
		}

		public CommandWithParams(Command command, CommandParameters parameters) {
			this.command = command;
			this.parameters = parameters;
		}

		//Getters
		public Command getCommand() {
			return command;
		}

		public CommandParameters getParameters() {
			return parameters;
		}
	}
}
