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

public class PauseMenu {
	private Button exitButton;
	private float yScale, xScale;

	public PauseMenu(GameResources gm) {
		float width = InfinityVariables.WIDTH;
		float height = InfinityVariables.HEIGHT;
		yScale = height / 720f;
		xScale = width / 1280f;
		exitButton = new Button(new Vector2f(530 * xScale, 35 * yScale), new Vector2f(215 * xScale, 80 * yScale));
	}

	public void render() {
		MenuRendering.renderButton(null, "Back to Main Menu", "Roboto-Bold", 528 * xScale, 607 * yScale, 280 * xScale,
				80 * yScale, MenuRendering.rgba(255, 255, 255, 255, MenuRendering.colorA), exitButton.insideButton());
	}

	public Button getExitButton() {
		return exitButton;
	}

}
