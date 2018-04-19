
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Map {
	
	private MapTile[][] map;
	public Point runway0Entry;
	public Point runway1Entry;
	public ArrayList<Queue<Instruction>> terminalDir0 = new ArrayList<Queue<Instruction>>();
	public ArrayList<Queue<Instruction>> terminalDir1 = new ArrayList<Queue<Instruction>>();
	public ArrayList<Queue<Instruction>> runwayDir0 = new ArrayList<Queue<Instruction>>();
	public ArrayList<Queue<Instruction>> runwayDir1 = new ArrayList<Queue<Instruction>>();
	
	public Map() {
		map = new MapTile[100][100];
		for(int x = 0; x < 100; x++) {
			for(int y =0; y < 100; y++)
				map[x][y] = MapTile.GRASS;
		}
		
		addRectangle(10,70,10,15, MapTile.RUNWAY);
		addRectangle(85,90,20,90, MapTile.RUNWAY);
		
		addRectangle(80,84,85,88, MapTile.TAXIWAY);
		addRectangle(77,80,30,88, MapTile.TAXIWAY);
		addRectangle(10,80,30,33, MapTile.TAXIWAY);
		addRectangle(13,84,23,26, MapTile.TAXIWAY);
		addRectangle(41,44,23,29, MapTile.TAXIWAY);
		
		addRectangle(83,83,23,26, MapTile.RUNWAYWAIT);
		
		addRectangle(65,68,16,30,MapTile.TAXIWAYSOUTH);
		addRectangle(10,68,30,33, MapTile.TAXIWAYWEST);
		addRectangle(10,13,16,29, MapTile.TAXIWAYNORTH);
		map[66][30] = MapTile.TAXIWAYSOUTH; // to make sure the plane goes to the middle of the runway
		map[11][30] = MapTile.TAXIWAYNORTH;
		addRectangle(10,13,17,17, MapTile.RUNWAYWAIT);
		addRectangle(10,13,11,11, MapTile.RUNWAYSTOP);
		
		addInstructions();
		addToRunwayInstructions();
		
		//TERMINALS
		addRectangle(41,44,30,47, MapTile.INTERSECTIONA1);
		addRectangle(35,40,35,37, MapTile.TERMINAL1);
		addRectangle(35,40,40,42, MapTile.TERMINAL2);
		addRectangle(35,40,45,47, MapTile.TERMINAL3);
		addRectangle(45,50,35,37, MapTile.TERMINAL4);
		addRectangle(45,50,40,42, MapTile.TERMINAL5);
		addRectangle(45,50,45,47, MapTile.TERMINAL6);
	
		//AIRPORT BUILDING
		addRectangle(0,55,50,90, MapTile.BUILDING);
		//TERMINALS 1-3 BUILDINGS
		addRectangle(30,35,35,50, MapTile.BUILDING);
		addRectangle(30,37,36,36, MapTile.BUILDING);
		addRectangle(30,37,41,41, MapTile.BUILDING);
		addRectangle(30,37,46,46, MapTile.BUILDING);
		//TERMINALS 3-6 BUILDINGS
		addRectangle(50,55,35,50, MapTile.BUILDING);
		addRectangle(48,50,36,36, MapTile.BUILDING);
		addRectangle(48,50,41,41, MapTile.BUILDING);
		addRectangle(48,50,46,46, MapTile.BUILDING);
		
		
		
		addRectangle(10,10,30,33, MapTile.STOP);
		runway0Entry = new Point();
		runway0Entry.setLocation(0,12);
		runway1Entry = new Point();
		runway1Entry.setLocation(88, 0);
		
	}
	
	private void addInstructions() {
		//RUNWAY 1
		Queue<Instruction> queue1,queue2,queue3,queue4,queue5,queue6;
		queue1 = new LinkedList<Instruction>();
		queue1.add(new Instruction(Direction.EAST, 67,12, planeStage.ONRUNWAY, false));
		queue1.add(new Instruction(Direction.SOUTH, 67,31, planeStage.TAXING, false));
		queue1.add(new Instruction(Direction.WEST, 43, 31, planeStage.TAXING, false));
		queue2 = new LinkedList<Instruction>(queue1);
		queue3 = new LinkedList<Instruction>(queue1);
		queue4 = new LinkedList<Instruction>(queue1);
		queue5 = new LinkedList<Instruction>(queue1);
		queue6 = new LinkedList<Instruction>(queue1);
		
		
		
		addTerminalInstructions(queue1, 0);
		addTerminalInstructions(queue2, 1);
		addTerminalInstructions(queue3, 2);
		addTerminalInstructions(queue4, 3);
		addTerminalInstructions(queue5, 4);
		addTerminalInstructions(queue6, 5);
		
		terminalDir0.add(queue1);
		terminalDir0.add(queue2);
		terminalDir0.add(queue3);
		terminalDir0.add(queue4);
		terminalDir0.add(queue5);
		terminalDir0.add(queue6);
		
		
		//RUNWAY 2
		Queue<Instruction> queue11,queue12,queue13,queue14,queue15,queue16;
		queue11 = new LinkedList<Instruction>();
		queue11.add(new Instruction(Direction.SOUTH, 88,86, planeStage.ONRUNWAY, false));
		queue11.add(new Instruction(Direction.WEST, 78, 86, planeStage.TAXING, false));
		queue11.add(new Instruction(Direction.NORTH, 78, 31, planeStage.TAXING, false));
		queue11.add(new Instruction(Direction.WEST, 43, 31, planeStage.TAXING, false));
		queue12 = new LinkedList<Instruction>(queue11);
		queue13 = new LinkedList<Instruction>(queue11);
		queue14 = new LinkedList<Instruction>(queue1);
		queue15 = new LinkedList<Instruction>(queue1);
		queue16 = new LinkedList<Instruction>(queue1);
		
		addTerminalInstructions(queue11, 0);
		addTerminalInstructions(queue12, 1);
		addTerminalInstructions(queue13, 2);
		addTerminalInstructions(queue14, 3);
		addTerminalInstructions(queue15, 4);
		addTerminalInstructions(queue16, 5);
		
		terminalDir1.add(queue11);
		terminalDir1.add(queue12);
		terminalDir1.add(queue13);
		terminalDir1.add(queue14);
		terminalDir1.add(queue15);
		terminalDir1.add(queue16);
		
	}

	private void addTerminalInstructions(Queue<Instruction> queue, int termNum) {
		if(termNum < 3) {
			queue.add(new Instruction(Direction.SOUTH, 43,36 + 5*termNum, planeStage.TAXING, false));
			queue.add(new Instruction(Direction.WEST, 38 , 36 + 5*termNum, planeStage.TAXING, false));
			queue.add(new Instruction(Direction.WEST, 38, 36 + 5*termNum, planeStage.TERMINAL, false));
			queue.add(new Instruction(Direction.EAST, 43, 36, planeStage.TAXING, false));
			queue.add(new Instruction(Direction.NORTH, 43, 31, planeStage.TAXING, true));
		}
		else {
			queue.add(new Instruction(Direction.SOUTH, 43,36 + 5*(termNum-3), planeStage.TAXING, false));
			queue.add(new Instruction(Direction.EAST, 48, 36 + 5*(termNum-3), planeStage.TAXING, false));
			queue.add(new Instruction(Direction.EAST, 48, 36 + 5*(termNum-3), planeStage.TERMINAL, false));
			queue.add(new Instruction(Direction.WEST, 43, 36, planeStage.TAXING, false));
			queue.add(new Instruction(Direction.NORTH, 43, 31, planeStage.TAXING, true));	
		}
	}
	
	private void addToRunwayInstructions() {
		Queue<Instruction> queue = new LinkedList<Instruction>();
		queue.add(new Instruction(Direction.WEST, 37, 31, planeStage.TAXING, false)); //Makes sure the plane turns off inInterSection
		queue.add(new Instruction(Direction.WEST, 11, 31, planeStage.TAXING, false));
		queue.add(new Instruction(Direction.NORTH, 11, 12, planeStage.TAXING, false));
		queue.add(new Instruction(Direction.EAST, 11, 12, planeStage.TAKEOFF, false));
		runwayDir0.add(queue);
		
		Queue<Instruction> queue11 = new LinkedList<Instruction>();
		queue11.add(new Instruction(Direction.NORTH, 43,24, planeStage.TAXING, false));
		queue11.add(new Instruction(Direction.EAST, 88,24, planeStage.TAXING, false));
		queue11.add(new Instruction(Direction.SOUTH, 88,24,planeStage.TAKEOFF, false));
		runwayDir1.add(queue11);
	}
	private void addRectangle(int x1, int x2, int y1, int y2, MapTile tile) {
		for(int x = x1; x <= x2; x++) {
			for(int y = y1; y <= y2; y++) {
				map[x][y] = tile;
			}
		}
		
	}
	
	public void drawMap(GraphicsContext gc, double drawScale) {
		for(int x = 0; x < 100; x++) {
			for(int y =0; y < 100; y++) {
				if(map[x][y] == MapTile.GRASS) 
					gc.setFill(Color.GREEN);
				else if(map[x][y] == MapTile.TAXIWAYEAST ||  
						map[x][y] == MapTile.TAXIWAYNORTH || map[x][y] == MapTile.TAXIWAYSOUTH || map[x][y] == MapTile.TAXIWAY)
					gc.setFill(Color.DARKGREY);
				else if(map[x][y] == MapTile.TAXIWAYWEST)
					gc.setFill(Color.GREY);
				else if(map[x][y] == MapTile.BUILDING) 
					gc.setFill(Color.DARKMAGENTA);
				else if(map[x][y] == MapTile.TERMINAL1 || map[x][y] == MapTile.TERMINAL2 || map[x][y] == MapTile.TERMINAL3 )
					gc.setFill(Color.CORAL);
				else if(map[x][y] == MapTile.RUNWAYWAIT)
					gc.setFill(Color.DARKCYAN);
				else if (map[x][y] == MapTile.RUNWAYSTOP)
					gc.setFill(Color.AQUA);
				else if(map[x][y] == MapTile.INTERSECTION)
					gc.setFill(Color.INDIANRED);
				else 
					gc.setFill(Color.BLACK);
				gc.fillRect(x*drawScale,y*drawScale,drawScale,drawScale);
			}
		}
	}
	
	public MapTile getTile(int x, int y) {return map[x][y];}
	public int getTerminals() {return terminalDir0.size();}

	public Queue<Instruction> termDirections(int i, int runway) {
		if(runway == 0) {
			return terminalDir0.get(i+3);
		}
		else return terminalDir1.get(i+3);
	}
	
	public Queue<Instruction> runwayDirections(int runway, int intersection){
		if(runway == 0)
			return runwayDir0.get(intersection-1);
		else 
			return runwayDir1.get(intersection-1);
	}
}
