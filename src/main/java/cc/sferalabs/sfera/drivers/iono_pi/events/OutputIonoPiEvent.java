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

import cc.sferalabs.sfera.drivers.iono_pi.IonoPi;
import cc.sferalabs.sfera.drivers.iono_pi.Output;
import cc.sferalabs.sfera.events.BooleanEvent;

/**
 * Event triggered when the state of a relay output changes.
 * 
 * @author Giampiero Baggiani
 * 
 * @sfera.event_id o&lt;n&gt; where &lt;n&gt; is the output's index, e.g. "o1"
 * @sfera.event_val boolean 'true' when the output is closed, 'false' when open
 *
 */
public class OutputIonoPiEvent extends BooleanEvent implements IonoPiEvent {

	private final Output output;

	public OutputIonoPiEvent(IonoPi source, Output output, boolean closed) {
		super(source, output.name().toLowerCase(Locale.ENGLISH), closed);
		this.output = output;
	}

	/**
	 * @return the output
	 */
	public Output getOutput() {
		return output;
	}
}
