import java.awt.Point;

public class Plane {
	int planeNum;
	private double xCord;
	private double yCord;
	private double xVel;
	private double yVel;
	private boolean flying;
	private Airport airport;
	private static Map map;
	private planeStage curStage;
	private int ticksSinceLastStage;
	
	public Plane(Airport airport, Map map, int planeNum) {
		this.airport = airport;
		this.map = map;
		curStage = planeStage.AIR;
		ticksSinceLastStage = 0;
		xVel = 0;
		yVel = 0;
		this.planeNum = planeNum;
	}
	
	public void nextTick() {
		ticksSinceLastStage++;
		xCord += xVel;
		yCord += yVel;
		checkLocation();
		if(curStage == planeStage.AIR)
			requestLanding();
		else if(curStage == planeStage.GOINGAROUND && ticksSinceLastStage >= 500)
			requestLanding();
		else if(curStage == planeStage.LANDING) {
			xVel = Math.max(0.05, xVel-0.0003);
		}
	}
	
	private void checkLocation() {
		if(curStage == planeStage.AIR || curStage == planeStage.GOINGAROUND)
			return;
		if(curStage == planeStage.LANDING) {
			if(map.getTile((int)xCord, (int) yCord) == MapTile.RUNWAYEXITSOUTH) {
				xVel = 0;
				yVel = 0.05;
				curStage = planeStage.ONRUNWAY;
				airport.leftRunway(planeNum);
			}
		}
	}
	
	private void requestLanding() {
		Point entry = airport.requestRunway(planeNum);
		if(entry == null) 
			curStage = planeStage.GOINGAROUND;
		else {
			xCord = entry.x;
			yCord = entry.y;
			if(xCord == 0) 
				xVel = 0.2;
			else if(xCord == 100)
				xVel = -0.2;
			else if(yCord == 0)
				yVel = 0.2;
			else
				yVel = -0.2;
			curStage = planeStage.LANDING;
			
		}
		ticksSinceLastStage = 0;
		//taxi
		//terminal
		//wait for passangers
		//taxi
		//get on runway
		//takeoff
	}
	
//---------------------------GETTERS----------------------------------
	public boolean isInAir() {
		if(curStage == planeStage.AIR || curStage == planeStage.GOINGAROUND)
			return true;
		return false;
	}
	public int getTicksSinceLastStage() {return ticksSinceLastStage;}
	public double getX() {return xCord;}
	public double getY() {return yCord;}
}
