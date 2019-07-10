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
import cc.sferalabs.sfera.events.ObjectEvent;

/**
 * Event triggered when data is read from a Wiegand interface.
 * 
 * @author Giampiero Baggiani
 * 
 * @sfera.event_id wiegand.i&lt;n&gt; where &lt;n&gt; is the interface's index,
 *                 i.e. "wiegand.i1" or "wiegand.i2"
 * @sfera.event_val data number (long) holding the read data or string
 *                  {@code "<bits_count>/<data_value>"} if option
 *                  {@code wiegand_event_bits_count} set to {@code true}
 *
 */
public class WiegandIonoPiEvent extends ObjectEvent implements IonoPiEvent {

	private final Wiegand wInterface;
	private final int bitsCount;
	private final long data;

	public WiegandIonoPiEvent(IonoPi source, Wiegand wInterface, int bitsCount, long data) {
		super(source, "wiegand.i" + (wInterface.ordinal() + 1),
				source.getWiegandEventBitsCountOption() ? bitsCount + "/" + data : data);
		this.wInterface = wInterface;
		this.bitsCount = bitsCount;
		this.data = data;
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

	/**
	 * @return the data received
	 */
	public long getData() {
		return data;
	}

}
