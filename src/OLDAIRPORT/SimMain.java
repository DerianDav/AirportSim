package OLDAIRPORT;
import Airport;
import Plane;

public class SimMain {

	private static Airport airport;
	private static Plane plane1;
	public SimMain() {
		airport = new Airport();
		plane1 = new Plane(airport);
		Plane plane2 = new Plane(airport);
	}
}
