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

package net.luxvacuos.lightengine.client.core;

import java.util.LinkedList;
import java.util.Queue;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.glfw.WindowManager;
import net.luxvacuos.lightengine.universal.core.TaskManager;

public class ClientTaskManager extends TaskManager {

	private Window asyncWindow;

	private Queue<Runnable> tasksRenderThread = new LinkedList<>(), tasksRenderBackgroundThread = new LinkedList<>();
	private Thread renderBackgroundThread;
	private boolean runBackgroundThread = true, syncInterrupt = true;
	private long renderBackgroundThreadID;

	@Override
	public void addTaskRenderThread(Runnable task) {
		if (task != null)
			tasksRenderThread.add(task);
	}

	@Override
	public void addTaskRenderBackgroundThread(Runnable task) {
		if (task != null) {
			tasksRenderBackgroundThread.add(task);
			if (!syncInterrupt) {
				syncInterrupt = true;
				renderBackgroundThread.interrupt();
			}
		}
	}

	public void updateRenderThread() {
		if (!tasksRenderThread.isEmpty()) {
			tasksRenderThread.poll().run();
		}
	}

	public void switchToSharedContext() {
		var handle = WindowManager.generateHandle(800, 600, "Async Window");
		handle.isVisible(false);
		asyncWindow = WindowManager.generateWindow(handle, GraphicalSubsystem.getMainWindow().getID());
		renderBackgroundThread = new Thread(() -> {
			WindowManager.createWindow(handle, asyncWindow, true);
			while (runBackgroundThread) {
				if (!tasksRenderBackgroundThread.isEmpty()) {
					while (!tasksRenderBackgroundThread.isEmpty())
						tasksRenderBackgroundThread.poll().run();
				} else {
					try {
						syncInterrupt = false;
						Thread.sleep(1000000l);
					} catch (InterruptedException e) {
					}
				}
			}
			asyncWindow.dispose();
		});
		renderBackgroundThread.setName("Render Background Thread");
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

	@Override
	public boolean isEmpty() {
		return super.isEmpty() && tasksRenderThread.isEmpty() && tasksRenderBackgroundThread.isEmpty();
	}

}
