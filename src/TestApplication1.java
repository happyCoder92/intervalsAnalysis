
/**
 * And example application you may want to analyze to test your verifier.
 * 
 * @author veselin.raychev@inf.ethz.ch
 *
 */
public class TestApplication1 {
	public static void adjustSpeed() {
		int value = AircraftControl.readSensor(0);
		if (value < 0) {
			value += 10;
		} else {
			value -= 10;
		}
		AircraftControl.adjustValue(0, value);
	}
	
	public static void adjustHeight() {
		int height = AircraftControl.readSensor(1);
		int power = AircraftControl.readSensor(2);
		if (power > 0) {
			if (height < 0) {
				height += power + 1;  // Power can be up to 999, height is <=-1, OK.
			} else {
				height -= power;  // Power can be up to 999, height is >= 0, OK.
			}
		}
		AircraftControl.adjustValue(1, height);
	}
	
	public static void adjustHeightWithBug() {
		int height = AircraftControl.readSensor(1);
		int power = AircraftControl.readSensor(2);
		if (power > 0) {
			if (height < 0) {
				height += power + 1;  // Power can be up to 999, height is <=-1, OK.
			} else {
				height -= power + 1;  // Power can be up to 999, height is >= 0, BUG.
			}
		}
		AircraftControl.adjustValue(1, height);
	}
	
	public static void adjustPressure() {
		int pressure = AircraftControl.readSensor(5);
		for (int i = 0; i < 16 * 1024 * 1024; ++i) {
			pressure = (pressure * 11) ^ i;
		}
		if (pressure < 1000 && pressure > -1000) {
			AircraftControl.adjustValue(5, pressure);
		}
	}
}
