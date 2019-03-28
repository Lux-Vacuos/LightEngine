package net.luxvacuos.lightengine.client.resources.internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.luxvacuos.lightengine.universal.resources.ResourceDefinition;
import net.luxvacuos.lightengine.universal.resources.ResourceType;
import net.luxvacuos.lightengine.universal.resources.SimpleResource;

public class ResDefsGenerator {

	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static void main(String[] args) {
		generateDefaultClient();
	}

	private static void generateDefaultClient() {
		ResourceDefinition rd = new ResourceDefinition();
		rd.getResources().put("ENGINE_Cursor", new SimpleResource(ResourceType.CURSOR, "assets/cursors/arrow.png"));
		rd.getResources().put("ENGINE_Icon32", new SimpleResource(ResourceType.ICON, "assets/icons/icon32.png"));
		rd.getResources().put("ENGINE_Icon64", new SimpleResource(ResourceType.ICON, "assets/icons/icon64.png"));
		rd.getDefinitions().add("engine/resdefinitions/defaultShaders.json");
		rd.getDefinitions().add("engine/resdefinitions/defaultISL.json");
		System.out.println(gson.toJson(rd));
	}

	private static void generateDefaultShaders() {
		ResourceDefinition rd = new ResourceDefinition();
		rd.getDefinitions().add("engine/resdefinitions/wmShaders.json");
		rd.getDefinitions().add("engine/resdefinitions/rendererShaders.json");
		System.out.println(gson.toJson(rd));
	}

