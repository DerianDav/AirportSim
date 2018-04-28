import java.io.Serializable;

public class Instruction implements Serializable{
	Direction dir;
	int xCord;
	int yCord;
	planeStage stage;
	boolean getInstruction;
	
	public Instruction(Direction dir, int x, int y, planeStage stage, boolean getInstruction) {
		this.dir = dir;
		xCord = x;
		yCord = y;
		this.stage = stage;
		this.getInstruction = getInstruction;
	}
}
