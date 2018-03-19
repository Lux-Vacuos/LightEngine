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

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.glfw.WindowHandle;
import net.luxvacuos.lightengine.client.rendering.glfw.WindowManager;
import net.luxvacuos.lightengine.universal.core.TaskManager;

public class ClientTaskManager extends TaskManager {

	private boolean asyncTmp = true;

	private Window asyncWindow;

	private long asyncThreadID;

	@Override
	public void init() {
		super.init();
		asyncThread = new Thread(() -> {
			while (asyncTmp) {
				if (!tasksAsync.isEmpty()) {
					tasksAsync.poll().run();
				} else {
					try {
						syncInterrupt = false;
						Thread.sleep(1000000l);
					} catch (InterruptedException e) {
					}
				}
			}
		});
		asyncThread.setDaemon(true);
		asyncThread.setName("Async Thread");
		asyncThread.start();
	}

	public void switchToSharedContext() {
		asyncTmp = false;
		asyncThread.interrupt();

		WindowHandle handle = WindowManager.generateHandle(800, 600, "Async Window");
		handle.isVisible(false);
		asyncWindow = WindowManager.generateWindow(handle, GraphicalSubsystem.getMainWindow().getID());
		asyncThread = new Thread(() -> {
			WindowManager.createWindow(handle, asyncWindow, true);
			asyncTmp = true;
			while (asyncTmp) {
				if (!tasksAsync.isEmpty()) {
					tasksAsync.poll().run();
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
		asyncThread.setName("Async Thread");
		asyncThread.start();
		asyncThreadID = asyncThread.getId();
	}

	public long getAsyncThreadID() {
		return asyncThreadID;
	}

	public void stopAsyncThread() {
		asyncTmp = false;
		asyncThread.interrupt();
	}

}
