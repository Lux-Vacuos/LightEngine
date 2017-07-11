package net.luxvacuos.lightengine.client.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;

import net.luxvacuos.lightengine.client.ecs.ClientComponents;
import net.luxvacuos.lightengine.client.ecs.entities.RenderEntity;
import net.luxvacuos.lightengine.universal.world.PhysicsSystem;

public class ClientPhysicsSystem extends PhysicsSystem {
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
				if(entity instanceof RenderEntity) {
					ClientComponents.RENDERABLE.get(entity).getModel().dispose();
				}
			}
			
			@Override
			public void entityAdded(Entity entity) {
			}
		});
	}

}
