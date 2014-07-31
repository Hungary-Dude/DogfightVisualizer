package Planes;

//This is the base class players extends.
//It contains the arena size and 4 plane objects representing the planes in the arena.
public abstract class PlaneControl {

	// note that these planes are just for your information, modifying these doesn't affect the actual plane instances, 
	// which are kept by the controller
	protected Plane[] myPlanes = new Plane[2];
	protected Plane[] enemyPlanes = new Plane[2];
	protected int arenaSize;
	protected int roundsLeft;
 
	public PlaneControl(int arenaSize, int rounds) {
		this.arenaSize = arenaSize;
		roundsLeft = rounds;
	}

	public final void setRoundsLeft(int rounds) {
		roundsLeft = rounds;
	}

	public final void setPlane1(Plane plane) {
		myPlanes[0] = plane;
	}

	public final void setPlane2(Plane plane) {
		myPlanes[1] = plane;
	}

	public final void setEnemyPlane1(Plane plane) {
		enemyPlanes[0] = plane;
	}

	public final void setEnemyPlane2(Plane plane) {
		enemyPlanes[1] = plane;
	}

	// Notifies you that a new fight is starting
	// FightsFought tells you how many fights will be fought.
	// the scores tell you how many fights each player has won.
	public void newFight(int fightsFought, int myScore, int enemyScore) {
		//
	}

	// notifies you that you'll be fighting anew opponent.
	// Fights is the amount of fights that will be fought against this opponent
	public void newOpponent(int fights) {
		//
	}

	// This will be called once every round, you must return an array of two moves.
	// The move at index 0 will be applied to your plane at index 0,
	// The move at index1 will be applied to your plane at index1.
	// Any further move will be ignored.
	// A missing or invalid move will be treated as flying forward without shooting.
	public abstract Move[] act();
}
