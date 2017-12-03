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

package net.luxvacuos.lightengine.tools.ui;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.LANG;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.rendering.nanovg.WindowMessage;
import net.luxvacuos.lightengine.client.ui.ComponentWindow;

public class EntityEditorWindow extends ComponentWindow {

	public EntityEditorWindow(int x, int y, int w, int h) {
		super(x, y, w, h, LANG.getRegistryItem("lightengine.entityeditor.name"));
	}
	
	@Override
	public void initApp() {
		super.setBackgroundColor(0.4f, 0.4f, 0.4f, 1f);
		
		super.initApp();
	}
	
	@Override
	public void processWindowMessage(int message, Object param) {
		if(message == WindowMessage.WM_CLOSE) {
			GraphicalSubsystem.getWindowManager().getWindowByClass("MainWindow").notifyWindow(WindowMessage.WM_RESTORE, null);
		}
		super.processWindowMessage(message, param);
	}

}
