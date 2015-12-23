/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Guerra24
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

import net.guerra24.infinity.client.core.InfinityVariables;
import net.guerra24.infinity.client.graphics.MenuRendering;
import net.guerra24.infinity.client.resources.GameResources;
import net.guerra24.infinity.universal.util.vector.Vector2f;

public class OptionsMenu {

	private Button exitButton;
	private Button dofButton;
	private Button shadowsButton;
	private Button godraysButton;

	private float xScale, yScale;

	public OptionsMenu(GameResources gm) {
		float width = InfinityVariables.WIDTH;
		float height = InfinityVariables.HEIGHT;
		yScale = height / 720f;
		xScale = width / 1280f;
		exitButton = new Button(new Vector2f(530 * xScale, 35 * yScale), new Vector2f(215 * xScale, 80 * yScale));
		godraysButton = new Button(new Vector2f(74 * xScale, 582 * yScale), new Vector2f(215 * xScale, 80 * yScale));
		shadowsButton = new Button(new Vector2f(74 * xScale, 480 * yScale), new Vector2f(215 * xScale, 80 * yScale));
		dofButton = new Button(new Vector2f(74 * xScale, 378 * yScale), new Vector2f(215 * xScale, 80 * yScale));
	}

	public void render() {
		if (InfinityVariables.useVolumetricLight) {
			MenuRendering.renderButton(null, "Light Rays: ON", "Roboto-Bold", 75 * xScale, 60 * yScale, 215 * xScale,
					80 * yScale, MenuRendering.rgba(255, 255, 255, 255, MenuRendering.colorA),
					godraysButton.insideButton());
		} else {
			MenuRendering.renderButton(null, "Light Rays: OFF", "Roboto-Bold", 75 * xScale, 60 * yScale, 215 * xScale,
					80 * yScale, MenuRendering.rgba(255, 255, 255, 255, MenuRendering.colorA),
					godraysButton.insideButton());
		}
		if (InfinityVariables.useShadows) {
			MenuRendering.renderButton(null, "Shadows: ON", "Roboto-Bold", 75 * xScale, 160 * yScale, 215 * xScale,
					80 * yScale, MenuRendering.rgba(255, 255, 255, 255, MenuRendering.colorA),
					shadowsButton.insideButton());
		} else {
			MenuRendering.renderButton(null, "Shadows: OFF", "Roboto-Bold", 75 * xScale, 160 * yScale, 215 * xScale,
					80 * yScale, MenuRendering.rgba(255, 255, 255, 255, MenuRendering.colorA),
					shadowsButton.insideButton());
		}
		if (InfinityVariables.useDOF) {
			MenuRendering.renderButton(null, "DoF: ON", "Roboto-Bold", 75 * xScale, 260 * yScale, 215 * xScale,
					80 * yScale, MenuRendering.rgba(255, 255, 255, 255, MenuRendering.colorA),
					dofButton.insideButton());
		} else {
			MenuRendering.renderButton(null, "DoF: OFF", "Roboto-Bold", 75 * xScale, 260 * yScale, 215 * xScale,
					80 * yScale, MenuRendering.rgba(255, 255, 255, 255, MenuRendering.colorA),
					dofButton.insideButton());
		}
		MenuRendering.renderButton(null, "Back", "Roboto-Bold", 528 * xScale, 607 * yScale, 215 * xScale,
				80 * yScale, MenuRendering.rgba(255, 255, 255, 255, MenuRendering.colorA),
				exitButton.insideButton());
	}

	public Button getExitButton() {
		return exitButton;
	}

	public Button getDofButton() {
		return dofButton;
	}

	public Button getShadowsButton() {
		return shadowsButton;
	}

	public Button getGodraysButton() {
		return godraysButton;
	}

}
