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

package net.luxvacuos.lightengine.client.rendering.api.nanovg.shaders;

import org.joml.Vector2f;

import net.luxvacuos.lightengine.client.rendering.api.opengles.shaders.ShaderProgram;
import net.luxvacuos.lightengine.client.rendering.api.opengles.shaders.data.Attribute;
import net.luxvacuos.lightengine.client.rendering.api.opengles.shaders.data.UniformBoolean;
import net.luxvacuos.lightengine.client.rendering.api.opengles.shaders.data.UniformSampler;
import net.luxvacuos.lightengine.client.rendering.api.opengles.shaders.data.UniformVec2;

public class WindowManagerShader extends ShaderProgram {

	private UniformSampler image = new UniformSampler("image");
	private UniformSampler window = new UniformSampler("window");
	private UniformVec2 resolution = new UniformVec2("resolution");
	private UniformVec2 windowPosition = new UniformVec2("windowPosition");
	private UniformBoolean blurBehind = new UniformBoolean("blurBehind");

	public WindowManagerShader(String type) {
		super("wm/" + type + ".vs", "wm/" + type + ".fs", new Attribute(0, "position"));
		super.storeAllUniformLocations(image, window, resolution, blurBehind, windowPosition);
		connectTextureUnits();
	}

	private void connectTextureUnits() {
		super.start();
		image.loadTexUnit(0);
		window.loadTexUnit(1);
		super.stop();
	}

	public void loadResolution(Vector2f res) {
		resolution.loadVec2(res);
	}

	public void loadBlurBehind(boolean blur) {
		blurBehind.loadBoolean(blur);
	}

	public void loadWindowPosition(Vector2f pos) {
		windowPosition.loadVec2(pos);
	}

}
