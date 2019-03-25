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

package net.luxvacuos.lightengine.client.rendering.opengl;

import static org.lwjgl.opengl.GL11C.GL_BACK;
import static org.lwjgl.opengl.GL11C.GL_FRONT;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.glCullFace;
import static org.lwjgl.opengl.GL11C.glDepthMask;
import static org.lwjgl.opengl.GL11C.glDrawElements;
import static org.lwjgl.opengl.GL20C.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.rendering.IResourceLoader;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.RawModel;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.SkydomeShader;
import net.luxvacuos.lightengine.client.util.Maths;
import net.luxvacuos.lightengine.universal.core.IWorldSimulation;

public class SkydomeRenderer {

	private RawModel dome;
	private SkydomeShader shader;
	private float scale;
	private Vector3f pos;

	public SkydomeRenderer(IResourceLoader loader) {
		if (ClientVariables.FAR_PLANE > 0 && Float.isInfinite(ClientVariables.FAR_PLANE))
			scale = 100000; // Arbitrary number
		else
			scale = ClientVariables.FAR_PLANE;
		dome = loader.loadObjModel("SkyDome");
		pos = new Vector3f();
		shader = new SkydomeShader();
		shader.start();
		shader.loadTransformationMatrix(Maths.createTransformationMatrix(pos, 0, 0, 0, scale));
		shader.stop();
	}

	public void render(CameraEntity camera, IWorldSimulation clientWorldSimulation, Vector3f lightPosition,
			boolean renderSun) {
		glDepthMask(false);
		glCullFace(GL_FRONT);
		shader.start();
		shader.loadCamera(camera);
		shader.loadTime(clientWorldSimulation.getGlobalTime());
		shader.loadLightPosition(lightPosition);
		shader.renderSun(renderSun);
		glBindVertexArray(dome.getVaoID());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glDrawElements(GL_TRIANGLES, dome.getVertexCount(), GL_UNSIGNED_INT, 0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);
		shader.stop();
		glCullFace(GL_BACK);
		glDepthMask(true);
	}

}
