package cc.sferalabs.sfera.drivers.iono_pi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class OneWireBusDevice extends cc.sferalabs.libs.iono_pi.onewire.OneWireBusDevice {

	protected OneWireBusDevice(String id) {
		super(id);
	}

	@Override
	public int readTemperature(int attempts) throws IOException {
		if (IonoPi.USE_KERNEL_MOD) {
			for (int i = 0; i < attempts; i++) {
				try {
					return Integer.parseInt(
							new String(Files.readAllBytes(Paths.get("/sys/bus/w1/devices", getId(), "temperature")),
									StandardCharsets.UTF_8).trim());
				} catch (Exception e) {
				}
			}
			throw new IOException(getId() + ": Could not read");
		} else {
			return super.readTemperature(attempts);
		}
	}

}
