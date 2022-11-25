package cc.sferalabs.sfera.drivers.iono_pi;

import java.io.IOException;
import java.util.Locale;

public enum Output {
	O1(cc.sferalabs.libs.iono_pi.IonoPi.Output.O1), O2(cc.sferalabs.libs.iono_pi.IonoPi.Output.O2),
	O3(cc.sferalabs.libs.iono_pi.IonoPi.Output.O3), O4(cc.sferalabs.libs.iono_pi.IonoPi.Output.O4),
	OC1(cc.sferalabs.libs.iono_pi.IonoPi.Output.OC1), OC2(cc.sferalabs.libs.iono_pi.IonoPi.Output.OC2),
	OC3(cc.sferalabs.libs.iono_pi.IonoPi.Output.OC3);

	private final cc.sferalabs.libs.iono_pi.IonoPi.Output pin;
	private final String fileName;

	/**
	 * @param pin
	 */
	Output(cc.sferalabs.libs.iono_pi.IonoPi.Output pin) {
		this.pin = pin;
		this.fileName = (pin.name().startsWith("OC") ? "open_coll" : "relay") + "/"
				+ pin.name().toLowerCase(Locale.ENGLISH);
	}

	/**
	 * @return
	 * @throws IOException
	 */
	boolean isClosed() throws IOException {
		if (IonoPi.USE_KERNEL_MOD) {
			return "1".equals(IonoPi.readSysFsFile(fileName));
		} else {
			return pin.isClosed();
		}
	}

	/**
	 * @param closed
	 * @throws IOException
	 */
	void set(boolean closed) throws IOException {
		if (IonoPi.USE_KERNEL_MOD) {
			IonoPi.writeSysFsFile(fileName, closed ? "1" : "0");
		} else {
			pin.set(closed);
		}
	}

}
