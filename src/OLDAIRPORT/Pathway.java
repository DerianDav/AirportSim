package OLDAIRPORT;

import java.awt.Point;

public class Pathway {
	private double x1Cord;
	private double x2Cord;
	private double y1Cord;
	private double y2Cord;
	
	
	public Pathway(double x1Cord2, double x2Cord2, double y1Cord2, double y2Cord2) {
		this.x1Cord = x1Cord2;
		this.x2Cord = x2Cord2;
		this.y1Cord = y1Cord2;
		this.y2Cord = y2Cord2;
	}

	public double getX1Cord() {return x1Cord;}
	public double getX2Cord() {return x2Cord;}
	public double getY1Cord() {return y1Cord;}
	public double getY2Cord() {return y2Cord;}
	public double getWidth()  {return x2Cord-x1Cord;}
	public double getHeight() {return y2Cord-y1Cord;}
	
}
