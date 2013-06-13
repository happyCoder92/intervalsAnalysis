
public class TestApplication4 {
	public static void adjustSpeed() {
		int value = AircraftControl.readSensor(0);
		if (0 > value) {
			value += 10;
		} else {
			value -= 10;
		}
		AircraftControl.adjustValue(0, value);
	}
	
	public static void adjustSpeedWithBug() {
		int value = AircraftControl.readSensor(0);
		if (0 < value) {
			value += 10;
		} else {
			value -= 10;
		}
		AircraftControl.adjustValue(0, value);
	}
	
	public static void adjustBug() {
		int value = AircraftControl.readSensor(0);
		if (1 < 2) {
			value += 10;
		}
		AircraftControl.adjustValue(0, value);
	}
	
	@SuppressWarnings("all")
	public static void adjustNoBug() {
		int value = AircraftControl.readSensor(0);
		if (1 > 2) {
			value += 10;
		}
		AircraftControl.adjustValue(0, value);
	}
}
