package Planes;

public class Point3D {
	public int x, y, z;

	public Point3D(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// Returns the sum of this Point3D and the one specified in the argument.
	public Point3D add(Point3D point3D) {
		return new Point3D(x + point3D.x, y + point3D.y, z + point3D.z);
	}

	// Returns the product of this Point3D and a factor.
	public Point3D multiply(int factor) {
		return new Point3D(factor*x, factor*y, factor*z);
	}

	// Returns true if both Point3D are the same.
	public boolean equals(Point3D point3D) {
		return x == point3D.x && y == point3D.y && z == point3D.z;
	}

	// Returns true if Point3D is within a 0-based arena of a specified size.
	public boolean isInArena(int size) {
		return Math.min(x, Math.min(y, z))>=0 && Math.max(x, Math.max(y, z))<size;
	}
}
