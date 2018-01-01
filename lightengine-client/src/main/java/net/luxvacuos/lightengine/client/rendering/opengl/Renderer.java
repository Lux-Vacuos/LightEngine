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

package net.luxvacuos.lightengine.client.rendering.opengl;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.lwjgl.opengl.ARBClipControl;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.ecs.entities.Sun;
import net.luxvacuos.lightengine.client.ecs.entities.SunCamera;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Light;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.ParticleTexture;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.WaterTile;
import net.luxvacuos.lightengine.client.rendering.opengl.pipeline.MultiPass;
import net.luxvacuos.lightengine.client.rendering.opengl.pipeline.PostProcess;
import net.luxvacuos.lightengine.client.resources.ResourceLoader;
import net.luxvacuos.lightengine.client.world.particles.Particle;
import net.luxvacuos.lightengine.universal.core.IWorldSimulation;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.subsystems.EventSubsystem;
import net.luxvacuos.lightengine.universal.util.IEvent;
import net.luxvacuos.lightengine.universal.util.registry.KeyCache;

public class Renderer {

	private static SkyboxRenderer skyboxRenderer;
	private static IDeferredPipeline deferredPipeline;
	private static IPostProcessPipeline postProcessPipeline;
	private static EnvironmentRenderer envRenderer;
	private static EnvironmentRenderer envRendererEntities;
	private static ParticleRenderer particleRenderer;
	private static IrradianceCapture irradianceCapture;
	private static PreFilteredEnvironment preFilteredEnvironment;
	private static WaterRenderer waterRenderer;
	private static LightRenderer lightRenderer;

	private static ShadowFBO shadowFBO;

	private static Frustum frustum;
	private static Window window;

	private static IRenderPass shadowPass, deferredPass, forwardPass, occlusionPass;

	private static float exposure = 1;

	private static int shadowResolution;

	private static boolean reloading, enabled;

	private static RenderingManager renderingManager;

	private static IEvent shadowMap, reload;

	public static void init(Window window) {
		if (!enabled) {
			renderingManager = new RenderingManager();
			Renderer.window = window;
			shadowResolution = (int) REGISTRY
					.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/Graphics/shadowsResolution"));
			ResourceLoader loader = window.getResourceLoader();

			if (shadowResolution > GLUtil.GL_MAX_TEXTURE_SIZE)
				shadowResolution = GLUtil.GL_MAX_TEXTURE_SIZE;

			frustum = new Frustum();
			TaskManager.addTask(() -> shadowFBO = new ShadowFBO(shadowResolution, shadowResolution));

			TaskManager.addTask(() -> skyboxRenderer = new SkyboxRenderer(loader));
			TaskManager.addTask(() -> deferredPipeline = new MultiPass(window));
			TaskManager.addTask(() -> postProcessPipeline = new PostProcess(window));
			TaskManager.addTask(() -> particleRenderer = new ParticleRenderer(loader));
			TaskManager
					.addTask(() -> envRenderer = new EnvironmentRenderer(loader.createEmptyCubeMap(32, true, false)));
			TaskManager.addTask(
					() -> envRendererEntities = new EnvironmentRenderer(loader.createEmptyCubeMap(128, true, false)));
			TaskManager.addTask(() -> preFilteredEnvironment = new PreFilteredEnvironment(
					loader.createEmptyCubeMap(128, true, true), window));
			TaskManager.addTask(() -> irradianceCapture = new IrradianceCapture(loader));
			TaskManager.addTask(() -> waterRenderer = new WaterRenderer(loader));
			TaskManager.addTask(() -> renderingManager.addRenderer(new EntityRenderer(loader)));
			lightRenderer = new LightRenderer();

			shadowMap = EventSubsystem.addEvent("lightengine.renderer.resetshadowmap", () -> {
				shadowResolution = (int) REGISTRY
						.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/Graphics/shadowsResolution"));
				if (shadowResolution > GLUtil.GL_MAX_TEXTURE_SIZE)
					shadowResolution = GLUtil.GL_MAX_TEXTURE_SIZE;
				TaskManager.addTask(() -> {
					shadowFBO.dispose();
					shadowFBO = new ShadowFBO(shadowResolution, shadowResolution);
				});
			});
			reload = EventSubsystem.addEvent("lightengine.renderer.resize", () -> {
				reloading = true;
				TaskManager.addTask(() -> {
					deferredPipeline.resize();
					postProcessPipeline.resize();
					reloading = false;
					EventSubsystem.triggerEvent("lightengine.renderer.postresize");
				});
			});
		}
		TaskManager.addTask(() -> {
			enabled = true;
			EventSubsystem.triggerEvent("lightengine.renderer.initialized");
		});
	}

