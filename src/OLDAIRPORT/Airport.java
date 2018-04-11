package OLDAIRPORT;
import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Airport {
	static Runway runway1 = new Runway(0,100,500,10,40, -0.3, 0, 0, 20, 1, 0);
	static TaxiWay taxiway1 = new TaxiWay(11,120,150, 40,300);
	ArrayList<Plane> planesOnGround = new ArrayList<Plane>();
	
	public Runway getRunway(int number) {
		if(runway1.inUse == false) {
			runway1.inUse = true;
			return runway1;
		}
	//	System.out.println("no valid runway");
		return null;
	}
	
	public void leftRunway(int number) {
		runway1.inUse = false;
		return;
	}
	
	int wow = Integer.parseInt("3");
	
	public void newPlane() {
		planesOnGround.add(new Plane(this));
	}
	
	public void nextTick() {
		for(Plane plane : planesOnGround)
			plane.nextTick();
	}
	
	public void drawSimulation(GraphicsContext gc) {
		drawAirport(gc);
		drawPlanes(gc);
	}
	
	private void drawAirport(GraphicsContext gc) {
		gc.setFill(new Color(0,0,0,1));
		gc.fillRect(runway1.getX1Cord(), runway1.getY1Cord(), runway1.getWidth(), runway1.getHeight());
		gc.setFill(new Color(0.5,0.5,0.5,1));
		gc.fillRect(taxiway1.getX1Cord(), taxiway1.getY1Cord(), taxiway1.getWidth(), taxiway1.getHeight());

	}
	
	private void drawPlanes(GraphicsContext gc) {
		gc.setFill(Color.AQUAMARINE);
		for(Plane plane : planesOnGround) {
			plane.nextTick();
			gc.fillOval(plane.getX(), plane.getY(), 10, 10);
		}
	}
}
