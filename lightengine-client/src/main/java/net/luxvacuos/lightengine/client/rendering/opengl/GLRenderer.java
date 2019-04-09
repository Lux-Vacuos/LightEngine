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
import static org.lwjgl.opengl.GL11C.GL_BLEND;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.GL_FRONT;
import static org.lwjgl.opengl.GL11C.GL_GREATER;
import static org.lwjgl.opengl.GL11C.GL_LESS;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.glBlendFunc;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glClearDepth;
import static org.lwjgl.opengl.GL11C.glCullFace;
import static org.lwjgl.opengl.GL11C.glDepthFunc;
import static org.lwjgl.opengl.GL11C.glDisable;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL32C.GL_TEXTURE_CUBE_MAP_SEAMLESS;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.ARBClipControl;

import com.badlogic.ashley.core.Family;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.ecs.components.ModelLoader;
import net.luxvacuos.lightengine.client.ecs.components.WaterTileComp;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.ecs.entities.Sun;
import net.luxvacuos.lightengine.client.ecs.entities.SunCamera;
import net.luxvacuos.lightengine.client.network.IRenderingData;
import net.luxvacuos.lightengine.client.rendering.IRenderer;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.opengl.IRenderPass.IForwardPass;
import net.luxvacuos.lightengine.client.rendering.opengl.IRenderPass.IGBufferPass;
import net.luxvacuos.lightengine.client.rendering.opengl.IRenderPass.IShadowPass;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Light;
import net.luxvacuos.lightengine.client.rendering.opengl.pipeline.MultiPass;
import net.luxvacuos.lightengine.client.rendering.opengl.pipeline.PostProcess;
import net.luxvacuos.lightengine.client.rendering.opengl.v2.DeferredPipeline;
import net.luxvacuos.lightengine.client.rendering.opengl.v2.PostProcessPipeline;
import net.luxvacuos.lightengine.client.ui.v2.surfaces.RendererSurface;
import net.luxvacuos.lightengine.client.world.ParticleDomain;
import net.luxvacuos.lightengine.universal.core.IWorldSimulation;
import net.luxvacuos.lightengine.universal.core.Task;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.subsystems.EventSubsystem;
import net.luxvacuos.lightengine.universal.util.IEvent;

public class GLRenderer implements IRenderer {

	private boolean enabled;

	private EnvironmentRenderer envRenderer;
	private EnvironmentRenderer envRendererEntities;
	private IrradianceCapture irradianceCapture;
	private PreFilteredEnvironment preFilteredEnvironment;

	private ParticleRenderer particleRenderer;
	private SkydomeRenderer skydomeRenderer;
	private WaterRenderer waterRenderer;
	private LightRenderer lightRenderer;
	private RenderingManager renderingManager;

	private ShadowFBO shadowFBO;

	private DeferredPipeline dp;
	private PostProcessPipeline pp;

	private Frustum frustum;
	private Window window;

	private IShadowPass shadowPass = (a) -> {
	};
	private IGBufferPass gbufferPass = (a) -> {
	};
	private IForwardPass forwardPass = (a, b, c, d, e, f) -> {
	};

	private float exposure = 1;

	private IEvent shadowMap;

	private RendererSurface surface;

	private RenderingSettings rs;

	private RendererData rnd;

	private Family waterFam = Family.one(WaterTileComp.class).get();
	private Family renderable = Family.one(ModelLoader.class).get();

	public GLRenderer(RenderingSettings rs) {
		this.rs = rs;
		rnd = new RendererData();
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

		window = GraphicalSubsystem.getMainWindow();
		var loader = window.getResourceLoader();

		renderingManager = new RenderingManager();
		lightRenderer = new LightRenderer();
		frustum = new Frustum();

		TaskManager.tm.submitRenderThread(new Task<Void>() {
			@Override
			protected Void call() {
				envRenderer = new EnvironmentRenderer(loader.createEmptyCubeMap(32, true, false));
				envRendererEntities = new EnvironmentRenderer(loader.createEmptyCubeMap(128, true, false));
				irradianceCapture = new IrradianceCapture(loader);
				rnd.irradianceCapture = irradianceCapture.getCubeMapTexture();
				preFilteredEnvironment = new PreFilteredEnvironment(loader.createEmptyCubeMap(128, true, true), window);
				rnd.brdfLUT = preFilteredEnvironment.getBRDFLUT();
				rnd.environmentMap = preFilteredEnvironment.getCubeMapTexture();
				particleRenderer = new ParticleRenderer(loader);
				skydomeRenderer = new SkydomeRenderer(loader);
				waterRenderer = new WaterRenderer(loader);
				renderingManager.addRenderer(new EntityRenderer());
				shadowFBO = new ShadowFBO(rs.shadowsResolution, rs.shadowsResolution);
				rnd.shadow = shadowFBO;
				dp = new MultiPass(window.getWidth(), window.getHeight());
				pp = new PostProcess(window.getWidth(), window.getHeight(), window.getNVGID());
				if (surface != null)
					surface.setImage(pp.getNVGTexture());
				return null;
			}
		});

		shadowMap = EventSubsystem.addEvent("lightengine.renderer.resetshadowmap",
				() -> TaskManager.tm.addTaskRenderThread(() -> {
					shadowFBO.dispose();
					shadowFBO = new ShadowFBO(rs.shadowsResolution, rs.shadowsResolution);
					rnd.shadow = shadowFBO;
				}));

		rnd.lights = lightRenderer.getLights();
		rnd.exposure = exposure;

		enabled = true;
		EventSubsystem.triggerEvent("lightengine.renderer.initialized");
	}

