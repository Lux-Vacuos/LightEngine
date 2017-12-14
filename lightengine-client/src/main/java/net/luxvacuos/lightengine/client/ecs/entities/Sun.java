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

import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.util.Maths;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.subsystems.EventSubsystem;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class Sun {

	private Vector3f rotation = new Vector3f(5, 0, -40);
	private Vector3f sunPosition = new Vector3f(0, 0, 0);
	private Vector3f invertedSunPosition = new Vector3f(0, 0, 0);
	private SunCamera camera;

	public Sun() {
		EventSubsystem.addEvent("lightengine.renderer.resetshadowmatrix", () -> {
			Matrix4f[] shadowProjectionMatrix = new Matrix4f[4];

			int shadowDrawDistance = (int) REGISTRY
					.getRegistryItem(new Key("/Light Engine/Settings/Graphics/shadowsDrawDistance"));
			shadowDrawDistance *= 2;
			shadowProjectionMatrix[0] = Maths.orthoSymmetric(-shadowDrawDistance / 25, shadowDrawDistance / 25,
					-shadowDrawDistance, shadowDrawDistance, false);
			shadowProjectionMatrix[1] = Maths.orthoSymmetric(-shadowDrawDistance / 10, shadowDrawDistance / 10,
					-shadowDrawDistance, shadowDrawDistance, false);
			shadowProjectionMatrix[2] = Maths.orthoSymmetric(-shadowDrawDistance / 4, shadowDrawDistance / 4,
					-shadowDrawDistance, shadowDrawDistance, false);
			shadowProjectionMatrix[3] = Maths.orthoSymmetric(-shadowDrawDistance, shadowDrawDistance,
					-shadowDrawDistance, shadowDrawDistance, false);
			TaskManager.addTask(() -> camera.setProjectionArray(shadowProjectionMatrix));
		});
		Matrix4f[] shadowProjectionMatrix = new Matrix4f[4];

		int shadowDrawDistance = (int) REGISTRY
				.getRegistryItem(new Key("/Light Engine/Settings/Graphics/shadowsDrawDistance"));
		shadowDrawDistance *= 2;
		shadowProjectionMatrix[0] = Maths.orthoSymmetric(-shadowDrawDistance / 25, shadowDrawDistance / 25,
				-shadowDrawDistance, shadowDrawDistance, false);
		shadowProjectionMatrix[1] = Maths.orthoSymmetric(-shadowDrawDistance / 10, shadowDrawDistance / 10,
				-shadowDrawDistance, shadowDrawDistance, false);
		shadowProjectionMatrix[2] = Maths.orthoSymmetric(-shadowDrawDistance / 4, shadowDrawDistance / 4,
				-shadowDrawDistance, shadowDrawDistance, false);
		shadowProjectionMatrix[3] = Maths.orthoSymmetric(-shadowDrawDistance, shadowDrawDistance, -shadowDrawDistance,
				shadowDrawDistance, false);
		camera = new SunCamera(shadowProjectionMatrix);
	}

	public Sun(Vector3f rotation, Matrix4f[] shadowProjectionMatrix) {
		this.rotation = rotation;
		camera = new SunCamera(shadowProjectionMatrix);
	}

	public void update(Vector3f cameraPosition, float rot, float delta) {
		camera.setPosition(cameraPosition);
		rotation.y = rot;
		camera.setRotation(new Vector3f(rotation.y, rotation.x, rotation.z));
		camera.updateShadowRay(true);
		sunPosition.set(camera.getDRay().getRay().dX * 10, camera.getDRay().getRay().dY * 10,
				camera.getDRay().getRay().dZ * 10);

		camera.updateShadowRay(false);
		invertedSunPosition.set(camera.getDRay().getRay().dX * 10, camera.getDRay().getRay().dY * 10,
				camera.getDRay().getRay().dZ * 10);
	}

	public CameraEntity getCamera() {
		return camera;
	}

	public Vector3f getInvertedSunPosition() {
		return invertedSunPosition;
	}

	public Vector3f getSunPosition() {
		return sunPosition;
	}

}
