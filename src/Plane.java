import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

public class Plane {
	
	private static final int alignTime = 500;
	
	int planeNum;
	private double xCord;
	private double yCord;
	private double xVel;
	private double yVel;
	private boolean flying;
	private Airport airport;
	private Direction curDir;
	private static Map map;
	private planeStage curStage;
	private int ticksSinceLastStage;
	private Queue<Instruction> instruction;
	private boolean inIntersection = false;
	private boolean onRunway = false;
	private boolean left = false;
	private int terminal = -1;
	private int timeAtTerminal = 2000;
	private int landRunway;
	private int takeoffRunway;
	
	public Plane(Airport airport, Map map, int planeNum, int takeoffRunway) {
		this.airport = airport; 
		this.map = map;
		curStage = planeStage.AIR;
		ticksSinceLastStage = 0;
		xVel = 0;
		yVel = 0;
		this.planeNum = planeNum;
		this.takeoffRunway = takeoffRunway;
	}
	
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
			if((int)xCord == 99) {
				airport.runway0InUse = false;
				left = true;
			}
			else if((int)yCord == 99) {
				airport.runway1InUse = false;
				left = true;
			}
			else {
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
					System.out.println(planeNum + ": " + terminal);	
			}
			if(ticksSinceLastStage <= timeAtTerminal)
				return;
		
			System.out.println("exiting term");
			curStage = planeStage.TAXING;
			instruction.poll();
			airport.leftTerminal(terminal);
		}
		else {
			checkLocation();
			checkForPlane();
		}
	}
	
	private void checkLocation() {
		MapTile curTile = null;
		boolean match = false;
		Instruction currentInt = null;
		if(curStage != planeStage.AIR && curStage != planeStage.GOINGAROUND && instruction != null) {
		//	System.out.println(xCord +" " + yCord);
			curTile = map.getTile((int)xCord, (int)yCord);	
			currentInt = instruction.peek();
		}
		if(curTile == MapTile.TAXIWAYWEST && inIntersection)
			inIntersection = false;
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
	
	private void requestLanding() {
		if(!airport.availableTerminal()) {
			curStage = planeStage.GOINGAROUND;
			ticksSinceLastStage = 0;
			return;
		}
		Point entry = airport.requestRunway(planeNum);
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
			System.out.println("request instructions");
			if(instruction == null)
				return;
			if(planeNum == 1);
		}
		ticksSinceLastStage = 0;
	}
	
	private void reachedPoint(MapTile curTile) {
		xCord = instruction.peek().xCord;
		yCord = instruction.peek().yCord;
		xVel = 0;
		yVel = 0;
		if(planeNum == 1 && instruction.peek().getInstruction)
			System.out.println("wow");
		if(instruction.peek().getInstruction)
			instruction = new LinkedList<Instruction>(map.runwayDirections(takeoffRunway, curTile.intersectionNum));
		else
			instruction.poll();
		planeStage lastStage = curStage;
		if(curStage == planeStage.LANDING)
			airport.leftRunway(landRunway);
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
}
