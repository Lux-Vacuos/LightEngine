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

package net.luxvacuos.lightengine.client.ui;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import java.util.List;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme;
import net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme.BackgroundStyle;
import net.luxvacuos.lightengine.client.util.Maths;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class DropDown<E> extends Button {

	private E value;
	private List<E> objects;
	private OnAction action;
	private boolean setSelected;

	public DropDown(float x, float y, float w, float h, E val, List<E> objects) {
		super(x, y, w, h, val.toString());
		this.objects = objects;
	}

	@Override
	public void init(Window window) {
		super.setOnButtonPress(() -> {
			TaskManager.tm.addTaskRenderThread(() -> GraphicalSubsystem.getWindowManager()
					.addWindow(new ComponentWindow((int) (rootComponent.rootX + alignedX),
							(int) (rootComponent.rootY + alignedY), (int) w, 300, "Dropdown") {

						@Override
						public void initApp() {
							super.toggleTitleBar();
							super.setDecorations(false);
							super.setBackgroundStyle(BackgroundStyle.TRANSPARENT);

							ScrollArea area = new ScrollArea(0, 0, w, h, 0, 0);
							area.setLayout(new FlowLayout(Direction.DOWN, 0, 0));
							area.setResizeH(true);
							area.setResizeV(true);
							int hh = 0;
							for (E e : objects) {
								ContextMenuButton btn = new ContextMenuButton(0, 0,
										w - (int) REGISTRY.getRegistryItem(
												new Key("/Light Engine/Settings/WindowManager/scrollBarSize")),
										30, e.toString());
								btn.setWindowAlignment(Alignment.LEFT_TOP);
								btn.setAlignment(Alignment.RIGHT_BOTTOM);
								btn.setOnButtonPress(() -> {
									value = e;
									text = e.toString();
									super.closeWindow();
									setSelected = false;
									if (action != null)
										action.onAction();
								});
								area.addComponent(btn);
								hh += 30;
							}
							h = Maths.minInt(hh, 200);
							super.addComponent(area);
							super.initApp();
							setSelected = true;
						}

						@Override
						public void alwaysUpdateApp(float delta) {
							if ((window.getMouseHandler().isButtonPressed(0)
									|| window.getMouseHandler().isButtonPressed(1)) && !insideWindow()) {
								super.closeWindow();
								setSelected = false;
							}
							super.alwaysUpdateApp(delta);
						}
					}));

		});
		super.init(window);
	}

	@Override
	public void render(Window window) {
		if (!enabled)
			return;
		if (setSelected)
			componentState = ComponentState.SELECTED;
		Theme.renderDropDownButton(window.getNVGID(), componentState, rootComponent.rootX + alignedX,
				window.getHeight() - rootComponent.rootY - alignedY - h, w, h, fontSize, font, entypo, text, false);
	}

	@Override
	public void setOnButtonPress(OnAction onPress) {
		this.action = onPress;
	}

	public E getValue() {
		return value;
	}

}
