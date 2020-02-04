package ru.otus.rmi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

/*
netstat -tulpn | grep 1099
 */
public class RmiServer extends UnicastRemoteObject implements EchoInterface {
  private static final long serialVersionUID = 1L;

  private static Logger logger = LoggerFactory.getLogger(RmiServer.class);
  private static final int SERVER_PORT = 8090;
  private static final int REGISTRY_PORT = 1099;


  private RmiServer(int port) throws RemoteException {
    super(port);
  }


  @Override
  public String echo(String data) {
    logger.info("data from client:{}", data);
    return "echo:" + data;

  }

  public static void main(String[] args) {
    try {
      LocateRegistry.createRegistry(REGISTRY_PORT);

      Naming.rebind("//localhost/EchoServer", new RmiServer(SERVER_PORT));
      logger.info("waiting for client connection");
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
    }
  }

}
