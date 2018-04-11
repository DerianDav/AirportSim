package OLDAIRPORT;

public class Plane {
	private double xCord;
	private double yCord;
	private double xVel;
	private double yVel;
	private boolean flying;
	private Airport airport;
	private planeStage curStage;
	private int ticksSinceLastStage;
	private Pathway pathway;
	
	public Plane(Airport airport) {
		this.airport = airport;
		curStage = planeStage.AIR;
		ticksSinceLastStage = 0;
	}
	
	public void nextTick() {
		ticksSinceLastStage++;
		xCord += xVel;
		yCord += yVel;
		if(curStage == planeStage.AIR)
			requestLanding();
		else if(curStage == planeStage.GOINGAROUND && ticksSinceLastStage >= 500)
			requestLanding();
		else if(curStage == planeStage.LANDING && ticksSinceLastStage >= 100) {
			curStage = planeStage.TAXING;
			System.out.println("Taxiing");
		}
		else if(curStage == planeStage.LANDING) {
			xVel -= 0.007;
		}
	}
	
	public void requestLanding() {
		pathway = airport.getRunway(0);
		if(pathway == null) {
			System.out.println("Going around");
			curStage = planeStage.GOINGAROUND;
			ticksSinceLastStage = 0;
			return;
		}
		System.out.println("landing on runway " + ((Runway)pathway).getRunwayNum());
		curStage = planeStage.LANDING;
		if(((Runway) pathway).getXVelChange() != 0)
			xVel = 5;
		if(((Runway) pathway).getYVelChange() != 0)
			yVel = 5;
		xCord = ((Runway) pathway).getXLandStart();
		yCord = ((Runway) pathway).getYLandStart();
		xVel = ((Runway) pathway).getXLandVel();
		yVel = ((Runway) pathway).getYLandVel();
		ticksSinceLastStage = 0;
		//taxi
		//terminal
		//wait for passangers
		//taxi
		//get on runway
		//takeoff
	}
	
//---------------------------GETTERS----------------------------------
	public Pathway getCurPathway() {return pathway;}
	public int getTicksSinceLastStage() {return ticksSinceLastStage;}
	public double getX() {return xCord;}
	public double getY() {return yCord;}
}
