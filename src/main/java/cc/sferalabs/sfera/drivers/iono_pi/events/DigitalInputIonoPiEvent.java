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

import cc.sferalabs.libs.iono_pi.IonoPi.DigitalInput;
import cc.sferalabs.sfera.drivers.iono_pi.IonoPi;
import cc.sferalabs.sfera.events.BooleanEvent;

/**
 * Event triggered when the state of a digital input changes.
 * 
 * @author Giampiero Baggiani
 * 
 * @sfera.event_id di&lt;n&gt; where &lt;n&gt; is the input's index, e.g. "di1"
 * @sfera.event_val boolean 'true' when the input is high, 'false' when low
 *
 */
public class DigitalInputIonoPiEvent extends BooleanEvent implements IonoPiEvent {

	private final DigitalInput input;

	public DigitalInputIonoPiEvent(IonoPi source, DigitalInput input, boolean high) {
		super(source, input.toString().toLowerCase(), high);
		this.input = input;
	}

	/**
	 * @return the input
	 */
	public DigitalInput getInput() {
		return input;
	}

}