	private void shadowPass(IRenderingData rd) {
		GPUProfiler.start("Shadow Pass");
		SunCamera sunCamera = rd.getSun().getCamera();
		if (rs.shadowsEnabled) {
			GPUProfiler.start("Directional");
			sunCamera.switchProjectionMatrix(0);
			// frustum.calculateFrustum(sunCamera);

			shadowFBO.begin();
			shadowFBO.changeTexture(0);
			glClear(GL_DEPTH_BUFFER_BIT);
			renderingManager.renderShadow(sunCamera);
			shadowPass.shadowPass(sunCamera);

			sunCamera.switchProjectionMatrix(1);
			// frustum.calculateFrustum(sunCamera);

			shadowFBO.changeTexture(1);
			glClear(GL_DEPTH_BUFFER_BIT);
			renderingManager.renderShadow(sunCamera);
			shadowPass.shadowPass(sunCamera);

			sunCamera.switchProjectionMatrix(2);
			// frustum.calculateFrustum(sunCamera);

			shadowFBO.changeTexture(2);
			glClear(GL_DEPTH_BUFFER_BIT);
			renderingManager.renderShadow(sunCamera);
			shadowPass.shadowPass(sunCamera);

			sunCamera.switchProjectionMatrix(3);
			// frustum.calculateFrustum(sunCamera);

			shadowFBO.changeTexture(3);
			glClear(GL_DEPTH_BUFFER_BIT);
			renderingManager.renderShadow(sunCamera);
			shadowPass.shadowPass(sunCamera);
			shadowFBO.end();
			GPUProfiler.end();
			GPUProfiler.start("Point lights");
			glCullFace(GL_FRONT);
			for (Light light : lightRenderer.getLights()) {
				if (light.isShadow()) {
					if (!light.isShadowMapCreated())
						continue;
					// frustum.calculateFrustum(light.getCamera());
					light.getShadowMap().begin();
					glClear(GL_DEPTH_BUFFER_BIT);
					renderingManager.renderShadow(light.getCamera());
					light.getShadowMap().end();
				}
			}
			glCullFace(GL_BACK);
			GPUProfiler.end();
		}
		GPUProfiler.end();
	}

	private void environmentPass(IRenderingData rd) {
		CameraEntity camera = rd.getCamera();
		IWorldSimulation worldSimulation = rd.getWorldSimulation();
		Sun sun = rd.getSun();

		GPUProfiler.start("Environment Pass");
		GPUProfiler.start("Irradiance");
		GPUProfiler.start("CubeMap Render");
		envRenderer.renderEnvironmentMap(camera.getPosition(), skydomeRenderer, worldSimulation, sun.getSunPosition(),
				window);
		GPUProfiler.end();
		GPUProfiler.start("Irradiance Capture");
		irradianceCapture.render(window, envRenderer.getCubeMapTexture().getTexture());
		GPUProfiler.end();
		GPUProfiler.end();
		GPUProfiler.start("Reflections");
		GPUProfiler.start("CubeMap Render");
		envRendererEntities.renderEnvironmentMap(camera.getPosition(), skydomeRenderer, renderingManager,
				worldSimulation, sun, shadowFBO, irradianceCapture.getCubeMapTexture(),
				preFilteredEnvironment.getCubeMapTexture(), preFilteredEnvironment.getBRDFLUT(), window);
		GPUProfiler.end();
		GPUProfiler.start("PreFilteredEnvironment");
		preFilteredEnvironment.render(window, envRendererEntities.getCubeMapTexture().getTexture());
		GPUProfiler.end();
		GPUProfiler.end();
		GPUProfiler.end();
	}

	private void occlusionPass() {
		GPUProfiler.start("Occlusion");
		// frustum.calculateFrustum(camera);
		glClear(GL_DEPTH_BUFFER_BIT);
		GPUProfiler.end();
	}

