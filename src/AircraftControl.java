
class AircraftControl {
	public static native int readSensor(int sensorId);
	public static native void adjustValue(int sensorId, int newValue);
}
