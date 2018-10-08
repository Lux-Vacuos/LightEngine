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
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.ARBClipControl;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.ecs.entities.Sun;
import net.luxvacuos.lightengine.client.ecs.entities.SunCamera;
import net.luxvacuos.lightengine.client.network.IRenderData;
import net.luxvacuos.lightengine.client.rendering.IRenderer;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.nanovg.IWindow;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Light;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.ParticleTexture;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.WaterTile;
import net.luxvacuos.lightengine.client.rendering.opengl.pipeline.MultiPass;
import net.luxvacuos.lightengine.client.rendering.opengl.pipeline.PostProcess;
import net.luxvacuos.lightengine.client.ui.windows.GLGameWindow;
import net.luxvacuos.lightengine.client.world.particles.Particle;
import net.luxvacuos.lightengine.universal.core.IWorldSimulation;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.subsystems.EventSubsystem;
import net.luxvacuos.lightengine.universal.util.IEvent;
import net.luxvacuos.lightengine.universal.util.registry.KeyCache;

public class GLRenderer implements IRenderer {

	private boolean enabled;

	private EnvironmentRenderer envRenderer;
	private EnvironmentRenderer envRendererEntities;
	private IrradianceCapture irradianceCapture;
	private PreFilteredEnvironment preFilteredEnvironment;

	private ParticleRenderer particleRenderer;
	private SkyboxRenderer skyboxRenderer;
	private WaterRenderer waterRenderer;
	private LightRenderer lightRenderer;
	private RenderingManager renderingManager;

	private IDeferredPipeline deferredPipeline;
	private IPostProcessPipeline postProcessPipeline;
	private static ShadowFBO shadowFBO;

	private Frustum frustum;
	private Window window;

	private IRenderPass shadowPass, deferredPass, forwardPass, occlusionPass;

	private float exposure = 1;

	private int shadowResolution;

	private IEvent shadowMap;

	private GLGameWindow gameWindow;

	public GLRenderer() {
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
	}

	@Override
	public void init() {
		if (enabled)
			return;

		shadowResolution = (int) REGISTRY
				.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/Graphics/shadowsResolution"));

		window = GraphicalSubsystem.getMainWindow();
		var loader = window.getResourceLoader();

		renderingManager = new RenderingManager();
		lightRenderer = new LightRenderer();
		frustum = new Frustum();

		TaskManager.tm.addTaskRenderThread(
				() -> envRenderer = new EnvironmentRenderer(loader.createEmptyCubeMap(32, true, false)));
		TaskManager.tm.addTaskRenderThread(
				() -> envRendererEntities = new EnvironmentRenderer(loader.createEmptyCubeMap(128, true, false)));
		TaskManager.tm.addTaskRenderThread(() -> irradianceCapture = new IrradianceCapture(loader));
		TaskManager.tm.addTaskRenderThread(
				() -> preFilteredEnvironment = new PreFilteredEnvironment(loader.createEmptyCubeMap(128, true, true),
						window));

		TaskManager.tm.addTaskRenderThread(() -> particleRenderer = new ParticleRenderer(loader));
		TaskManager.tm.addTaskRenderThread(() -> skyboxRenderer = new SkyboxRenderer(loader));
		TaskManager.tm.addTaskRenderThread(() -> waterRenderer = new WaterRenderer(loader));
		TaskManager.tm.addTaskRenderThread(() -> renderingManager.addRenderer(new EntityRenderer()));

		TaskManager.tm.addTaskRenderThread(() -> deferredPipeline = new MultiPass(window));
		TaskManager.tm.addTaskRenderThread(() -> postProcessPipeline = new PostProcess(window));
		TaskManager.tm.addTaskRenderThread(() -> shadowFBO = new ShadowFBO(shadowResolution, shadowResolution));

		shadowMap = EventSubsystem.addEvent("lightengine.renderer.resetshadowmap", () -> {
			shadowResolution = (int) REGISTRY
					.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/Graphics/shadowsResolution"));
			TaskManager.tm.addTaskRenderThread(() -> {
				shadowFBO.dispose();
				shadowFBO = new ShadowFBO(shadowResolution, shadowResolution);
			});
		});

		TaskManager.tm.addTaskRenderThread(() -> {
			enabled = true;
			gameWindow = new GLGameWindow();
			gameWindow.setImageID(postProcessPipeline.getNVGImage());
			GraphicalSubsystem.getWindowManager().addWindow(0, gameWindow);
			EventSubsystem.triggerEvent("lightengine.renderer.initialized");
		});
	}

