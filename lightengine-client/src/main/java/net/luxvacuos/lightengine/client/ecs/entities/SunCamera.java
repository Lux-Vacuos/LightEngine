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

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.resources.CastRay;
import net.luxvacuos.lightengine.client.util.Maths;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.subsystems.EventSubsystem;
import net.luxvacuos.lightengine.universal.util.IEvent;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class SunCamera extends CameraEntity {

	private Vector2f center;

	private Matrix4f[] projectionArray;

	private IEvent shadowReset;

	public SunCamera() {
		super("sun");
	}

	@Override
	public void init() {
		shadowReset = EventSubsystem.addEvent("lightengine.renderer.resetshadowmatrix", () -> {
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
			TaskManager.tm.addTaskRenderThread(() -> setProjectionArray(shadowProjectionMatrix));
		});

		int shadowDrawDistance = (int) REGISTRY
				.getRegistryItem(new Key("/Light Engine/Settings/Graphics/shadowsDrawDistance"));
		shadowDrawDistance *= 2;
		projectionArray = new Matrix4f[4];
		projectionArray[0] = Maths.orthoSymmetric(-shadowDrawDistance / 25, shadowDrawDistance / 25,
				-shadowDrawDistance, shadowDrawDistance, false);
		projectionArray[1] = Maths.orthoSymmetric(-shadowDrawDistance / 10, shadowDrawDistance / 10,
				-shadowDrawDistance, shadowDrawDistance, false);
		projectionArray[2] = Maths.orthoSymmetric(-shadowDrawDistance / 4, shadowDrawDistance / 4, -shadowDrawDistance,
				shadowDrawDistance, false);
		projectionArray[3] = Maths.orthoSymmetric(-shadowDrawDistance, shadowDrawDistance, -shadowDrawDistance,
				shadowDrawDistance, false);

		center = new Vector2f(1024, 1024);
		setProjectionMatrix(projectionArray[0]);
		setViewMatrix(Maths.createViewMatrix(this));
		castRay = new CastRay(getProjectionMatrix(), getViewMatrix(), center, 2048, 2048);
		super.init();
	}

	@Override
	public void dispose() {
		super.dispose();
		EventSubsystem.removeEvent("lightengine.renderer.resetshadowmatrix", shadowReset);
	}

	@Override
	public Vector3f getRotation() {
		return localRotation;
	}

	public void updateShadowRay(boolean inverted) {
		setViewMatrix(Maths.createViewMatrix(this));
		if (inverted)
			castRay.update(
					getProjectionMatrix(), Maths.createViewMatrixPos(localPosition, Maths
							.createViewMatrixRot(localRotation.x() + 180, localRotation.y(), localRotation.z(), null)),
					center, 2048, 2048);
		else
			castRay.update(getProjectionMatrix(), getViewMatrix(), center, 2048, 2048);
	}

	public void switchProjectionMatrix(int id) {
		setProjectionMatrix(this.projectionArray[id]);
	}

	public void setProjectionArray(Matrix4f[] projectionArray) {
		this.projectionArray = projectionArray;
	}

	public Matrix4f[] getProjectionArray() {
		return projectionArray;
	}

	public CastRay getDRay() {
		return castRay;
	}

}
