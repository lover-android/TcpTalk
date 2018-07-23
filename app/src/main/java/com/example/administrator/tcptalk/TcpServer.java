package com.example.administrator.tcptalk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by administrator on 18-7-18.
 */

public class TcpServer {
    private ServerSocket ss;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    public TcpServer(){
        try {
            ss = new ServerSocket(8889);
            while(true){
                //等待连接请求
                socket = ss.accept();
                //获取ip和端口
                String remoteIP =  socket.getInetAddress().getHostAddress();
                String remotePort = ":"+socket.getLocalPort();
                System.out.println("客户端请求IP为 :"+remoteIP+remotePort);
                //获取请求过来的数据
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = in .readLine();
                System.out.println("客户端的请求信息 :"+ line);
                //返回处理信息
                out= new PrintWriter(socket.getOutputStream(),true);
                out.println(getReturnMsg(line));
                out.close();
                in.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getReturnMsg(String msg){
        if (msg == null) {
            return "";
        }
        String returnMsg = "" ;
        if (msg.contains("魔镜在吗")) {
            returnMsg = "本大神全天在线，亲，有什么可以帮你？";
        }else if(msg.contains("最漂亮的女人")){
            returnMsg = "你的女朋友";
        }else if(msg.contains("我帅不帅")){
            returnMsg = "帅到嗨甘啦";
        }else if(msg.contains("谁和我最般配")){
            returnMsg = "世上最漂亮的女人";
        }else if(msg.contains("优点")){
            returnMsg = "贤惠，身材火辣，自信，体贴。。。太多了，说不完";
        }else if(msg.contains("缺点")){
            returnMsg = "方向感不太好，现在还没找到你。。。";
        }else if(msg.contains("她是谁")){
            returnMsg = "如花！如花！如花！！！重要的事说三遍";
        }else{
            returnMsg = "跟你不熟，并抛出一个白眼";
        }
        return returnMsg;
    }

    public static void main(String[] args){
        new TcpServer();
    }
}
