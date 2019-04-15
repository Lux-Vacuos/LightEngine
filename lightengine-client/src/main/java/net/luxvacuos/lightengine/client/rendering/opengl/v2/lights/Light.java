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

package net.luxvacuos.lightengine.client.rendering.opengl.v2.lights;

import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.ecs.entities.SpotlightCamera;
import net.luxvacuos.lightengine.universal.core.Task;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.ecs.entities.LEEntity;

public class Light extends LEEntity {

	private int type;
	private Vector3f color;
	private float radius, inRadius;
	private boolean useShadows;
	private LightShadowMap shadowMap;
	private SpotlightCamera camera;

	public Light(Vector3f position, Vector3f color, Vector3f rotation, float radius, float inRadius) {
		this.localPosition = position;
		this.color = color;
		this.localRotation = rotation;
		this.radius = radius;
		this.inRadius = inRadius;
		type = 1;
	}

	public Light(Vector3f position, Vector3f color) {
		this.localPosition = position;
		this.color = color;
		type = 0;
	}

	@Override
	public void init() {
		if (type == 1) {
			camera = new SpotlightCamera(radius * 2f, 512);
			super.addEntity(camera);
			if (useShadows)
				TaskManager.tm.submitRenderThread(new Task<Void>() {
					@Override
					protected Void call() {
						shadowMap = new LightShadowMap(512);
						return null;
					}
				}).get();
		}
		GraphicalSubsystem.getRenderer().getLightRenderer().addLight(this);
		super.init();
	}

	@Override
	public void dispose() {
		super.dispose();
		if (useShadows)
			TaskManager.tm.submitRenderThread(new Task<Void>() {
				@Override
				protected Void call() {
					shadowMap.dispose();
					return null;
				}
			});
		GraphicalSubsystem.getRenderer().getLightRenderer().removeLight(this);
	}

	public int getType() {
		return type;
	}

	public Vector3f getColor() {
		return color;
	}

	public Vector3f getDirection() {
		return camera.getDirection();
	}

	public float getRadius() {
		return radius;
	}

	public float getInRadius() {
		return inRadius;
	}

	public void setUseShadows(boolean useShadows) {
		this.useShadows = useShadows;
	}

	public boolean useShadows() {
		return useShadows;
	}

	public LightShadowMap getShadowMap() {
		return shadowMap;
	}

	public SpotlightCamera getCamera() {
		return camera;
	}

}
