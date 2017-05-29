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

package net.luxvacuos.lightengine.client.core.states;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.rendering.api.opengl.Renderer;
import net.luxvacuos.lightengine.client.ui.windows.CrashWindow;
import net.luxvacuos.lightengine.universal.core.AbstractEngine;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class CrashState extends AbstractState {

	public static Throwable t;

	public CrashState() {
		super(StateNames.CRASH);
	}

	@Override
	public void start() {
		GraphicalSubsystem.getWindowManager()
				.addWindow(new CrashWindow(0, (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")),
						(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")),
						(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")), t));
	}

	@Override
	public void render(AbstractEngine lightengine, float delta) {
		Renderer.clearBuffer(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		Renderer.clearColors(1, 1, 1, 1);
		GraphicalSubsystem.getWindowManager().render();
	}

	@Override
	public void update(AbstractEngine lightengine, float delta) {
		GraphicalSubsystem.getWindowManager().update(delta);
	}

}
