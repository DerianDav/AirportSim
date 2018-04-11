package OLDAIRPORT;


public class Runway extends Pathway{
	private int num;
	public boolean inUse;
	private double xVelChange;
	private double yVelChange;
	private double xLandStart;
	private double yLandStart;
	private double xLandVel;
	private double yLandVel;
	
	public Runway(int num, double x1Cord, double x2Cord, double y1Cord, double y2Cord,
			double xVelChange, double yVelChange, double xLandStart, double yLandStart,
			double xLandVel, double yLandVel) {
		super(x1Cord,x2Cord,y1Cord,y2Cord);
		this.num = num;
		inUse = false;
		this.xVelChange = xVelChange;
		this.yVelChange = yVelChange;
		this.xLandStart = xLandStart;
		this.yLandStart = yLandStart;
		this.xLandVel = xLandVel;
		this.yLandVel = yLandVel;
	}
	
	public int getRunwayNum() {return num;}
	public double getXVelChange() {return xVelChange;}
	public double getYVelChange() {return yVelChange;}
	public double getXLandStart() {return xLandStart;}
	public double getYLandStart() {return yLandStart;}
	public double getXLandVel() {return xLandVel;}
	public double getYLandVel() {return yLandVel;}
	
}
