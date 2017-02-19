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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import cc.sferalabs.libs.iono_pi.DigitalInputListener;
import cc.sferalabs.libs.iono_pi.IonoPi.AnalogInput;
import cc.sferalabs.libs.iono_pi.IonoPi.DigitalIO;
import cc.sferalabs.libs.iono_pi.IonoPi.DigitalInput;
import cc.sferalabs.libs.iono_pi.IonoPi.Output;
import cc.sferalabs.libs.iono_pi.IonoPi.Wiegand;
import cc.sferalabs.libs.iono_pi.onewire.OneWireBusDevice;
import cc.sferalabs.sfera.core.Configuration;
import cc.sferalabs.sfera.core.services.Task;
import cc.sferalabs.sfera.core.services.TasksManager;
import cc.sferalabs.sfera.drivers.Driver;
import cc.sferalabs.sfera.drivers.iono_pi.events.AnalogInputIonoPiEvent;
import cc.sferalabs.sfera.drivers.iono_pi.events.DigitalInputIonoPiEvent;
import cc.sferalabs.sfera.drivers.iono_pi.events.LedIonoPiEvent;
import cc.sferalabs.sfera.drivers.iono_pi.events.OneWireBusDeviceIonoPiEvent;
import cc.sferalabs.sfera.drivers.iono_pi.events.OneWireMaxHumidityIonoPiEvent;
import cc.sferalabs.sfera.drivers.iono_pi.events.OneWireMaxTemperatureIonoPiEvent;
import cc.sferalabs.sfera.drivers.iono_pi.events.OutputIonoPiEvent;
import cc.sferalabs.sfera.events.Bus;

/**
 *
 * @author Giampiero Baggiani
 *
 */
public class IonoPi extends Driver {

	private final static long ONE_WIRE_READ_INTERVAL = 2 * 1000;
	private final static long OUTPUTS_READ_INTERVAL = 5 * 60 * 1000;

	private final IonoPi thisIonoPi;

	private final DigitalInputListener digitalInputslistener = new DigitalInputListener() {

		@Override
		public void onChange(DigitalInput di, boolean high) {
			Bus.post(new DigitalInputIonoPiEvent(thisIonoPi, di, high));
		}
	};

	private boolean oneWireBus;
	private boolean oneWireMax;
	private List<DigitalIO> oneWireMaxPins;

	private long readInterval;

	private long lastOneWireRead;
	private long lastOutputsRead;

	public IonoPi(String id) {
		super(id);
		thisIonoPi = this;
	}

	@Override
	protected boolean onInit(Configuration config) throws InterruptedException {
		try {
			cc.sferalabs.libs.iono_pi.IonoPi.init();
		} catch (Exception e) {
			log.error("Initialization error", e);
			return false;
		}

		readInterval = config.get("read_interval", 2000);
		boolean w1 = config.get("w1", false);
		boolean w2 = config.get("w2", false);
		oneWireBus = config.get("one_wire_bus", false);
		Object oneWireMaxPinsConf = config.get("one_wire_max", null);
		if (oneWireMaxPinsConf == null) {
			oneWireMax = false;
		} else {
			oneWireMax = true;
			try {
				if (oneWireMaxPinsConf instanceof Number) {
					oneWireMaxPinsConf = Arrays.asList((int) oneWireMaxPinsConf);
				}
				@SuppressWarnings("unchecked")
				List<Integer> list = (List<Integer>) oneWireMaxPinsConf;
				oneWireMaxPins = new ArrayList<>(list.size());
				for (Integer ttl : list) {
					DigitalIO dio = DigitalIO.values()[ttl - 1];
					oneWireMaxPins.add(dio);
				}
			} catch (Exception e) {
				log.error("Illegal 1-Wire max configuration", e);
				return false;
			}
		}

		if (w1) {
			if (oneWireBus) {
				log.error("Cannot use Wiegand 1 while 1-Wire bus is enabled");
				return false;
			}
			if (oneWireMax && (oneWireMaxPins.contains(DigitalIO.TTL1)
					|| oneWireMaxPins.contains(DigitalIO.TTL2))) {
				log.error("Cannot use both Wiegand and 1-Wire max on TTL1 and TTL2");
				return false;
			}
		}
		if (w2) {
			if (oneWireMax && (oneWireMaxPins.contains(DigitalIO.TTL3)
					|| oneWireMaxPins.contains(DigitalIO.TTL4))) {
				log.error("Cannot use both Wiegand and 1-Wire max on TTL3 and TTL4");
				return false;
			}
		}
		if (oneWireBus && oneWireMax && oneWireMaxPins.contains(DigitalIO.TTL1)) {
			log.error("Cannot use both 1-Wire bus and 1-Wire max on TTL1");
			return false;
		}

		for (DigitalInput di : DigitalInput.values()) {
			di.setListener(digitalInputslistener);
			Bus.post(new DigitalInputIonoPiEvent(this, di, di.isHigh()));
		}

		if (w1) {
			TasksManager.execute(new WiegandMonitor(Wiegand.W1, this));
		}
		if (w2) {
			TasksManager.execute(new WiegandMonitor(Wiegand.W2, this));
		}

		if (oneWireBus) {
			log.debug("Enabled 1-Wire bus");
		}

		if (oneWireMax) {
			for (DigitalIO dio : oneWireMaxPins) {
				log.debug("Enabled 1-Wire max on " + dio.toString());
			}
		}

		return true;
	}

