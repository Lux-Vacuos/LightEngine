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

package net.luxvacuos.lightengine.client.rendering.nanovg;

import org.lwjgl.nanovg.NVGLUFramebuffer;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.nanovg.NanoVGGLES3;

import net.luxvacuos.lightengine.client.rendering.glfw.RenderingAPI;

public final class NVGFramebuffers {

	private static IFBO fboi;

	public static void nvgluBindFramebuffer(long ctx, NVGLUFramebuffer fb) {
		fboi.nvgluBindFramebuffer(ctx, fb);
	}

	public static NVGLUFramebuffer nvgluCreateFramebuffer(long ctx, int w, int h, int imageFlags) {
		return fboi.nvgluCreateFramebuffer(ctx, w, h, imageFlags);
	}

	public static void nvgluDeleteFramebuffer(long ctx, NVGLUFramebuffer fb) {
		fboi.nvgluDeleteFramebuffer(ctx, fb);
	}

	public static void init(RenderingAPI api) {
		NVGFramebuffers b = new NVGFramebuffers();
		switch (api) {
		case GL:
			fboi = b.new FBOGL();
			break;
		case GLES:
			fboi = b.new FBOGLES();
			break;
		default:
			break;
		}
	}

	private interface IFBO {
		public void nvgluBindFramebuffer(long ctx, NVGLUFramebuffer fb);

		public NVGLUFramebuffer nvgluCreateFramebuffer(long ctx, int w, int h, int imageFlags);

		public void nvgluDeleteFramebuffer(long ctx, NVGLUFramebuffer fb);
	}

	private class FBOGL implements IFBO {

		@Override
		public void nvgluBindFramebuffer(long ctx, NVGLUFramebuffer fb) {
			NanoVGGL3.nvgluBindFramebuffer(ctx, fb);
		}

		@Override
		public NVGLUFramebuffer nvgluCreateFramebuffer(long ctx, int w, int h, int imageFlags) {
			return NanoVGGL3.nvgluCreateFramebuffer(ctx, w, h, imageFlags);
		}

		@Override
		public void nvgluDeleteFramebuffer(long ctx, NVGLUFramebuffer fb) {
			NanoVGGL3.nvgluDeleteFramebuffer(ctx, fb);
		}

	}

	private class FBOGLES implements IFBO {

		@Override
		public void nvgluBindFramebuffer(long ctx, NVGLUFramebuffer fb) {
			NanoVGGLES3.nvgluBindFramebuffer(ctx, fb);
		}

		@Override
		public NVGLUFramebuffer nvgluCreateFramebuffer(long ctx, int w, int h, int imageFlags) {
			return NanoVGGLES3.nvgluCreateFramebuffer(ctx, w, h, imageFlags);
		}

		@Override
		public void nvgluDeleteFramebuffer(long ctx, NVGLUFramebuffer fb) {
			NanoVGGLES3.nvgluDeleteFramebuffer(ctx, fb);
		}

	}

}
