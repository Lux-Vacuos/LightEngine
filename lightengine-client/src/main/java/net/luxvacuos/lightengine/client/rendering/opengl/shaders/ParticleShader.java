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

package net.luxvacuos.lightengine.client.rendering.opengl.shaders;

import org.joml.Matrix4f;

import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.Attribute;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformFloat;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformMatrix;
import net.luxvacuos.lightengine.universal.core.subsystems.ResManager;
import net.luxvacuos.lightengine.universal.resources.ResourceType;

public class ParticleShader extends ShaderProgram {

	private UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	private UniformFloat numberOfRows = new UniformFloat("numberOfRows");

	public ParticleShader() {
		super(ResManager.getResourceOfType("ENGINE_RND_Particle_VS", ResourceType.SHADER).get(),
				ResManager.getResourceOfType("ENGINE_RND_Particle_FS", ResourceType.SHADER).get(),
				new Attribute(0, "position"), new Attribute(1, "modelViewMatrix"), new Attribute(5, "texOffsets"),
				new Attribute(6, "blendFactor"));
		super.storeUniforms(projectionMatrix);
		super.validate();
	}

	public void loadNumberOfRows(float rows) {
		numberOfRows.loadFloat(rows);
	}

	public void loadProjectionMatrix(Matrix4f projectionMatrix) {
		this.projectionMatrix.loadMatrix(projectionMatrix);
	}

}
