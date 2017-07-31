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

import org.lwjgl.glfw.GLFW;

import net.luxvacuos.igl.vector.Vector2d;
import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.ecs.ClientComponents;
import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.api.opengl.Renderer;
import net.luxvacuos.lightengine.client.resources.CastRay;
import net.luxvacuos.lightengine.client.util.Maths;
import net.luxvacuos.lightengine.universal.ecs.Components;
import net.luxvacuos.lightengine.universal.ecs.components.Health;
import net.luxvacuos.lightengine.universal.ecs.components.Rotation;
import net.luxvacuos.lightengine.universal.ecs.components.Velocity;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class PlayerCamera extends CameraEntity {

	private boolean jump = false;
	private float speed = 1f;
	private int mouseSpeed = 8;
	private final int maxLookUp = 90;
	private final int maxLookDown = -90;
	private boolean flyMode = true;
	private Vector2d center;

	public PlayerCamera(String name, String uuid) {
		super(name, uuid);
		this.add(new Health(20));

		if (flyMode)
			Components.AABB.get(this).setEnabled(false);
		Components.AABB.get(this).setGravity(!flyMode);
		int width = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width"));
		int height = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height"));

		ClientComponents.PROJECTION_MATRIX.get(this)
				.setProjectionMatrix(Renderer.createProjectionMatrix(width, height,
						(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Core/fov")),
						ClientVariables.NEAR_PLANE, ClientVariables.FAR_PLANE));
		ClientComponents.VIEW_MATRIX.get(this).setViewMatrix(Maths.createViewMatrix(this));
		center = new Vector2d(width / 2, height / 2);
		castRay = new CastRay(getProjectionMatrix(), getViewMatrix(), center, width, height);
	}

	@Override
	public void update(float delta) {
		Window window = GraphicalSubsystem.getMainWindow();
		KeyboardHandler kbh = window.getKeyboardHandler();
		MouseHandler mh = window.getMouseHandler();
		Rotation rotation = Components.ROTATION.get(this);

		float mouseDX = mh.getDX() * mouseSpeed * delta;
		float mouseDY = mh.getDY() * mouseSpeed * delta;
		if (rotation.getY() + mouseDX >= 360)
			rotation.setY(rotation.getY() + mouseDX - 360);
		else if (rotation.getY() + mouseDX < 0)
			rotation.setY(360 - rotation.getY() + mouseDX);
		else
			rotation.setY(rotation.getY() + mouseDX);

		if (rotation.getX() - mouseDY >= maxLookDown && rotation.getX() - mouseDY <= maxLookUp)
			rotation.setX(rotation.getX() - mouseDY);
		else if (rotation.getX() - mouseDY < maxLookDown)
			rotation.setX(maxLookDown);
		else if (rotation.getX() - mouseDY > maxLookUp)
			rotation.setX(maxLookUp);

		Velocity vel = Components.VELOCITY.get(this);

		if (kbh.isKeyPressed(GLFW.GLFW_KEY_W)) {
			vel.setZ(vel.getZ() + -Math.cos(Math.toRadians(rotation.getY())) * this.speed);
			vel.setX(vel.getX() + Math.sin(Math.toRadians(rotation.getY())) * this.speed);
		} else if (kbh.isKeyPressed(GLFW.GLFW_KEY_S)) {
			vel.setZ(vel.getZ() - -Math.cos(Math.toRadians(rotation.getY())) * this.speed);
			vel.setX(vel.getX() - Math.sin(Math.toRadians(rotation.getY())) * this.speed);
		}

		if (kbh.isKeyPressed(GLFW.GLFW_KEY_D)) {
			vel.setZ(vel.getZ() + Math.sin(Math.toRadians(rotation.getY())) * this.speed);
			vel.setX(vel.getX() + Math.cos(Math.toRadians(rotation.getY())) * this.speed);
		} else if (kbh.isKeyPressed(GLFW.GLFW_KEY_A)) {
			vel.setZ(vel.getZ() - Math.sin(Math.toRadians(rotation.getY())) * this.speed);
			vel.setX(vel.getX() - Math.cos(Math.toRadians(rotation.getY())) * this.speed);
		}

		this.speed = (kbh.isCtrlPressed() ? (this.flyMode ? 6f : 2f) : 1f);

		if (this.flyMode) {
			if (kbh.isKeyPressed(GLFW.GLFW_KEY_SPACE))
				vel.setY(5f * this.speed);
			else if (kbh.isShiftPressed())
				vel.setY(-5f * this.speed);
		} else {
			if (kbh.isKeyPressed(GLFW.GLFW_KEY_SPACE) && !jump) {
				vel.setY(6f);
				jump = true;
			}

			if (kbh.isShiftPressed() && !jump)
				speed = 0.2f;
			else if (kbh.isCtrlPressed())
				speed = 2f;
			else
				speed = 1f;

			if (vel.getY() == 0 && !kbh.isKeyPressed(GLFW.GLFW_KEY_SPACE))
				jump = false;
		}
	}

}