	private static void generateWMShaders() {
		ResourceDefinition rd = new ResourceDefinition();
		rd.getResources().put("ENGINE_WM_3DWindow_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/wm/3DWindow.vs"));
		rd.getResources().put("ENGINE_WM_3DWindow_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/wm/3DWindow.fs"));
		System.out.println(gson.toJson(rd));
	}

	private static void generateRendererShaders() {
		ResourceDefinition rd = new ResourceDefinition();
		rd.getResources().put("ENGINE_RND_BRDFIntegrationMap_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/BRDFIntegrationMap.vs"));
		rd.getResources().put("ENGINE_RND_BRDFIntegrationMap_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/BRDFIntegrationMap.fs"));
		rd.getResources().put("ENGINE_RND_EntityBasic_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/EntityBasic.vs"));
		rd.getResources().put("ENGINE_RND_EntityBasic_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/EntityBasic.fs"));
		rd.getResources().put("ENGINE_RND_EntityDeferred_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/EntityDeferred.vs"));
		rd.getResources().put("ENGINE_RND_EntityDeferred_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/EntityDeferred.fs"));
		rd.getResources().put("ENGINE_RND_EntityForward_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/EntityForward.vs"));
		rd.getResources().put("ENGINE_RND_EntityForward_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/EntityForward.fs"));
		rd.getResources().put("ENGINE_RND_IrradianceCapture_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/IrradianceCapture.vs"));
		rd.getResources().put("ENGINE_RND_IrradianceCapture_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/IrradianceCapture.fs"));
		rd.getResources().put("ENGINE_RND_Particle_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/Particle.vs"));
		rd.getResources().put("ENGINE_RND_Particle_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/Particle.fs"));
		rd.getResources().put("ENGINE_RND_PreFilteredEnvironment_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/PreFilteredEnvironment.vs"));
		rd.getResources().put("ENGINE_RND_PreFilteredEnvironment_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/PreFilteredEnvironment.fs"));
		rd.getResources().put("ENGINE_RND_Skydome_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/Skydome.vs"));
		rd.getResources().put("ENGINE_RND_Skydome_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/Skydome.fs"));
		rd.getResources().put("ENGINE_RND_Water_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/Water.vs"));
		rd.getResources().put("ENGINE_RND_Water_GS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/Water.gs"));
		rd.getResources().put("ENGINE_RND_Water_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/Water.fs"));
		rd.getDefinitions().add("engine/resdefinitions/deferredShaders.json");
		rd.getDefinitions().add("engine/resdefinitions/postProcessShaders.json");
		System.out.println(gson.toJson(rd));
	}

	private static void generateDeferredShaders() {
		ResourceDefinition rd = new ResourceDefinition();
		rd.getResources().put("ENGINE_RND_DFR_Bloom_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/Bloom.vs"));
		rd.getResources().put("ENGINE_RND_DFR_Bloom_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/Bloom.fs"));
		rd.getResources().put("ENGINE_RND_DFR_BloomMask_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/BloomMask.vs"));
		rd.getResources().put("ENGINE_RND_DFR_BloomMask_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/BloomMask.fs"));
		rd.getResources().put("ENGINE_RND_DFR_ColorCorrection_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/ColorCorrection.vs"));
		rd.getResources().put("ENGINE_RND_DFR_ColorCorrection_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/ColorCorrection.fs"));
		rd.getResources().put("ENGINE_RND_DFR_Final_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/Final.vs"));
		rd.getResources().put("ENGINE_RND_DFR_Final_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/Final.fs"));
		rd.getResources().put("ENGINE_RND_DFR_GaussianBlur_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/GaussianBlur.vs"));
		rd.getResources().put("ENGINE_RND_DFR_GaussianBlur_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/GaussianBlur.fs"));
		rd.getResources().put("ENGINE_RND_DFR_LensFlares_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/LensFlares.vs"));
		rd.getResources().put("ENGINE_RND_DFR_LensFlares_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/LensFlares.fs"));
		rd.getResources().put("ENGINE_RND_DFR_LensFlaresMod_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/LensFlaresMod.vs"));
		rd.getResources().put("ENGINE_RND_DFR_LensFlaresMod_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/LensFlaresMod.fs"));
		rd.getResources().put("ENGINE_RND_DFR_Lighting_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/Lighting.vs"));
		rd.getResources().put("ENGINE_RND_DFR_Lighting_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/Lighting.fs"));
		rd.getResources().put("ENGINE_RND_DFR_LocalLightsPass_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/LocalLightsPass.vs"));
		rd.getResources().put("ENGINE_RND_DFR_LocalLightsPass_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/LocalLightsPass.fs"));
		rd.getResources().put("ENGINE_RND_DFR_Reflections_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/Reflections.vs"));
		rd.getResources().put("ENGINE_RND_DFR_Reflections_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/Reflections.fs"));
		rd.getResources().put("ENGINE_RND_DFR_VolumetricLight_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/VolumetricLight.vs"));
		rd.getResources().put("ENGINE_RND_DFR_VolumetricLight_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/deferred/VolumetricLight.fs"));
		System.out.println(gson.toJson(rd));
	}

	private static void generatePostProcessShaders() {
		ResourceDefinition rd = new ResourceDefinition();
		rd.getResources().put("ENGINE_RND_POST_ChromaticAberration_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/postprocess/ChromaticAberration.vs"));
		rd.getResources().put("ENGINE_RND_POST_ChromaticAberration_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/postprocess/ChromaticAberration.fs"));
		rd.getResources().put("ENGINE_RND_POST_DoF_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/postprocess/DoF.vs"));
		rd.getResources().put("ENGINE_RND_POST_DoF_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/postprocess/DoF.fs"));
		rd.getResources().put("ENGINE_RND_POST_Final_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/postprocess/Final.vs"));
		rd.getResources().put("ENGINE_RND_POST_Final_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/postprocess/Final.fs"));
		rd.getResources().put("ENGINE_RND_POST_MotionBlur_VS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/postprocess/MotionBlur.vs"));
		rd.getResources().put("ENGINE_RND_POST_MotionBlur_FS",
				new SimpleResource(ResourceType.SHADER, "assets/shaders/postprocess/MotionBlur.fs"));
		System.out.println(gson.toJson(rd));
	}

	private static void generateISL() {
		ResourceDefinition rd = new ResourceDefinition();
		rd.getResources().put("ENGINE_ISL_common",
				new SimpleResource(ResourceType.ISL, "assets/shaders/includes/common.isl"));
		rd.getResources().put("ENGINE_ISL_global",
				new SimpleResource(ResourceType.ISL, "assets/shaders/includes/global.isl"));
		rd.getResources().put("ENGINE_ISL_lighting",
				new SimpleResource(ResourceType.ISL, "assets/shaders/includes/lighting.isl"));
		rd.getResources().put("ENGINE_ISL_materials",
				new SimpleResource(ResourceType.ISL, "assets/shaders/includes/materials.isl"));
		System.out.println(gson.toJson(rd));
	}

}
