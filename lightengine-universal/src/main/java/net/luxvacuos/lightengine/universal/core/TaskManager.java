/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2017 Lux Vacuos
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

public final class TaskManager {

	private TaskManager() {
	}

	private static Queue<Runnable> tasks = new LinkedList<>(), tasksAsync = new LinkedList<>(),
			updateThreadTasks = new LinkedList<>();
	private static AsyncExecutor asyncExecutor;
	private static Thread asyncThread;

	public static void init() {
		asyncExecutor = new AsyncExecutor(2);
		asyncThread = new Thread(() -> {
			while (true) {
				if (!tasksAsync.isEmpty()) {
					tasksAsync.poll().run();
				} else {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
				}
			}
		});
		asyncThread.setDaemon(true);
		asyncThread.setName("Async Thread");
		asyncThread.start();
	}

	public static void update() {
		if (!tasks.isEmpty()) {
			tasks.poll().run();
		}
	}

	public static void updateThread() {
		if (!updateThreadTasks.isEmpty()) {
			updateThreadTasks.poll().run();
		}
	}

	public static void addTask(Runnable task) {
		if (task != null)
			tasks.add(task);
	}

	public static void addTaskAsync(Runnable task) {
		if (task != null)
			tasksAsync.add(task);
	}

	public static void addTaskUpdate(Runnable task) {
		if (task != null)
			updateThreadTasks.add(task);
	}

	public static boolean isEmpty() {
		return tasks.isEmpty();
	}

	public static boolean isEmptyAsync() {
		return tasksAsync.isEmpty();
	}

	public static AsyncExecutor getAsyncExecutor() {
		return asyncExecutor;
	}

}
