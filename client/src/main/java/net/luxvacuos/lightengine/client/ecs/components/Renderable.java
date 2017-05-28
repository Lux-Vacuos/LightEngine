package net.luxvacuos.lightengine.client.ecs.components;

import com.hackhalo2.nbt.exceptions.NBTException;
import com.hackhalo2.nbt.tags.TagCompound;

import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.Model;
import net.luxvacuos.lightengine.universal.ecs.components.VoxelComponent;

public class Renderable implements VoxelComponent {

	private Model model;

	public Renderable(Model model) {
		this.model = model;
	}

	public Model getModel() {
		return this.model;
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
