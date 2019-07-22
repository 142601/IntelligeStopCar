/**
 * 此类用于更新文本框的显示具有以下功能：
 * 1.保存此次输入的文本框的信息
 * 2.实施更新文本框的内容
 */
package com.red;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class IpInfo {

    //文本框上要显示的内容 ipAdd,ipPort
    private String ipAdd;
    private int ipPort;
    private Context context;
    final private String fileName = "data";

    public IpInfo(Context context1){
        context = context1;
    }

    public IpInfo(String ipAdd, int ipPort,Context context1) {
        context = context1;

        try {
            File file = new File(fileName);
            if (!file.exists()) {
                this.ipAdd = ipAdd;
                this.ipPort = ipPort;
                save();
            } else {
                update();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getIpAdd() {
        return ipAdd;
    }

    public int getIpPort() {
        return ipPort;
    }

    public void setIpAdd(String ipAdd) {
        this.ipAdd = ipAdd;
    }

    public void setIpPort(int ipPort) {
        this.ipPort = ipPort;
    }

    public void save(){
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = context.openFileOutput(fileName,Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(ipAdd);
            writer.newLine();
            writer.write(String.valueOf(ipPort));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(){
        FileInputStream in = null;
        BufferedReader reader = null;
        try {
            in = context.openFileInput(fileName);
            reader = new BufferedReader(new InputStreamReader(in));
            ipAdd = reader.readLine();
            try {
                ipPort = Integer.parseInt(reader.readLine());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
