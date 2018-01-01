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

package net.luxvacuos.lightengine.client.ecs.entities;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.bulletphysics.linearmath.Transform;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.universal.ecs.Components;
import net.luxvacuos.lightengine.universal.ecs.components.Player;
import net.luxvacuos.lightengine.universal.ecs.entities.PlayerEntity;
import net.luxvacuos.lightengine.universal.util.VectoVec;

public class FPSPlayer extends PlayerEntity {

	public FPSPlayer(String name, Vector3f pos) {
		super(name, pos);
	}

	@Override
	public void update(float delta) {
		Window window = GraphicalSubsystem.getMainWindow();
		KeyboardHandler kbh = window.getKeyboardHandler();
		MouseHandler mh = window.getMouseHandler();

		float mouseDX = mh.getDX() * PlayerCamera.MOUSE_SPEED * delta;
		localRotation.y += mouseDX;
		localRotation.y %= 360;

		Player p = Components.PLAYER.get(this);

		javax.vecmath.Vector3f walkDirection = new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f);
		float walkVelocity = 1.1f * 2.0f;
		if (kbh.isCtrlPressed())
			walkVelocity *= 3f;
		float walkSpeed = walkVelocity * delta * p.characterScale;

		if (kbh.isKeyPressed(GLFW.GLFW_KEY_W)) {
			walkDirection.z += (float) -Math.cos(Math.toRadians(localRotation.y()));
			walkDirection.x += (float) Math.sin(Math.toRadians(localRotation.y()));
		} else if (kbh.isKeyPressed(GLFW.GLFW_KEY_S)) {
			walkDirection.z += (float) Math.cos(Math.toRadians(localRotation.y()));
			walkDirection.x += (float) -Math.sin(Math.toRadians(localRotation.y()));
		}

		if (kbh.isKeyPressed(GLFW.GLFW_KEY_D)) {
			walkDirection.z += (float) Math.sin(Math.toRadians(localRotation.y()));
			walkDirection.x += (float) Math.cos(Math.toRadians(localRotation.y()));
		} else if (kbh.isKeyPressed(GLFW.GLFW_KEY_A)) {
			walkDirection.z += (float) -Math.sin(Math.toRadians(localRotation.y()));
			walkDirection.x += (float) -Math.cos(Math.toRadians(localRotation.y()));
		}

		if (kbh.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
			p.character.jump();
		}
		walkDirection.scale(walkSpeed);
		p.character.setWalkDirection(walkDirection);

		super.update(delta);
	}

	@Override
	public void afterUpdate(float delta) {
		super.afterUpdate(delta);
		Transform characterWorldTrans = Components.PLAYER.get(this).ghostObject.getWorldTransform(new Transform());
		localPosition = VectoVec.toVec3(characterWorldTrans.origin);
	}

}
