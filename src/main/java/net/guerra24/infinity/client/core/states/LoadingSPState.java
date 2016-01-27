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

package net.guerra24.infinity.client.core.states;

import net.guerra24.infinity.client.core.GlobalStates;
import net.guerra24.infinity.client.core.GlobalStates.GameState;
import net.guerra24.infinity.client.core.Infinity;
import net.guerra24.infinity.client.core.InfinityVariables;
import net.guerra24.infinity.client.core.State;
import net.guerra24.infinity.client.graphics.VectorsRendering;
import net.guerra24.infinity.client.resources.GameResources;
import net.guerra24.infinity.client.world.entities.PlayerCamera;

/**
 * Loading Screen State
 * 
 * @author danirod
 * @category Kernel
 */
public class LoadingSPState implements State {

	private float xScale, yScale;

	public LoadingSPState() {
		float width = InfinityVariables.WIDTH;
		float height = InfinityVariables.HEIGHT;
		yScale = height / 720f;
		xScale = width / 1280f;
	}

	@Override
	public void update(Infinity infinity, GlobalStates states, float delta) {
		GameResources gm = infinity.getGameResources();

		((PlayerCamera) gm.getCamera()).setMouse(gm.getDisplay());
		gm.getSoundSystem().rewind("menu1");
		gm.getSoundSystem().stop("menu1");
		gm.getSoundSystem().rewind("menu2");
		gm.getSoundSystem().stop("menu2");
		states.setState(GameState.GAME_SP);

	}

	@Override
	public void render(Infinity infinity, GlobalStates states, float delta) {
		GameResources gm = infinity.getGameResources();
		gm.getRenderer().prepare();
		gm.getDisplay().beingNVGFrame();
		VectorsRendering.renderText("Loading World...", "Roboto-Bold", 530 * xScale, 358 * yScale, 40 * yScale,
				VectorsRendering.rgba(255, 255, 255, 160, VectorsRendering.colorA),
				VectorsRendering.rgba(255, 255, 255, 160, VectorsRendering.colorB));
		gm.getDisplay().endNVGFrame();
	}

}