	@Override
	protected boolean loop() throws InterruptedException {
		try {
			for (AnalogInput ai : AnalogInput.values()) {
				Bus.postIfChanged(new AnalogInputIonoPiEvent(this, ai, ai.read()));
			}

			if (System.currentTimeMillis() > lastOutputsRead + OUTPUTS_READ_INTERVAL) {
				Bus.postIfChanged(new OutputIonoPiEvent(this, Output.O1, Output.O1.isClosed()));
				Bus.postIfChanged(new OutputIonoPiEvent(this, Output.O2, Output.O2.isClosed()));
				Bus.postIfChanged(new OutputIonoPiEvent(this, Output.O3, Output.O3.isClosed()));
				Bus.postIfChanged(new OutputIonoPiEvent(this, Output.O4, Output.O4.isClosed()));
				Bus.postIfChanged(
						new LedIonoPiEvent(this, cc.sferalabs.libs.iono_pi.IonoPi.LED.isOn()));
				lastOutputsRead = System.currentTimeMillis();
			}

			if ((oneWireBus || oneWireMax)
					&& System.currentTimeMillis() > lastOneWireRead + ONE_WIRE_READ_INTERVAL) {
				TasksManager.execute(new Task("IonoPi1WireMonitor-") {

					@Override
					protected void execute() {
						if (oneWireBus) {
							try {
								for (OneWireBusDevice d : cc.sferalabs.libs.iono_pi.IonoPi.OneWire
										.getBusDevices()) {
									Bus.postIfChanged(
											new OneWireBusDeviceIonoPiEvent(thisIonoPi, d));
								}
							} catch (Exception e) {
								log.warn("1-Wire bus read error", e);
							}
						}
						if (oneWireMax) {
							for (DigitalIO dio : oneWireMaxPins) {
								int[] t_rh;
								try {
									t_rh = cc.sferalabs.libs.iono_pi.IonoPi.OneWire
											.maxDetectRead(dio, 7);
									if (t_rh != null) {
										Bus.postIfChanged(new OneWireMaxTemperatureIonoPiEvent(
												thisIonoPi, dio, t_rh[0]));
										Bus.postIfChanged(new OneWireMaxHumidityIonoPiEvent(
												thisIonoPi, dio, t_rh[1]));
									}
								} catch (IOException e) {
									log.warn("1-Wire max read error", e);
								}

							}
						}
						lastOneWireRead = System.currentTimeMillis();
					}
				});
			}
		} catch (Exception e) {
			log.error("Loop error", e);
			return false;
		}

		Thread.sleep(readInterval);

		return true;
	}

	@Override
	protected void onQuit() {
		cc.sferalabs.libs.iono_pi.IonoPi.shutdown();
	}