	@Override
	public void render(IRenderData renderData, float delta) {
		if (!enabled)
			return;
		ImmutableArray<Entity> entitiesT = renderData.getEngine().getEntities();
		Map<ParticleTexture, List<Particle>> particles = ParticleDomain.getParticles();
		List<WaterTile> waterTiles = null;
		CameraEntity camera = renderData.getCamera();
		IWorldSimulation worldSimulation = renderData.getWorldSimulation();
		Sun sun = renderData.getSun();

		Array<Entity> entitiesR = new Array<>(entitiesT.toArray(Entity.class));
		ImmutableArray<Entity> entities = new ImmutableArray<>(entitiesR);
		resetState();
		renderingManager.preProcess(entities, camera);
		GPUProfiler.start("Shadows");
		SunCamera sunCamera = sun.getCamera();
		if ((boolean) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/Graphics/shadows"))) {
			GPUProfiler.start("Directional");
			sunCamera.switchProjectionMatrix(0);
			frustum.calculateFrustum(sunCamera);

			shadowFBO.begin();
			shadowFBO.changeTexture(0);
			glClear(GL_DEPTH_BUFFER_BIT);
			if (shadowPass != null)
				shadowPass.render(camera, sunCamera, frustum, shadowFBO);
			renderingManager.renderShadow(sunCamera);

			sunCamera.switchProjectionMatrix(1);
			frustum.calculateFrustum(sunCamera);

			shadowFBO.changeTexture(1);
			glClear(GL_DEPTH_BUFFER_BIT);
			if (shadowPass != null)
				shadowPass.render(camera, sunCamera, frustum, shadowFBO);
			renderingManager.renderShadow(sunCamera);

			sunCamera.switchProjectionMatrix(2);
			frustum.calculateFrustum(sunCamera);

			shadowFBO.changeTexture(2);
			glClear(GL_DEPTH_BUFFER_BIT);
			if (shadowPass != null)
				shadowPass.render(camera, sunCamera, frustum, shadowFBO);
			renderingManager.renderShadow(sunCamera);

			sunCamera.switchProjectionMatrix(3);
			frustum.calculateFrustum(sunCamera);

			shadowFBO.changeTexture(3);
			glClear(GL_DEPTH_BUFFER_BIT);
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
					frustum.calculateFrustum(light.getCamera());
					light.getShadowMap().begin();
					glClear(GL_DEPTH_BUFFER_BIT);
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
		GPUProfiler.start("Occlusion");
		frustum.calculateFrustum(camera);
		glClear(GL_DEPTH_BUFFER_BIT);
		if (occlusionPass != null)
			occlusionPass.render(camera, sunCamera, frustum, shadowFBO);
		GPUProfiler.end();
		GPUProfiler.start("G-Buffer pass");
		ARBClipControl.glClipControl(ARBClipControl.GL_LOWER_LEFT, ARBClipControl.GL_ZERO_TO_ONE);
		deferredPipeline.begin();
		glDepthFunc(GL_GREATER);
		glClearDepth(0.0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
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
		GPUProfiler.start("Water");
		waterRenderer.render(waterTiles, camera, worldSimulation.getGlobalTime(), frustum);
		GPUProfiler.end();
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
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
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

	@Override
	public void resize(int width, int height) {
		if (!enabled)
			return;
		deferredPipeline.resize(width, height);
		postProcessPipeline.resize(width, height);
		EventSubsystem.triggerEvent("lightengine.renderer.postresize");
		gameWindow.setImageID(postProcessPipeline.getNVGImage());
	}

	@Override
	public void dispose() {
		if (!enabled)
			return;
		enabled = false;
		gameWindow.closeWindow();
		TaskManager.tm.addTaskRenderThread(() -> envRenderer.cleanUp());
		TaskManager.tm.addTaskRenderThread(() -> envRendererEntities.cleanUp());
		TaskManager.tm.addTaskRenderThread(() -> shadowFBO.dispose());
		TaskManager.tm.addTaskRenderThread(() -> deferredPipeline.dispose());
		TaskManager.tm.addTaskRenderThread(() -> postProcessPipeline.dispose());
		TaskManager.tm.addTaskRenderThread(() -> particleRenderer.cleanUp());
		TaskManager.tm.addTaskRenderThread(() -> irradianceCapture.dispose());
		TaskManager.tm.addTaskRenderThread(() -> preFilteredEnvironment.dispose());
		TaskManager.tm.addTaskRenderThread(() -> waterRenderer.dispose());
		TaskManager.tm.addTaskRenderThread(() -> renderingManager.dispose());
		lightRenderer.dispose();
		EventSubsystem.removeEvent("lightengine.renderer.resetshadowmap", shadowMap);
	}

	@Override
	public void resetState() {
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glEnable(GL_DEPTH_TEST);
		glDisable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void setShadowPass(IRenderPass shadowPass) {
		this.shadowPass = shadowPass;
	}

	@Override
	public void setDeferredPass(IRenderPass deferredPass) {
		this.deferredPass = deferredPass;
	}

	@Override
	public void setForwardPass(IRenderPass forwardPass) {
		this.forwardPass = forwardPass;
	}

	@Override
	public void setOcclusionPass(IRenderPass occlusionPass) {
		this.occlusionPass = occlusionPass;
	}

	@Override
	public Frustum getFrustum() {
		return frustum;
	}

	@Override
	public LightRenderer getLightRenderer() {
		return lightRenderer;
	}

	@Override
	public IWindow getWindow() {
		return gameWindow;
	}

}
