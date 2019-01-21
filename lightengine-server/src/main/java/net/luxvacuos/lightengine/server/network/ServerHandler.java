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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.universal.network.packets.ClientConnect;
import net.luxvacuos.lightengine.universal.network.packets.ClientDisconnect;
import net.luxvacuos.lightengine.universal.network.packets.Message;

public class ServerHandler extends ChannelInboundHandlerAdapter {

	public static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	public ServerHandler() {
	}

	@Override
	public void handlerAdded(ChannelHandlerContext context) throws Exception {
		channels.add(context.channel());
		super.handlerAdded(context);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext context) throws Exception {
		channels.remove(context.channel());
		super.handlerRemoved(context);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
		if (obj instanceof Message) {
			handleMessage((Message) obj);
		} else if (obj instanceof String) {
			handleString(ctx, (String) obj);
		} else if (obj instanceof ClientConnect) {
			handleConnect((ClientConnect) obj);
		} else if (obj instanceof ClientDisconnect) {
			handleDisconnect((ClientDisconnect) obj);
		}
		super.channelRead(ctx, obj);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
		super.exceptionCaught(ctx, cause);
	}

	private void handleConnect(ClientConnect con) {
		channels.writeAndFlush(new Message("Server", "Connected " + con.getName()));
		Logger.log("Connected " + con.getName());
	}

	private void handleDisconnect(ClientDisconnect con) {
		channels.writeAndFlush(new Message("Server", "Disconected " + con.getName()));
		Logger.log("Disconected " + con.getName());
	}

	private void handleString(ChannelHandlerContext ctx, String str) {
		Logger.log("Client [" + ctx.channel().id().asLongText() + "]: " + str);
	}

	private void handleMessage(Message msg) {
		Logger.log("[" + msg.getSender() + "]: " + msg.getMessage());
	}

}
