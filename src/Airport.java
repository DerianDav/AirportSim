import java.awt.Point;
import java.util.ArrayList;
import java.util.Queue;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Airport {
	private final static int totalRunways = 1;
	ArrayList<Plane> planesOnGround = new ArrayList<Plane>();
	
	public static Map map = new Map();
	private static int[] runwayUsage;
	public static boolean runway0InUse = false;
	public static boolean runway1InUse = false;
	public static boolean i2InUse = false;
	public static boolean i1InUse = false;
	public static int landedPlanes;
	
	//terminalUsage
	private boolean[] terminalUsage = new boolean[map.getTerminals()];
	
	private static int totalPlanes = 0;
	
	public Airport() {
		runwayUsage = new int[totalRunways];
		for(int i = 0; i < totalRunways; i++)
			runwayUsage[i] = -1;
		for(int i = 0; i < map.getTerminals(); i++)
			terminalUsage[i] = false;
		landedPlanes = 0;
	}
	
	//A plane ask for a open runway
	// returns the location of where it should be flying from on the screen
	// if there is no open runway this returns null
	public Point requestRunway(int planeNum) {
		if(runway0InUse == false) {
			runway0InUse = true;
			landedPlanes++;
			return map.runway0Entry;
		}
		if(runway1InUse == false) {
			runway1InUse = true;
			landedPlanes++;
			return map.runway1Entry;
		}
		return null;
	}
	 
	public void leftRunway(int runway) {
		if(runway == 0)
			runway0InUse = false;
		else
			runway1InUse = false;
	}
	
	public void newPlane(int takeoffRunway) {
		planesOnGround.add(new Plane(this, map, totalPlanes, takeoffRunway));
		totalPlanes++;
	}
	
	public void nextTick() {
		for(int i = 0; i < planesOnGround.size(); i++) {
			if(planesOnGround.get(i).hasLeft()) {
		//		terminalUsage[plane.getTerminal()] = false;
				planesOnGround.remove(i);
				landedPlanes--;
			}
			planesOnGround.get(i).nextTick();
		}
	}
	
	public void drawSimulation(GraphicsContext gc, double drawScale) {
		map.drawMap(gc,drawScale);
		drawPlanes(gc,drawScale);
	}
	
	private void drawPlanes(GraphicsContext gc, double drawScale) {
		gc.setFill(Color.AQUAMARINE);
		for(Plane plane : planesOnGround) {
			plane.nextTick();
			if(plane.isInAir())
				return;
			gc.fillOval(plane.getX() * drawScale, plane.getY() * drawScale, 10, 10);
		}
	}

	//checks if there is a plane in the cord [xCord][Ycord]
	public boolean planeDetection(int xCord, int yCord) {
		for(Plane plane : planesOnGround) {
			if((int) plane.getX() == xCord && (int)plane.getY() == yCord) {
				return true;
			}
		}
		return false;
	}

	//returns a queue of directions the plane should take to reach the given terminal
	int count = 0;
	public Queue<Instruction> getTerminal(int runway) {
		count++;
		if(count == 4)
		System.out.println("LOL");
		for(int i = 0; i < map.getTerminals(); i++) {
			if(terminalUsage[i] == false) {
				terminalUsage[i] = true;
				return map.termDirections(i, runway);
			}
		}
		return null;
	}
	
	public int currentPlanes() {
		return landedPlanes;
	}
	
	public boolean availableTerminal() {
		for(int i = 0; i < map.getTerminals(); i++) {
			if(terminalUsage[i] == false) {
				return true;
			}
		}
		return false;
	}
	
	public void leftTerminal(int terminal) {System.out.println(terminal);
		terminalUsage[terminal] = false;
	}
}
