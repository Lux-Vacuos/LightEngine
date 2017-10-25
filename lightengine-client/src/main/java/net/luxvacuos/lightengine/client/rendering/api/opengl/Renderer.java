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

package net.luxvacuos.lightengine.client.rendering.api.opengl;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.ecs.entities.Sun;
import net.luxvacuos.lightengine.client.ecs.entities.SunCamera;
import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.CubeMapTexture;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.Light;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.ParticleTexture;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.WaterTile;
import net.luxvacuos.lightengine.client.rendering.api.opengl.pipeline.MultiPass;
import net.luxvacuos.lightengine.client.rendering.api.opengl.pipeline.PostProcess;
import net.luxvacuos.lightengine.client.ui.OnAction;
import net.luxvacuos.lightengine.client.world.particles.Particle;
import net.luxvacuos.lightengine.universal.core.IWorldSimulation;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.util.registry.KeyCache;

public class Renderer {

	private static SkyboxRenderer skyboxRenderer;
	private static IDeferredPipeline deferredPipeline;
	private static IPostProcessPipeline postProcessPipeline;
	private static EnvironmentRenderer environmentRenderer;
	private static ParticleRenderer particleRenderer;
	private static IrradianceCapture irradianceCapture;
	private static PreFilteredEnvironment preFilteredEnvironment;
	private static WaterRenderer waterRenderer;
	private static LightRenderer lightRenderer;

	private static ShadowFBO shadowFBO;

	private static Frustum frustum;
	private static Window window;

	private static IRenderPass shadowPass, deferredPass, forwardPass, occlusionPass;

	private static OnAction onResize;

	private static float exposure = 1;

	private static int shadowResolution;

	private static boolean reloading, enabled;

	private static RenderingManager renderingManager;

	public static void init(Window window) {
		if (!enabled) {
			renderingManager = new RenderingManager();
			Renderer.window = window;
			shadowResolution = (int) REGISTRY
					.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/Graphics/shadowsResolution"));

			if (shadowResolution > GLUtil.GL_MAX_TEXTURE_SIZE)
				shadowResolution = GLUtil.GL_MAX_TEXTURE_SIZE;

			TaskManager.addTask(() -> frustum = new Frustum());
			TaskManager.addTask(() -> shadowFBO = new ShadowFBO(shadowResolution, shadowResolution));

			TaskManager.addTask(() -> skyboxRenderer = new SkyboxRenderer(window.getResourceLoader()));
			TaskManager.addTask(() -> deferredPipeline = new MultiPass());
			TaskManager.addTask(() -> postProcessPipeline = new PostProcess(window));
			TaskManager.addTask(() -> particleRenderer = new ParticleRenderer(window.getResourceLoader()));
			TaskManager.addTask(() -> irradianceCapture = new IrradianceCapture(window.getResourceLoader()));
			TaskManager.addTask(() -> environmentRenderer = new EnvironmentRenderer(
					new CubeMapTexture(window.getResourceLoader().createEmptyCubeMap(128, true, false), 128)));
			TaskManager.addTask(() -> preFilteredEnvironment = new PreFilteredEnvironment(window));
			TaskManager.addTask(() -> waterRenderer = new WaterRenderer(window.getResourceLoader()));
			TaskManager.addTask(() -> renderingManager.addRenderer(new EntityRenderer(window.getResourceLoader())));
			lightRenderer = new LightRenderer();
			enabled = true;
		}
	}

