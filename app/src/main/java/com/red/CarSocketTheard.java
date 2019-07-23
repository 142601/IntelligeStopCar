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

    // 接收缓存区
    private char[] receiveBuf = new char[30];

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

    //信息结束标记e
    private char endchar = 'e';

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

        int theNumberOfconnect = 0;

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

                        if (!socket.isOutputShutdown()) {
//                            socket.getOutputStream().write(askData.getBytes("UTF-8"));
                            out.println(askData);

                            Log.d("CarSocketThread","已经发送完数据");
                            flagOfAsk = false;
//                        out.println(askData);
                        }
                    }
                    // 接受数据
                    if (!flagOfAsk ) {

//                        flagOfAsk = true;

                        if(!socket.isInputShutdown()) {

                            Log.d("CarSocketThread","正在接收数据中");

                            in.read(receiveBuf,0,1);
                            Log.d("CarSocketThread","接收到数据 firstcarData = " + receiveBuf[0]);

                            if(receiveBuf[0] == 'd') {

                                int indexOfBuf = 1;
                                boolean flagOfreceiveOK = false;

                                while(true){
                                    in.read(receiveBuf,indexOfBuf,1);
                                    if(indexOfBuf > 29 ){
                                        break;
                                    }

                                    if(receiveBuf[indexOfBuf] == endchar){
                                        flagOfreceiveOK = true;
                                        break;
                                    }

                                    indexOfBuf ++;
                                }

                                if(flagOfreceiveOK) {
                                    carData = new String(receiveBuf);
//                                    Log.d("CarSocketThread","接收到数据 carData = " );

//                                    for(int a = 0;a <= indexOfBuf;a ++) {
//                                        Log.d("CarSocketThread"," " + receiveBuf[a]);
//                                    }

                                    if ((carData != null) && (carData.startsWith("d"))) {
                                        flagOfreceive = false;
                                        flagOfAsk = false;
                                    } else {
                                        carData = null;
                                    }
                                }
                            }
                        }

                    }
                } else {
                    Log.d("CarSocketThread","正在重新连接....");
                    theNumberOfconnect ++;
                    if(theNumberOfconnect > 100){
                        break;
                    }
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

            this.socket.connect(socket, 1000);

            Log.d("CarSocketThread","连接成功");

            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    this.socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
