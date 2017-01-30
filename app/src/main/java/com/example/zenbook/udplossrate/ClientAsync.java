package com.example.zenbook.udplossrate;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientAsync implements Runnable {
    private int sequence;
    private DatagramSocket clientSocket;

    public ClientAsync(int sequence, DatagramSocket datagramSocket){
        this.sequence = sequence;
        this.clientSocket = datagramSocket;
    }

    @Override
    public void run() {
        try {
            InetAddress IPAddress = InetAddress.getByName("192.168.1.35");
//            InetAddress IPAddress = InetAddress.getByName("nbtc.ee.psu.ac.th");
            String sentence = sequence + "";
            if(sequence == 1001) sentence = "end";
            byte[] sendData = sentence.getBytes();

//            ServerAsync serverAsync = new ServerAsync(clientSocket);
//            Thread receive = new Thread(serverAsync);
//            receive.start();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
            clientSocket.send(sendPacket);

                /*
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                modifiedSentence = new String(receivePacket.getData());
                String message = new String(receivePacket.getData(),receivePacket.getOffset(),receivePacket.getLength());
                count++;
                System.out.println("FROM SERVER:" + message + " " + count);
                clientSocket.close();
                */


            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
//        }
    }
}