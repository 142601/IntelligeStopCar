package com.red;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ShowInfoActivity extends AppCompatActivity implements Runnable{

    final private String TAG = "ShowInfoActivity";
    // 创建文本引用，
    private TextView car1,car2,car3,car4,car5,car6;
    private Button btOk;
    // socket 的ip端口,通过其他的activity传递
    private String HOST;
    private int PORT;
    //socket的连接
    private Socket socket;
    // socket 的接收器
    private BufferedReader in = null;
    private PrintWriter out = null;
    // 请求数据
    final private String askData = "0";
    private Boolean flagOfAsk = true;//请求数据标志位
    private String carData = "";// 数据！！！！
    // 创建文本框列表
    private ArrayList<TextView> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);

        // 引用View
        btOk = findViewById(R.id.Button_OK_info);
        car1 = findViewById(R.id.Tex01);
        car2 = findViewById(R.id.Tex02);
        car3 = findViewById(R.id.Tex03);
        car4 = findViewById(R.id.Tex04);
        car5 = findViewById(R.id.Tex05);
        car6 = findViewById(R.id.Tex06);

        arrayList.add(car1);
        arrayList.add(car2);
        arrayList.add(car3);
        arrayList.add(car4);
        arrayList.add(car5);
        arrayList.add(car6);

        new Thread() {
            public void run () {
                try{
                    //获得ip
                    IpInfo ip = new IpInfo(ShowInfoActivity.this);
                    ip.update();
                    HOST = ip.getIpAdd();
                    PORT = ip.getIpPort();
                    Log.i(TAG,ip.getIpAdd() + ": " + ip.getIpPort());
                    Log.i(TAG,HOST + " " + PORT);
                    //创建链接输入输出流
                    socket = new Socket(HOST,PORT);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                            socket.getOutputStream())), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("carDriveDemo",true);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        carHandler(carData,arrayList);

        new Thread(ShowInfoActivity.this).start();
    }

    @Override
    public void run() {

        try {
            if (socket.isConnected())

                try {
                    // 发送信息请求车位数据
                    if (flagOfAsk) {
                        flagOfAsk = false;
                        if (!socket.isOutputShutdown()) {
                            out.println(askData);
                        }

                    }
                    // 接受数据
                    if (!flagOfAsk && !socket.isInputShutdown()) {
                        carData = in.readLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void carHandler (String carDataDemo,ArrayList<TextView> arrayList) {
        // 判断是否数据标志位
        if(!carDataDemo.startsWith("d")) {
            return;
        }

        // 获得Drawable
        Resources myColor=getBaseContext().getResources();
        Drawable fullColor = myColor.getDrawable(R.color.colorCarFull);
        Drawable emptyColor = myColor.getDrawable(R.color.colorCarEmpty);
        //字符串格式转变
        String carDemoLast = carDataDemo.replace(",",";");
        String[] strings = carDemoLast.split(";");
        int index = 0;
        for (int i =0; i <= strings.length; i++) {
            try {
                index = Integer.parseInt(strings[2 * i + 1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if(Integer.parseInt(strings[index+1]) == 0) {
                arrayList.get(index).setBackground(fullColor);
            } else {
                arrayList.get(index).setBackground(emptyColor);
            }
        }
    }
}
