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

package net.luxvacuos.lightengine.client.ui.windows;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import net.luxvacuos.lightengine.client.rendering.api.nanovg.WindowMessage;
import net.luxvacuos.lightengine.client.rendering.api.opengl.Renderer;
import net.luxvacuos.lightengine.client.ui.ComponentWindow;
import net.luxvacuos.lightengine.client.ui.Image;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.util.registry.KeyCache;

public class GameWindow extends ComponentWindow {

	private Image game;

	public GameWindow(int x, int y, int w, int h) {
		super(x, y, w, h, "game");
	}

	@Override
	public void initApp() {
		super.setBackgroundColor(0.0f, 0.0f, 0.0f, 1f);
		super.setResizable(false);
		super.setDecorations(false);
		super.setAsBackground(true);
		super.setBlurBehind(false);
		super.toggleTitleBar();

		game = new Image(0, 0, w, h, Renderer.getNVGImage(), false);
		game.setResizeH(true);
		game.setResizeV(true);

		super.addComponent(game);
		super.initApp();
	}

	@Override
	public void processWindowMessage(int message, Object param) {
		if (message == WindowMessage.WM_RESIZE) {
			y = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/height"));
			w = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/width"));
			h = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/height"));
			TaskManager.addTask(() -> game.setImage(Renderer.getNVGImage()));
		}
		super.processWindowMessage(message, param);
	}

}
