package com.example.administrator.tcptalk;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    Socket socket;
    BufferedReader in;
    PrintWriter out;

    private EditText input;
    private TextView show;
    private Button send;
    //private TcpClient client;
    
    private String inputInfo;
    String buffer = "";

    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x11) {
                Bundle bundle = msg.getData();
                show.append("魔镜:"+bundle.getString("msg")+"\n");
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        //client = new TcpClient();

        input = (EditText) findViewById(R.id.input);
        show = (TextView) findViewById(R.id.show);
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                inputInfo = input.getText().toString();
                show.append("询问:"+inputInfo+"\n");
                //启动线程 向服务器发送和接收信息
                new MyThread(inputInfo).start();
                input.setText("");
            }
        });

    }

    class MyThread extends Thread {

        public String request;

        public MyThread(String request) {
            this.request = request;
        }

        @Override
        public void run() {
            //定义消息
            Message msg = new Message();
            msg.what = 0x11;
            Bundle bundle = new Bundle();
            bundle.clear();
            try {
                System.out.println("尝试连接服务器ip端口 172.31.12.125:8889");
                socket = new Socket("172.31.12.125", 8889);
                System.out.println("客户端已经成功连接至服务器");
                //请求信息拼接
                String ipRequest = "在吗";
                //String requestLength = String.format("%04d", ipRequest.length());//获取长度，不足的补0，如长度56，则输出0056
                //ipRequest = requestLength+ipRequest;
                System.out.println("发送给服务端的信息是:" + request);
                InputStream in_withcode = new ByteArrayInputStream(request.getBytes());
                BufferedReader line = new BufferedReader(new InputStreamReader(in_withcode));
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println(line.readLine());
                //获取服务端返回信息
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //System.out.println(in.readLine());
                String linein = null;
                buffer="";
                while ((linein = in.readLine()) != null) {
                    //System.out.println("linein = " + linein);
                    buffer = linein + buffer;
                }
                bundle.putString("msg", buffer.toString());
                msg.setData(bundle);
                //发送消息 修改UI线程中的组件
                myHandler.sendMessage(msg);
                out.close();
                in.close();
                socket.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
