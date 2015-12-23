package net.guerra24.infinity.client.resources.scenes;

import java.util.ArrayList;
import java.util.List;

import net.guerra24.infinity.client.resources.GameResources;
import net.guerra24.infinity.client.resources.models.ModelTexture;
import net.guerra24.infinity.client.resources.models.Tessellator;
import net.guerra24.infinity.client.resources.models.TexturedModel;
import net.guerra24.infinity.client.resources.models.WaterTile;
import net.guerra24.infinity.client.world.SimplexNoise;
import net.guerra24.infinity.client.world.entities.Entity;
import net.guerra24.infinity.client.world.entities.IEntity;
import net.guerra24.infinity.client.world.entities.Mob;
import net.guerra24.infinity.universal.util.vector.Vector2f;
import net.guerra24.infinity.universal.util.vector.Vector3f;
import net.guerra24.infinity.universal.util.vector.Vector4f;

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
/*
		Tessellator tess = new Tessellator(gm);
		SimplexNoise noise = new SimplexNoise(512, 0.5, gm.getRand().nextInt());

		tess.begin(tex);
		for (int x = 0; x < 512; x++) {
			for (int z = 0; z < 512; z++) {
				genFace(tess, noise, x, z, new Vector2f(0, 0), new Vector4f(0, 0, 0, 0));
				genFace(tess, noise, x, z + 1, new Vector2f(0, 1), new Vector4f(0, 0, 0, 0));
				genFace(tess, noise, x + 1, z + 1, new Vector2f(1, 1), new Vector4f(0, 0, 0, 0));
				genFace(tess, noise, x + 1, z, new Vector2f(1, 0), new Vector4f(0, 0, 0, 0));
			}
		}

		tess.end();
		models.add(tess);
	*/}

	private void genFace(Tessellator tess, SimplexNoise noise, float x, float z, Vector2f texcoords, Vector4f data) {
		float Zup = ((float) noise.getNoise((int) x, (int) z + 1) * 2.0f - 1.0f);
		float Zdown = ((float) noise.getNoise((int) x, (int) z - 1) * 2.0f - 1.0f);
		float Zleft = ((float) noise.getNoise((int) x - 1, (int) z) * 2.0f - 1.0f);
		float Zright = ((float) noise.getNoise((int) x + 1, (int) z) * 2.0f - 1.0f);
		float height = ((float) noise.getNoise((int) x, (int) z) * 2.0f - 1.0f);
		Vector3f normal = new Vector3f((Zleft - Zright) / x, 1, -(Zdown - Zup) / height);
		tess.vertex3f(new Vector3f(x, ((float) noise.getNoise((int) x, (int) z) * 2.0f - 1.0f) * 20, z));
		tess.texture2f(texcoords);
		tess.normal3f(normal);
		tess.data4f(data);
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