	public static void render(ImmutableArray<Entity> entitiesT, Map<ParticleTexture, List<Particle>> particles,
			List<WaterTile> waterTiles, CameraEntity cameraT, IWorldSimulation worldSimulation, Sun sunT, float delta) {
		if (!enabled)
			return;
		Array<Entity> entitiesR = new Array<>(entitiesT.toArray(Entity.class));
		ImmutableArray<Entity> entities = new ImmutableArray<>(entitiesR);
		CameraEntity camera = cameraT;
		Sun sun = sunT;
		lightRenderer.update(delta);
		resetState();
		renderingManager.preProcess(entities);
		GPUProfiler.start("Main Renderer");
		GPUProfiler.start("EnvMap");
		environmentRenderer.renderEnvironmentMap(camera.getPosition(), skyboxRenderer, worldSimulation,
				sun.getSunPosition(), window);
		GPUProfiler.end();
		GPUProfiler.start("IrrMap");
		irradianceCapture.render(window, environmentRenderer.getCubeMapTexture().getID());
		GPUProfiler.end();
		GPUProfiler.start("PreFilEnv");
		preFilteredEnvironment.render(window, environmentRenderer.getCubeMapTexture().getID());
		GPUProfiler.end();
		GPUProfiler.start("Shadows");
		SunCamera sunCamera = (SunCamera) sun.getCamera();
		if ((boolean) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/Graphics/shadows"))) {
			GPUProfiler.start("Directional");
			sunCamera.switchProjectionMatrix(0);
			frustum.calculateFrustum(sunCamera);

			shadowFBO.begin();
			shadowFBO.changeTexture(0);
			clearBuffer(GL_DEPTH_BUFFER_BIT);
			if (shadowPass != null)
				shadowPass.render(camera, sunCamera, frustum, shadowFBO);
			renderingManager.renderShadow(sunCamera);

			sunCamera.switchProjectionMatrix(1);
			frustum.calculateFrustum(sunCamera);

			shadowFBO.changeTexture(1);
			clearBuffer(GL_DEPTH_BUFFER_BIT);
			if (shadowPass != null)
				shadowPass.render(camera, sunCamera, frustum, shadowFBO);
			renderingManager.renderShadow(sunCamera);

			sunCamera.switchProjectionMatrix(2);
			frustum.calculateFrustum(sunCamera);

			shadowFBO.changeTexture(2);
			clearBuffer(GL_DEPTH_BUFFER_BIT);
			if (shadowPass != null)
				shadowPass.render(camera, sunCamera, frustum, shadowFBO);
			renderingManager.renderShadow(sunCamera);

			sunCamera.switchProjectionMatrix(3);
			frustum.calculateFrustum(sunCamera);

			shadowFBO.changeTexture(3);
			clearBuffer(GL_DEPTH_BUFFER_BIT);
			if (shadowPass != null)
				shadowPass.render(camera, sunCamera, frustum, shadowFBO);
			renderingManager.renderShadow(sunCamera);
			shadowFBO.end();
			GPUProfiler.end();
			GPUProfiler.start("Shadow lights");
			glCullFace(GL_FRONT);
			for (Light light : lightRenderer.getLights()) {
				if (light.isShadow()) {
					light.getShadowMap().begin();
					clearBuffer(GL_DEPTH_BUFFER_BIT);
					if (shadowPass != null)
						shadowPass.render(camera, light.getCamera(), frustum, null);
					renderingManager.renderShadow(light.getCamera());
					light.getShadowMap().end();
				}
			}
			glCullFace(GL_BACK);
			GPUProfiler.end();
		}
		GPUProfiler.end();
		GPUProfiler.start("Occlusion");
		frustum.calculateFrustum(camera);
		clearBuffer(GL_DEPTH_BUFFER_BIT);
		if (occlusionPass != null)
			occlusionPass.render(camera, sunCamera, frustum, shadowFBO);
		GPUProfiler.end();
		GPUProfiler.start("G-Buffer pass");
		deferredPipeline.begin();
		clearBuffer(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GPUProfiler.start("Skybox");
		skyboxRenderer.render(camera, worldSimulation, sun.getSunPosition());
		GPUProfiler.end();
		GPUProfiler.start("External");
		if (deferredPass != null)
			deferredPass.render(camera, sunCamera, frustum, shadowFBO);
		GPUProfiler.end();
		GPUProfiler.start("RenderingManager");
		renderingManager.render(camera);
		GPUProfiler.end();
		deferredPipeline.end();
		GPUProfiler.end();
		GPUProfiler.start("Deferred Rendering");
		deferredPipeline.preRender(camera, sun, worldSimulation, lightRenderer.getLights(),
				irradianceCapture.getCubeMapTexture(), preFilteredEnvironment.getCubeMapTexture(),
				preFilteredEnvironment.getBRDFLUT(), shadowFBO, exposure);
		GPUProfiler.end();
		GPUProfiler.start("Post process pre-pass");
		postProcessPipeline.begin();
		clearBuffer(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		deferredPipeline.render(postProcessPipeline.getFBO());
		GPUProfiler.start("Forward");
		if (forwardPass != null)
			forwardPass.render(camera, sunCamera, frustum, shadowFBO);
		waterRenderer.render(waterTiles, camera, environmentRenderer.getCubeMapTexture(),
				deferredPipeline.getLastTexture(), deferredPipeline.getMainFBO().getDepthTex(),
				worldSimulation.getGlobalTime(), frustum);
		particleRenderer.render(particles, camera);
		GPUProfiler.end();
		postProcessPipeline.end();
		GPUProfiler.end();
		GPUProfiler.start("Post process effects");
		postProcessPipeline.preRender(window.getNVGID(), camera);
		GPUProfiler.end();
		GPUProfiler.end();
		renderingManager.end();
	}

	public static void cleanUp() {
		if (enabled) {
			enabled = false;
			if (environmentRenderer != null)
				TaskManager.addTask(() -> environmentRenderer.cleanUp());
			if (shadowFBO != null)
				TaskManager.addTask(() -> shadowFBO.dispose());
			if (deferredPipeline != null)
				TaskManager.addTask(() -> deferredPipeline.dispose());
			if (postProcessPipeline != null)
				TaskManager.addTask(() -> postProcessPipeline.dispose());
			if (particleRenderer != null)
				TaskManager.addTask(() -> particleRenderer.cleanUp());
			if (irradianceCapture != null)
				TaskManager.addTask(() -> irradianceCapture.dispose());
			if (preFilteredEnvironment != null)
				TaskManager.addTask(() -> preFilteredEnvironment.dispose());
			if (waterRenderer != null)
				TaskManager.addTask(() -> waterRenderer.dispose());
			if (renderingManager != null)
				TaskManager.addTask(() -> renderingManager.dispose());
			if (lightRenderer != null)
				TaskManager.addTask(() -> lightRenderer.dispose());
		}
	}

	public static int getResultTexture() {
		return postProcessPipeline.getResultTexture();
	}

	public static void setShadowPass(IRenderPass shadowPass) {
		Renderer.shadowPass = shadowPass;
	}

	public static void setDeferredPass(IRenderPass deferredPass) {
		Renderer.deferredPass = deferredPass;
	}

	public static void setForwardPass(IRenderPass forwardPass) {
		Renderer.forwardPass = forwardPass;
	}

	public static void setOcclusionPass(IRenderPass occlusionPass) {
		Renderer.occlusionPass = occlusionPass;
	}

	public static void setOnResize(OnAction onResize) {
		Renderer.onResize = onResize;
	}

	public static Frustum getFrustum() {
		return frustum;
	}

	public static LightRenderer getLightRenderer() {
		return lightRenderer;
	}

	public static void reloadShadowMaps() {
		if (!enabled)
			return;
		Logger.log("Reloading Shadow Maps");
		shadowFBO.dispose();
		shadowResolution = (int) REGISTRY
				.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/Graphics/shadowsResolution"));

		if (shadowResolution > GLUtil.GL_MAX_TEXTURE_SIZE)
			shadowResolution = GLUtil.GL_MAX_TEXTURE_SIZE;
		shadowFBO = new ShadowFBO(shadowResolution, shadowResolution);
	}

	public static void reloadDeferred() {
		if (!enabled)
			return;
		if (!reloading) {
			reloading = true;
			if (onResize != null)
				onResize.onAction();
			Logger.log("Reloading Deferred");
			deferredPipeline.dispose();
			postProcessPipeline.dispose();
			deferredPipeline = new MultiPass();
			postProcessPipeline = new PostProcess(window);
			reloading = false;
		}
	}

	public static void init() {
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
	}

	public static void resetState() {
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glEnable(GL_DEPTH_TEST);
		glDisable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	public static void clearColors(float r, float g, float b, float a) {
		glClearColor(r, g, b, a);
	}

	public static void clearBuffer(int values) {
		glClear(values);
	}

	public static Matrix4f createProjectionMatrix(int width, int height, float fov, float nearPlane, float farPlane) {
		return createProjectionMatrix(new Matrix4f(), width, height, fov, nearPlane, farPlane);
	}

	public static Matrix4f createProjectionMatrix(Matrix4f proj, int width, int height, float fov, float nearPlane,
			float farPlane) {
		return proj.setPerspective((float) Math.toRadians(fov), (float) width / (float) height, nearPlane, farPlane);
	}

}
