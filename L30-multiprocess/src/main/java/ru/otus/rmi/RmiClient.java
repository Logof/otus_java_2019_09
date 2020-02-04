package ru.otus.rmi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Naming;

public class RmiClient {
  private static Logger logger = LoggerFactory.getLogger(RmiClient.class);

  public static void main(String[] args) throws Exception {

    EchoInterface echoInterface = (EchoInterface) Naming.lookup("//localhost/EchoServer");
    var dataFromServer = echoInterface.echo("hello");
    logger.info("response from the server:{}", dataFromServer);

  }
}
