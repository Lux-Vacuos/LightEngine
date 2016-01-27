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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import net.guerra24.infinity.client.bootstrap.Bootstrap;

public class GameSettings {

	private Properties prop;
	private File settings;

	private int version = 5;

	public GameSettings() {
		settings = new File(InfinityVariables.settings);
		prop = new Properties();
		if (settings.exists()) {
			try {
				prop.load(new FileInputStream(settings));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			new File(Bootstrap.getPrefix() + "infinity/assets/game/").mkdirs();
		}
		if (getVersion() == 1) {
			InfinityVariables.useShadows = Boolean.parseBoolean(getValue("useShadows"));
			InfinityVariables.useVolumetricLight = Boolean.parseBoolean(getValue("useVolumetricLight"));
			InfinityVariables.useFXAA = Boolean.parseBoolean(getValue("useFXAA"));
		} else if (getVersion() == 2) {
			InfinityVariables.useShadows = Boolean.parseBoolean(getValue("useShadows"));
			InfinityVariables.useVolumetricLight = Boolean.parseBoolean(getValue("useVolumetricLight"));
			InfinityVariables.useFXAA = Boolean.parseBoolean(getValue("useFXAA"));
			InfinityVariables.VSYNC = Boolean.parseBoolean(getValue("VSYNC"));
			InfinityVariables.FPS = Integer.parseInt(getValue("FPS"));
			InfinityVariables.UPS = Integer.parseInt(getValue("UPS"));
		} else if (getVersion() == 3) {
			InfinityVariables.useShadows = Boolean.parseBoolean(getValue("useShadows"));
			InfinityVariables.useVolumetricLight = Boolean.parseBoolean(getValue("useVolumetricLight"));
			InfinityVariables.useFXAA = Boolean.parseBoolean(getValue("useFXAA"));
			InfinityVariables.useMotionBlur = Boolean.parseBoolean(getValue("useMotionBlur"));
			InfinityVariables.useDOF = Boolean.parseBoolean(getValue("useDOF"));
			InfinityVariables.VSYNC = Boolean.parseBoolean(getValue("VSYNC"));
			InfinityVariables.FPS = Integer.parseInt(getValue("FPS"));
			InfinityVariables.UPS = Integer.parseInt(getValue("UPS"));
		} else if (getVersion() == 4) {
			InfinityVariables.useShadows = Boolean.parseBoolean(getValue("useShadows"));
			InfinityVariables.useVolumetricLight = Boolean.parseBoolean(getValue("useVolumetricLight"));
			InfinityVariables.useFXAA = Boolean.parseBoolean(getValue("useFXAA"));
			InfinityVariables.useMotionBlur = Boolean.parseBoolean(getValue("useMotionBlur"));
			InfinityVariables.useDOF = Boolean.parseBoolean(getValue("useDOF"));
			InfinityVariables.useReflections = Boolean.parseBoolean(getValue("useReflections"));
			InfinityVariables.useParallax = Boolean.parseBoolean(getValue("useParallax"));
			InfinityVariables.VSYNC = Boolean.parseBoolean(getValue("VSYNC"));
			InfinityVariables.FPS = Integer.parseInt(getValue("FPS"));
			InfinityVariables.UPS = Integer.parseInt(getValue("UPS"));
		} else if (getVersion() == 5) {
			InfinityVariables.useShadows = Boolean.parseBoolean(getValue("useShadows"));
			InfinityVariables.useVolumetricLight = Boolean.parseBoolean(getValue("useVolumetricLight"));
			InfinityVariables.useFXAA = Boolean.parseBoolean(getValue("useFXAA"));
			InfinityVariables.useMotionBlur = Boolean.parseBoolean(getValue("useMotionBlur"));
			InfinityVariables.useDOF = Boolean.parseBoolean(getValue("useDOF"));
			InfinityVariables.useReflections = Boolean.parseBoolean(getValue("useReflections"));
			InfinityVariables.useParallax = Boolean.parseBoolean(getValue("useParallax"));
			InfinityVariables.VSYNC = Boolean.parseBoolean(getValue("VSYNC"));
			InfinityVariables.FPS = Integer.parseInt(getValue("FPS"));
			InfinityVariables.UPS = Integer.parseInt(getValue("UPS"));
				InfinityVariables.FOV = Integer.parseInt(getValue("FOV"));
		} else {
			updateSetting();
			save();
		}
	}

	public void registerValue(String key, String data) {
		prop.setProperty(key, data);
	}

	public String getValue(String key) {
		String res = prop.getProperty(key);
		return res;
	}

	private int getVersion() {
		String b = getValue("SettingsVersion");
		if (b == null)
			b = "1";
		int a = Integer.parseInt(b);
		return a;
	}

	public void save() {
		try {
			prop.store(new FileOutputStream(settings), "Infinity Settings");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void updateSetting() {
		registerValue("SettingsVersion", Integer.toString(version));
		registerValue("useShadows", Boolean.toString(InfinityVariables.useShadows));
		registerValue("useVolumetricLight", Boolean.toString(InfinityVariables.useVolumetricLight));
		registerValue("useFXAA", Boolean.toString(InfinityVariables.useFXAA));
		registerValue("useMotionBlur", Boolean.toString(InfinityVariables.useMotionBlur));
		registerValue("useDOF", Boolean.toString(InfinityVariables.useDOF));
		registerValue("useReflections", Boolean.toString(InfinityVariables.useReflections));
		registerValue("useParallax", Boolean.toString(InfinityVariables.useParallax));
		registerValue("VSYNC", Boolean.toString(InfinityVariables.VSYNC));
		registerValue("FPS", Integer.toString(InfinityVariables.FPS));
		registerValue("UPS", Integer.toString(InfinityVariables.UPS));
		registerValue("FOV", Integer.toString(InfinityVariables.FOV));
	}

}