	private void gBufferPass(IRenderingData rd) {
		CameraEntity camera = rd.getCamera();
		IWorldSimulation worldSimulation = rd.getWorldSimulation();
		Sun sun = rd.getSun();

		GPUProfiler.start("G-Buffer pass");
		ARBClipControl.glClipControl(ARBClipControl.GL_LOWER_LEFT, ARBClipControl.GL_ZERO_TO_ONE);
		dp.bind();
		glDepthFunc(GL_GREATER);
		glClearDepth(0.0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GPUProfiler.start("External");
		gbufferPass.gBufferPass(camera);
		GPUProfiler.end();
		GPUProfiler.start("RenderingManager");
		renderingManager.render(camera);
		GPUProfiler.end();
		GPUProfiler.start("Water");
		waterRenderer.render(rd.getEngine().getEntitiesFor(waterFam), camera, worldSimulation.getGlobalTime(), frustum);
		GPUProfiler.end();
		GPUProfiler.start("Skybox");
		skydomeRenderer.render(camera, worldSimulation, sun.getSunPosition(), true, true);
		GPUProfiler.end();
		glClearDepth(1.0);
		glDepthFunc(GL_LESS);
		dp.unbind();
		ARBClipControl.glClipControl(ARBClipControl.GL_LOWER_LEFT, ARBClipControl.GL_NEGATIVE_ONE_TO_ONE);
		GPUProfiler.end();
	}

	private void deferredPass(IRenderingData rd) {
		GPUProfiler.start("Deferred Pass");
		dp.process(rs, rnd, rd);
		GPUProfiler.end();
	}

	private void forwardPass(IRenderingData rd) {
		CameraEntity camera = rd.getCamera();
		Sun sun = rd.getSun();

		GPUProfiler.start("Forward Pass");
		ARBClipControl.glClipControl(ARBClipControl.GL_LOWER_LEFT, ARBClipControl.GL_ZERO_TO_ONE);
		pp.bind();
		glDepthFunc(GL_GREATER);
		glClearDepth(0.0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		dp.render(pp.getMain());
		GPUProfiler.start("External");
		forwardPass.forwardPass(camera, sun, shadowFBO, irradianceCapture.getCubeMapTexture(),
				preFilteredEnvironment.getCubeMapTexture(), preFilteredEnvironment.getBRDFLUT());
		GPUProfiler.end();
		GPUProfiler.start("Particles");
		particleRenderer.render(ParticleDomain.getParticles(), camera);
		GPUProfiler.end();
		GPUProfiler.start("RenderingManager");
		renderingManager.renderForward(camera, sun, shadowFBO, irradianceCapture.getCubeMapTexture(),
				preFilteredEnvironment.getCubeMapTexture(), preFilteredEnvironment.getBRDFLUT());
		GPUProfiler.end();
		glClearDepth(1.0);
		glDepthFunc(GL_LESS);
		pp.unbind();
		ARBClipControl.glClipControl(ARBClipControl.GL_LOWER_LEFT, ARBClipControl.GL_NEGATIVE_ONE_TO_ONE);
		GPUProfiler.end();
	}

	private void postFXPass(IRenderingData rd) {
		GPUProfiler.start("PostFX");
		pp.process(rs, rnd, rd);
		GPUProfiler.end();
	}

	@Override
	public void render(IRenderingData rd, float delta) {
		if (!enabled)
			return;
		resetState();
		renderingManager.preProcess(rd.getEngine().getEntitiesFor(renderable));

		GPUProfiler.start("3D Renderer");

		shadowPass(rd);

		environmentPass(rd);

		// occlusionPass();

		gBufferPass(rd);

		deferredPass(rd);

		forwardPass(rd);

		postFXPass(rd);

		GPUProfiler.end();
		renderingManager.end();

		pp.render();

		rnd.previousCameraPosition.set(rd.getCamera().getPosition());
		rnd.previousViewMatrix.set(rd.getCamera().getViewMatrix());

		if (window.getKeyboardHandler().isKeyPressed(GLFW.GLFW_KEY_F2)) {
			window.getKeyboardHandler().ignoreKeyUntilRelease(GLFW.GLFW_KEY_F2);
			Logger.log("Reloading Shaders...");
			dp.reloadShaders();
			pp.reloadShaders();
		}
	}

	@Override
	public void resize(int width, int height) {
		if (!enabled)
			return;
		dp.resize(width, height);
		pp.resize(width, height);
		EventSubsystem.triggerEvent("lightengine.renderer.postresize");
		if (surface != null)
			surface.setImage(pp.getNVGTexture());
	}

	@Override
	public void dispose() {
		if (!enabled)
			return;
		enabled = false;
		TaskManager.tm.submitRenderThread(new Task<Void>() {
			@Override
			protected Void call() {
				envRenderer.cleanUp();
				envRendererEntities.cleanUp();
				shadowFBO.dispose();
				dp.dispose();
				pp.dispose();
				particleRenderer.cleanUp();
				irradianceCapture.dispose();
				preFilteredEnvironment.dispose();
				waterRenderer.dispose();
				renderingManager.dispose();
				return null;
			}
		}).get();
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
	public void setShadowPass(IShadowPass shadowPass) {
		this.shadowPass = shadowPass;
	}

	@Override
	public void setGBufferPass(IGBufferPass gbufferPass) {
		this.gbufferPass = gbufferPass;
	}

	@Override
	public void setForwardPass(IForwardPass forwardPass) {
		this.forwardPass = forwardPass;
	}

	@Override
	public void setSurface(RendererSurface surface) {
		if (this.surface != null)
			this.surface.setImage(-1);
		this.surface = surface;
		if (enabled)
			this.surface.setImage(pp.getNVGTexture());
	}

	@Override
	public Frustum getFrustum() {
		return frustum;
	}

	@Override
	public LightRenderer getLightRenderer() {
		return lightRenderer;
	}

}