	public static void render(ImmutableArray<Entity> entitiesT, Map<ParticleTexture, List<Particle>> particles,
			List<WaterTile> waterTiles, CameraEntity camera, IWorldSimulation worldSimulation, Sun sun, float delta) {
		if (!enabled || reloading)
			return;
		Array<Entity> entitiesR = new Array<>(entitiesT.toArray(Entity.class));
		ImmutableArray<Entity> entities = new ImmutableArray<>(entitiesR);
		resetState();
		renderingManager.preProcess(entities, camera);
		GPUProfiler.start("Main Renderer");
		GPUProfiler.start("IrradianceMap");
		envRenderer.renderEnvironmentMap(camera.getPosition(), skyboxRenderer, worldSimulation, sun.getSunPosition(),
				window);
		irradianceCapture.render(window, envRenderer.getCubeMapTexture().getID());
		GPUProfiler.end();
		GPUProfiler.start("EnvironmentMap");
		envRendererEntities.renderEnvironmentMap(camera.getPosition(), skyboxRenderer, renderingManager,
				worldSimulation, sun, shadowFBO, irradianceCapture.getCubeMapTexture(),
				preFilteredEnvironment.getCubeMapTexture(), preFilteredEnvironment.getBRDFLUT(), window);
		GPUProfiler.start("PreFilteredEnvironment");
		preFilteredEnvironment.render(window, envRendererEntities.getCubeMapTexture().getID());
		GPUProfiler.end();
		GPUProfiler.end();
		GPUProfiler.start("Shadows");
		SunCamera sunCamera = sun.getCamera();
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
					if (!light.isShadowMapCreated())
						continue;
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
		ARBClipControl.glClipControl(ARBClipControl.GL_LOWER_LEFT, ARBClipControl.GL_ZERO_TO_ONE);
		deferredPipeline.begin();
		glDepthFunc(GL_GREATER);
		glClearDepth(0.0);
		clearBuffer(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GPUProfiler.start("Skybox");
		skyboxRenderer.render(camera, worldSimulation, sun.getSunPosition(), true);
		GPUProfiler.end();
		GPUProfiler.start("External");
		if (deferredPass != null)
			deferredPass.render(camera, sunCamera, frustum, shadowFBO);
		GPUProfiler.end();
		GPUProfiler.start("RenderingManager");
		renderingManager.render(camera);
		GPUProfiler.end();
		waterRenderer.render(waterTiles, camera, worldSimulation.getGlobalTime(), frustum);
		glClearDepth(1.0);
		glDepthFunc(GL_LESS);
		deferredPipeline.end();
		ARBClipControl.glClipControl(ARBClipControl.GL_LOWER_LEFT, ARBClipControl.GL_NEGATIVE_ONE_TO_ONE);
		GPUProfiler.end();
		GPUProfiler.start("Deferred Rendering");
		deferredPipeline.preRender(camera, sun, worldSimulation, lightRenderer.getLights(),
				irradianceCapture.getCubeMapTexture(), preFilteredEnvironment.getCubeMapTexture(),
				preFilteredEnvironment.getBRDFLUT(), shadowFBO, exposure);
		GPUProfiler.end();
		GPUProfiler.start("PostFX Pre-Pass");
		ARBClipControl.glClipControl(ARBClipControl.GL_LOWER_LEFT, ARBClipControl.GL_ZERO_TO_ONE);
		postProcessPipeline.begin();
		glDepthFunc(GL_GREATER);
		glClearDepth(0.0);
		clearBuffer(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		deferredPipeline.render(postProcessPipeline.getFBO());
		GPUProfiler.start("Forward");
		if (forwardPass != null)
			forwardPass.render(camera, sunCamera, frustum, shadowFBO);
		particleRenderer.render(particles, camera);
		renderingManager.renderForward(camera, sun, shadowFBO, irradianceCapture.getCubeMapTexture(),
				preFilteredEnvironment.getCubeMapTexture(), preFilteredEnvironment.getBRDFLUT());
		GPUProfiler.end();
		glClearDepth(1.0);
		glDepthFunc(GL_LESS);
		postProcessPipeline.end();
		ARBClipControl.glClipControl(ARBClipControl.GL_LOWER_LEFT, ARBClipControl.GL_NEGATIVE_ONE_TO_ONE);
		GPUProfiler.end();
		GPUProfiler.start("PostFX");
		postProcessPipeline.preRender(camera);
		GPUProfiler.end();
		GPUProfiler.end();
		renderingManager.end();
	}

	public static void cleanUp() {
		if (enabled) {
			enabled = false;
			if (envRenderer != null)
				TaskManager.addTask(() -> envRenderer.cleanUp());
			if (envRendererEntities != null)
				TaskManager.addTask(() -> envRendererEntities.cleanUp());
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
			EventSubsystem.removeEvent("lightengine.renderer.resetshadowmap", shadowMap);
			EventSubsystem.removeEvent("lightengine.renderer.resize", reload);
		}
	}

	public static int getNVGImage() {
		return postProcessPipeline.getNVGImage();
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

	public static Frustum getFrustum() {
		return frustum;
	}

	public static LightRenderer getLightRenderer() {
		return lightRenderer;
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
		return createProjectionMatrix(new Matrix4f(), width, height, fov, nearPlane, farPlane, false);
	}

	public static Matrix4f createProjectionMatrix(int width, int height, float fov, float nearPlane, float farPlane,
			boolean zZeroToOne) {
		return createProjectionMatrix(new Matrix4f(), width, height, fov, nearPlane, farPlane, zZeroToOne);
	}

	public static Matrix4f createProjectionMatrix(Matrix4f proj, int width, int height, float fov, float nearPlane,
			float farPlane, boolean zZeroToOne) {
		if (zZeroToOne && farPlane > 0 && Float.isInfinite(farPlane)) {
			float y_scale = (float) (1f / Math.tan(Math.toRadians(fov / 2f)));
			float x_scale = y_scale / ((float) width / (float) height);
			proj.identity();
			proj.m00(x_scale);
			proj.m11(y_scale);
			proj.m22(0);
			proj.m23(-1);
			proj.m32(nearPlane);
			proj.m33(0);
			proj.assumePerspective();
		} else {
			proj.setPerspective((float) Math.toRadians(fov), (float) width / (float) height, nearPlane, farPlane,
					zZeroToOne);
		}
		return proj;
	}

}
