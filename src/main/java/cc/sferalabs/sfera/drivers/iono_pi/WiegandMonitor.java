/*-
 * +======================================================================+
 * Iono Pi
 * ---
 * Copyright (C) 2017 Sfera Labs S.r.l.
 * ---
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * -======================================================================-
 */

package cc.sferalabs.sfera.drivers.iono_pi;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.sferalabs.libs.iono_pi.IonoPi.Wiegand;
import cc.sferalabs.libs.iono_pi.wiegand.WiegandListener;
import cc.sferalabs.sfera.core.services.Task;
import cc.sferalabs.sfera.core.services.TasksManager;
import cc.sferalabs.sfera.drivers.iono_pi.events.WiegandIonoPiEvent;
import cc.sferalabs.sfera.events.Bus;
import cc.sferalabs.sfera.util.files.FilesWatcher;

/**
 *
 * @author Giampiero Baggiani
 *
 */
public class WiegandMonitor {

	private final WiegandListener wiegandListener = new WiegandListener() {

		@Override
		public boolean onData(Wiegand wi, int count, long data) {
			Bus.post(new WiegandIonoPiEvent(driver, wi, count, data));
			return true;
		}
	};

	private final IonoPi driver;
	private final Wiegand wi;
	private final Logger log;
	private final String filePrefix;

	private UUID listenerId;

	/**
	 * @param wi
	 * @param driver
	 */
	WiegandMonitor(Wiegand wi, IonoPi driver) {
		this.log = LoggerFactory.getLogger(getClass().getName() + "." + driver.getId() + "." + wi.name());
		this.driver = driver;
		this.wi = wi;
		this.filePrefix = "wiegand/" + wi.name().toLowerCase(Locale.ENGLISH) + "_";
	}

	/**
	 * @param pulse_itvl_max
	 * @param pulse_itvl_min
	 * @param pulse_width_max
	 * @param pulse_width_min
	 * @throws IOException
	 */
	void init(int pulse_itvl_max, int pulse_itvl_min, int pulse_width_max, int pulse_width_min) throws IOException {
		if (IonoPi.USE_KERNEL_MOD) {
			IonoPi.writeSysFsFile(filePrefix + "pulse_itvl_max", "" + pulse_itvl_max);
			IonoPi.writeSysFsFile(filePrefix + "pulse_itvl_min", "" + pulse_itvl_min);
			IonoPi.writeSysFsFile(filePrefix + "pulse_width_max", "" + pulse_width_max);
			IonoPi.writeSysFsFile(filePrefix + "pulse_width_min", "" + pulse_width_min);
			IonoPi.writeSysFsFile(filePrefix + "enabled", "1");
			IonoPi.readSysFsFile(filePrefix + "data");

			listenerId = FilesWatcher.register(Paths.get("/sys/class/ionopi", filePrefix + "data"),
					"IonoPi." + wi.name(), new Runnable() {

						@Override
						public void run() {
							try {
								String[] ts_bits_data = IonoPi.readSysFsFile(filePrefix + "data").split(" ");
								int bits = Integer.parseInt(ts_bits_data[1]);
								long data = Long.parseLong(ts_bits_data[2]);
								wiegandListener.onData(wi, bits, data);
							} catch (IOException e) {
								log.error("Error reading data", e);
							}
						}
					}, false, false);
		} else {
			TasksManager.execute(new Task("IonoPi." + wi.name()) {

				@Override
				protected void execute() {
					log.debug("Wiegand monitor started");
					try {
						wi.monitor(wiegandListener);
					} catch (Exception e) {
						log.error("Wiegand monitor exception", e);
					}
					log.debug("Wiegand monitor stopped");
				}
			});
		}
	}

	/**
	 * @throws IOException
	 */
	void quit() throws IOException {
		if (IonoPi.USE_KERNEL_MOD) {
			if (listenerId != null) {
				FilesWatcher.unregister(listenerId);
			}
			IonoPi.writeSysFsFile(filePrefix + "enabled", "0");
		}
	}

}
