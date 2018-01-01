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

package net.luxvacuos.lightengine.client.rendering.opengl.objects;

import static org.lwjgl.assimp.Assimp.aiLightSource_POINT;
import static org.lwjgl.assimp.Assimp.aiLightSource_SPOT;

import org.joml.Vector3f;
import org.lwjgl.assimp.AILight;

import net.luxvacuos.lightengine.client.ecs.entities.SpotlightCamera;
import net.luxvacuos.lightengine.client.rendering.opengl.LightShadowMap;
import net.luxvacuos.lightengine.client.rendering.opengl.Renderer;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.ecs.entities.LEEntity;

public class Light extends LEEntity {

	private Vector3f color;
	private float radius, inRadius;
	private int type;
	private boolean shadow;
	private LightShadowMap shadowMap;
	private boolean shadowMapCreated;
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

	public Light(AILight light) {
		this.localPosition = new Vector3f(light.mPosition().x(), light.mPosition().y(), light.mPosition().z());
		this.color = new Vector3f(light.mColorDiffuse().r(), light.mColorDiffuse().g(), light.mColorDiffuse().b());
		System.out.println(localPosition);
		switch (light.mType()) {
		case aiLightSource_POINT:
			type = 0;
			System.out.println("Point");
			break;
		case aiLightSource_SPOT:
			System.out.println("Spot");
			this.localRotation = new Vector3f(light.mDirection().x(), light.mDirection().y(), light.mDirection().z());
			System.out.println(localRotation);
			this.radius = light.mAngleOuterCone();
			this.inRadius = light.mAngleInnerCone();
			type = 1;
			break;
		}
	}

	@Override
	public void init() {
		if (type == 1) {
			camera = new SpotlightCamera(radius * 2f, 1024, 1024);
			super.addEntity(camera);
			if (shadow)
				TaskManager.addTask(() -> {
					shadowMap = new LightShadowMap(1024, 1024);
					shadowMapCreated = true;
				});
		}
		System.out.println("A");
		Renderer.getLightRenderer().addLight(this);
		super.init();
	}

	@Override
	public void dispose() {
		TaskManager.addTask(() -> shadowMap.dispose());
		Renderer.getLightRenderer().removeLight(this);
		super.dispose();
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

	public boolean isShadowMapCreated() {
		return shadowMapCreated;
	}

}
