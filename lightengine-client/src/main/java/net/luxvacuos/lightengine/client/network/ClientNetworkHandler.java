/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2017 Lux Vacuos
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
import java.util.Random;
import java.util.UUID;

import org.joml.Vector3f;

import com.badlogic.ashley.core.Engine;

import io.netty.channel.ChannelHandlerContext;
import net.luxvacuos.lightengine.client.core.ClientWorldSimulation;
import net.luxvacuos.lightengine.client.core.subsystems.NetworkSubsystem;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.ecs.entities.PlayerCamera;
import net.luxvacuos.lightengine.client.ecs.entities.RenderPlayerEntity;
import net.luxvacuos.lightengine.client.ecs.entities.Sun;
import net.luxvacuos.lightengine.client.world.ClientPhysicsSystem;
import net.luxvacuos.lightengine.universal.core.IWorldSimulation;
import net.luxvacuos.lightengine.universal.ecs.Components;
import net.luxvacuos.lightengine.universal.ecs.entities.BasicEntity;
import net.luxvacuos.lightengine.universal.ecs.entities.PlayerEntity;
import net.luxvacuos.lightengine.universal.network.AbstractChannelHandler;
import net.luxvacuos.lightengine.universal.network.packets.ClientConnect;
import net.luxvacuos.lightengine.universal.network.packets.ClientDisconnect;
import net.luxvacuos.lightengine.universal.network.packets.Time;
import net.luxvacuos.lightengine.universal.network.packets.UpdateBasicEntity;

public class ClientNetworkHandler extends AbstractChannelHandler {

	private BasicEntity player;
	private CameraEntity camera;
	private Sun sun;

	public ClientNetworkHandler(BasicEntity player) {
		worldSimulation = new ClientWorldSimulation(10000);
		engine = new Engine();
		engine.addSystem(new ClientPhysicsSystem());
		if (player == null)
			this.player = new PlayerCamera("player" + new Random().nextInt(1000), UUID.randomUUID().toString());
		else
			this.player = player;
		sun = new Sun();
		engine.addEntity(this.player); 
		this.player.addEntity(sun.getCamera());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
		if (obj instanceof Time) {
			handleTime((Time) obj);
		} else if (obj instanceof ClientConnect) {
			handleConnect((ClientConnect) obj);
		} else if (obj instanceof ClientDisconnect) {
			handleDisconnect((ClientDisconnect) obj);
		} else if (obj instanceof UpdateBasicEntity) {
			handleUpdateBasicEntity((UpdateBasicEntity) obj);
		}
		super.channelRead(ctx, obj);
	}

	@Override
	public void update(float delta) {
		engine.update(delta);
		worldSimulation.update(delta);
		sun.update(worldSimulation.getRotation(), delta);
		NetworkSubsystem.sendPacket(new UpdateBasicEntity(Components.UUID.get(player).getUUID(), player.getPosition(),
				player.getRotation(), new Vector3f(), player.getScale()));
	}

	@Override
	public void dispose() {
		engine.removeAllEntities();
	}

	private void handleTime(Time time) {
		worldSimulation.setTime(time.getTime());
	}

	private void handleConnect(ClientConnect con) {
		if (con.getUUID() == Components.UUID.get(player).getUUID())
			return;
		PlayerEntity p = new RenderPlayerEntity(con.getName(), con.getUUID().toString());
		p.setPosition(0, 2, 0);
		engine.addEntity(p);
		players.put(con.getUUID(), p);
	}

	private void handleDisconnect(ClientDisconnect con) {
		if (con.getUUID() == Components.UUID.get(player).getUUID())
			return;
		PlayerEntity p = players.remove(con.getUUID());
		engine.removeEntity(p);
	}

	private void handleUpdateBasicEntity(UpdateBasicEntity ube) {
		if (ube.getUUID() == Components.UUID.get(player).getUUID())
			return;
		PlayerEntity e = players.get(ube.getUUID());
		e.setPosition(ube.getPosition());
		e.setRotation(ube.getRotation());
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
