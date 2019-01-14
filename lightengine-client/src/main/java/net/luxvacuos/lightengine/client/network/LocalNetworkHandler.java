/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2018 Lux Vacuos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.luxvacuos.lightengine.client.network;

import java.util.Map;
import java.util.UUID;

import com.badlogic.ashley.core.Engine;

import net.luxvacuos.lightengine.client.core.ClientWorldSimulation;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.ecs.entities.Sun;
import net.luxvacuos.lightengine.client.world.ClientPhysicsSystem;
import net.luxvacuos.lightengine.universal.core.IWorldSimulation;
import net.luxvacuos.lightengine.universal.ecs.entities.BasicEntity;
import net.luxvacuos.lightengine.universal.ecs.entities.PlayerEntity;
import net.luxvacuos.lightengine.universal.network.AbstractChannelHandler;

public class LocalNetworkHandler extends AbstractChannelHandler implements IRenderData {

	protected BasicEntity player;
	protected CameraEntity camera;
	protected Sun sun;

	public LocalNetworkHandler(BasicEntity player) {
		worldSimulation = new ClientWorldSimulation(8500);
		engine = new Engine();
		engine.addSystem(new ClientPhysicsSystem());
		this.player = player;
		if (player instanceof CameraEntity)
			this.camera = (CameraEntity) player;
		sun = new Sun();
		engine.addEntity(this.player);
		this.player.addEntity(sun.getCamera());
	}

	@Override
	public void update(float delta) {
		engine.update(delta);
		worldSimulation.update(delta);
		sun.update(worldSimulation.getRotation(), delta);
	}

	@Override
	public void dispose() {
		engine.removeAllEntities();
	}

	@Override
	public Map<UUID, PlayerEntity> getPlayers() {
		return players;
	}

	@Override
	public Engine getEngine() {
		return engine;
	}

	@Override
	public IWorldSimulation getWorldSimulation() {
		return worldSimulation;
	}

	public BasicEntity getPlayer() {
		return player;
	}

	public CameraEntity getCamera() {
		return camera;
	}

	public void setCamera(CameraEntity camera) {
		this.camera = camera;
	}

	public Sun getSun() {
		return sun;
	}

}
