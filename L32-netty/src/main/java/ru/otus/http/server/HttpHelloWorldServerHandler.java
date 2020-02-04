package ru.otus.http.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.List;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;


/*
http://localhost:8080/?name=Jone
http://localhost:8080/
 */
public class HttpHelloWorldServerHandler extends SimpleChannelInboundHandler<HttpObject> {
  private static final Logger logger = LoggerFactory.getLogger(HttpHelloWorldServerHandler.class);

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
  }

  @Override
  public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
    if (msg instanceof HttpRequest) {
      HttpRequest req = (HttpRequest) msg;
      HttpMethod method = req.method();
      logger.info("method:{}", method.name());

      boolean keepAlive = HttpUtil.isKeepAlive(req);

      QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
      List<String> nameList = decoder.parameters().get("name");
      String data;
      if (nameList != null && !nameList.isEmpty()) {
        data = method.name() + " Hi, " + nameList.get(0) + ", now is:" + LocalTime.now();
      } else {
        data = method.name() + " Hi, now is:" + LocalTime.now();
      }

      FullHttpResponse response = new DefaultFullHttpResponse(req.protocolVersion(), OK, Unpooled.copiedBuffer(data, CharsetUtil.UTF_8));
      response.headers()
          .set(CONTENT_TYPE, TEXT_PLAIN)
          .setInt(CONTENT_LENGTH, response.content().readableBytes());

      if (keepAlive) {
        if (!req.protocolVersion().isKeepAliveDefault()) {
          response.headers().set(CONNECTION, KEEP_ALIVE);
        }
      } else {
        response.headers().set(CONNECTION, CLOSE);
      }

      ChannelFuture channelFuture = ctx.write(response);

      if (!keepAlive) {
        channelFuture.addListener(ChannelFutureListener.CLOSE);
      }
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.error(cause.getMessage(), cause);
    ctx.close();
  }
}
