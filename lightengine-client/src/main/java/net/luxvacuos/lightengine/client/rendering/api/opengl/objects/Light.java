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

package net.luxvacuos.lightengine.client.rendering.api.opengl.objects;

import net.luxvacuos.igl.vector.Vector3d;
import net.luxvacuos.igl.vector.Vector3f;
import net.luxvacuos.lightengine.client.ecs.entities.SpotlightCamera;
import net.luxvacuos.lightengine.client.rendering.api.opengl.LightShadowMap;

public class Light {

	private Vector3d position;
	private Vector3f color;
	private Vector3d rotation;
	private float radius, inRadius;
	private int type;
	private boolean shadow;
	private LightShadowMap shadowMap;
	private SpotlightCamera camera;

	public Light(Vector3d position, Vector3f color, Vector3d rotation, float radius, float inRadius) {
		this.position = position;
		this.color = color;
		this.rotation = rotation;
		this.radius = radius;
		this.inRadius = inRadius;
		type = 1;
	}

	public Light(Vector3d position, Vector3f color) {
		this.position = position;
		this.color = color;
		type = 0;
	}

	public void init() {
		camera = new SpotlightCamera(radius * 2f, 512, 512);
		if (shadow) {
			shadowMap = new LightShadowMap(512, 512);
		}
	}

	public void update(float delta) {
		camera.setPosition(position);
		camera.setRotation(Vector3d.add(rotation, new Vector3d(180,0,0), null));
		camera.update(delta);
	}

	public Vector3d getPosition() {
		return position;
	}

	public Vector3f getColor() {
		return color;
	}

	public Vector3d getRotation() {
		return rotation;
	}

	public Vector3d getDirection() {
		return camera.getDirection();
	}

	public float getRadius() {
		return radius;
	}

	public float getInRadius() {
		return inRadius;
	}

	public int getType() {
		return type;
	}

	public boolean isShadow() {
		return shadow;
	}

	public LightShadowMap getShadowMap() {
		return shadowMap;
	}
	
	public SpotlightCamera getCamera() {
		return camera;
	}

	public void setShadow(boolean shadow) {
		this.shadow = shadow;
	}

}
