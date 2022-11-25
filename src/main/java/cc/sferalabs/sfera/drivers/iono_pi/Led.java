package cc.sferalabs.sfera.drivers.iono_pi;

import java.io.IOException;

public abstract class Led {

	/**
	 * @return
	 * @throws IOException
	 */
	static boolean isOn() throws IOException {
		if (IonoPi.USE_KERNEL_MOD) {
			return "1".equals(IonoPi.readSysFsFile("led/status"));
		} else {
			return cc.sferalabs.libs.iono_pi.IonoPi.LED.isOn();
		}
	}

	/**
	 * @param on
	 * @throws IOException
	 */
	static void set(boolean on) throws IOException {
		if (IonoPi.USE_KERNEL_MOD) {
			IonoPi.writeSysFsFile("led/status", on ? "1" : "0");
		} else {
			cc.sferalabs.libs.iono_pi.IonoPi.LED.set(on);
		}
	}

}
