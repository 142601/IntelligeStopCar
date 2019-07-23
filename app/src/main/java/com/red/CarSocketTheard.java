package com.red;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class CarSocketTheard extends Thread {

    public static String carData = null;// 数据！！！！

    private Socket socket;
    // socket 的ip端口,通过其他的activity传递
    private String HOST;
    private int PORT;
    // 请求数据
    final private String askData = "1\n" ;
    private Boolean flagOfAsk = true;//请求数据标志位

    // socket 的接收器
    private BufferedReader in = null;
    private PrintWriter out = null;

    // ip地址信息,从contex获取
    private IpInfo ip;

    public CarSocketTheard(IpInfo ip) {
        super();
        this.ip = ip;
    }

    @Override
    public void run() {

        this.connect();

        boolean flagOfreceive = true;

        while (flagOfreceive) {
            if(ShowInfoActivity.flageOfclose) {

                Log.d("CarSocketThread","子线程已关闭");

                break;
            }

            Log.d("CarSocketThread","子线程正在运行");

            try {

                if (socket.isConnected()) {
                    // 发送信息请求车位数据
                    if (flagOfAsk) {
                        flagOfAsk = false;
                        if (!socket.isOutputShutdown()) {
//                            socket.getOutputStream().write(askData.getBytes("UTF-8"));
                            out.println(askData);
                            flagOfAsk = false;
                            Log.d("CarSocketThread","已经发送完数据");
//                        out.println(askData);
                        }
                    }
                    // 接受数据
                    if (!flagOfAsk && !socket.isInputShutdown()) {

                        Log.d("CarSocketThread","正在接收数据中");

                        carData = in.readLine();
                        Log.d("CarSocketThread","接收到数据 carData = " + carData);
                        flagOfAsk = true;
                        if ((carData != null) && (carData.startsWith("d"))) {
                            flagOfreceive = false;
                            flagOfAsk = false;
                        } else {
                            carData = null;
                        }
                    }
                } else {
                    Log.d("CarSocketThread","正在重新连接....");
                    this.connect();
                    Log.d("CarSocketThread","连接成功");
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    private void connect() {
        try {
            ip.update();
            HOST = ip.getIpAdd();
            PORT = ip.getIpPort();

            //创建链接输入输出流
            socket = new Socket();
            SocketAddress socket = new InetSocketAddress(HOST, PORT);

            Log.d("CarSocketThread","正在连接中....");

            this.socket.connect(socket, 3000);

            Log.d("CarSocketThread","连接成功");

            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    this.socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
