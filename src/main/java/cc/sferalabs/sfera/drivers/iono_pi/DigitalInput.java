package cc.sferalabs.sfera.drivers.iono_pi;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

import cc.sferalabs.libs.iono_pi.DigitalInputListener;
import cc.sferalabs.sfera.util.files.FilesWatcher;

public enum DigitalInput {
	DI1(cc.sferalabs.libs.iono_pi.IonoPi.DigitalInput.DI1), DI2(cc.sferalabs.libs.iono_pi.IonoPi.DigitalInput.DI2),
	DI3(cc.sferalabs.libs.iono_pi.IonoPi.DigitalInput.DI3), DI4(cc.sferalabs.libs.iono_pi.IonoPi.DigitalInput.DI4),
	DI5(cc.sferalabs.libs.iono_pi.IonoPi.DigitalInput.DI5), DI6(cc.sferalabs.libs.iono_pi.IonoPi.DigitalInput.DI6);

	private final cc.sferalabs.libs.iono_pi.IonoPi.DigitalInput pin;
	private final String debFileName;
	private UUID listenerId;

	/**
	 * @param pin
	 */
	DigitalInput(cc.sferalabs.libs.iono_pi.IonoPi.DigitalInput pin) {
		this.pin = pin;
		this.debFileName = "digital_in/" + pin.name().toLowerCase(Locale.ENGLISH) + "_deb";
	}

	/**
	 * @param millis
	 * @throws IOException
	 */
	void setDebounce(int millis) throws IOException {
		if (IonoPi.USE_KERNEL_MOD) {
			IonoPi.writeSysFsFile(debFileName + "_on_ms", "" + millis);
			IonoPi.writeSysFsFile(debFileName + "_off_ms", "" + millis);
		} else {
			pin.setDebounce(millis);
		}
	}

	/**
	 * @param listener
	 * @throws IOException
	 */
	void setListener(DigitalInputListener listener) throws IOException {
		if (IonoPi.USE_KERNEL_MOD) {
			if (listenerId != null) {
				FilesWatcher.unregister(listenerId);
			}
			if (listener != null) {
				listener.onChange(pin, isHigh());
				listenerId = FilesWatcher.register(Paths.get("/sys/class/ionopi", debFileName), "IonoPi." + debFileName,
						new Runnable() {

							@Override
							public void run() {
								try {
									listener.onChange(pin, isHigh());
								} catch (IOException e) {
								}
							}
						}, false, false);
			} else {
				listenerId = null;
			}
		} else {
			pin.setListener(listener);
		}
	}

	/**
	 * @return
	 * @throws IOException
	 */
	boolean isHigh() throws IOException {
		if (IonoPi.USE_KERNEL_MOD) {
			return "1".equals(IonoPi.readSysFsFile(debFileName));
		} else {
			return pin.isHigh();
		}
	}
}
