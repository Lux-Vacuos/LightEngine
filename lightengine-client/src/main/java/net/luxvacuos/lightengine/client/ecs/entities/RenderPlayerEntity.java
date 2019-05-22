package net.luxvacuos.lightengine.client.ecs.entities;

import net.luxvacuos.lightengine.client.ecs.components.ModelLoader;
import net.luxvacuos.lightengine.universal.ecs.entities.PlayerEntity;

public class RenderPlayerEntity extends PlayerEntity {

	public RenderPlayerEntity(String name) {
		super(name);
		add(new ModelLoader("models/player.blend"));
	}
	
	public RenderPlayerEntity(String name, String uuid) {
		super(name, uuid);
		add(new ModelLoader("models/player.blend"));
	}

}
