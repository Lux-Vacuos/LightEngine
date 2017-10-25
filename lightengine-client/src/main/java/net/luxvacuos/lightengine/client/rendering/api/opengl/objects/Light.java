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

import static org.lwjgl.assimp.Assimp.aiLightSource_POINT;
import static org.lwjgl.assimp.Assimp.aiLightSource_SPOT;

import org.joml.Vector3f;
import org.lwjgl.assimp.AILight;

import net.luxvacuos.lightengine.client.ecs.entities.SpotlightCamera;
import net.luxvacuos.lightengine.client.rendering.api.opengl.LightShadowMap;

public class Light {

	private Vector3f position;
	private Vector3f color;
	private Vector3f rotation;
	private float radius, inRadius;
	private int type;
	private boolean shadow;
	private LightShadowMap shadowMap;
	private SpotlightCamera camera;

	public Light(Vector3f position, Vector3f color, Vector3f rotation, float radius, float inRadius) {
		this.position = position;
		this.color = color;
		this.rotation = rotation;
		this.radius = radius;
		this.inRadius = inRadius;
		type = 1;
	}

	public Light(Vector3f position, Vector3f color) {
		this.position = position;
		this.color = color;
		type = 0;
	}

	public Light(AILight light) {
		this.position = new Vector3f(light.mPosition().x(), light.mPosition().y(), light.mPosition().z());
		this.color = new Vector3f(light.mColorDiffuse().r(), light.mColorDiffuse().g(), light.mColorDiffuse().b());
		System.out.println(position);
		switch (light.mType()) {
		case aiLightSource_POINT:
			type = 0;
			System.out.println("Point");
			break;
		case aiLightSource_SPOT:
			System.out.println("Spot");
			this.rotation= new Vector3f(light.mDirection().x(),light.mDirection().y(),light.mDirection().z());
			System.out.println(rotation);
			this.radius = light.mAngleOuterCone();
			this.inRadius = light.mAngleInnerCone();
			type = 1;
			break;
		}
	}

	public void init() {
		if (shadow && type == 1) {
			camera = new SpotlightCamera(radius * 2f, 1024, 1024);
			shadowMap = new LightShadowMap(1024, 1024);
		}
	}

	public void update(float delta) {
		if (type == 1) {
			camera.setPosition(position);
			camera.setRotation(rotation.add(new Vector3f(180, 0, 0)));
			camera.update(delta);
		}
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getColor() {
		return color;
	}

	public Vector3f getRotation() {
		return rotation;
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
