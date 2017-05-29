package com.example.zenbook.udplossrate;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends Activity {
    
    private static int number_of_data_per_packet = 1;
    private static int number_of_packet = 100;
    private static int time_for_another_packet_in_millisecond = 1;
    private static int time_before_end_in_millisecond = 1000;
    
    private TextView textView;
    private LinearLayout scrollView;
    
    private File file;
    private FileOutputStream fileoutputStream;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        scrollView = (LinearLayout) findViewById(R.id.scrollLayout);
        Button button = (Button)findViewById(R.id.button);
        createFile();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
//                Global.lossRate = 0;
//
//                textView = new TextView(getBaseContext());
//                textView.setTextColor(Color.BLACK);
//                textView.setTextSize(20);
//                scrollView.addView(textView);
                
                new Thread(new Runnable() {
                    public void run() {
                        while(true) {
                            process();
//                            tempProcess();
                        }
                    }
                }).start();
            }
        });
    }
    
    private void process(){
        runOnUiThread(new Thread(new Runnable() {
            public void run() {
            
                //TODO: INITIAL
                textView = new TextView(getBaseContext());
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(20);
                scrollView.addView(textView);
                //TODO: END ENITIAL
            }
        }));
        System.out.println("*********************************************************************START*************************************************");
        Thread receive = new Thread();
        Thread send;
    
        DatagramSocket clientSocket = null;
        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        ServerAsync serverAsync = new ServerAsync(clientSocket);
        receive = new Thread(serverAsync);
        receive.start();
    
        int number = number_of_packet;
        int count = 0;
        String sendingMessage = "";
        for (int i = 0; i < number; i++) {
            ClientAsync clientAsync;
//            System.out.println(i+":"+count);
//            sendingMessage += Double.parseDouble(new DecimalFormat("###.##").format(Math.sin(i * 0.1 * Math.PI / 2))) + "&";
            count++;
            if(count == number_of_data_per_packet) {
                sendingMessage += Double.parseDouble(new DecimalFormat("###.##").format(Math.sin(i * 0.1 * Math.PI / 2)));
                System.out.println(sendingMessage.getBytes().length);
                clientAsync = new ClientAsync(sendingMessage, clientSocket);
                System.out.println("SEND FRAGMENT");
                System.out.println("*********************************************************************FRAGMENT*************************************************");
                sendingMessage = "";
                count = 0;
        
                send = new Thread(clientAsync);
                send.start();
            } else{
                sendingMessage += Double.parseDouble(new DecimalFormat("###.##").format(Math.sin(i * 0.1 * Math.PI / 2))) + "&";
            }
            if (i == (number - 1)) {
                try {
                    Thread.sleep(time_before_end_in_millisecond);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (sendingMessage.equals("")) {
                    clientAsync = new ClientAsync("end" + getCurrentBand(getBaseContext()), clientSocket);
                } else {
                    clientAsync = new ClientAsync(sendingMessage + "&end" + getCurrentBand(getBaseContext()), clientSocket);
                }
                count = 0;
                sendingMessage = "";
                System.out.println("Sent end");
            
                send = new Thread(clientAsync);
                send.start();
            }
            runOnUiThread(new Thread(new Runnable() {
                public void run() {
                    textView.setText("Running...");
                }
            }));
        }
        try {
            receive.join();
            try {
                Calendar c = Calendar.getInstance();
//                System.out.println("Current time => "+c.getTime());
    
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = df.format(c.getTime());
    
                fileoutputStream = new FileOutputStream(file, true);
                fileoutputStream.write((formattedDate + "," + Global.lossRate + "," + getCurrentBand(getBaseContext())).getBytes());
                fileoutputStream.write("\n".getBytes());
                fileoutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Thread(new Runnable() {
                public void run() {
                    textView.setText(Global.lossRate + "");
                }
            }));
            Thread.sleep(time_for_another_packet_in_millisecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void processBackup1(){
        runOnUiThread(new Thread(new Runnable() {
            public void run() {
            
                //TODO: INITIAL
                Global.lossRate = 0;
                textView = new TextView(getBaseContext());
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(20);
                scrollView.addView(textView);
                //TODO: END ENITIAL
            }
        }));
    
        Thread receive = new Thread();
        Thread send;
    
        DatagramSocket clientSocket = null;
        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        ServerAsync serverAsync = new ServerAsync(clientSocket);
        receive = new Thread(serverAsync);
        receive.start();
    
        int number = 1000;
        int count = 0;
        String sendingMessage = "";
        for (int i = 0; i < number; i++) {
//                                try {
//                                    DatagramSocket clientSocket = new DatagramSocket();
//                                    ServerAsync serverAsync = new ServerAsync(clientSocket);
//                                    receive = new Thread(serverAsync);
//                                    receive.start();
            ClientAsync clientAsync;
            if (i == (number - 1)) {
                if (sendingMessage.equals("")) {
                    clientAsync = new ClientAsync("end", clientSocket);
                } else {
                    clientAsync = new ClientAsync(sendingMessage + "&end", clientSocket);
                }
                count = 0;
                sendingMessage = "";
                System.out.println("Sent");
            
                send = new Thread(clientAsync);
                send.start();
            } else {
//                                if(count == 100) {
////                                        clientAsync = new ClientAsync(Double.parseDouble(new DecimalFormat("###.##").format(Math.sin(i * 0.1 * Math.PI / 2))), clientSocket);
//                                    sendingMessage += Double.parseDouble(new DecimalFormat("###.##").format(Math.sin(i * 0.1 * Math.PI / 2)));
//                                    clientAsync = new ClientAsync(sendingMessage, clientSocket);
//                                    System.out.println(sendingMessage);
//                                    sendingMessage = "";
//                                    count = 0;
//
//                                    send = new Thread(clientAsync);
//                                    send.start();
//                                }
//                                else{
                System.out.println(count);
                sendingMessage += Double.parseDouble(new DecimalFormat("###.##").format(Math.sin(i * 0.1 * Math.PI / 2))) + "&";
                count++;
//                                }
            }

//                                    Thread.sleep(65);
        
            runOnUiThread(new Thread(new Runnable() {
                public void run() {
                    textView.setText("Running...");
                }
            }));
//                                } catch (SocketException e) {
//                                    e.printStackTrace();
//                                } //catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
        }
        try {
            receive.join();
            runOnUiThread(new Thread(new Runnable() {
                public void run() {
                    textView.setText(Global.lossRate + "");
                }
            }));
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void createFile(){
        File externalD = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
        if (!externalD.exists()) {
//            System.out.println("Created Folder");
            File dir = new File(Environment.getExternalStorageDirectory() + "/Download/");
            dir.mkdirs();
            file = new File(externalD, "UDP.txt");
            try {
                file.createNewFile();
                fileoutputStream = new FileOutputStream(file,false);
                fileoutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
//            System.out.println("Already have folder: " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
            if(!new File(externalD + "UDP.txt").exists()){
//                System.out.println("Created File");
                file = new File(externalD, "UDP.txt");
                try {
                    file.createNewFile();
                    fileoutputStream = new FileOutputStream(file,false);
                    fileoutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                System.out.println(file.exists());
            }
        }
    }
    
    public static String getCurrentBand(Context context) {
        String band = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    band = connectionInfo.getFrequency() + "";
                } else {
                    band = "Version is lower than LOLIPOP";
                }
            }
        }else{
            band = "Connected to CELLULAR";
        }
        return band;
    }
}
