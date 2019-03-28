package net.luxvacuos.lightengine.client.resources.internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.luxvacuos.lightengine.client.rendering.glfw.Cursor;
import net.luxvacuos.lightengine.client.rendering.glfw.Icon;
import net.luxvacuos.lightengine.client.resources.config.GraphicalSubConfig;

public class ConfigGenerator {

	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static void main(String[] args) {
		generateGraphicalSubConfig();
	}

	private static void generateGraphicalSubConfig() {
		GraphicalSubConfig gsg = new GraphicalSubConfig(
				new Icon[] { new Icon("ENGINE_Icon32"), new Icon("ENGINE_Icon64") }, new Cursor("ENGINE_Cursor", 0, 0));
		System.out.println(gson.toJson(gsg));
	}

}
