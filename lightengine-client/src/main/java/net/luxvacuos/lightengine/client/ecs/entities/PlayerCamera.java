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

import javax.vecmath.Vector3f;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import com.bulletphysics.linearmath.Transform;

import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.api.opengles.Renderer;
import net.luxvacuos.lightengine.client.resources.CastRay;
import net.luxvacuos.lightengine.client.util.Maths;
import net.luxvacuos.lightengine.universal.ecs.Components;
import net.luxvacuos.lightengine.universal.ecs.components.Health;
import net.luxvacuos.lightengine.universal.ecs.components.Player;
import net.luxvacuos.lightengine.universal.ecs.components.Rotation;
import net.luxvacuos.lightengine.universal.util.VectoVec;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class PlayerCamera extends CameraEntity {

	private int mouseSpeed = 8;
	private final int maxLookUp = 90;
	private final int maxLookDown = -90;
	protected Vector2f center;

	public PlayerCamera(String name, String uuid) {
		super(name, uuid);
		this.add(new Health(20));
		int width = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width"));
		int height = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height"));

		setProjectionMatrix(Renderer.createProjectionMatrix(width, height,
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Core/fov")), ClientVariables.NEAR_PLANE,
				ClientVariables.FAR_PLANE));
		setViewMatrix(Maths.createViewMatrix(this));
		center = new Vector2f(width / 2f, height / 2f);
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

		Player p = Components.PLAYER.get(this);
		Transform characterWorldTrans = p.ghostObject.getWorldTransform(new Transform());
		setPosition(VectoVec.toVec3(characterWorldTrans.origin));

		Vector3f walkDirection = new Vector3f(0.0f, 0.0f, 0.0f);
		float walkVelocity = 1.1f * 2.0f;
		if (kbh.isCtrlPressed())
			walkVelocity *= 3f;
		float walkSpeed = walkVelocity * delta * p.characterScale;

		if (kbh.isKeyPressed(GLFW.GLFW_KEY_W)) {
			walkDirection.z += (float) -Math.cos(Math.toRadians(rotation.getY()));
			walkDirection.x += (float) Math.sin(Math.toRadians(rotation.getY()));
		} else if (kbh.isKeyPressed(GLFW.GLFW_KEY_S)) {
			walkDirection.z += (float) Math.cos(Math.toRadians(rotation.getY()));
			walkDirection.x += (float) -Math.sin(Math.toRadians(rotation.getY()));
		}

		if (kbh.isKeyPressed(GLFW.GLFW_KEY_D)) {
			walkDirection.z += (float) Math.sin(Math.toRadians(rotation.getY()));
			walkDirection.x += (float) Math.cos(Math.toRadians(rotation.getY()));
		} else if (kbh.isKeyPressed(GLFW.GLFW_KEY_A)) {
			walkDirection.z += (float) -Math.sin(Math.toRadians(rotation.getY()));
			walkDirection.x += (float) -Math.cos(Math.toRadians(rotation.getY()));
		}

		if (kbh.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
			p.character.jump();
		}
		walkDirection.scale(walkSpeed);
		p.character.setWalkDirection(walkDirection);
	}

}
