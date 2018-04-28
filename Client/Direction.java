

import java.io.Serializable;

public enum Direction implements Serializable{
	WEST(-1,0),EAST(1,0),NORTH(0,-1),SOUTH(0,1);
	
	int x, y;
	
	Direction(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
 