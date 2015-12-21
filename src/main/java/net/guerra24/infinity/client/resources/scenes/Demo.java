package net.guerra24.infinity.client.resources.scenes;

import java.util.ArrayList;
import java.util.List;

import net.guerra24.infinity.client.resources.GameResources;
import net.guerra24.infinity.client.resources.models.Tessellator;
import net.guerra24.infinity.universal.util.vector.Vector2f;
import net.guerra24.infinity.universal.util.vector.Vector3f;
import net.guerra24.infinity.universal.util.vector.Vector4f;

public class Demo {

	private List<Tessellator> models;

	public Demo(GameResources gm) {
		models = new ArrayList<Tessellator>();
		int tex = gm.getLoader().loadTextureEntity("TestGrid");
		Tessellator tess = new Tessellator(gm);
		tess.begin(tex);
		tess.vertex3f(new Vector3f(0, 0, 0));
		tess.texture2f(new Vector2f(0, 0));
		tess.normal3f(new Vector3f(0, 1, 0));
		tess.data4f(new Vector4f(0, 0, 0, 0));

		tess.vertex3f(new Vector3f(0, 0, 100));
		tess.texture2f(new Vector2f(0, 1));
		tess.normal3f(new Vector3f(0, 1, 0));
		tess.data4f(new Vector4f(0, 0, 0, 0));

		tess.vertex3f(new Vector3f(100, 0, 100));
		tess.texture2f(new Vector2f(1, 1));
		tess.normal3f(new Vector3f(0, 1, 0));
		tess.data4f(new Vector4f(0, 0, 0, 0));

		tess.vertex3f(new Vector3f(100, 0, 0));
		tess.texture2f(new Vector2f(1, 0));
		tess.normal3f(new Vector3f(0, 1, 0));
		tess.data4f(new Vector4f(0, 0, 0, 0));
		// Wall
		tess.vertex3f(new Vector3f(0, 10, 100));
		tess.texture2f(new Vector2f(1, 1));
		tess.normal3f(new Vector3f(1, 0, 0));
		tess.data4f(new Vector4f(0, 0, 0, 0));

		tess.vertex3f(new Vector3f(0, 0, 100));
		tess.texture2f(new Vector2f(0, 1));
		tess.normal3f(new Vector3f(1, 0, 0));
		tess.data4f(new Vector4f(0, 0, 0, 0));

		tess.vertex3f(new Vector3f(0, 0, 0));
		tess.texture2f(new Vector2f(0, 0));
		tess.normal3f(new Vector3f(1, 0, 0));
		tess.data4f(new Vector4f(0, 0, 0, 0));

		tess.vertex3f(new Vector3f(0, 10, 0));
		tess.texture2f(new Vector2f(1, 0));
		tess.normal3f(new Vector3f(1, 0, 0));
		tess.data4f(new Vector4f(0, 0, 0, 0));

		tess.end();
		models.add(tess);
	}

	public List<Tessellator> getModels() {
		return models;
	}

}
