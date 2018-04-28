
import java.awt.Point;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Logic behind a single plane
 * @author Derian Davila Acuna
 *
 */
public class Plane implements Serializable{
	
	private static final int alignTime = 500;
	
	int planeNum;
	private double xCord;
	private double yCord;
	public double xVel;
	public double yVel;
	public boolean flying;
	public Airport airport;
	public Direction curDir;
	public static Map map;
	public planeStage curStage;
	public int ticksSinceLastStage;
	public Queue<Instruction> instruction;
	public boolean inIntersection = false;
	public boolean onRunway = false;
	public boolean left = false;
	public int terminal = -1;
	public int timeAtTerminal = 2000;
	public int landRunway;
	public int takeoffRunway;
	public boolean foreign;
	
	//RGB stored as ints since javaFX isn't serializable
	public int rColor;
	public int gColor;
	public int bColor;
	
	public Plane(Airport airport, Map map, int planeNum, int takeoffRunway, int rColor, int gColor, int bColor) {
		this.airport = airport; 
		this.map = map;
		curStage = planeStage.AIR;
		ticksSinceLastStage = 0;
		xVel = 0;
		yVel = 0;
		this.planeNum = planeNum;
		this.takeoffRunway = takeoffRunway;
		foreign = false;
		this.rColor = rColor;
		this.gColor = gColor;
		this.bColor = bColor;
	}
	
	public Plane(Airport airport, PlaneShell plane, Map map) {
		planeNum = plane.planeNum;
		xCord = plane.xCord;
		yCord = plane.yCord;
		xVel = plane.xVel;
		yVel = plane.yVel;
		flying = plane.flying;
		curDir = plane.curDir;
		curStage = plane.curStage;
		ticksSinceLastStage = plane.ticksSinceLastStage;
		instruction = plane.instruction;
		inIntersection = plane.inIntersection;
		onRunway = plane.onRunway;
		left = plane.left;
		terminal = plane.terminal;
		timeAtTerminal = plane.timeAtTerminal;
		landRunway = plane.landRunway;
		takeoffRunway = plane.takeoffRunway;
		foreign = plane.foreign;
		rColor = plane.rColor;
		bColor = plane.bColor;
		gColor = plane.gColor;
		this.airport = airport;
		this.map = map;
		
	}
	
	/**
	 * updates the position of the plane based on the current velocity
	 * depending on the stage the plane it is it may call requestLanding, checkLocation, and checkForPlane
	 */
	public void nextTick() {
		ticksSinceLastStage++;
		xCord += xVel;
		yCord += yVel;
		
		if(curStage == planeStage.AIR)
			requestLanding();
		else if(curStage == planeStage.GOINGAROUND && ticksSinceLastStage >= 700)
			requestLanding();
		else if(curStage == planeStage.LANDING) {
			if(landRunway == 0) {
				xVel = Math.max(0.05, xVel-0.0003);
				curDir = Direction.EAST;
				if((int)xCord > 20)
				checkLocation();
			}
			else {
				xVel = 0;
				yVel = Math.max(0.05, yVel-0.0003);
				curDir = Direction.SOUTH;
				if((int)yCord >80)
					checkLocation();
			}
		}
		else if(curStage == planeStage.TAKEOFF) {
			//makes sure the plane gets deleted by the airport after this
			if((int)xCord == 99) {
				airport.runway0InUse = false;
				left = true;
			}
			else if((int)yCord == 99) {
				airport.runway1InUse = false;
				left = true;
			}
			else {
				//start accelerating the plane up to 0.1 in a dir
				if(ticksSinceLastStage > alignTime) {
					if(takeoffRunway == 0) {
						xVel = Math.min(0.1, xVel+0.003);
						curDir = Direction.EAST;
					}
					else if (takeoffRunway == 1) {
						yVel = Math.min(0.1, yVel+0.003);
						curDir = Direction.SOUTH;
					}
				}
					
			}
		}
		else if(curStage == planeStage.TERMINAL) {
			if(ticksSinceLastStage == 500) {
				 terminal = map.getTile((int)xCord, (int)yCord).terminalNum;
			}
			if(ticksSinceLastStage <= timeAtTerminal)
				return;
			//make sure there is no plane in the intersection
			if(airport.i1InUse)
				return;
			airport.i1InUse = true;
			inIntersection = true;
			curStage = planeStage.TAXING;
			instruction.poll();
			airport.leftTerminal(terminal);
		}
		else {//if the plane is simply taxing
			checkLocation();
			checkForPlane();
		}
	}
	
