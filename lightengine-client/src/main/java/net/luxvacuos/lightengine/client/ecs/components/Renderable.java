package net.luxvacuos.lightengine.client.ecs.components;

import com.hackhalo2.nbt.exceptions.NBTException;
import com.hackhalo2.nbt.tags.TagCompound;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.Model;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.ecs.components.LEComponent;

public class Renderable implements LEComponent {

	private Model model;
	private boolean loaded;

	public Renderable(Model model) {
		this.model = model;
		this.loaded = true;
	}
	
	public Renderable(String path) {
		TaskManager.addTask(() -> {
			this.model = GraphicalSubsystem.getMainWindow().getAssimpResourceLoader().loadModel(path);
			this.loaded = true;
		});
	}

	public Model getModel() {
		return this.model;
	}
	
	public boolean isLoaded() {
		return this.loaded;
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
