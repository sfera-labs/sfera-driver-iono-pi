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

import java.util.Locale;

import cc.sferalabs.libs.iono_pi.IonoPi.DigitalIO;
import cc.sferalabs.sfera.drivers.iono_pi.IonoPi;
import cc.sferalabs.sfera.events.NumberEvent;

/**
 * Event triggered when the relative humidity value read from the MaxDetect
 * 1-Wire device changes.
 * 
 * @author Giampiero Baggiani
 * 
 * @sfera.event_id onewire.max.&lt;ttl&gt;.rh where &lt;ttl&gt; is the pin the
 *                 device is connected to
 * @sfera.event_val the relative humidity value read, in %
 *
 */
public class OneWireMaxHumidityIonoPiEvent extends NumberEvent implements IonoPiEvent {

	private final int ttlIndex;

	public OneWireMaxHumidityIonoPiEvent(IonoPi source, DigitalIO dio, int val) {
		super(source, "onewire.max." + dio.name().toLowerCase(Locale.ENGLISH) + ".rh", val / 10.0);
		this.ttlIndex = dio.ordinal() + 1;
	}

	/**
	 * @return the TTL pin index
	 */
	public int getTtlIndex() {
		return ttlIndex;
	}

}
