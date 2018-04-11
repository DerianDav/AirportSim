package OLDAIRPORT;

import java.awt.Point;

public class TaxiWay extends Pathway {
	private int num;
	public boolean inUse;
	
	public TaxiWay(int num, double x1Cord, double x2Cord, double y1Cord, double y2Cord) {
		super(x1Cord,x2Cord,y1Cord,y2Cord);
		this.num = num;
		inUse = false;
	}
	
}