	/**
	 * Sets the state of the green LED
	 * 
	 * @param on
	 *            {@code true} for on, {@code false} for off
	 */
	public void setLed(boolean on) {
		cc.sferalabs.libs.iono_pi.IonoPi.LED.set(on);
		Bus.postIfChanged(new LedIonoPiEvent(this, on));
	}

	/**
	 * @param o
	 * @param closed
	 */
	private void setO(Output o, boolean closed) {
		o.set(closed);
		Bus.postIfChanged(new OutputIonoPiEvent(this, o, closed));
	}

	/**
	 * Sets the state of relay O1
	 * 
	 * @param closed
	 *            {@code true} to close the relay, {@code false} to open it
	 */
	public void setO1(boolean closed) {
		setO(Output.O1, closed);
	}

	/**
	 * Sets the state of relay O2
	 * 
	 * @param closed
	 *            {@code true} to close the relay, {@code false} to open it
	 */
	public void setO2(boolean closed) {
		setO(Output.O2, closed);
	}

	/**
	 * Sets the state of relay O3
	 * 
	 * @param closed
	 *            {@code true} to close the relay, {@code false} to open it
	 */
	public void setO3(boolean closed) {
		setO(Output.O3, closed);
	}

	/**
	 * Sets the state of relay O4
	 * 
	 * @param closed
	 *            {@code true} to close the relay, {@code false} to open it
	 */
	public void setO4(boolean closed) {
		setO(Output.O4, closed);
	}

	/**
	 * Sets the state of open collector OC1
	 * 
	 * @param closed
	 *            {@code true} to close the open collector, {@code false} to
	 *            open it
	 */
	public void setOc1(boolean closed) {
		Output.OC1.set(closed);
	}

	/**
	 * Sets the state of open collector OC2
	 * 
	 * @param closed
	 *            {@code true} to close the open collector, {@code false} to
	 *            open it
	 */
	public void setOc2(boolean closed) {
		Output.OC2.set(closed);
	}

	/**
	 * Sets the state of open collector OC3
	 * 
	 * @param closed
	 *            {@code true} to close the open collector, {@code false} to
	 *            open it
	 */
	public void setOc3(boolean closed) {
		Output.OC3.set(closed);
	}

	/**
	 * Returns the state of the green LED
	 * 
	 * @return {@code true} if the LED is on, {@code false} otherwise
	 */
	public boolean isLedOn() {
		return cc.sferalabs.libs.iono_pi.IonoPi.LED.isOn();
	}

	/**
	 * Returns the state of the specified relay
	 * 
	 * @param index
	 *            the index of the relay to address (1 to 4)
	 * @return {@code true} if the relay is closed, {@code false} otherwise
	 */
	public boolean isRelClosed(int index) {
		Output o;
		switch (index) {
		case 1:
			o = Output.O1;
			break;

		case 2:
			o = Output.O2;
			break;

		case 3:
			o = Output.O3;
			break;

		case 4:
			o = Output.O4;
			break;

		default:
			throw new IndexOutOfBoundsException("" + index);
		}

		return o.isClosed();
	}

	/**
	 * Returns the state of the specified digital input
	 * 
	 * @param index
	 *            the index of the digital input to address (1 to 6)
	 * @return {@code true} if the digital input is high, {@code false}
	 *         otherwise
	 */
	public boolean isDiHigh(int index) {
		return DigitalInput.values()[index - 1].isHigh();
	}

	/**
	 * Returns the date/time provided by the hardware RTC
	 * 
	 * @return the date/time provided by the hardware RTC
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws InterruptedException
	 *             if the current thread is interrupted
	 */
	public String getHwClockDate() throws IOException, InterruptedException {
		Process p = new ProcessBuilder("hwclock", "-r").start();
		if (p.waitFor() != 0) {
			throw new IOException("abnormal termination");
		}
		InputStream is = p.getInputStream();
		if (is == null) {
			throw new IOException("no output");
		}
		try (Scanner s = new Scanner(is)) {
			s.useDelimiter("\\A");
			if (!s.hasNext()) {
				throw new IOException("empty output");
			}
			return s.next().trim();
		}
	}

}
