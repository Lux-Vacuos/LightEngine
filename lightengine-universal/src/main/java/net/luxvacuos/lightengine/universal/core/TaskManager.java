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

import java.util.LinkedList;
import java.util.Queue;

import com.badlogic.gdx.utils.async.AsyncExecutor;

public class TaskManager {

	public static TaskManager tm;

	private Queue<Runnable> tasksMainThread = new LinkedList<>(), tasksBackgroundThread = new LinkedList<>();
	private AsyncExecutor asyncExecutor;
	private Thread backgroundThread;
	private boolean syncInterrupt;

	public void init() {
		asyncExecutor = new AsyncExecutor(2);
		backgroundThread = new Thread(() -> {
			while (true) {
				if (!tasksBackgroundThread.isEmpty()) {
					tasksBackgroundThread.poll().run();
				} else {
					try {
						syncInterrupt = false;
						Thread.sleep(1000000l);
					} catch (InterruptedException e) {
					}
				}
			}
		});
		backgroundThread.setDaemon(true);
		backgroundThread.setName("Background Thread");
		backgroundThread.start();
	}

	public void addTaskMainThread(Runnable task) {
		if (task != null)
			tasksMainThread.add(task);
	}

	public void addTaskBackgroundThread(Runnable task) {
		if (task != null) {
			tasksBackgroundThread.add(task);
			if (!syncInterrupt) {
				syncInterrupt = true;
				backgroundThread.interrupt();
			}
		}
	}

	public void addTaskRenderThread(Runnable task) {
		throw new UnsupportedOperationException();
	}

	public void addTaskRenderBackgroundThread(Runnable task) {
		throw new UnsupportedOperationException();
	}

	public void updateMainThread() {
		if (!tasksMainThread.isEmpty()) {
			tasksMainThread.poll().run();
		}
	}

	public AsyncExecutor getAsyncExecutor() {
		return asyncExecutor;
	}

	public boolean isEmpty() {
		return tasksMainThread.isEmpty() && tasksBackgroundThread.isEmpty();
	}

}
