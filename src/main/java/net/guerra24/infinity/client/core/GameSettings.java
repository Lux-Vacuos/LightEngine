package net.guerra24.infinity.client.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class GameSettings {

	private Properties prop;
	private File settings;

	private int version = 3;

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
			new File("assets/game/").mkdirs();
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
		if (res == null)
			return "1";
		else
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
			prop.store(new FileOutputStream(settings), "Voxel Settings");
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
		registerValue("VSYNC", Boolean.toString(InfinityVariables.VSYNC));
		registerValue("FPS", Integer.toString(InfinityVariables.FPS));
		registerValue("UPS", Integer.toString(InfinityVariables.UPS));
	}

}