	/**
	 * Does various actions depending on the tile the plane is on and the square it is on/has passed
	 * INTERSECTIONA1 - checks if another plane is in the intersection before driving on it
	 * RUNWAYWAIT -  checks if the runway is being used before driving onto it
	 * 
	 */
	private void checkLocation() {
		MapTile curTile = null; 
		boolean match = false;
		Instruction currentInt = null;
		if(curStage != planeStage.AIR && curStage != planeStage.GOINGAROUND && instruction != null) {
			curTile = map.getTile((int)xCord, (int)yCord);	
			currentInt = instruction.peek();
		}
		else if((curStage != planeStage.AIR || curStage != planeStage.GOINGAROUND) && instruction == null)
			return;
		//need to stop the plane at the intersection if another plane is on it
		if(curTile == MapTile.INTERSECTIONA1) {
			if(airport.i1InUse && !inIntersection) {
				xVel = 0;
				yVel = 0;
				return;
			}
			else {
				airport.i1InUse = true;
				inIntersection = true;
			}
		}
		if(curTile == MapTile.RUNWAYWAIT) {
			if(takeoffRunway == 0) {
				if(airport.runway0InUse && !onRunway) {
					xVel = 0;
					yVel = 0;
					return;
				}
				else {
					airport.runway0InUse = true;
					onRunway = true;
				
				}
			}
			else {
				if(airport.runway1InUse && !onRunway) {
					xVel = 0;
					yVel = 0;
					return;
				}
				else {
					airport.runway1InUse = true;
					onRunway = true;
				
				}
			}
		}
		
		if(currentInt == null) {
			xVel = 0;
			yVel = 0;
		}
		//LOCATION CHECKS, if the plane has passed the intended location call reachedPoint else keep the velocity in its current direction at 0.05
		else if(xCord != currentInt.xCord) {
			if(currentInt.dir == Direction.WEST && xCord < currentInt.xCord)
				reachedPoint(curTile);
			else if(currentInt.dir == Direction.EAST && xCord > currentInt.xCord) 
				reachedPoint(curTile);
			else if(curStage == planeStage.TAXING || curStage == planeStage.ONRUNWAY) {
				xVel = currentInt.dir.x * 0.05;
				yVel = currentInt.dir.y * 0.05;
			}
		}
		else if(yCord != currentInt.yCord) {
			if(currentInt.dir == Direction.SOUTH && yCord > currentInt.yCord) 
				reachedPoint(curTile);
			else if(currentInt.dir == Direction.NORTH && yCord < currentInt.yCord) 
				reachedPoint(curTile);
			else if(curStage == planeStage.TAXING || curStage == planeStage.ONRUNWAY) {
				xVel = currentInt.dir.x * 0.05;
				yVel = currentInt.dir.y * 0.05;
			}
		}
		
		
		else {System.out.println("else");
			xVel = 0;
			yVel = 0;
		}
	}
	
	/**
	 * checks for a plane 3 squares in front of it
	 * if there is a plane in that direciton then this stops this plane
	 */
	public void checkForPlane() {
		if(curDir == null)
			return;
		switch(curDir) {
			case WEST:
				if(airport.planeDetection((int)xCord-3, (int) yCord)) {
					xVel = 0;
					yVel = 0;
				}
				return;
			case EAST:
				if(airport.planeDetection((int)xCord+3, (int) yCord)) {System.out.println(planeNum);
					xVel = 0;
					yVel = 0;
				}
				return;
			case NORTH:
				if(airport.planeDetection((int)xCord, (int) yCord-3)) {
					xVel = 0;
					yVel = 0;
				}
				return;
			case SOUTH:
				if(airport.planeDetection((int)xCord, (int) yCord+3)) {
					xVel = 0;
					yVel = 0;
				}
				return;
			default:
				return;
		}
	}
	
	/**
	 * request to land at the airport
	 * if there is no terminal or no availbe runway then the plane waits for x ticks before calling this again
	 * also fetches the instructions once it has been given a runway to land to
	 */
	private void requestLanding() {
		if(foreign)
			return;
		if(!airport.availableTerminal()) {
			curStage = planeStage.GOINGAROUND;
			ticksSinceLastStage = 0;
			return;
		}
		Point entry = airport.requestRunway();
		if(entry == null) {
			curStage = planeStage.GOINGAROUND;
			ticksSinceLastStage = 0;
		}else {
			if(entry.x == 0)
				landRunway = 0;
			else
				landRunway = 1;
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
			instruction = new LinkedList<Instruction>(airport.getTerminal(landRunway));
			if(instruction == null)
				return;
			if(planeNum == 1);
			if(airport.isOnline())
				airport.sendUpdatedInstructions(this);
		}
		ticksSinceLastStage = 0;
	}
	
	/** 
	 * if the plane has reached or passed the point in the instruction the plane is forced to the point in the instruction
	 * sets up curStage and curDir with the next instructions stage and direction
	 * @param curTile - the tile the plane is on
	 */
	private void reachedPoint(MapTile curTile) {
		xCord = instruction.peek().xCord;
		yCord = instruction.peek().yCord;
		xVel = 0;
		yVel = 0;
		if(instruction.peek().getInstruction && !foreign) {
			instruction = new LinkedList<Instruction>(map.runwayDirections(takeoffRunway, curTile.intersectionNum));
			if(airport.isOnline())
				airport.sendUpdatedInstructions(this);
		}
		else
			instruction.poll();
		planeStage lastStage = curStage;
		if(curStage == planeStage.LANDING)
			airport.leftRunway(landRunway);
		if(instruction.peek() == null)
			return;
		curStage = instruction.peek().stage;
		curDir = instruction.peek().dir;
		if(lastStage != curStage)
			ticksSinceLastStage = 0;
		if(curTile != MapTile.INTERSECTIONA1) {
			airport.i1InUse = false;
			inIntersection = false;
		}
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
	public boolean hasLeft() {return left;}
	public int getTerminal() {return terminal;}
	public PlaneShell getShell() {return new PlaneShell(this);}
}
