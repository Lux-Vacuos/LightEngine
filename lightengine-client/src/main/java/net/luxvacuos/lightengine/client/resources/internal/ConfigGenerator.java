package net.luxvacuos.lightengine.client.resources.internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.luxvacuos.lightengine.client.rendering.glfw.Cursor;
import net.luxvacuos.lightengine.client.rendering.glfw.Icon;
import net.luxvacuos.lightengine.client.resources.config.GraphicalSubConfig;
import net.luxvacuos.lightengine.client.resources.config.SoundSubConfig;
import net.luxvacuos.lightengine.client.util.LoggerSoundSystem;
import net.luxvacuos.lightengine.universal.resources.gson.ClassTypeAdapter;
import paulscode.sound.codecs.CodecJOgg;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class ConfigGenerator {

	private static Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassTypeAdapter())
			.setPrettyPrinting().create();

	public static void main(String[] args) {
		generateSoundSubConfig();
	}

	private static void generateGraphicalSubConfig() {
		GraphicalSubConfig gsg = new GraphicalSubConfig(
				new Icon[] { new Icon("ENGINE_Icon32"), new Icon("ENGINE_Icon64") }, new Cursor("ENGINE_Cursor", 0, 0));
		System.out.println(gson.toJson(gsg));
	}

	private static void generateSoundSubConfig() {
		SoundSubConfig ssc = new SoundSubConfig();
		ssc.setSoundFilesPackage("assets/");
		ssc.setCustomLogger(LoggerSoundSystem.class);
		ssc.getLibraries().add(LibraryLWJGLOpenAL.class);
		ssc.getCodecs().add(ssc.new Codec("ogg", CodecJOgg.class));
		System.out.println(gson.toJson(ssc));
	}

}
