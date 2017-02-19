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

import cc.sferalabs.libs.iono_pi.IonoPi.AnalogInput;
import cc.sferalabs.sfera.drivers.iono_pi.IonoPi;
import cc.sferalabs.sfera.events.NumberEvent;

/**
 * Event triggered when the value of an analog input changes.
 * 
 * @author Giampiero Baggiani
 * 
 * @sfera.event_id ai&lt;n&gt; where &lt;n&gt; is the input's index, e.g. "ai1"
 * @sfera.event_val val the voltage value read on the input, in V
 * 
 */
public class AnalogInputIonoPiEvent extends NumberEvent implements IonoPiEvent {

	private final AnalogInput input;

	public AnalogInputIonoPiEvent(IonoPi source, AnalogInput input, float value) {
		super(source, input.toString().toLowerCase(), value);
		this.input = input;
	}

	/**
	 * @return the input
	 */
	public AnalogInput getInput() {
		return input;
	}

}
