/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2019 Lux Vacuos
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

package net.luxvacuos.lightengine.server.network;

import java.util.Map;
import java.util.UUID;

import com.badlogic.ashley.core.Engine;

import io.netty.channel.ChannelHandlerContext;
import net.luxvacuos.lightengine.server.core.ServerWorldSimulation;
import net.luxvacuos.lightengine.universal.core.IWorldSimulation;
import net.luxvacuos.lightengine.universal.ecs.Components;
import net.luxvacuos.lightengine.universal.ecs.entities.PlayerEntity;
import net.luxvacuos.lightengine.universal.network.AbstractChannelHandler;
import net.luxvacuos.lightengine.universal.network.packets.ClientConnect;
import net.luxvacuos.lightengine.universal.network.packets.ClientDisconnect;
import net.luxvacuos.lightengine.universal.network.packets.Time;
import net.luxvacuos.lightengine.universal.network.packets.UpdateBasicEntity;
import net.luxvacuos.lightengine.universal.world.PhysicsSystem;

public class ServerNetworkHandler extends AbstractChannelHandler {

	public ServerNetworkHandler() {
		worldSimulation = new ServerWorldSimulation();
		engine = new Engine();
		engine.addSystem(new PhysicsSystem());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
		if (obj instanceof ClientConnect) {
			handleConnect((ClientConnect) obj, ctx);
		} else if (obj instanceof ClientDisconnect) {
			handleDisconnect((ClientDisconnect) obj);
		} else if (obj instanceof UpdateBasicEntity) {
			handleUpdateBasicEntity((UpdateBasicEntity) obj);
		}
		super.channelRead(ctx, obj);
	}

	@Override
	public void update(float delta) {
		worldSimulation.update(delta);
		engine.update(delta);
		ServerHandler.channels.writeAndFlush(new Time(worldSimulation.getTime()));
	}

	@Override
	public void dispose() {
		engine.removeAllEntities();
	}

	private void handleConnect(ClientConnect con, ChannelHandlerContext ctx) {
		for (PlayerEntity pl : players.values()) {
			ctx.channel().writeAndFlush(
					new ClientConnect(Components.UUID.get(pl).getUUID(), Components.NAME.get(pl).getName()));
		}
		PlayerEntity p = new PlayerEntity(con.getName(), con.getUUID().toString());
		p.setPosition(0, 2, 0);
		engine.addEntity(p);
		players.put(con.getUUID(), p);
		ServerHandler.channels.writeAndFlush(con);
	}

	private void handleDisconnect(ClientDisconnect con) {
		engine.removeEntity(players.remove(con.getUUID()));
		ServerHandler.channels.writeAndFlush(con);
	}

	private void handleUpdateBasicEntity(UpdateBasicEntity ube) {
		PlayerEntity e = players.get(ube.getUUID());
		e.setPosition(ube.getPosition());
		e.setRotation(ube.getRotation());
		ServerHandler.channels.writeAndFlush(ube);
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

}
