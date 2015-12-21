package net.guerra24.infinity.client.resources.scenes;

import java.util.ArrayList;
import java.util.List;

import net.guerra24.infinity.client.resources.GameResources;
import net.guerra24.infinity.client.resources.models.ModelTexture;
import net.guerra24.infinity.client.resources.models.Tessellator;
import net.guerra24.infinity.client.resources.models.TexturedModel;
import net.guerra24.infinity.client.resources.models.WaterTile;
import net.guerra24.infinity.client.world.entities.Entity;
import net.guerra24.infinity.client.world.entities.IEntity;
import net.guerra24.infinity.client.world.entities.Mob;
import net.guerra24.infinity.universal.util.vector.Vector3f;

public class Demo {

	private List<Tessellator> models;
	private List<IEntity> mobs;
	private List<WaterTile> waters;

	public Demo(GameResources gm) {
		models = new ArrayList<Tessellator>();
		waters = new ArrayList<WaterTile>();
		mobs = new ArrayList<IEntity>();
		int tex = gm.getLoader().loadTextureEntity("TestGrid");
		TexturedModel model = new TexturedModel(gm.getLoader().getObjLoader().loadObjModel("demo", gm.getLoader()),
				new ModelTexture(tex));
		Mob demo1 = new Mob(new Entity(model, new Vector3f(0, 0, 0), 0, 0, 0, 1.023f));
		mobs.add(demo1);
		for (int x = 0; x < 5; x++) {
			for (int z = 0; z < 5; z++) {
				waters.add(new WaterTile(-12.028f + x, -3.1f - z, 0.43f));
			}
		}
	}

	public List<Tessellator> getModels() {
		return models;
	}

	public List<IEntity> getMobs() {
		return mobs;
	}

	public List<WaterTile> getWaters() {
		return waters;
	}

}
