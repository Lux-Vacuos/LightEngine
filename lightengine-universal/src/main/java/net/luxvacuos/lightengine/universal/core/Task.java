/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2018 Lux Vacuos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.luxvacuos.lightengine.universal.core;

public abstract class Task<V> {

	private volatile boolean done;
	private V value;
	private Thread t;

	public boolean isDone() {
		return done;
	}

	public V get() {
		if (!done) {
			t = Thread.currentThread();
			try {
				Thread.sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
			}
		}
		return value;
	}

	public void onCompleted(V value) {
	}

	/**
	 * <b>INTERNAL FUNCTION</b>
	 */
	public void callI() {
		if (done)
			return;
		value = call();
		done = true;
		if (t != null)
			t.interrupt();
		onCompleted(value);
	}

	protected abstract V call();

}
