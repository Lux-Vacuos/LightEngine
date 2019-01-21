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

package net.luxvacuos.lightengine.client.ui.windows;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;

import java.io.File;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.rendering.nanovg.WindowMessage;
import net.luxvacuos.lightengine.client.ui.Alignment;
import net.luxvacuos.lightengine.client.ui.ComponentWindow;
import net.luxvacuos.lightengine.client.ui.Image;
import net.luxvacuos.lightengine.client.ui.Spinner;
import net.luxvacuos.lightengine.client.ui.Text;
import net.luxvacuos.lightengine.universal.core.PackageLoader;

public class LoadWindow extends ComponentWindow {
	private Text message;

	public LoadWindow() {
		super("System Loader");
	}

	@Override
	public void initApp() {
		super.setBlurBehind(false);
		super.setBackgroundColor("#FFFFFFFF");

		Image lv = new Image(0, 0, 512, 512,
				GraphicalSubsystem.getMainWindow().getResourceLoader().loadNVGTexture("LuxVacuos-Logo"));
		lv.setAlignment(Alignment.CENTER);
		lv.setWindowAlignment(Alignment.CENTER);
		super.addComponent(lv);

		super.initApp();
		this.notifyWindow(WindowMessage.WM_FADE_IN, null);
	}

	public void addSpinner() {
		Spinner load = new Spinner(-28, 8, 20);
		load.setWindowAlignment(Alignment.RIGHT_BOTTOM);
		super.addComponent(load);
	}

	public boolean onLoadFailed() {
		message = new Text("Searching for package", 0, -220);
		message.setWindowAlignment(Alignment.CENTER);
		message.setAlign(NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
		message.setColor(0, 0, 0, 1f);
		super.addComponent(message);
		return this.searchForPackage();
	}

	private boolean searchForPackage() {
		File pak = PackageLoader.searchPackage();
		if (pak.exists()) {
			message.setText("Package found... Loading");
			if (PackageLoader.loadPackage(pak)) {
				PackageLoader.initPackage();
				return true;
			} else {
				message.setText("Error in package load");
				return false;
			}
		} else {
			message.setText("Package not found");
			return false;
		}
	}

}
