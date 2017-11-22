package com.example.zenbook.udplossrate;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientAsync implements Runnable {
    private String sequence;
    private DatagramSocket clientSocket;

    public ClientAsync(String sequence, DatagramSocket datagramSocket){
        this.sequence = sequence;
        System.out.println(sequence);
        this.clientSocket = datagramSocket;
    }

    @Override
    public void run() {
        try {
//            InetAddress IPAddress = InetAddress.getByName("172.31.193.231");
//            InetAddress IPAddress = InetAddress.getByName("192.168.1.40");
//            InetAddress IPAddress = InetAddress.getByName("172.31.16.5");
            InetAddress IPAddress = InetAddress.getByName("nbtc.ee.psu.ac.th");
            String sentence = sequence + "";
//            if(sequence.equals("end")) sentence = "end";
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
