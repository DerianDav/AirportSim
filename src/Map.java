
import java.awt.Point;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Map {
	
	private MapTile[][] map;
	public Point runway0Entry;
	
	public Map() {
		map = new MapTile[100][100];
		for(int x = 0; x < 100; x++) {
			for(int y =0; y < 100; y++)
				map[x][y] = MapTile.GRASS;
		}
		
		for(int x = 10; x < 71; x++) {
			for(int y = 10; y < 15; y++) {
				if(x > 65)
					map[x][y] = MapTile.RUNWAYEXITSOUTH;
				else
					map[x][y] = MapTile.RUNWAY;
			}
		}
		runway0Entry = new Point();
		runway0Entry.setLocation(0,12);
		
	}
	
	
	public void drawMap(GraphicsContext gc, double drawScale) {
		for(int x = 0; x < 100; x++) {
			for(int y =0; y < 100; y++) {
				if(map[x][y] == MapTile.GRASS) 
					gc.setFill(Color.GREEN);
				else 
					gc.setFill(Color.BLACK);
				gc.fillRect(x*drawScale,y*drawScale,drawScale,drawScale);
			}
		}
	}
	
	public MapTile getTile(int x, int y) {return map[x][y];}
}
