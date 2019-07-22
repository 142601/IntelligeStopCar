package com.red;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ShowInfoActivity extends AppCompatActivity implements Runnable{

    final private String TAG = "ShowInfoActivity";
    // 创建文本引用，
    private TextView car1,car2,car3,car4,car5,car6;
    private Button btOk;
    // socket 的接收器
    private BufferedReader in = null;
    private PrintWriter out = null;
    // 创建文本框列表
    private ArrayList<TextView> arrayList = new ArrayList<>();
    // 获得数据
    private String carData = null;

    CarSocketTheard carSockerConnect;

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



        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("carDriveDemo",true);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        IpInfo ip = new IpInfo(ShowInfoActivity.this);

        carSockerConnect = new CarSocketTheard(ip);

        carSockerConnect.start();

        new Thread(ShowInfoActivity.this).start();
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
        int indexOfCar = 0;
        int indexOfData = 0;
        for (int i =0; i <= (strings.length-3)/2; i++) {
            try {
                indexOfCar = Integer.parseInt(strings[2 * i + 1]);
                indexOfData = Integer.parseInt(strings[2 * i + 2]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if(indexOfData == 0) {
                arrayList.get(indexOfCar - 1).setBackground(fullColor);
            } else {
                arrayList.get(indexOfCar - 1).setBackground(emptyColor);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void run() {
        while(true){
            carData = CarSocketTheard.carData;
            if(carData != null && carData.startsWith("d")){
                carHandler(carData,arrayList);
            }
        }
    }
}
