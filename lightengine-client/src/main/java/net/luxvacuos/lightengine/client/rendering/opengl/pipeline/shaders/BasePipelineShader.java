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

package net.luxvacuos.lightengine.client.rendering.opengl.pipeline.shaders;

import org.joml.Vector2f;

import net.luxvacuos.lightengine.client.rendering.opengl.RenderingSettings;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.ShaderProgram;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.Attribute;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformBoolean;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformInteger;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformVec2;
import net.luxvacuos.lightengine.universal.core.subsystems.ResManager;
import net.luxvacuos.lightengine.universal.resources.ResourceType;

public class BasePipelineShader extends ShaderProgram {

	private UniformVec2 resolution = new UniformVec2("resolution");

	private UniformInteger shadowDrawDistance = new UniformInteger("shadowDrawDistance");

	private UniformBoolean useFXAA = new UniformBoolean("useFXAA");
	private UniformBoolean useDOF = new UniformBoolean("useDOF");
	private UniformBoolean useMotionBlur = new UniformBoolean("useMotionBlur");
	private UniformBoolean useReflections = new UniformBoolean("useReflections");
	private UniformBoolean useVolumetricLight = new UniformBoolean("useVolumetricLight");
	private UniformBoolean useAmbientOcclusion = new UniformBoolean("useAmbientOcclusion");
	private UniformBoolean useChromaticAberration = new UniformBoolean("useChromaticAberration");
	private UniformBoolean useLensFlares = new UniformBoolean("useLensFlares");
	private UniformBoolean useShadows = new UniformBoolean("useShadows");

	private UniformInteger frame = new UniformInteger("frame");

	public BasePipelineShader(String name) {
		super(ResManager.getResourceOfType("ENGINE_RND_" + name + "_VS", ResourceType.SHADER).get(),
				ResManager.getResourceOfType("ENGINE_RND_" + name + "_FS", ResourceType.SHADER).get(),
				new Attribute(0, "position"));
		super.storeUniforms(resolution, shadowDrawDistance, useFXAA, useDOF, useMotionBlur, useReflections,
				useVolumetricLight, useAmbientOcclusion, useChromaticAberration, useLensFlares, useShadows, frame);
		super.validate();
	}

	public void loadSettings(RenderingSettings rs) {
		this.useDOF.loadBoolean(rs.depthOfFieldEnabled);
		this.useFXAA.loadBoolean(rs.fxaaEnabled);
		this.useMotionBlur.loadBoolean(rs.motionBlurEnabled);
		this.useVolumetricLight.loadBoolean(rs.volumetricLightEnabled);
		this.useReflections.loadBoolean(rs.ssrEnabled);
		this.useAmbientOcclusion.loadBoolean(rs.ambientOcclusionEnabled);
		this.shadowDrawDistance.loadInteger(rs.shadowsDrawDistance);
		this.useChromaticAberration.loadBoolean(rs.chromaticAberrationEnabled);
		this.useLensFlares.loadBoolean(rs.lensFlaresEnabled);
		this.useShadows.loadBoolean(rs.shadowsEnabled);
	}

	public void loadFrame(int frame) {
		this.frame.loadInteger(frame);
	}

	public void loadResolution(Vector2f res) {
		resolution.loadVec2(res);
	}

}
