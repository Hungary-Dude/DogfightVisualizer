package Planes;

public class Move {
	public Direction direction;
	public boolean changeDirection;
	public boolean shoot;

	public Move(Direction direction, boolean changeDirection, boolean shoot) {
		this.direction = direction;
		this.changeDirection = changeDirection;
		this.shoot = shoot;
	}
	
	public String getAsString() {
		return "direction: " + direction.getAsString() + " change: " + Boolean.toString(changeDirection) + " shoot: " + Boolean.toString(shoot);
	}
}
