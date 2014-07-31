package Planes;

// A helper class for working with directions. 
public class Direction {
	// The three main directions, -1 means the first letter is in the direction, 1 means the second is, 0 means neither is.
	private int NS, WE, DU;

	// Creates a direction from 3 integers.
	public Direction(int NSDir, int WEDir, int DUDir) {
		NS = (NSDir>0)?1:(NSDir==0)?0:-1;
		WE = (WEDir>0)?1:(WEDir==0)?0:-1;
		DU = (DUDir>0)?1:(DUDir==0)?0:-1;
	}

	// Creates a direction from a directionstring.
	public Direction(String direction) {
		NS = (direction.indexOf("N")>-1)?-1:(direction.indexOf("S")>-1)?1:0;
		WE = (direction.indexOf("W")>-1)?-1:(direction.indexOf("E")>-1)?1:0;
		DU = (direction.indexOf("D")>-1)?-1:(direction.indexOf("U")>-1)?1:0;
	}
	
	// Returns this direction as a String.
	public String getAsString() {
		return String.valueOf(getMainDirections());
	}
	
	// Returns The direction projected onto the NS-axis.
	// -1 means heading north.
	public int getNSDir() {
		return NS;
	}

	// Returns The direction projected onto the WE-axis.
	// -1 means heading west.
	public int getWEDir() {
		return WE;
	}

	// Returns The direction projected onto the DU-axis.
	// -1 means heading down.
	public int getDUDir() {
		return DU;
	}

	// Returns a Point3D representing the direction.
	public Point3D getAsPoint3D() {
		return new Point3D(NS, WE, DU);
	}

	// Returns an array of chars representing the main directions.
	public char[] getMainDirections() {
		char[] directions = new char[Math.abs(NS) + Math.abs(WE) + Math.abs(DU)];
		int count = 0;

		if (NS!=0) {
			directions[count] = (NS==-1)?'N':'S';
			count++;
		}

		if (WE!=0) {
			directions[count] = (WE==-1)?'W':'E';
			count++;
		}

		if (DU!=0) {
			directions[count] = (DU==-1)?'D':'U';

		}

		return directions;
	}

	// Returns all possible turning directions.
	public Direction[] getPossibleDirections() {
		Direction[] directions;
		if (Math.abs(NS) + Math.abs(WE) + Math.abs(DU) < 3) {
			directions = new Direction[9];
		} else {
			directions = new Direction[7];
		}
		int directionCount = 0;

		for (int tempNS = -1; tempNS<=1; tempNS++) {
			for (int tempWE = -1; tempWE<=1; tempWE++) {
				for (int tempDU = -1; tempDU<=1; tempDU++) {
					Direction tempDirection = new Direction(tempNS,tempWE, tempDU);
					if (isValidDirection(tempDirection)) {
						directions[directionCount] = tempDirection;
						directionCount++;
					}
				}
			}
		}

		return directions;
	}

	// Returns true if a direction is a valid direction to change to
	public boolean isValidDirection(Direction direction) {
		return (((direction.getNSDir() - NS)*(direction.getNSDir() - NS) + (direction.getWEDir() - WE)*(direction.getWEDir() - WE) + (direction.getDUDir() - DU)*(direction.getDUDir() - DU)) < 3 &&
				((direction.getNSDir() != 0 && direction.getNSDir() == NS) || (direction.getWEDir() != 0 && direction.getWEDir() == WE) || (direction.getDUDir() != 0 && direction.getDUDir() == DU)));
	}
}
