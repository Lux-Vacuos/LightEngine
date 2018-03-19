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

	protected Queue<Runnable> tasks = new LinkedList<>(), tasksAsync = new LinkedList<>(),
			updateThreadTasks = new LinkedList<>();
	protected AsyncExecutor asyncExecutor;
	protected Thread asyncThread;
	protected boolean syncInterrupt;
	
	public void init() {
		asyncExecutor = new AsyncExecutor(2);
	}

	public void update() {
		if (!tasks.isEmpty()) {
			tasks.poll().run();
		}
	}

	public void updateThread() {
		if (!updateThreadTasks.isEmpty()) {
			updateThreadTasks.poll().run();
		}
	}

	public void addTask(Runnable task) {
		if (task != null)
			tasks.add(task);
	}

	public void addTaskAsync(Runnable task) {
		if (task != null) {
			tasksAsync.add(task);
			if (!syncInterrupt) {
				syncInterrupt = true;
				asyncThread.interrupt();
			}
		}
	}

	public void addTaskUpdate(Runnable task) {
		if (task != null)
			updateThreadTasks.add(task);
	}

	public boolean isEmpty() {
		return tasks.isEmpty();
	}

	public boolean isEmptyAsync() {
		return tasksAsync.isEmpty();
	}

	public AsyncExecutor getAsyncExecutor() {
		return asyncExecutor;
	}

}
