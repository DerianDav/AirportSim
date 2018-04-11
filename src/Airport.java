import java.awt.Point;
import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Airport {
	private final static int totalRunways = 1;
	ArrayList<Plane> planesOnGround = new ArrayList<Plane>();
	
	public static Map map = new Map();
	private static int[] runwayUsage;
	public static boolean runway0InUse = false;
	
	private static int totalPlanes = 0;
	
	public Airport() {
		runwayUsage = new int[totalRunways];
		for(int i = 0; i < totalRunways; i++)
			runwayUsage[i] = -1;
		
	}
	
	//A plane ask for a open runway
	// returns the location of where it should be flying from on the screen
	// if there is no open runway this returns null
	public Point requestRunway(int planeNum) {
		if(runwayUsage[0] == -1) {
			runwayUsage[0] = planeNum;
			return map.runway0Entry;
		}
		return null;
	}
	
	public void leftRunway(int planeNum) {
		for(int i = 0; i < totalRunways; i++) {
			if(runwayUsage[i] == planeNum) {
				runwayUsage[i] = -1;
				return;
			}
		}
	}
	
	public void newPlane() {
		planesOnGround.add(new Plane(this, map, totalPlanes));
		totalPlanes++;
	}
	
	public void nextTick() {
		for(Plane plane : planesOnGround)
			plane.nextTick();
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
}
