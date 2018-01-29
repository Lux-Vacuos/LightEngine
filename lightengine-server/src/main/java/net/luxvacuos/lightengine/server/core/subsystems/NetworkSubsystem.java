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

package net.luxvacuos.lightengine.server.core.subsystems;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import net.luxvacuos.lightengine.server.network.ServerHandler;
import net.luxvacuos.lightengine.universal.core.subsystems.ISubsystem;
import net.luxvacuos.lightengine.universal.network.AbstractNettyNetworkHandler;
import net.luxvacuos.lightengine.universal.network.LastChannelHandler;
import net.luxvacuos.lightengine.universal.network.ManagerChannelHandler;

public class NetworkSubsystem extends AbstractNettyNetworkHandler implements ISubsystem {

	private static ManagerChannelHandler mch;
	private static ServerBootstrap b;
	private static EventLoopGroup workerGroup;

	@Override
	public void init() {
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		mch = new ManagerChannelHandler();
		b = new ServerBootstrap();
		b.group(bossGroup, workerGroup);
		b.option(ChannelOption.SO_BACKLOG, 128);
		b.childOption(ChannelOption.SO_KEEPALIVE, true);
		b.channel(NioServerSocketChannel.class);
		b.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel channel) throws Exception {
				ChannelPipeline pipeline = channel.pipeline();
				pipeline.addLast("decoder",
						new ObjectDecoder(ClassResolvers.softCachingResolver(ClassLoader.getSystemClassLoader())));
				pipeline.addLast("encoder", new ObjectEncoder());
				pipeline.addLast("handler", new ServerHandler());
				pipeline.addLast(mch);
				pipeline.addLast(new LastChannelHandler());
			}
		});
	}

	@Override
	public void restart() {
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public void preRender(float delta) {
	}

	@Override
	public void postRender(float delta) {
	}

	@Override
	public void dispose() {
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}

	public static void bind(int port) throws InterruptedException {
		future = b.bind(port).sync();
	}

	public static void disconnect() throws InterruptedException {
		future.channel().closeFuture().sync();
	}

	public static void sendPacket(Object obj) {
		future.channel().writeAndFlush(obj);
	}

	public static ManagerChannelHandler getManagerChannelHandler() {
		return mch;
	}

}
