package net.luxvacuos.lightengine.client.ecs.entities;

import net.luxvacuos.lightengine.client.ecs.components.Renderable;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.Model;
import net.luxvacuos.lightengine.universal.ecs.entities.BasicEntity;

public class RenderEntity extends BasicEntity {

	public RenderEntity(String name, Model model) {
		super(name);
		add(new Renderable(model));
	}

}
