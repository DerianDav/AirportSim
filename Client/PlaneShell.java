

import java.io.Serializable;
import java.util.Queue;

public class PlaneShell implements Serializable{
	int planeNum;
	public double xCord;
	public double yCord;
	public double xVel;
	public double yVel;
	public boolean flying;
	public Direction curDir;
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
	public int rColor;
	public int gColor;
	public int bColor;
	public PlaneShell(Plane plane) {
		planeNum = plane.planeNum;
		xCord = plane.getX();
		yCord = plane.getY();
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
	}
}
