package com.mqtt.server;

import java.net.URISyntaxException;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  
/** 
 * Created by tiantao on 14-12-2. 
 */  
public class MqttClientTest2 {  
    private static final Logger LOG = LoggerFactory.getLogger(MqttClientTest2.class);  
    private final static String CONNECTION_STRING = "tcp://192.168.103.233:1883";  
    private final static boolean CLEAN_START = true;  
    private final static short KEEP_ALIVE = 30;// 低耗网络，但是又需要及时获取数据，心跳30s  
    public final  static long RECONNECTION_ATTEMPT_MAX=6;  
    public final  static long RECONNECTION_DELAY=2000;  
  
    public final static int SEND_BUFFER_SIZE=2*1024*1024;//发送最大缓冲为2M  
  
    public static void main(String[] args)   {  
        //创建MQTT对象  
        MQTT mqtt = new MQTT();  
        
        try {  
            //设置mqtt broker的ip和端口  
            mqtt.setHost(CONNECTION_STRING);  
            //连接前清空会话信息  
            mqtt.setCleanSession(CLEAN_START);  
            mqtt.setClientId("server");
            //设置重新连接的次数  
            mqtt.setReconnectAttemptsMax(RECONNECTION_ATTEMPT_MAX);  
            //设置重连的间隔时间  
            mqtt.setReconnectDelay(RECONNECTION_DELAY);  
            //设置心跳时间  
            mqtt.setKeepAlive(KEEP_ALIVE);  
            //设置缓冲的大小  
            mqtt.setSendBufferSize(SEND_BUFFER_SIZE);  
  
            mqtt.setUserName("guest");// 服务器认证用户名
			mqtt.setPassword("guest");// 服务器认证密码
			mqtt.setWillRetain(true);// 若想要在发布“遗嘱”消息时拥有retain选项，则为true
            //获取mqtt的连接对象BlockingConnection  
            final CallbackConnection connection = mqtt.callbackConnection();  

            //添加连接的监听事件  
            connection.listener(new Listener() {  
                public void onDisconnected() {  
                }  
                public void onConnected() {  
                }  
                public void onPublish(UTF8Buffer topic, Buffer payload, Runnable ack) {  
                    // You can now process a received message from a topic.  
                    // Once process execute the ack runnable.  
                    ack.run();  
                    System.out.println("topic:"+topic.toString()+"="+new String(payload.getData()));  
                }  
                public void onFailure(Throwable value) {  
                }  
            });  
            //添加连接事件  
            connection.connect(new Callback<Void>() {  
                /** 
                 * 连接失败的操作 
                 */  
                public void onFailure(Throwable value) {  
                    // If we could not connect to the server.  
                    System.out.println("MQTTCallbackServer.CallbackConnection.connect.onFailure"+"连接失败......"+value.getMessage());  
                    value.printStackTrace();  
                }  
                /** 
                 * 连接成功的操作 
                 * @param v 
                 */  
                public void onSuccess(Void v) {  
                    
                    final Topic[] topic ={new Topic("tokuduaedb222fdf736c94",QoS.AT_LEAST_ONCE)}; //"tokudu/aedb222fdf736c94";  
                    connection.subscribe(topic, new Callback<byte[]>() {
						@Override
						public void onSuccess(byte[] arg0) {
							System.out.println("========订阅成功=======");
						}
						@Override
						public void onFailure(Throwable arg0) {
						}
					});
//                    connection.getDispatchQueue().execute(new Runnable() {  
//                        public void run() {  
//                            //在这里进行相应的订阅、发布、停止连接等等操作    
//                        }  
//                    }); 
                    try {  
                        Thread.sleep(2000);  
                    } catch (InterruptedException e) {  
                        e.printStackTrace();  
                    }  
                }
            });  
            while(true){}
        } catch (URISyntaxException e) {  
            e.printStackTrace();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }finally{  
        }  
    }  
}