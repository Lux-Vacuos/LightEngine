package net.luxvacuos.infinity.client.world.entities;

import net.luxvacuos.infinity.client.rendering.api.opengl.objects.TexturedModel;
import net.luxvacuos.infinity.client.world.entities.components.RendereableComponent;
import net.luxvacuos.infinity.universal.ecs.components.Position;
import net.luxvacuos.infinity.universal.ecs.components.Rotation;
import net.luxvacuos.infinity.universal.ecs.components.Scale;
import net.luxvacuos.infinity.universal.world.entities.AbstractEntity;

public class BasicEntity extends AbstractEntity {

	public BasicEntity(TexturedModel model) {
		add(new Position());
		add(new Rotation());
		add(new RendereableComponent());
		add(new Scale());
		getComponent(RendereableComponent.class).model = model;
	}

}
