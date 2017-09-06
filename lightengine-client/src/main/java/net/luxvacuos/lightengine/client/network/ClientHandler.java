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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.universal.network.packets.Disconnect;
import net.luxvacuos.lightengine.universal.network.packets.Message;
import net.luxvacuos.lightengine.universal.network.packets.Time;

public class ClientHandler extends ChannelInboundHandlerAdapter {
	
	public ClientHandler() {
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
		try {
			if (obj instanceof Message) {
				handleMessage((Message) obj);
			} else if (obj instanceof String) {
				handleString((String) obj);
			} else if (obj instanceof Time) {
				handleTime((Time) obj);
			} else if (obj instanceof Disconnect) {
				handleDisconnect((Disconnect) obj);
			}
		} finally {
			ReferenceCountUtil.release(obj);
		}
		super.channelRead(ctx, obj);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
		super.exceptionCaught(ctx, cause);
	}

	private void handleString(String str) {
		Logger.log("[Server]: " + str);
	}

	private void handleMessage(Message msg) {
		Logger.log("[" + msg.getSender() + "]: " + msg.getMessage());
	}

	private void handleTime(Time time) {
	}

	private void handleDisconnect(Disconnect disconnect) {
	}
}