package com.example.zenbook.udplossrate;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.DatagramSocket;
import java.net.SocketException;

public class MainActivity extends Activity {

    private TextView textView;
    private LinearLayout scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scrollView = (LinearLayout) findViewById(R.id.scrollLayout);
        Button button = (Button)findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Global.lossRate = 0;

                    textView = new TextView(getBaseContext());
                    textView.setTextColor(Color.BLACK);
                    textView.setTextSize(20);
                    scrollView.addView(textView);

                    new Thread(new Runnable() {
                        public void run() {
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


                            for (int i = 0; i < 1001; i++) {
//                                try {
//                                    DatagramSocket clientSocket = new DatagramSocket();
//                                    ServerAsync serverAsync = new ServerAsync(clientSocket);
//                                    receive = new Thread(serverAsync);
//                                    receive.start();
                                    ClientAsync clientAsync = new ClientAsync(i + 1, clientSocket);
                                    send = new Thread(clientAsync);
                                    send.start();

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
                                        textView.setText(Global.lossRate+"");
                                    }
                                }));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });
    }

    private void process(){

    }
}
