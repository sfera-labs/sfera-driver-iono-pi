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

import cc.sferalabs.libs.iono_pi.IonoPi.Wiegand;
import cc.sferalabs.sfera.drivers.iono_pi.IonoPi;
import cc.sferalabs.sfera.events.NumberEvent;

/**
 * Event triggered when data is read from a Wiegand interface.
 * 
 * @author Giampiero Baggiani
 * 
 * @sfera.event_id wiegand.i&lt;n&gt; where &lt;n&gt; is the interface's index,
 *                 i.e. "wiegand.i1" or "wiegand.i2"
 * @sfera.event_val data number (long) holding the read data
 *
 */
public class WiegandIonoPiEvent extends NumberEvent implements IonoPiEvent {

	private final Wiegand wInterface;
	private final int bitsCount;

	public WiegandIonoPiEvent(IonoPi source, Wiegand wInterface, int bitsCount, long data) {
		super(source, "wiegand.i" + (wInterface.ordinal() + 1), data);
		this.wInterface = wInterface;
		this.bitsCount = bitsCount;
	}

	/**
	 * @return the Wiegand interface
	 */
	public Wiegand getWiegandInterface() {
		return wInterface;
	}

	/**
	 * @return the number of bits received
	 */
	public int getBitsCount() {
		return bitsCount;
	}

}
