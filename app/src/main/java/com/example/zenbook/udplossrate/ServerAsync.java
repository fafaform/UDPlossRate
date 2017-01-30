package com.example.zenbook.udplossrate;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by ZENBOOK on 7/9/2016.
 */
public class ServerAsync implements Runnable {

    private DatagramSocket datagramSocket;

    public ServerAsync(DatagramSocket datagramSocket){
        this.datagramSocket = datagramSocket;
    }

    @Override
    public void run() {
        try {
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            datagramSocket.receive(receivePacket);
            String message = new String(receivePacket.getData(),receivePacket.getOffset(),receivePacket.getLength());
            Global.lossRate++;
            if(message.equals(Global.lossRate+"")) {
                System.out.println("RECEIVED: " + message);
                Global.lossRate = Integer.parseInt(message)-1;
            }else{
                System.err.println("RECEIVED: " + message);
                Global.lossRate = Integer.parseInt(message)-1;
            }
            datagramSocket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
