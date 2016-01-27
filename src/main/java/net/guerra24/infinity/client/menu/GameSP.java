/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 Guerra24
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.guerra24.infinity.client.menu;

import net.guerra24.infinity.client.core.CoreInfo;
import net.guerra24.infinity.client.core.InfinityVariables;
import net.guerra24.infinity.client.graphics.VectorsRendering;
import net.guerra24.infinity.client.graphics.nanovg.Timers;
import net.guerra24.infinity.client.resources.GameResources;

public class GameSP {
	private float xScale, yScale;

	public GameSP(GameResources gm) {
		float width = InfinityVariables.WIDTH;
		float height = InfinityVariables.HEIGHT;
		yScale = height / 720f;
		xScale = width / 1280f;
	}

	public void render(GameResources gm) {
		if (!InfinityVariables.hideHud)
			renderHud(gm);

		if (InfinityVariables.debug) {
			VectorsRendering.renderText(
					"Infinity " + InfinityVariables.version + " (" + InfinityVariables.state + "-Build "
							+ InfinityVariables.build + ")",
					"Roboto-Bold", 5 * xScale, 12 * yScale, 25 * yScale,
					VectorsRendering.rgba(160, 160, 160, 200, VectorsRendering.colorA),
					VectorsRendering.rgba(255, 255, 255, 255, VectorsRendering.colorB));
			VectorsRendering.renderText("Used VRam: " + gm.getDisplay().getUsedVRAM() + "KB " + " UPS: " + CoreInfo.ups,
					"Roboto-Bold", 5 * xScale, 100 * yScale, 25 * yScale,
					VectorsRendering.rgba(160, 160, 160, 200, VectorsRendering.colorA),
					VectorsRendering.rgba(255, 255, 255, 255, VectorsRendering.colorB));
			VectorsRendering.renderText(
					"Position XYZ:  " + gm.getCamera().getPosition().getX() + "  " + gm.getCamera().getPosition().getY()
							+ "  " + gm.getCamera().getPosition().getZ(),
					"Roboto-Bold", 5 * xScale, 142 * yScale, 25 * yScale,
					VectorsRendering.rgba(160, 160, 160, 200, VectorsRendering.colorA),
					VectorsRendering.rgba(255, 255, 255, 255, VectorsRendering.colorB));
			VectorsRendering.renderText(
					"Pitch:  " + gm.getCamera().getPitch() + "   Yaw: " + gm.getCamera().getYaw() + "   Roll: "
							+ gm.getCamera().getRoll(),
					"Roboto-Bold", 5 * xScale, 164 * yScale, 25 * yScale,
					VectorsRendering.rgba(160, 160, 160, 200, VectorsRendering.colorA),
					VectorsRendering.rgba(255, 255, 255, 255, VectorsRendering.colorB));
			Timers.renderDebugDisplay(5 * xScale, 24 * yScale, 300 * xScale, 55 * yScale);
		}
	}

	private void renderHud(GameResources gm) {
	}

}
