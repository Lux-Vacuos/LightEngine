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

package net.luxvacuos.lightengine.client.rendering.opengl.pipeline;

import net.luxvacuos.lightengine.client.rendering.opengl.v2.DeferredPipeline;

public class MultiPass extends DeferredPipeline {

	public MultiPass(int width, int height) {
		super(width, height);
	}

	private Lighting lighting;
	private VolumetricLight volumetricLight;
	private BloomMask bloomMask;
	private GaussianBlur gH1, gH2, gV1, gV2;
	private Reflections reflections;
	private ColorCorrection colorCorrection;
	private LensFlares lensFlares;
	private LensFlareMod lensFlareMod;
	private Bloom bloom;
	private PointLightPass pointLightPass;

	public void setupPasses() {
		volumetricLight = new VolumetricLight(0.5f);
		super.passes.add(volumetricLight);

		gH1 = new GaussianBlur(false, 0.5f);
		super.passes.add(gH1);

		gV1 = new GaussianBlur(true, 0.5f);
		super.passes.add(gV1);

		lighting = new Lighting();
		super.passes.add(lighting);

		// pointLightPass = new PointLightPass("PointLight", width, height);
		// super.passes.add(pointLightPass);

		reflections = new Reflections();
		super.passes.add(reflections);

		bloomMask = new BloomMask();
		super.passes.add(bloomMask);

		super.passes.add(gH1);
		super.passes.add(gV1);

		bloom = new Bloom();
		super.passes.add(bloom);

		lensFlares = new LensFlares();
		super.passes.add(lensFlares);

		lensFlareMod = new LensFlareMod();
		super.passes.add(lensFlareMod);

		colorCorrection = new ColorCorrection();
		super.passes.add(colorCorrection);

	}

}
