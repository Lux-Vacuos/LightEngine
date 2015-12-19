package net.guerra24.infinity.client.core;

/**
 * State
 * 
 * @author danirod
 * @category Kernel
 */
public abstract class State {

	private int id;

	public State(int id) {
		this.id = id;
	}

	public abstract void update(Infinity voxel, GlobalStates states, float delta);

	public abstract void render(Infinity voxel, GlobalStates states, float delta);

	public int getId() {
		return id;
	}

}
