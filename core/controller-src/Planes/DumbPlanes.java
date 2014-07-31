package Planes;

public class DumbPlanes extends PlaneControl {

	public DumbPlanes(int arenaSize, int rounds) {
		super(arenaSize, rounds);
	}

	@Override
	public Move[] act() {
		Move[] moves = new Move[2];
		for (int i=0; i<2; i++) {
			if (!myPlanes[i].isAlive()) {
				moves[i] = new Move(new Direction("N"), false, false); // If we're dead we just return something, it doesn't matter anyway.
				continue;
			}
			Direction[] possibleDirections = myPlanes[i].getPossibleDirections(); // Let's see where we can go.
			
			for (int j=0; j<possibleDirections.length*3; j++) {
				
				int random = (int) Math.floor((Math.random()*possibleDirections.length)); // We don't want to be predictable, so we pick a random direction out of the possible ones.
				
				if (myPlanes[i].getPosition().add(possibleDirections[random].getAsPoint3D()).isInArena(arenaSize)) { // We'll try not to fly directly into a wall.
					moves[i] = new Move(possibleDirections[random], Math.random()>0.5, myPlanes[i].canShoot() && Math.random()>0.2);
					continue; // I'm happy with this move for this plane.
				}
				
				// Uh oh.
				random = (int) Math.floor((Math.random()*possibleDirections.length));
				moves[i] = new Move(possibleDirections[random], Math.random()>0.5, myPlanes[i].canShoot() && Math.random()>0.2);
			}
		}
		
		return moves;
	}
	
	@Override
	public void newFight(int fightsFought, int myScore, int enemyScore) {
		// Using information is for schmucks.
	}
	
	@Override
	public void newOpponent(int fights) {
		// What did I just say about information?
	}
}
