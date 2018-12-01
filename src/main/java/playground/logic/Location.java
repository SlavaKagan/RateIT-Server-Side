package playground.logic;

public class Location {
	private double x;
	private double y;
	
	public Location() {
	}
	
	public Location(double x, double y) {
		this();
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public static double getDistance(double xstart, double ystart, double xtarget, double ytarget) {
		return Math.sqrt(Math.pow(xtarget - xstart, 2) + Math.pow(ytarget - ystart, 2));
	}
	
	public static double getDistance(Location start, Location target) {
		return Math.sqrt(Math.pow(target.x - start.x, 2) + Math.pow(target.y - start.y, 2));
	}
	@Override
	public String toString() {
		return "{\"x\":" + x + ", \"y\":" + y + "}";
	}
}