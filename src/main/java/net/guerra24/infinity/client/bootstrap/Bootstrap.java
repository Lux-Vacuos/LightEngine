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

package net.guerra24.infinity.client.bootstrap;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Calendar;

import net.guerra24.infinity.client.core.Infinity;
import net.guerra24.infinity.client.core.InfinityVariables;
import net.guerra24.infinity.client.util.Logger;

/**
 * Initialize the basic game code
 * 
 * @author Guerra24 <pablo230699@hotmail.com>
 */
public class Bootstrap {
	/**
	 * OS info
	 */
	private static Platform platform;

	/**
	 * 
	 * Gets the OS
	 * 
	 * @return The OS and the architecture
	 * 
	 */
	public static Platform getPlatform() {
		if (platform == null) {
			final String OS = System.getProperty("os.name").toLowerCase();
			final String ARCH = System.getProperty("os.arch").toLowerCase();

			boolean isWindows = OS.contains("windows");
			boolean isLinux = OS.contains("linux");
			boolean isMac = OS.contains("mac");
			boolean is64Bit = ARCH.equals("amd64") || ARCH.equals("x86_64");

			platform = Platform.UNKNOWN;

			if (isWindows)
				platform = is64Bit ? Platform.WINDOWS_64 : Platform.WINDOWS_32;
			if (isLinux)
				platform = is64Bit ? Platform.LINUX_64 : Platform.LINUX_32;
			if (isMac)
				platform = Platform.MACOSX;
		}

		return platform;
	}

	/**
	 * Enumerator of the OS
	 * 
	 *
	 */
	public enum Platform {
		WINDOWS_32, WINDOWS_64, MACOSX, LINUX_32, LINUX_64, UNKNOWN;

	}

	/**
	 * Launcher main function
	 * 
	 * @param args
	 *            Not Used
	 */
	public static void main(String[] args) {
		Thread.currentThread().setName("Voxel Main");
		if (!InfinityVariables.debug) {
			PrintStream out;
			try {
				out = new PrintStream(new FileOutputStream("assets/log.txt"));
				System.setOut(out);
				System.setErr(out);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		try {
			parseArgs(args);
		} catch (ArrayIndexOutOfBoundsException aioe) {
			Logger.error("Error: Arguments were wrong", aioe);
			System.exit(1);
		} catch (Exception ex) {
			Logger.error(ex);
			System.exit(1);
		}
		checkSomeValues();
		new Infinity();
	}

	private static void checkSomeValues() {
		Calendar christmas = Calendar.getInstance();
		if (christmas.get(Calendar.MONTH) == Calendar.DECEMBER) {
			InfinityVariables.christmas = true;
			InfinityVariables.RED = 0.882f;
			InfinityVariables.GREEN = 1;
			InfinityVariables.BLUE = 1;
		}
	}

	/**
	 * Handles all Voxel available args
	 * 
	 * @param args
	 *            Array of args
	 */
	private static void parseArgs(String[] args) {
		boolean gaveWidth = false, gaveHeight = false, gaveFov = false;
		boolean gaveFps = false, gaveShadows = false;
		boolean gaveAutostart = false, gaveFXAA = false, gaveDOF = false;
		boolean gaveMotionBlur = false, gaveBloom = false;

		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "-width":
				if (gaveWidth)
					throw new IllegalStateException("Width already given");
				InfinityVariables.WIDTH = Integer.parseInt(args[++i]);
				if (InfinityVariables.WIDTH <= 0)
					throw new IllegalArgumentException("Width must be positive");
				gaveWidth = true;
				break;
			case "-height":
				if (gaveHeight)
					throw new IllegalStateException("Height already given");
				InfinityVariables.HEIGHT = Integer.parseInt(args[++i]);
				if (InfinityVariables.HEIGHT <= 0)
					throw new IllegalArgumentException("Height must be positive");
				gaveHeight = true;
				break;
			case "-fov":
				if (gaveFov)
					throw new IllegalStateException("FOV already given");
				InfinityVariables.FOV = Integer.parseInt(args[++i]);
				if (InfinityVariables.FOV <= 20 || InfinityVariables.FOV >= 120)
					throw new IllegalArgumentException("FOV must be in (20, 120) range");
				gaveFov = true;
				break;
			case "-fps":
				if (gaveFps)
					throw new IllegalStateException("FPS already given");
				InfinityVariables.FPS = Integer.parseInt(args[++i]);
				if (InfinityVariables.FPS <= 0) {
					throw new IllegalArgumentException("FPS must be positive");
				}
				gaveFps = true;
				break;
			case "-useShadows":
				if (gaveShadows)
					throw new IllegalStateException("Shadows already given");
				InfinityVariables.useShadows = true;
				gaveShadows = true;
				break;
			case "-useFXAA":
				if (gaveFXAA)
					throw new IllegalStateException("FXAA already given");
				InfinityVariables.useFXAA = true;
				gaveFXAA = true;
				break;
			case "-useDOF":
				if (gaveDOF)
					throw new IllegalStateException("DOF already given");
				if (InfinityVariables.useMotionBlur)
					throw new IllegalArgumentException("DOF only be activated if Motion Blur is disabled");
				InfinityVariables.useDOF = true;
				gaveDOF = true;
				break;
			case "-useMotionBlur":
				if (gaveMotionBlur)
					throw new IllegalStateException("Motion Blur already given");
				if (InfinityVariables.useDOF)
					throw new IllegalArgumentException("Motion Blur only be activated if DOF is disabled");
				InfinityVariables.useMotionBlur = true;
				gaveMotionBlur = true;
				break;
			case "-useBloom":
				if (gaveBloom)
					throw new IllegalStateException("Bloom already given");
				InfinityVariables.useBloom = true;
				gaveBloom = true;
				break;
			case "-autostart":
				if (gaveAutostart)
					throw new IllegalStateException("Autostart already given");
				InfinityVariables.autostart = true;
				gaveAutostart = true;
				break;
			default:
				if (args[i].startsWith("-")) {
					throw new IllegalArgumentException("Unknown argument: " + args[i].substring(1));
				} else {
					throw new IllegalArgumentException("Unknown token: " + args[i]);
				}
			}
		}
	}

}