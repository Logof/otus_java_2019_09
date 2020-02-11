package ru.otus.dataserver;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class ServerHandler extends ChannelInboundHandlerAdapter {
  private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);

  private static final String QUEUE_NAME = "QUEUE_DATA";
  private Connection connection;
  private Channel channel;
  private boolean connectionToQueueAvailable = false;
  private byte[] byteBuffer = new byte[10240];

  ServerHandler() {
    try {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("localhost");
      connection = factory.newConnection();
      channel = connection.createChannel();
      channel.queueDeclare(QUEUE_NAME, false, false, false, null);
      connectionToQueueAvailable = true;
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
    }

  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    if (connectionToQueueAvailable) {
      connection.close();
    }
    super.channelInactive(ctx);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
    ByteBuf in = (ByteBuf) msg;
    try {
      int requiredSize = in.readableBytes();
      if (requiredSize > byteBuffer.length) {
        byteBuffer = new byte[requiredSize * 2];
      }
      in.readBytes(byteBuffer, 0, requiredSize);

      logger.info("data from client: {}, length:{}", new String(byteBuffer, 0, requiredSize), requiredSize);
      putToQueue(Arrays.copyOf(byteBuffer, requiredSize));
    } finally {
      ReferenceCountUtil.release(msg);
    }
  }

  private void putToQueue(byte[] message) throws IOException {
    if (connectionToQueueAvailable) {
      channel.basicPublish("", QUEUE_NAME, null, message);
    }
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.error(cause.getMessage(), cause);
    ctx.close();
  }

}
