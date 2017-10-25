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
import net.luxvacuos.lightengine.client.ecs.entities.PlayerCamera;
import net.luxvacuos.lightengine.client.ecs.entities.RenderPlayerEntity;
import net.luxvacuos.lightengine.client.world.ClientPhysicsSystem;
import net.luxvacuos.lightengine.universal.core.IWorldSimulation;
import net.luxvacuos.lightengine.universal.ecs.Components;
import net.luxvacuos.lightengine.universal.ecs.entities.PlayerEntity;
import net.luxvacuos.lightengine.universal.network.AbstractNetworkHandler;
import net.luxvacuos.lightengine.universal.network.packets.ClientConnect;
import net.luxvacuos.lightengine.universal.network.packets.ClientDisconnect;
import net.luxvacuos.lightengine.universal.network.packets.Time;
import net.luxvacuos.lightengine.universal.network.packets.UpdateBasicEntity;

public class ClientNetworkHandler extends AbstractNetworkHandler {

	private PlayerCamera player;
	private Client client;

	public ClientNetworkHandler(Client client) {
		this.client = client;
		worldSimulation = new ClientWorldSimulation(10000);
		engine = new Engine();
		engine.addSystem(new ClientPhysicsSystem());
		player = new PlayerCamera("player" + new Random().nextInt(1000), UUID.randomUUID().toString());
		engine.addEntity(player);
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
		client.sendPacket(new UpdateBasicEntity(Components.UUID.get(player).getUUID(), player.getPosition(),
				player.getRotation(), new Vector3f(), Components.SCALE.get(player).getScale()));
	}

	@Override
	public void dispose() {
		engine.removeAllEntities();
	}

	private void handleTime(Time time) {
		worldSimulation.setTime(time.getTime());
	}

	private void handleConnect(ClientConnect con) {
		PlayerEntity p = new RenderPlayerEntity(con.getName(), con.getUUID().toString());
		Components.POSITION.get(p).set(0, 2, 0);
		engine.addEntity(p);
		players.put(con.getUUID(), p);
	}

	private void handleDisconnect(ClientDisconnect con) {
		PlayerEntity p = players.remove(con.getUUID());
		engine.removeEntity(p);
	}

	private void handleUpdateBasicEntity(UpdateBasicEntity ube) {
		PlayerEntity e = players.get(ube.getUUID());
		Components.POSITION.get(e).set(ube.getPosition());
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

	public PlayerCamera getPlayer() {
		return player;
	}

}
