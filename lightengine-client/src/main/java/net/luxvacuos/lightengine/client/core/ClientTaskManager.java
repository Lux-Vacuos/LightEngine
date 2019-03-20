/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2019 Lux Vacuos
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

package net.luxvacuos.lightengine.client.core;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.glfw.WindowManager;
import net.luxvacuos.lightengine.universal.core.Task;
import net.luxvacuos.lightengine.universal.core.TaskManager;

public class ClientTaskManager extends TaskManager {

	private Window asyncWindow;

	private Queue<Task<?>> tasksRenderThread = new ConcurrentLinkedQueue<>(),
			tasksRenderBackgroundThread = new ConcurrentLinkedQueue<>();
	private Thread renderThread, renderBackgroundThread;
	private boolean runBackgroundThread = true, syncInterrupt = true;
	private long renderBackgroundThreadID;

	@Override
	public void addTaskRenderThread(Runnable task) {
		if (task == null)
			return;
		this.submitRenderThread(new Task<Void>() {
			@Override
			protected Void call() {
				task.run();
				return null;
			}
		});
	}

	@Override
	public void addTaskRenderBackgroundThread(Runnable task) {
		if (task == null)
			return;
		this.submitRenderBackgroundThread(new Task<Void>() {
			@Override
			protected Void call() {
				task.run();
				return null;
			}
		});
	}

	@Override
	public <T> Task<T> submitRenderThread(Task<T> t) {
		if (t == null)
			return null;

		if (Thread.currentThread().equals(renderThread))
			t.callI();
		else
			tasksRenderThread.add(t);
		return t;
	}

	@Override
	public <T> Task<T> submitRenderBackgroundThread(Task<T> t) {
		if (t == null)
			return null;

		if (Thread.currentThread().equals(renderBackgroundThread))
			t.callI();
		else {
			tasksRenderBackgroundThread.add(t);
			if (!syncInterrupt) {
				syncInterrupt = true;
				renderBackgroundThread.interrupt();
			}
		}
		return t;
	}

	public void updateRenderThread() {
		if (!tasksRenderThread.isEmpty())
			tasksRenderThread.poll().callI();
	}

	public void switchToSharedContext() {
		var handle = WindowManager.generateHandle(1, 1, "Async Window");
		handle.isVisible(false);
		asyncWindow = WindowManager.generateWindow(handle, GraphicalSubsystem.getMainWindow().getID());
		renderBackgroundThread = new Thread(() -> {
			WindowManager.createWindow(handle, asyncWindow, true);
			while (runBackgroundThread) {
				if (!tasksRenderBackgroundThread.isEmpty()) {
					while (!tasksRenderBackgroundThread.isEmpty())
						tasksRenderBackgroundThread.poll().callI();
				} else {
					try {
						syncInterrupt = false;
						Thread.sleep(Long.MAX_VALUE);
					} catch (InterruptedException e) {
					}
				}
				asyncWindow.updateDisplay(0);
			}
			asyncWindow.dispose();
		});
		renderBackgroundThread.setName("Render Background");
		renderBackgroundThread.start();
		renderBackgroundThreadID = renderBackgroundThread.getId();
	}

	public void stopRenderBackgroundThread() {
		runBackgroundThread = false;
		renderBackgroundThread.interrupt();
	}

	public long getRenderBackgroundThreadID() {
		return renderBackgroundThreadID;
	}

	public void setRenderThread(Thread renderThread) {
		this.renderThread = renderThread;
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty() && tasksRenderThread.isEmpty() && tasksRenderBackgroundThread.isEmpty();
	}

}
