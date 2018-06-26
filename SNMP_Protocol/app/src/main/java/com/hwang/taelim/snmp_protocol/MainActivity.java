package com.hwang.taelim.snmp_protocol;

// Android import
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;

// SNMP import
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    // 서버 주소
    public static final String IP_addr = "kuwiden.iptime.org";

    // 통신 포트
    public static final int PORT_number = 11161;

    // 결과 표시(get and set)
    public TextView textViewGetAndSet = null;

    // 결과 표시(walk)
    public TextView textViewWalk = null;

    // snmpget 결과
    public SnmpGet snmpGet = null;

    // snmpset 결과
    public SnmpSet snmpSet = null;

    // snmpwalk 결과
    public SnmpWalk snmpWalk = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // snmpget 버튼
        Button btnGet = (Button)findViewById(R.id.button);

        // snmpset 버튼
        Button btnSet = (Button)findViewById(R.id.button2);

        // snmpwalk 버튼
        Button btnWalk = (Button)findViewById(R.id.button3);

        // textViewGetAndSet의 layout 연결
        textViewGetAndSet = (TextView) findViewById(R.id.textView);

        // textViewWalk의 layout 연결
        textViewWalk = (TextView) findViewById(R.id.textView2);

        // snmpget 버튼 클릭 시
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SnmpGet 클래스 생성
                snmpGet = new SnmpGet();
                //보내기 시작
                snmpGet.start();
            }
        });

    }

    // snmpget class
    class SnmpGet extends Thread{
        public void run(){
            try{
                //UDP 통신용 소켓 생성
                DatagramSocket socket = new DatagramSocket();
                //서버 주소 변수
                InetAddress serverAddr = InetAddress.getByName(IP_addr);

                //보낼 데이터 생성
                byte[] buf = ("Hello World").getBytes();

                //패킷으로 변경
                DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, 11161);

                //패킷 전송
                socket.send(packet);

                //데이터 수신 대기
                socket.receive(packet);
                //데이터 수신되었다면 문자열로 변환
                String msg = new String(packet.getData());

                //txtView에 표시
                textViewGetAndSet.setText(msg);
            }catch (Exception e){

            }
        }
    }
    // snmpset class
    class SnmpSet extends Thread{
        public void run(){
            try{
                //UDP 통신용 소켓 생성
                DatagramSocket socket = new DatagramSocket();
                //서버 주소 변수
                InetAddress serverAddr = InetAddress.getByName(IP_addr);

                //보낼 데이터 생성
                byte[] buf = ("Hello World").getBytes();

                //패킷으로 변경
                DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, 11161);

                //패킷 전송
                socket.send(packet);

                //데이터 수신 대기
                socket.receive(packet);
                //데이터 수신되었다면 문자열로 변환
                String msg = new String(packet.getData());

                //txtView에 표시
                textViewGetAndSet.setText(msg);
            }catch (Exception e){

            }
        }
    }
    // snmpwalk class
    class SnmpWalk extends Thread{
        public void run(){
            try{
                //UDP 통신용 소켓 생성
                DatagramSocket socket = new DatagramSocket();
                //서버 주소 변수
                InetAddress serverAddr = InetAddress.getByName(IP_addr);

                //보낼 데이터 생성
                byte[] buf = ("Hello World").getBytes();

                //패킷으로 변경
                DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, 11161);

                //패킷 전송
                socket.send(packet);

                //데이터 수신 대기
                socket.receive(packet);
                //데이터 수신되었다면 문자열로 변환
                String msg = new String(packet.getData());

                //txtView에 표시
                textViewWalk.setText(msg);
            }catch (Exception e){

            }
        }
    }
}
