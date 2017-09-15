package net.luxvacuos.lightengine.client.ecs.entities;

import com.bulletphysics.linearmath.Transform;

import net.luxvacuos.lightengine.client.ecs.ClientComponents;
import net.luxvacuos.lightengine.client.ecs.components.Renderable;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.Model;
import net.luxvacuos.lightengine.universal.ecs.Components;
import net.luxvacuos.lightengine.universal.ecs.components.Collision;
import net.luxvacuos.lightengine.universal.ecs.entities.BasicEntity;
import net.luxvacuos.lightengine.universal.util.VectoVec;
import net.luxvacuos.lightengine.universal.world.DynamicObject;

public class RenderEntity extends BasicEntity {
	private boolean loadedColl;
	public boolean addedToSim;

	public RenderEntity(String name, Model model) {
		super(name);
		add(new Renderable(model));
		Transform transform = new Transform();
		transform.setIdentity();
		transform.origin.set(VectoVec.toVec3(Components.POSITION.get(this).getPosition()));
		add(new Collision(new DynamicObject(model.getShape(), transform, 0)));
	}

	public RenderEntity(String name, String uuid, Model model) {
		super(name, uuid);
		add(new Renderable(model));
		Transform transform = new Transform();
		transform.setIdentity();
		transform.origin.set(VectoVec.toVec3(Components.POSITION.get(this).getPosition()));
		add(new Collision(new DynamicObject(model.getShape(), transform, 0)));
	}

	public RenderEntity(String name, String path) {
		super(name);
		add(new Renderable(path));
	}

	public RenderEntity(String name, String uuid, String path) {
		super(name, uuid);
		add(new Renderable(path));
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		if (!loadedColl) {
			Renderable re = ClientComponents.RENDERABLE.get(this);
			if (re.isLoaded()) {
				loadedColl = true;
				Transform transform = new Transform();
				transform.setIdentity();
				transform.origin.set(VectoVec.toVec3(Components.POSITION.get(this).getPosition()));
				add(new Collision(new DynamicObject(re.getModel().getShape(), transform, 0)));
			}
		}

	}

	public boolean isLoadedColl() {
		return loadedColl;
	}

}
