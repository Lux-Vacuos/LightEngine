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

package net.luxvacuos.lightengine.client.ui.v2;

import java.nio.ByteBuffer;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;

import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Surface;
import net.luxvacuos.lightengine.client.resources.tasks.IOToByteBuffer;
import net.luxvacuos.lightengine.universal.core.Task;
import net.luxvacuos.lightengine.universal.core.TaskManager;

public class Image extends Surface {

	private NVGPaint paint;
	private NVGColor color;
	private int image = -1;
	private Task<ByteBuffer> task;

	public Image(String path) {
		task = TaskManager.tm.submitBackgroundThread(new IOToByteBuffer("assets/textures/menu/LuxVacuos-Logo.png"));
	}

	@Override
	public void init(long ctx, MouseHandler mh, KeyboardHandler kh) {
		super.init(ctx, mh, kh);
		color = Theme.setColor("#FFFFFFFF");
		paint = NVGPaint.malloc();
		ByteBuffer buffer = task.get();
		image = nvgCreateImageMem(ctx, 0, buffer);
		memFree(buffer);
	}

	@Override
	protected void renderSurface(float delta) {
		super.renderSurface(delta);
		if (image == -1)
			return;
		nvgImagePattern(ctx, elementPos.x, elementPos.y, elementPos.z, elementPos.w, 0, image, 1.0f, paint);
		nvgBeginPath(ctx);
		nvgRect(ctx, elementPos.x, elementPos.y, elementPos.z, elementPos.w);
		nvgFillColor(ctx, color);
		nvgFillPaint(ctx, paint);
		nvgFill(ctx);
	}

	@Override
	public void dispose() {
		super.dispose();
		paint.free();
		if (image != -1)
			nvgDeleteImage(ctx, image);
	}

	public void setImage(int image) {
		this.image = image;
	}

}
