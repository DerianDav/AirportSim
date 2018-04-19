public enum MapTile{
		GRASS(-1,-1), 
		RUNWAY(-1,-1), RUNWAYEXITSOUTH(-1,-1),
		TAXIWAYSOUTH(-1,-1), TAXIWAYNORTH(-1,-1), TAXIWAYEAST(-1,-1), TAXIWAYWEST(-1,-1),
		TAXIWAY(-1,-1),
		TERMINAL1(-1,0), TERMINAL2(-1,1), TERMINAL3(-1,2),
		TERMINAL4(-1,3), TERMINAL5(-1,4), TERMINAL6(-1,5),  
		TERMINALSTOP(-1,-1), TERMINALEXITNORTH(-1,-1),
		STOP(-1,-1), RUNWAYWAIT(-1,-1), RUNWAYSTOP(-1,-1),
		BUILDING(-1,-1),
		INTERSECTION(0, -1), INTERSECTIONA1(1, -1), I2(0, -1);

	int intersectionNum;
	int terminalNum;
	
	MapTile(int intersectionNum, int terminalNum) {
		this.intersectionNum = intersectionNum;
		this.terminalNum = terminalNum;
	}
	}
	 