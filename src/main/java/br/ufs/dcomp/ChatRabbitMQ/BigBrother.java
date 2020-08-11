package br.ufs.dcomp.ChatRabbitMQ;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import com.google.protobuf.ByteString;
import br.ufs.dcomp.ChatRabbitMQ.ChatProtoBuf.Mensagem;

public class BigBrother extends Thread {
  
  // attributes
  public boolean chatControl = true;
  public String chatUI = "";
  public String uploadField = "";
  
  // constructor
  public BigBrother() {
    
  }
  
  // methods get set
  public boolean getChatControl() {
    return this.chatControl;
  }
  
  public void setChatControl(boolean chatControl) {
    this.chatControl = chatControl;
  }
  
  public String getChatUI() {
    return this.chatUI;
  }
  
  public void setChatUI(String chatUI) {
    this.chatUI = chatUI;
  }
  
  public String getUploadField() {
    return this.uploadField;
  }
  
  public void setUploadField(String uploadField) {
    this.uploadField = uploadField;
  }
  
  
  // methods
  public boolean checkMessage(String message, User u) throws Exception {
    // /command
    if (message.charAt(0) == '/' && message.substring(1).length() > 0) {
      if (message.substring(1).equals("exit")) setChatControl(false);
      return true;
    }
    // @name
    if (message.charAt(0) == '@' && message.substring(1).length() > 0) {
      if (!message.substring(1).equals(u.getUsername())) setChatUI(message);
      if (getChatUI().length() > 0 
        && message.substring(1).equals(u.getUsername())) setChatUI("");
      return true;
    }
    // #groups message
    if (message.charAt(0) == '#' && message.substring(1).length() > 0) {
      setChatUI(message);
      return true;
    }
    // !groups
    if (message.charAt(0) == '!' && message.substring(1).length() > 0) {
      
      // !upload
      if (message.substring(1,7).equals("upload") 
        && message.substring(8).length() > 0) {
          setUploadField(message.substring(8));
          return false;
      }
      // !addUser
      if (message.substring(1,8).equals("addUser")
        && message.substring(9).length() > 0) {
          Group.addUser(
            message.substring(9, message.indexOf(" ", 9)), // user
            message.substring(                             // group
              10 + message.substring(9, message.indexOf(" ", 9)).length()
            )
          );
      }
      // !addGroup
      if (message.substring(1,9).equals("addGroup")
        && message.substring(10).length() > 0) {
          Group.createGroup(message.substring(10));
      }
      // !removeGroup grupo1
      if (message.substring(1,12).equals("removeGroup")
        && message.substring(13).length() > 0) {
          Group.removeGroup(message.substring(13));
      }
      // !delFromGroup beta grupo1
      if (message.substring(1,13).equals("delFromGroup")
        && message.substring(14).length() > 0) {
          Group.delFromGroup(
            message.substring(14, message.indexOf(" ", 14)), // user
            message.substring(                             // group
              15 + message.substring(14, message.indexOf(" ", 14)).length()
            )
          );
      }
      return true;
    }
    return false;
  }
  
  public String checkReceiver(User u) {
    if (chatUI.length() > 0 && this.chatUI.charAt(0) == '@' ) {
      return chatUI.substring(1);
    }
    if (chatUI.length() > 0 && this.chatUI.charAt(0) == '#' ) {
      return chatUI.substring(1);
    }
    return u.getUsername();
  }
  
  public byte[] packMessage(String message, User u) throws Exception {
    
    // protobuff
    Mensagem.Builder mensagem = Mensagem.newBuilder();
    Mensagem.Conteudo.Builder conteudo = Mensagem.Conteudo.newBuilder();
    
    // date and time
    Date d = new Date();
    DateFormat data = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    String dt = data.format(d);
    
    // mensagem
    mensagem.setSender(u.getUsername());
    mensagem.setDate(dt.substring(0,10));
    mensagem.setTime(dt.substring(12));
    if (getChatUI().length() > 0 && getChatUI().charAt(0) == '#') {
      mensagem.setGroup(getChatUI());
    }
    
    // content
    if (getUploadField().length() > 0) {
      conteudo.setType("File");
      conteudo.setBody(
        ByteString.copyFrom(
          Files.readAllBytes(Paths.get(getUploadField()))
          )
        );
        if (getUploadField().charAt(0) == '/') {
          conteudo.setName(
            message.substring(getUploadField().lastIndexOf("/"))); 
        } else {
          conteudo.setName(getUploadField());
        }
      setUploadField("");
      System.out.println("!>>! Fazendo Upload... !<<!");
    } else { 
      conteudo.setType("String");
      conteudo.setBody(ByteString.copyFrom(message.getBytes("UTF-8")));
    }
    mensagem.setContent(conteudo);
    
    return mensagem.build().toByteArray();
    
  }
  
  public void unpackMessage(byte[] packedMessage) throws Exception {
    
    Mensagem mensagemx = Mensagem.parseFrom(packedMessage);
    Mensagem.Conteudo contentx = mensagemx.getContent();
    
    if (contentx.getType().equals("File")) {
      Files.write(
        Paths.get(contentx.getName()),
        contentx.getBody().toByteArray()
      );
      System.out.println("(Arquivo recebido: " + contentx.getName() + ")");
    }
    
    if (mensagemx.getGroup().length() > 0) {
      System.out.print(
        "(" 
        + mensagemx.getDate() 
        + " às " 
        + mensagemx.getTime() 
        + ") "
        + mensagemx.getSender() 
        + mensagemx.getGroup()
        + " diz: " 
        + contentx.getBody().toStringUtf8() 
        + "\n" + getChatUI() + ">> "
      );
    } else {
      System.out.print(
        "(" 
        + mensagemx.getDate() 
        + " às " 
        + mensagemx.getTime() 
        + ") "
        + mensagemx.getSender() 
        + " diz: " 
        + contentx.getBody().toStringUtf8() 
        + "\n" + getChatUI() + ">> "
      );
    }
  }
  
}