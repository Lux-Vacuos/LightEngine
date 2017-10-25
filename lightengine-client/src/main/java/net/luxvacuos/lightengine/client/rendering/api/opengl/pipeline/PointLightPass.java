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

package net.luxvacuos.lightengine.client.rendering.api.opengl.pipeline;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE14;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.GL_TEXTURE5;
import static org.lwjgl.opengl.GL13.GL_TEXTURE6;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.ecs.entities.Sun;
import net.luxvacuos.lightengine.client.ecs.entities.SunCamera;
import net.luxvacuos.lightengine.client.rendering.api.opengl.DeferredPass;
import net.luxvacuos.lightengine.client.rendering.api.opengl.FBO;
import net.luxvacuos.lightengine.client.rendering.api.opengl.IDeferredPipeline;
import net.luxvacuos.lightengine.client.rendering.api.opengl.Renderer;
import net.luxvacuos.lightengine.client.rendering.api.opengl.ShadowFBO;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.CubeMapTexture;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.Light;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.RawModel;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.Texture;
import net.luxvacuos.lightengine.client.rendering.api.opengl.shaders.DeferredShadingShader;
import net.luxvacuos.lightengine.client.util.Maths;
import net.luxvacuos.lightengine.universal.core.IWorldSimulation;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class PointLightPass extends DeferredPass {

	private FBO fbos[];

	public PointLightPass(String name, int width, int height) {
		super(name, width, height);
	}

	@Override
	public void init() {
		fbos = new FBO[2];
		fbos[0] = new FBO(width, height, GL_RGBA16F, GL_RGBA, GL_FLOAT);
		fbos[1] = new FBO(width, height, GL_RGBA16F, GL_RGBA, GL_FLOAT);
		shader = new DeferredShadingShader(name);
		shader.start();
		shader.loadResolution(new Vector2f(width, height));
		shader.loadSkyColor(ClientVariables.skyColor);
		shader.stop();
	}

	@Override
	public void render(FBO[] auxs, IDeferredPipeline pipe, CubeMapTexture irradianceCapture,
			CubeMapTexture environmentMap, Texture brdfLUT, ShadowFBO shadow) {
	}

	public void render(FBO[] auxs, IDeferredPipeline pipe, CubeMapTexture irradianceCapture,
			CubeMapTexture environmentMap, Texture brdfLUT, ShadowFBO shadow, List<Light> lights) {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, pipe.getMainFBO().getDiffuseTex());
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, pipe.getMainFBO().getPositionTex());
		glActiveTexture(GL_TEXTURE2);
		glBindTexture(GL_TEXTURE_2D, pipe.getMainFBO().getNormalTex());
		glActiveTexture(GL_TEXTURE3);
		glBindTexture(GL_TEXTURE_2D, pipe.getMainFBO().getDepthTex());
		glActiveTexture(GL_TEXTURE4);
		glBindTexture(GL_TEXTURE_2D, pipe.getMainFBO().getPbrTex());
		glActiveTexture(GL_TEXTURE5);
		glBindTexture(GL_TEXTURE_2D, pipe.getMainFBO().getMaskTex());
		glActiveTexture(GL_TEXTURE6);
		glBindTexture(GL_TEXTURE_2D, auxs[0].getTexture());
		for (int x = 0; x < lights.size(); x++) {
			Light l = lights.get(x);
			if (l.isShadow()) {
				glActiveTexture(GL_TEXTURE14 + x);
				glBindTexture(GL_TEXTURE_2D, l.getShadowMap().getShadowMap());
			}
		}
	}

	@Override
	public void process(CameraEntity camera, Sun sun, Matrix4f previousViewMatrix, Vector3f previousCameraPosition,
			IWorldSimulation clientWorldSimulation, List<Light> tLights, FBO[] auxs, IDeferredPipeline pipe,
			RawModel quad, CubeMapTexture irradianceCapture, CubeMapTexture environmentMap, Texture brdfLUT,
			ShadowFBO shadowFBO, float exposure) {
		List<List<Light>> totalLights = chopped(tLights, 18);
		for (List<Light> lights : totalLights) {
			FBO tmp = fbos[0];
			fbos[0] = fbos[1];
			fbos[1] = tmp;

			fbos[0].begin();
			shader.start();
			shader.loadUnderWater(false);
			shader.loadMotionBlurData(camera, previousViewMatrix, previousCameraPosition);
			shader.loadLightPosition(sun.getSunPosition(), sun.getInvertedSunPosition());
			shader.loadviewMatrix(camera);
			shader.loadSettings(false, false, false,
					(boolean) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Graphics/volumetricLight")),
					(boolean) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Graphics/reflections")),
					(boolean) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Graphics/ambientOcclusion")),
					(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Graphics/shadowsDrawDistance")),
					false, (boolean) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Graphics/lensFlares")),
					(boolean) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Graphics/shadows")));
			shader.loadSunPosition(Maths.convertTo2F(new Vector3f(sun.getSunPosition()), camera.getProjectionMatrix(),
					Maths.createViewMatrixRot(camera.getRotation().x(), camera.getRotation().y(),
							camera.getRotation().z(), DeferredPass.tmp),
					width, height));
			shader.loadExposure(exposure);
			shader.loadPointLightsPos(lights);
			shader.loadTime(clientWorldSimulation.getTime());
			shader.loadLightMatrix(sun.getCamera().getViewMatrix());
			shader.loadBiasMatrix(((SunCamera) sun.getCamera()).getProjectionArray());
			Renderer.clearBuffer(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			render(auxs, pipe, irradianceCapture, environmentMap, brdfLUT, shadowFBO, lights);
			glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
			shader.stop();
			fbos[0].end();

			auxs[0] = fbos[0];
		}
	}

	@Override
	public void dispose() {
		fbos[0].dispose();
		fbos[1].dispose();
		shader.dispose();
	}

	private static <T> List<List<T>> chopped(List<T> list, final int L) {
		List<List<T>> parts = new ArrayList<List<T>>();
		final int N = list.size();
		for (int i = 0; i < N; i += L) {
			parts.add(new ArrayList<T>(list.subList(i, Math.min(N, i + L))));
		}
		return parts;
	}

}
