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

package net.guerra24.infinity.client.core;

import net.guerra24.infinity.client.core.states.GameSPState;
import net.guerra24.infinity.client.core.states.InPauseState;
import net.guerra24.infinity.client.core.states.LoadingSPState;
import net.guerra24.infinity.client.core.states.MainMenuState;
import net.guerra24.infinity.client.core.states.OptionsState;
import net.guerra24.infinity.client.core.states.WorldSelectionState;

/**
 * States Handler
 * 
 * @author danirod
 * @category Kernel
 */
public class GlobalStates {

	public boolean loop = false;

	private GameState state;

	private GameState oldState;

	public enum GameState {
		GAME_SP(new GameSPState()), MAINMENU(new MainMenuState()), IN_PAUSE(new InPauseState()), LOADING_WORLD(
				new LoadingSPState()), OPTIONS(new OptionsState()), WORLD_SELECTION(new WorldSelectionState());

		GameState(State state) {
			this.state = state;
		}

		State state;
	}

	public GlobalStates() {
		loop = true;
		state = InfinityVariables.autostart ? GameState.LOADING_WORLD : GameState.MAINMENU;
	}

	public void doUpdate(Infinity infinity, float delta) {
		state.state.update(infinity, this, delta);
		if (infinity.getGameResources().getDisplay().isCloseRequested())
			loop = false;
	}

	public void doRender(Infinity infinity, float alpha) {
		state.state.render(infinity, this, alpha);
	}

	public GameState getState() {
		return state;
	}

	public void setState(GameState state) {
		if (!state.equals(this.state)) {
			this.oldState = this.state;
			this.state = state;
		}
	}

	public GameState getOldState() {
		return oldState;
	}
}
