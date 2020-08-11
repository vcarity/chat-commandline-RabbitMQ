package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.Scanner;

public class Chat {

  private final static String URI_NAME = "amqp://something:somethingelse" +
                                         "something@email.something." +
                                         "com/something";

  public static void main(String[] argv) throws Exception {

    // attributes
    Scanner scanner = new Scanner(System.in);
    BigBrother bb = new BigBrother();
    String message;

    // asking username
    System.out.print("User: ");
    User u = new User(scanner.nextLine(), bb);
    if (u.getUsername().length() == 0) System.exit(0);
    System.out.println();

    // creating connection
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUri(URI_NAME);
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    channel.queueDeclare(u.getUsername(), false, false, false, null);
    System.out.println("CHAT[ON]");
    u.runReceiver();

    // chat loop
    while (bb.getChatControl()) {
      System.out.print(bb.getChatUI() + ">> ");
        message = scanner.nextLine();
        if (message.length() > 0 && !bb.checkMessage(message, u)) {
          if (bb.getChatUI().length() > 0 && bb.getChatUI().charAt(0) == '#') {
            Group.sendMessage(u, bb, message);
          } else {
            channel.basicPublish(
              "",
              bb.checkReceiver(u),
              null,
              bb.packMessage(message, u)
            );
          }
        }
      }

    // closing connection
    channel.close();
    connection.close();
    System.out.println("CHAT[OFF]");
    System.exit(0);
  }

}
