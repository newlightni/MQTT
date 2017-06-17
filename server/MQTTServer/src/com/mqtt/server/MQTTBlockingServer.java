package com.mqtt.server;

import java.net.URISyntaxException;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

/**
 * 采用Future模式 发布主题
 *
 */
public class MQTTFutureServer {
	private final static String CONNECTION_STRING = "tcp://192.168.103.233:1883";
	private final static boolean CLEAN_START = true;
	private final static String CLIENT_ID = "server";
	private final static short KEEP_ALIVE = 30;// 低耗网络，但是又需要及时获取数据，心跳30s

	public static Topic[] topics = { new Topic("mqtt/aaa", QoS.EXACTLY_ONCE),
			new Topic("mqtt/bbb", QoS.AT_MOST_ONCE),
			new Topic("mqtt/ccc", QoS.AT_LEAST_ONCE) };	

	public final static long RECONNECTION_ATTEMPT_MAX = 6;
	public final static long RECONNECTION_DELAY = 2000;

	public final static int SEND_BUFFER_SIZE = 2 * 1024 * 1024;// 发送最大缓冲为2M

	public static void main(String[] args) {
		MQTT mqtt = new MQTT();
		try {
			mqtt.setHost(CONNECTION_STRING);
			mqtt.setCleanSession(CLEAN_START);
			mqtt.setKeepAlive(KEEP_ALIVE);
			mqtt.setClientId(CLIENT_ID);
			mqtt.setReconnectAttemptsMax(RECONNECTION_ATTEMPT_MAX);
			mqtt.setReconnectDelay(RECONNECTION_DELAY);
			mqtt.setSendBufferSize(SEND_BUFFER_SIZE);
			mqtt.setTrafficClass(8);

			mqtt.setMaxReadRate(0);// 设置连接的最大接收速率，单位为bytes/s。默认为0，即无限制
			mqtt.setMaxWriteRate(0);// 设置连接的最d大发送速率，单位为bytes/s。默认为0，即无限制
			mqtt.setUserName("guest");
			mqtt.setPassword("guest");

			final BlockingConnection connection = mqtt.blockingConnection();
			connection.connect();
			int count = 1;
			String topic = "mqtt/ddd";
			for (int i = 0; i < 5; i++) {
				// message="Hello "+count+" MQTT  aa  "+i;
				String message = "newlightni" + i;
				connection.publish(topic, new String(message.getBytes(),
						"UTF-8").getBytes(), QoS.AT_LEAST_ONCE, false);
				System.out.println("MQTTFutureServer.publish Message "
						+ "Topic Title :" + topic + " context :" + message);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			
		}
	}
}
















