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

package cc.sferalabs.sfera.drivers.iono_pi.events;

import java.io.IOException;

import cc.sferalabs.libs.iono_pi.onewire.OneWireBusDevice;
import cc.sferalabs.sfera.drivers.iono_pi.IonoPi;
import cc.sferalabs.sfera.events.NumberEvent;

/**
 * Event triggered when the value read from a device on the 1-Wire bus changes.
 * 
 * @author Giampiero Baggiani
 * 
 * @sfera.event_id onewire.bus.d&lt;id&gt; where &lt;id&gt; is the ID of the 1-Wire
 *                 device (e.g. 'onewire.bus.d28_00000aabbccc')
 * @sfera.event_val val value read from the device
 *
 */
public class OneWireBusDeviceIonoPiEvent extends NumberEvent implements IonoPiEvent {

	private final OneWireBusDevice device;

	public OneWireBusDeviceIonoPiEvent(IonoPi source, OneWireBusDevice device) throws IOException {
		super(source, "onewire.bus.d" + device.getId().replace('-', '_'), device.readTemperature(3) / 1000.0);
		this.device = device;
	}

	/**
	 * @return the device
	 */
	public OneWireBusDevice getDevice() {
		return device;
	}
}
