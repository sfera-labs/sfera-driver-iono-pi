package cc.sferalabs.sfera.drivers.iono_pi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cc.sferalabs.libs.iono_pi.IonoPi.DigitalIO;

public abstract class OneWire {

	/**
	 * @return
	 * @throws IOException
	 */
	static List<? extends cc.sferalabs.libs.iono_pi.onewire.OneWireBusDevice> getBusDevices() throws IOException {
		if (IonoPi.USE_KERNEL_MOD) {
			List<OneWireBusDevice> list;
			Path devsPath = Paths.get("/sys/bus/w1/devices/");
			try (Stream<Path> paths = Files.walk(devsPath, 1)) {
				list = paths.filter(p -> !p.equals(devsPath)).filter(Files::isDirectory).map(Path::getFileName)
						.map(Path::toString).filter(id -> !id.startsWith("w")).map(OneWireBusDevice::new)
						.collect(Collectors.toList());
			}
			return list;
		} else {
			return cc.sferalabs.libs.iono_pi.IonoPi.OneWire.getBusDevices();
		}
	}

	/**
	 * @param ttl
	 * @param attempts
	 * @return
	 * @throws IOException
	 */
	static int[] maxDetectRead(DigitalIO ttl, int attempts) throws IOException {
		if (IonoPi.USE_KERNEL_MOD) {
			throw new IOException("not implemented");
		} else {
			return cc.sferalabs.libs.iono_pi.IonoPi.OneWire.maxDetectRead(ttl, attempts);
		}
	}

}
