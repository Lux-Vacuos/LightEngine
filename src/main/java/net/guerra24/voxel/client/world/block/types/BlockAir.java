package net.guerra24.voxel.client.world.block.types;

import net.guerra24.voxel.client.resources.models.WaterTile;
import net.guerra24.voxel.client.world.block.BlockEntity;
import net.guerra24.voxel.client.world.block.IBlock;
import net.guerra24.voxel.universal.util.vector.Vector3f;
import net.guerra24.voxel.universal.util.vector.Vector8f;

public class BlockAir extends IBlock {

	@Override
	public byte getId() {
		return 0;
	}

	@Override
	public WaterTile getWaterTitle(Vector3f pos) {
		return null;
	}

	@Override
	public BlockEntity getSingleModel(Vector3f pos) {
		return null;
	}

	@Override
	public boolean usesSingleModel() {
		return false;
	}

	@Override
	public Vector8f texCoordsUp() {
		return null;
	}

	@Override
	public Vector8f texCoordsDown() {
		return null;
	}

	@Override
	public Vector8f texCoordsFront() {
		return null;
	}

	@Override
	public Vector8f texCoordsBack() {
		return null;
	}

	@Override
	public Vector8f texCoordsRight() {
		return null;
	}

	@Override
	public Vector8f texCoordsLeft() {
		return null;
	}

}
