# Dogfight Visualizer #
Visualizer for the Dogfight KOTH Challenge

Built with LibGDX ( http://libgdx.com )

# Notes #
As of right now the UI looks horrible. I will try to improve it, watch for updates...

# Usage #
Run the batch/shell files in dist/

If running from source, the main class is zove.koth.dogfight.desktop.DesktopLauncher

Windows users: You must have java in your PATH environment variable

Linux users: I haven't tested my shell script and I'm not good at writing them so correct the shell script if something is wrong

# Importing into Eclipse #
Clone this repo into your eclipse project and then run `gradlew desktop:run`  
Hopefully gradle will take care of dependencies...

# Running without importing #
Copy any plane class files into `dist/`. Then, run `dist/desktop-1.0.jar` with this command:

```
java -cp your-class-folder/;desktop-1.0.jar;Planes.jar zove.koth.dogfight.desktop.DesktopLauncher package.YourPlaneController1 package.YourPlaneController2 ...
```

# To update the controller source #
I will try to update it myself based on new controller versions (if any)

To update yourself, note the following static methods and where they need to be called from (See my modified Controller for examples):

```
DogfightVisualizer.reportEntries(PlaneControl[] entries) - called from Controller.main() after the array of entries is created 
DogfightVisualizer.beginMatch(String player1Name, String player2Name, int totalFights) - Called at the top of Controller.matchUp() 
DogfightVisualizer.newFight(int fightNumber) - Called at the top of Controller.fight() 
DogfightVisualizer.newRound(int roundNumber) - Called at the top of the round loop in Controller.fight() 
DogfightVisualizer.updatePlanes(Plane player1_plane1, Plane player1_plane2, Plane player2_plane1, Plane player2_plane2) - Called after both planes' moves are processed in the round loop in Controller.fight() 
DogfightVisualizer.planeShot(Point3D planeLocation, Point3D maxShootLocation) - Called in Controller.handleShooting() after if (moves[i].shoot && planes[i].canShoot()) { like this: 
	Point3D[] range = planes[i].getShootRange(); 
	if (range.length > 0) 
		DogfightVisualizer.planeShot(planes[i].getPosition(), range[range.length - 1]); 
DogfightVisualizer.fightWinner(String winnerName) - Called when the winner of a fight is determined 
DogfightVisualizer.matchupEnded(String winnerName) - Called when the winner of a matchup is determined 
DogfightVisualizer.overallWinner(String winner, int points) - Called after all matchups are complete when an overall winner is determined 
DogfightVisualizer.reportFinalScore(String name, int score) - Called for each plane after all matchups are complete 
```