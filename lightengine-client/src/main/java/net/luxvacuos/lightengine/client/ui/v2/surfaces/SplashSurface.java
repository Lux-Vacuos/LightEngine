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

package net.luxvacuos.lightengine.client.ui.v2.surfaces;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;

import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Alignment;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Surface;
import net.luxvacuos.lightengine.client.ui.v2.Image;
import net.luxvacuos.lightengine.client.ui.v2.Text;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;

public class SplashSurface extends Surface {

	private boolean exitOnKey;

	@Override
	public void init(long ctx, MouseHandler mh, KeyboardHandler kh) {
		super.init(ctx, mh, kh);
		super.setHorizontalAlignment(Alignment.STRETCH).setVerticalAlignment(Alignment.STRETCH);
		super.setBackgroundColor("#FFFFFFFF");
		super.addSurface(new Image("assets/textures/menu/LuxVacuos-Logo.png").setWidth(512).setHeight(512)
				.setHorizontalAlignment(Alignment.CENTER).setVerticalAlignment(Alignment.CENTER));
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		if (kh.isKeyPressed(GLFW_KEY_ENTER) && exitOnKey) {
			kh.ignoreKeyUntilRelease(GLFW_KEY_ENTER);
			TaskManager.tm.addTaskMainThread(() -> StateMachine.dispose());
		}
	}

	public void mainNotFound() {
		Text error = new Text("Main state not found, press 'Enter' to exit.");
		error.setFontSize(28);
		error.setY(200);
		error.setHorizontalAlignment(Alignment.CENTER).setVerticalAlignment(Alignment.CENTER);
		error.setForegroundColor("#000000FF");
		exitOnKey = true;
		super.addSurface(error);
	}
}
