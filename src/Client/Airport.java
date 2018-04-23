package Client;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Controls the management of the airport and a certain number of planes
 * @author Derian Davila Acuna
 */
public class Airport {
	private final static int totalRunways = 1;
	ArrayList<Plane> planesOnGround = new ArrayList<Plane>();
	ClientServer clientServer;
	
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
		clientServer = new ClientServer(this);
	}
	
	//A plane ask for a open runway
	// returns the location of where it should be flying from on the screen
	// if there is no open runway this returns null
	/**
	 *  Looks for an empty runway to let the requesting plane use
	 * @return the Entry point to land on the runway (starts on a edge of the map)
	 */
	public Point requestRunway() {
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
	 
	/**
	 * Lets other planes use the given runway once the plane that was using it has left it 
	 * @param runway - the runway the plane was using
	 */
	public void leftRunway(int runway) {
		if(runway == 0)
			runway0InUse = false;
		else
			runway1InUse = false;
	}
	
	/**
	 * Creates a new plane that needs to takeoff off from a given runway
	 * @param takeoffRunway - the runway the plane has request to leave from
	 */
	public void newPlane(int takeoffRunway) {
		Plane plane = new Plane(this,map,totalPlanes,takeoffRunway);
		planesOnGround.add(plane);
		totalPlanes++;
		try {
			clientServer.newPlane(plane);
		} catch (IOException e) {}
	}
	
	/**
	 * Goes through one tick of the simulation
	 * Each tick checks if a plane has left, if so it removes the plane from the list else, this calls the next tick for each plane
	 */
	public void nextTick() {
		if(planesOnGround.size() == 0)
			return;
		for(int i = 0; i < planesOnGround.size(); i++) {
			if(planesOnGround.get(i).hasLeft()) {
		//		terminalUsage[plane.getTerminal()] = false;
				planesOnGround.remove(i);
				landedPlanes--;
				return;
			}
			planesOnGround.get(i).nextTick();
		}
	}
	
	/**
	 * Calls the drawMap from the map and drawPlanes for the planes
	 * @param gc - the Graphics Context from the canvas on the GUI
	 * @param drawScale - the scale used to draw these objects
	 * @param topBar - the height of the bar at the top of the sim
	 */
	public void drawSimulation(GraphicsContext gc, double drawScale, int topBar) {
		map.drawMap(gc,drawScale, topBar);
		drawPlanes(gc,drawScale, topBar);
	}
	
	/**
	 * Draws all the planes as a circle on the map
	 * @param gc - the Graphics Context from the canvas on the GUI
	 * @param drawScale - the scale used to draw these objects
	 * @param topBar - the height of the bar at the top of the sim
	 */
	private void drawPlanes(GraphicsContext gc, double drawScale, int topBar) {
		gc.setFill(Color.AQUAMARINE);
		for(Plane plane : planesOnGround) {
			plane.nextTick();
			if(plane.isInAir())
				return;
			gc.fillOval(plane.getX() * drawScale, plane.getY() * drawScale + topBar, 10, 10);
		}
	}

	//checks if there is a plane in the cord [xCord][Ycord]
	/**
	 * Detects if their is a plane in cord xCord,yCord
	 * @param xCord - xCord to check for a plane
	 * @param yCord - yCord to check for a plane
	 * @return returns true or false depending if a plane was found at the given cordinates
	 */
	public boolean planeDetection(int xCord, int yCord) {
		for(Plane plane : planesOnGround) {
			if((int) plane.getX() == xCord && (int)plane.getY() == yCord) {
				return true;
			}
		}
		return false;
	}

	//returns a queue of directions the plane should take to reach the given terminal
	/**
	 * Looks for an empty terminal and fetches the instructions to that terminal
	 * @param runway - the runway the plane is starting from
	 * @return returns the instructions to a terminal or null if all the terminals are taken
	 */
	public Queue<Instruction> getTerminal(int runway) {
		for(int i = 0; i < map.getTerminals(); i++) {
			if(terminalUsage[i] == false) {
				terminalUsage[i] = true;
				return map.termDirections(i, runway);
			}
		}
		return null;
	}
	
	/**
	 * @return the amount of planes currently on the ground
	 */
	public int currentPlanes() {
		return landedPlanes;
	}
	
	/**
	 * @return returns true if their is a terminal available else this returns false
	 */
	public boolean availableTerminal() {
		for(int i = 0; i < map.getTerminals(); i++) {
			if(terminalUsage[i] == false) {
				return true;
			}
		}
		return false;
	}

	/**
	 * changes terminalUsage for that terminal to false to let other planes use this terminal
	 * @param terminal - the terminal the plane is leaving from
	 */
	public void leftTerminal(int terminal) {System.out.println(terminal);
		terminalUsage[terminal] = false;
	}
}
