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

package net.luxvacuos.lightengine.client.ecs.entities;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import org.joml.Vector2f;

import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.opengl.Renderer;
import net.luxvacuos.lightengine.client.util.Maths;
import net.luxvacuos.lightengine.universal.core.subsystems.EventSubsystem;
import net.luxvacuos.lightengine.universal.util.IEvent;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class PlayerCamera extends CameraEntity {

	public static final int MOUSE_SPEED = 8;
	private final int maxLookUp = 90;
	private final int maxLookDown = -90;
	protected Vector2f center;
	private IEvent resize;

	public PlayerCamera(String name, String uuid) {
		super(name, uuid);
	}

	@Override
	public void init() {
		int width = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width"));
		int height = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height"));

		super.setProjectionMatrix(Renderer.createProjectionMatrix(width, height,
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Core/fov")), ClientVariables.NEAR_PLANE,
				ClientVariables.FAR_PLANE, true));
		super.setViewMatrix(Maths.createViewMatrix(this));

		resize = EventSubsystem.addEvent("lightengine.renderer.resize", () -> {
			super.setProjectionMatrix(Renderer.createProjectionMatrix(
					(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")),
					(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")),
					(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Core/fov")),
					ClientVariables.NEAR_PLANE, ClientVariables.FAR_PLANE, true));
		});
		localPosition.set(0, 0.35f, 0f);
		super.init();
	}

	@Override
	public void update(float delta) {
		Window window = GraphicalSubsystem.getMainWindow();
		MouseHandler mh = window.getMouseHandler();

		float mouseDY = mh.getDY() * MOUSE_SPEED * delta;
		if (localRotation.x() - mouseDY >= maxLookDown && localRotation.x() - mouseDY <= maxLookUp)
			localRotation.x -= mouseDY;
		else if (localRotation.x() - mouseDY < maxLookDown)
			localRotation.x = maxLookDown;
		else if (localRotation.x() - mouseDY > maxLookUp)
			localRotation.x = maxLookUp;

		super.update(delta);
	}

	@Override
	public void dispose() {
		EventSubsystem.removeEvent("lightengine.renderer.resize", resize);
		super.dispose();
	}

}
