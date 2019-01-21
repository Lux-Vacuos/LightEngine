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

package net.luxvacuos.lightengine.client.network;

import org.joml.Vector3f;

import io.netty.channel.ChannelHandlerContext;
import net.luxvacuos.lightengine.client.core.subsystems.NetworkSubsystem;
import net.luxvacuos.lightengine.client.ecs.entities.RenderPlayerEntity;
import net.luxvacuos.lightengine.universal.ecs.Components;
import net.luxvacuos.lightengine.universal.ecs.entities.BasicEntity;
import net.luxvacuos.lightengine.universal.ecs.entities.PlayerEntity;
import net.luxvacuos.lightengine.universal.network.packets.ClientConnect;
import net.luxvacuos.lightengine.universal.network.packets.ClientDisconnect;
import net.luxvacuos.lightengine.universal.network.packets.Time;
import net.luxvacuos.lightengine.universal.network.packets.UpdateBasicEntity;

public class ClientNetworkHandler extends LocalNetworkHandler {

	public ClientNetworkHandler(BasicEntity player) {
		super(player);
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
		super.update(delta);
		NetworkSubsystem.sendPacket(new UpdateBasicEntity(Components.UUID.get(player).getUUID(), player.getPosition(),
				player.getRotation(), new Vector3f(), player.getScale()));
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

}
