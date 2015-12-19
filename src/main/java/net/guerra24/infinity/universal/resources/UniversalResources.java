package net.guerra24.infinity.universal.resources;

import net.guerra24.infinity.client.resources.GameResources;
import net.guerra24.infinity.client.resources.models.ModelTexture;
import net.guerra24.infinity.client.resources.models.RawModel;
import net.guerra24.infinity.client.resources.models.TexturedModel;

public class UniversalResources {

	public static TexturedModel player;

	public static void loadUniversalResources(GameResources gm) {
		ModelTexture texture = new ModelTexture(gm.getLoader().loadTextureEntity("player"));
		RawModel model = gm.getLoader().getObjLoader().loadObjModel("player", gm.getLoader());
		player = new TexturedModel(model, texture);
	}

}
