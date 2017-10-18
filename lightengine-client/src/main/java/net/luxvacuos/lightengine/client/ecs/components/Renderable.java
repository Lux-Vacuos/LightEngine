package net.luxvacuos.lightengine.client.ecs.components;

import com.badlogic.gdx.utils.async.AsyncResult;
import com.hackhalo2.nbt.exceptions.NBTException;
import com.hackhalo2.nbt.tags.TagCompound;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.rendering.api.opengl.EntityRenderer;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.Model;
import net.luxvacuos.lightengine.universal.ecs.components.LEComponent;

public class Renderable implements LEComponent {

	private Model model;
	private AsyncResult<Model> asyncModel;
	private boolean loaded;
	private int rendererID = EntityRenderer.ENTITY_RENDERER_ID;

	public Renderable(Model model) {
		this.model = model;
		this.loaded = true;
	}

	public Renderable(String path) {
		this.asyncModel = GraphicalSubsystem.getMainWindow().getAssimpResourceLoader().loadAsyncModel(path);
	}

	public Model getModel() {
		return this.model;
	}

	public boolean isLoaded() {
		if(this.loaded)
			return true;
		if (this.asyncModel.isDone()) {
			this.model = this.asyncModel.get();
			this.loaded = true;
		}
		return this.loaded;
	}

	public int getRendererID() {
		return rendererID;
	}

	@Override
	public void load(TagCompound compound) throws NBTException {
		// TODO Auto-generated method stub

	}

	@Override
	public TagCompound save() {
		// TODO Auto-generated method stub
		return null;
	}

}
