package com.coreaiot.cle.examples;

import java.io.*;
import java.net.*;
import java.util.zip.*;

public class App {
  public static void main(String[] args) {
    String hostname = "127.0.0.1";
    int port = 55555;

    try {
      InetAddress address = InetAddress.getByName(hostname);
      DatagramSocket socket = new DatagramSocket();

      String msg = "subscribe:zlib";
      byte[] buff = msg.getBytes();
      DatagramPacket request = new DatagramPacket(buff, buff.length, address, port);
      socket.send(request);

      while (true) {
        byte[] buffer = new byte[65535];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
        socket.receive(response);

        try {
          byte[] unzipped = unzip(buffer);
          String json = new String(unzipped, 0, unzipped.length);
          System.out.println(json);
        } catch (Exception e) {
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static byte[] unzip(byte[] data) throws IOException, DataFormatException {
    Inflater inf = new Inflater();
    inf.setInput(data);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    while (!inf.finished()) {
      int count = inf.inflate(buffer);
      baos.write(buffer, 0, count);
    }
    baos.close();
    return baos.toByteArray();
  }
}
