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
	private final int rawValue;

	public AnalogInputIonoPiEvent(IonoPi source, AnalogInput input, int value) {
		super(source, input.toString().toLowerCase(), toVoltage(input, value));
		this.input = input;
		this.rawValue = value;
	}

	/**
	 * 
	 * @param input
	 * @param value
	 * @return
	 */
	private static float toVoltage(AnalogInput input, int value) {
		float factor;
		if (input == AnalogInput.AI1 || input == AnalogInput.AI2) {
			factor = 0.007319f;
		} else {
			factor = 0.000725f;
		}

		return value * factor;
	}

	/**
	 * @return the input
	 */
	public AnalogInput getInput() {
		return input;
	}

	/**
	 * @return the raw analog value read from the input
	 */
	public int getRawValue() {
		return rawValue;
	}

}
