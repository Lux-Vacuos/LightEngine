package net.luxvacuos.lightengine.client.ecs.entities;

import net.luxvacuos.lightengine.client.ecs.components.Renderable;
import net.luxvacuos.lightengine.universal.ecs.entities.PlayerEntity;

public class RenderPlayerEntity extends PlayerEntity {

	public RenderPlayerEntity(String name) {
		super(name);
		//add(new Renderable("models/player.blend"));
	}
	
	public RenderPlayerEntity(String name, String uuid) {
		super(name, uuid);
		//add(new Renderable("models/player.blend"));
	}

}
