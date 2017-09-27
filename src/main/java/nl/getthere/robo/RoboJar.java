package nl.getthere.robo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

import nl.getthere.robo.aangeleverd.mock.RoboMock;
import nl.getthere.robo.commandparameters.CommandParameters;
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
		this.getCommandStack().add(new CommandWithParams(Command.MOVE_TO, new MoveToCommandParameters(this.getCameraClient().getPosition().getX(), 0)));

		mainLoop: while (!this.getCommandStack().isEmpty()) {
			switch (this.getCommandStack().peek().getCommand()) {
				case MOVE_TO:
					this.handleMoveToCommand();
					break;
				case ROTATE:
					this.handleRotateCommand();
					break;
				default:
					throw new RuntimeException("Don't know how to run command " + this.getCommandStack().peek().getCommand());
			}
		}

		System.out.println("Command stack is empty. Exit...");
	}
	
	private void handleRotateCommand() {
		RotateCommandParameters parameters = (RotateCommandParameters) this.getCommandStack().peek().getParameters();
		
	}

	private void handleMoveToCommand() {
		MoveToCommandParameters parameters = (MoveToCommandParameters) this.getCommandStack().peek().getParameters();

		moveLoop: while (true) {
			//Determine direction
			Position position = this.getCameraClient().getPosition();
			System.out.println("Moving from '"+position+"' to '"+parameters+"'");
			
			//Are we there yet?
			boolean onXTarget = Math.abs(position.getX() - parameters.getTargetX()) < 20;
			boolean onYTarget = Math.abs(position.getY() - parameters.getTargetY()) < 20;
			if(onXTarget && onYTarget){
				System.out.println("Target position ('"+parameters+"') reached ('"+position+"')");
				//Target position reached
				this.getCommandStack().pop();
				break moveLoop;
			}
			
			//Determine direction to travel to
			boolean needToRotate = false;
			int targetRadial = 123;
			if (needToRotate) {
				//Correct direction
				this.getCommandStack().push(new CommandWithParams(Command.ROTATE, new RotateCommandParameters(targetRadial)));
				break moveLoop;
			} else {
				//Move to position
				int targetSpeed = 200;
				
				//If close by, move slower
				if((Math.abs(position.getX() - parameters.getTargetX()) < 50) || Math.abs(position.getY() - parameters.getTargetY()) < 50){
					targetSpeed = 100;
				}
				System.out.println("Move forward at speed "+targetSpeed);
				this.getSerialClient().setLeftSpeed(targetSpeed);
				this.getSerialClient().setRightSpeed(targetSpeed);
			}

			//Sleep so that the robot can move
			try {
				Thread.sleep(500);
			} catch (InterruptedException ign) {
			}
		}
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
