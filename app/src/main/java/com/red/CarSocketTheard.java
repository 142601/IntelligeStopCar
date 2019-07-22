package com.red;

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
            try {

                if (socket.isConnected()) {
                    // 发送信息请求车位数据
                    if (flagOfAsk) {
                        flagOfAsk = false;
//                        if (!socket.isOutputShutdown()) {
//                            socket.getOutputStream().write(askData.getBytes("UTF-8"));
                        out.println(carData);
//                        }
                    }
                    // 接受数据
//                    if (!flagOfAsk && !socket.isInputShutdown()) {
                    if (!flagOfAsk ) {

                        carData = in.readLine();
                        flagOfAsk = true;
                        if (carData.startsWith("d")) {
                            flagOfreceive = false;
                            flagOfAsk = false;
                        } else {
                            carData = null;
                        }
                    }
                } else {
                    this.connect();
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
            this.socket.connect(socket, 3000);

            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    this.socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
