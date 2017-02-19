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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.sferalabs.libs.iono_pi.IonoPi.Wiegand;
import cc.sferalabs.libs.iono_pi.wiegand.WiegandListener;
import cc.sferalabs.sfera.core.services.Task;
import cc.sferalabs.sfera.drivers.iono_pi.events.WiegandIonoPiEvent;
import cc.sferalabs.sfera.events.Bus;

/**
 *
 * @author Giampiero Baggiani
 *
 */
public class WiegandMonitor extends Task {

	private final WiegandListener wiegandListener = new WiegandListener() {

		@Override
		public boolean onData(Wiegand wi, int count, long data) {
			Bus.post(new WiegandIonoPiEvent(driver, wi, count, data));
			return true; // TODO
		}
	};

	private final IonoPi driver;
	private final Wiegand wi;
	private final Logger log;

	/**
	 * @param wi
	 * @param driver
	 */
	WiegandMonitor(Wiegand wi, IonoPi driver) {
		super("IonoPiWiegandMonitor-" + driver.getId() + "-" + wi.toString());
		this.log = LoggerFactory.getLogger(getClass().getName() + "." + driver.getId());
		this.driver = driver;
		this.wi = wi;
	}

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

}
