package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;

public class Group {

  public static void createGroup(String grupo) throws Exception {

    ConnectionFactory factory = new ConnectionFactory();
    factory.setUri("amqp://something:something@email" +
                     ".rmq.com/something");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    channel.exchangeDeclare(grupo, "fanout");
    channel.close();
    connection.close();

  }

  public static void addUser(String u, String grupo) throws Exception {

    ConnectionFactory factory = new ConnectionFactory();
    factory.setUri("amqp://something:somethingelse@email" +
                     ".rmq.com/something");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    channel.queueBind(u, grupo, "");

  }

  public static void sendMessage(User u, BigBrother bb, String message)
    throws Exception {

    ConnectionFactory factory = new ConnectionFactory();
    factory.setUri("amqp://something:somethingelse@email" +
                     ".rmq.com/something");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    channel.basicPublish(
      bb.getChatUI().substring(1),
      "",
      null,
      bb.packMessage(message, u)
    );
  }

  public static void delFromGroup(String u, String grupo) throws Exception {

    ConnectionFactory factory = new ConnectionFactory();
    factory.setUri("amqp://something:somethingelse@email" +
                     ".rmq.com/something");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    channel.queueUnbind(u, grupo, "");
    channel.close();
	  connection.close();

  }

  public static void removeGroup(String grupo) throws Exception {

    ConnectionFactory factory = new ConnectionFactory();
    factory.setUri("amqp://something:somethingelse@email" +
                     ".rmq.com/something");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    channel.exchangeDelete(grupo);
    channel.close();
    connection.close();

  }

}
