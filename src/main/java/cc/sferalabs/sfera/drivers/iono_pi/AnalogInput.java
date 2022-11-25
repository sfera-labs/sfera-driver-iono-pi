package cc.sferalabs.sfera.drivers.iono_pi;

import java.io.IOException;
import java.util.Locale;

public enum AnalogInput {
	AI1(cc.sferalabs.libs.iono_pi.IonoPi.AnalogInput.AI1), AI2(cc.sferalabs.libs.iono_pi.IonoPi.AnalogInput.AI2),
	AI3(cc.sferalabs.libs.iono_pi.IonoPi.AnalogInput.AI3), AI4(cc.sferalabs.libs.iono_pi.IonoPi.AnalogInput.AI4);

	private final cc.sferalabs.libs.iono_pi.IonoPi.AnalogInput pin;
	private final String fileName;

	/**
	 * @param pin
	 */
	AnalogInput(cc.sferalabs.libs.iono_pi.IonoPi.AnalogInput pin) {
		this.pin = pin;
		this.fileName = "analog_in/" + pin.name().toLowerCase(Locale.ENGLISH) + "_raw";
	}

	/**
	 * @return
	 * @throws IOException
	 */
	int read() throws IOException {
		if (IonoPi.USE_KERNEL_MOD) {
			return Integer.parseInt(IonoPi.readSysFsFile(fileName));
		} else {
			return pin.read();
		}
	}

}
