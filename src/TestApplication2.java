
public class TestApplication2 {

	public static void wideningTestN() {
		int i = 0;
		int j = 0;
		do {
			i += 2;
			if (i > 50)
				++i;
			j = i&16;
		} while ((i & 1) == 0);
		AircraftControl.adjustValue(j, 5);
	}
	
	public static void wideningTest() {
		int a = AircraftControl.readSensor(5);
		int i = 0;
		while( i <= 20 ) {
			while( a <= 998 ) {
				a++;
			}
			a = 0;
			++i;
		}
		AircraftControl.adjustValue(5, a);
	}
	
	// safe
	public static void wideningTest1() {
		int pressure = AircraftControl.readSensor(5);
		int todo = 0;
		while( todo <= 20 ) {
			while( pressure <= 998 ) {
				pressure++;
			}
			pressure = 0;
			todo += 2;
		}
		AircraftControl.adjustValue(5, pressure);
	}

	// unsafe
	public static void wideningTest2() {
		int i = 0;
		while(10000 >= i) {
			int j = 0;
			while(10 >= j) {
				i = (j % 2 == 0) ? i+2 : i-1;
				j++;
			} 
		}
		AircraftControl.adjustValue(2, i);
	}

	// safe
	public static void unreacheableTest(){
		// maybe could use branches() to handle this?
		int a =AircraftControl.readSensor(0);
		int b = 1000;
		if(a == 1000){
			// Do nothing
		}else{
			b-=1;
		}
		AircraftControl.adjustValue(0, b);
	}

	// safe
	public static void branchingTest() { 
		int x = 10;
		if(x < 0) {
			x = -1000;
		} else {
			x = 15;
		}
		AircraftControl.adjustValue(x, x);
	}
}
