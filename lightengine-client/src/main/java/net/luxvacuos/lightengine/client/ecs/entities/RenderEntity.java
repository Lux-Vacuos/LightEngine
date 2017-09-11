package net.luxvacuos.lightengine.client.ecs.entities;

import net.luxvacuos.lightengine.client.ecs.components.Renderable;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.Model;
import net.luxvacuos.lightengine.universal.ecs.entities.BasicEntity;

public class RenderEntity extends BasicEntity {

	public RenderEntity(String name, Model model) {
		super(name);
		add(new Renderable(model));
	}

	public RenderEntity(String name, String uuid, Model model) {
		super(name, uuid);
		add(new Renderable(model));
	}

	public RenderEntity(String name, String path) {
		super(name);
		add(new Renderable(path));
	}

	public RenderEntity(String name, String uuid, String path) {
		super(name, uuid);
		add(new Renderable(path));
	}

}
