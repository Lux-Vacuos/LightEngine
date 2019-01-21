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

package net.luxvacuos.lightengine.universal.network;

import com.badlogic.gdx.utils.Array;

import io.netty.channel.ChannelHandlerContext;

public class ManagerChannelHandler extends SharedChannelHandler {

	private Array<SharedChannelHandler> channels = new Array<>();

	public ManagerChannelHandler() {
	}

	public void addChannelHandler(SharedChannelHandler c) {
		channels.add(c);
	}

	public void removeChannelHandler(SharedChannelHandler c) {
		channels.removeValue(c, true);
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		synchronized (channels) {
			for (SharedChannelHandler c : channels)
				c.channelRegistered(ctx);
		}
		super.channelRegistered(ctx);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		synchronized (channels) {
			for (SharedChannelHandler c : channels)
				c.channelUnregistered(ctx);
		}
		super.channelUnregistered(ctx);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		synchronized (channels) {
			for (SharedChannelHandler c : channels)
				c.channelActive(ctx);
		}
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		synchronized (channels) {
			for (SharedChannelHandler c : channels)
				c.channelInactive(ctx);
		}
		super.channelInactive(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		synchronized (channels) {
			for (SharedChannelHandler c : channels)
				c.channelRead(ctx, msg);
		}

		super.channelRead(ctx, msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		synchronized (channels) {
			for (SharedChannelHandler c : channels)
				c.channelReadComplete(ctx);
		}
		super.channelReadComplete(ctx);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		synchronized (channels) {
			for (SharedChannelHandler c : channels)
				c.userEventTriggered(ctx, evt);
		}
		super.userEventTriggered(ctx, evt);
	}

	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
		synchronized (channels) {
			for (SharedChannelHandler c : channels)
				c.channelWritabilityChanged(ctx);
		}
		super.channelWritabilityChanged(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		synchronized (channels) {
			for (SharedChannelHandler c : channels)
				c.exceptionCaught(ctx, cause);
		}
		super.exceptionCaught(ctx, cause);
	}

}
