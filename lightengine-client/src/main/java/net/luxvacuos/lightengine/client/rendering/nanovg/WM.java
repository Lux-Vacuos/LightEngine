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

package net.luxvacuos.lightengine.client.rendering.nanovg;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.ui.Alignment;
import net.luxvacuos.lightengine.client.ui.ComponentWindow;
import net.luxvacuos.lightengine.client.ui.Text;

public final class WM {

	public static void generateTestWindows() {
		Text win0T = new Text("Default Window", 0, 0);
		win0T.setAlign(NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
		win0T.setWindowAlignment(Alignment.CENTER);

		Text win1T = new Text("Can't be resized", 0, 0);
		win1T.setAlign(NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
		win1T.setWindowAlignment(Alignment.CENTER);

		Text win2T = new Text("Disabled titlebar", 0, 0);
		win2T.setAlign(NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
		win2T.setWindowAlignment(Alignment.CENTER);

		Text win3T = new Text("No decorations", 0, 0);
		win3T.setAlign(NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
		win3T.setWindowAlignment(Alignment.CENTER);

		Text win4T = new Text("No decorations and disabled titlebar", 0, 0);
		win4T.setAlign(NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
		win4T.setWindowAlignment(Alignment.CENTER);
		win4T.setFontSize(15);

		ComponentWindow win0 = new ComponentWindow(20, 700, 200, 200, "Window0");
		win0.addComponent(win0T);
		win0.extendFrame(10, 10, 10, 10);
		ComponentWindow win1 = new ComponentWindow(240, 700, 200, 200, "Window1");
		win1.addComponent(win1T);
		win1.setResizable(false);
		ComponentWindow win2 = new ComponentWindow(480, 700, 200, 200, "Window2");
		win2.addComponent(win2T);
		win2.toggleTitleBar();
		ComponentWindow win3 = new ComponentWindow(700, 700, 200, 200, "Window3");
		win3.addComponent(win3T);
		win3.setDecorations(false);
		ComponentWindow win4 = new ComponentWindow(920, 700, 200, 200, "Window4");
		win4.addComponent(win4T);
		win4.toggleTitleBar();
		win4.setDecorations(false);

		ComponentWindow win5 = new ComponentWindow(20, 480, 200, 200, "Window5");
		win5.setBackgroundColor(0.4f, 0.4f, 0.4f, 1f);
		ComponentWindow win6 = new ComponentWindow(240, 480, 200, 200, "Window6");
		win6.setBackgroundColor(0.4f, 0.4f, 0.4f, 1f);
		win6.setResizable(false);
		ComponentWindow win7 = new ComponentWindow(480, 480, 200, 200, "Window7");
		win7.setBackgroundColor(0.4f, 0.4f, 0.4f, 1f);
		win7.toggleTitleBar();
		ComponentWindow win8 = new ComponentWindow(700, 480, 200, 200, "Window8");
		win8.setBackgroundColor(0.4f, 0.4f, 0.4f, 1f);
		win8.setDecorations(false);
		ComponentWindow win9 = new ComponentWindow(920, 480, 200, 200, "Window9");
		win9.setBackgroundColor(0.4f, 0.4f, 0.4f, 1f);
		win9.toggleTitleBar();
		win9.setDecorations(false);

		GraphicalSubsystem.getWindowManager().addWindow(win0);
		GraphicalSubsystem.getWindowManager().addWindow(win1);
		GraphicalSubsystem.getWindowManager().addWindow(win2);
		GraphicalSubsystem.getWindowManager().addWindow(win3);
		GraphicalSubsystem.getWindowManager().addWindow(win4);
		GraphicalSubsystem.getWindowManager().addWindow(win5);
		GraphicalSubsystem.getWindowManager().addWindow(win6);
		GraphicalSubsystem.getWindowManager().addWindow(win7);
		GraphicalSubsystem.getWindowManager().addWindow(win8);
		GraphicalSubsystem.getWindowManager().addWindow(win9);
	}

}
