package ru.otus.http.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

import java.net.URI;

public class PostClient {

  private static final int PORT = 8080;
  private static final String HOST = "localhost";

  public static void main(String[] args) throws Exception {

    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(group)
          .channel(NioSocketChannel.class)
          .handler(new HttpClientInitializer());

      URI uri = new URI("http://" + HOST + ":" + PORT);
      HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath());
      request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);

      Channel channel = bootstrap.connect(HOST, PORT).sync().channel();

      channel.writeAndFlush(request);
      channel.closeFuture().sync();
    } finally {
      group.shutdownGracefully();
    }
  }
}
