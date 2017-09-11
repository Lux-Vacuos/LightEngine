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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import net.luxvacuos.lightengine.universal.network.AbstractNettyNetworkHandler;
import net.luxvacuos.lightengine.universal.network.LastChannelHandler;

public class Client extends AbstractNettyNetworkHandler {

	private String host = "localhost";
	private int port = 44454;

	public Client() {
	}

	@Override
	public void run(ChannelInboundHandlerAdapter... channels) {
		workGroup = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(workGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel channel) throws Exception {
					ChannelPipeline pipeline = channel.pipeline();
					pipeline.addLast("decoder",
							new ObjectDecoder(ClassResolvers.softCachingResolver(ClassLoader.getSystemClassLoader())));
					pipeline.addLast("encoder", new ObjectEncoder());
					pipeline.addLast("handler", new ClientHandler());
					for (ChannelInboundHandlerAdapter channel_ : channels) {
						pipeline.addLast(channel_);
					}
					pipeline.addLast(new LastChannelHandler());
				}
			});
			future = b.connect(host, port).sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void sendPacket(Object obj) {
		future.channel().writeAndFlush(obj);
	}

}