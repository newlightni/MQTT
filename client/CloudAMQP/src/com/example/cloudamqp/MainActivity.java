package com.example.cloudamqp;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {
	

	private final static String CONNECTION_STRING = "tcp://192.168.103.233:1883";
	private final static boolean CLEAN_START = true;
	private final static short KEEP_ALIVE = 30;

	public static Topic[] topics = { new Topic("mqtt/ddd", QoS.AT_LEAST_ONCE) };

	public final static long RECONNECTION_ATTEMPT_MAX = 6;
	public final static long RECONNECTION_DELAY = 2000;

	public final static int SEND_BUFFER_SIZE = 2 * 1024 * 1024;
	// 线程回掉
	Handler handler = new Handler();
	class updateUIThread implements Runnable {
		private String msg;

		public updateUIThread(String msg) {
			this.msg = msg;
		}

		@Override
		public void run() {
			// textView.setText(msg);
			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			builder.setMessage(msg);
			builder.setTitle("消息提示");
			builder.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			builder.create().show();
		}

	}

	class msgRecv implements Runnable {

		@Override
		public void run() {
			try {
				MQTT mqtt = new MQTT();
				mqtt.setHost(CONNECTION_STRING);
				mqtt.setCleanSession(CLEAN_START);
				mqtt.setReconnectAttemptsMax(RECONNECTION_ATTEMPT_MAX);
				mqtt.setReconnectDelay(RECONNECTION_DELAY);
				mqtt.setKeepAlive(KEEP_ALIVE);
				mqtt.setSendBufferSize(SEND_BUFFER_SIZE);
				
				//获取android唯一的标识
				String CLIENT_ID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
				mqtt.setClientId(CLIENT_ID);
				mqtt.setUserName("guest");
				mqtt.setPassword("guest");

				final BlockingConnection connection = mqtt.blockingConnection();
				connection.connect();
				connection.subscribe(topics);
				Message msg = handler.obtainMessage();
				
				long start=System.currentTimeMillis();
				while (true) {
					org.fusesource.mqtt.client.Message message = connection.receive();
					
					System.out.println("MQTTFutureClient接收消息 "
							+ "Topic Title :" + message.getTopic()
							+ " context :"
							+ new String(message.getPayloadBuffer().getData(),"UTF-8"));
					msg.obj = "MQTTFutureClient接收消息--> " + "Topic Title :"
							+ message.getTopic() + " context :"
							+ new String(message.getPayloadBuffer().getData(),"UTF-8");
					
					message.ack();
					updateUIThread t=new updateUIThread(msg.toString());
					handler.post(t);
					
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
		}
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// new Thread(newThread).start();
		new Thread(new msgRecv()).start();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
