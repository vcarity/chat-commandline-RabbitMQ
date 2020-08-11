package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;
import java.io.IOException;

public class User extends Thread{

  // attributes
  public String username;
  public BigBrother bb;

  // constructor
  public User (String username, BigBrother bb) {
    setUsername(username);
    this.bb = bb;
  }

  // methods
  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void runReceiver() {
    try {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setUri("amqp://something:somethingelse@email" +
                     ".rmq.com/something");
      Connection connection = factory.newConnection();
      Channel receiver = connection.createChannel();

      receiver.queueDeclare(getUsername(), false, false, false, null);
      System.out.println("!>>! Esperando recebimento de mensagens... !<<!");

      QueueingConsumer consumer = new QueueingConsumer(receiver) {
      public void handleDelivery(
        String consumerTag,
        Envelope envelope,
        AMQP.BasicProperties properties,
        byte[] body
        ) throws IOException {
          try {
            bb.unpackMessage(body);
          } catch (Exception ex) {
            ex.printStackTrace();
          }
      }
    };
    receiver.basicConsume(getUsername(), true, consumer);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

}
