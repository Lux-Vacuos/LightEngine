package net.guerra24.infinity.client.resources.scenes;

import java.util.ArrayList;
import java.util.List;

import net.guerra24.infinity.client.resources.GameResources;
import net.guerra24.infinity.client.resources.models.Tessellator;
import net.guerra24.infinity.client.resources.models.TessellatorTextureAtlas;
import net.guerra24.infinity.client.resources.models.WaterTile;
import net.guerra24.infinity.universal.util.vector.Vector2f;

public class Demo {

	private List<Tessellator> models;
	private List<WaterTile> waters;

	public Demo(GameResources gm) {
		models = new ArrayList<Tessellator>();
		waters = new ArrayList<WaterTile>();
		int tex = gm.getLoader().loadTextureEntity("TestGrid");
		int normal = gm.getLoader().loadTextureEntity("TestGrid_normal");
		TessellatorTextureAtlas tessAtlas = new TessellatorTextureAtlas(256, 256, 256, tex);
		tessAtlas.registerTextureCoords("None", new Vector2f(256, 256));
		// TexturedModel model = new
		// TexturedModel(gm.getLoader().getObjLoader().loadObjModel("demo"),
		// new ModelTexture(tex));
		// GameEntity demo1 = new GameEntity(model, new Vector3f(0, 0, 0), 0, 0,
		// 0, 1);
		for (int x = 0; x < 5; x++) {
			for (int z = 0; z < 5; z++) {
				waters.add(new WaterTile(x + 2, z + 2, 1.1f));
			}
		}
		// gm.getPhysicsEngine().addEntity(demo1);
		Tessellator tess = new Tessellator(gm);

		tess.begin(tex, normal, tex);
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				tess.generateCube(x, 0, z, 1, true, true, false, false, false, false, tessAtlas, "None");
			}
		}
		for (int z = 0; z < 16; z++) {
			for (int y = 0; y < 16; y++) {
				tess.generateCube(-1, y + 1, z, 1, false, false, true, false, false, false, tessAtlas, "None");
			}
		}

		tess.end();
		models.add(tess);
	}

	public List<Tessellator> getModels() {
		return models;
	}

	public List<WaterTile> getWaters() {
		return waters;
	}

}
