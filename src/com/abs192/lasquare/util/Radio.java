package com.abs192.lasquare.util;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.abs192.lasquare.game.GameListener;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

public class Radio {

	private static Radio instance;
	public static boolean connected;
	private Socket socket;

	private Radio() {
	}

	public void get(Context context, String url, Listener<String> rl,
			ErrorListener el) {

		if (ConnectionUpdateReceiver.checkConnection(context)) {

			RequestQueue queue = Volley.newRequestQueue(context);

			StringRequest jsObjRequest = new StringRequest(Request.Method.GET,
					url, rl, el);
			// Add the request to the RequestQueue.
			queue.add(jsObjRequest);
		}

	}

	public void postRoom(Context context, Listener<String> rl,
			ErrorListener el, final String... parameters) {
		if (ConnectionUpdateReceiver.checkConnection(context)) {

			RequestQueue queue = Volley.newRequestQueue(context);

			StringRequest jsObjRequest = new StringRequest(Request.Method.POST,
					URLStore.POST_URL, rl, el) {
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					Map<String, String> params = new HashMap<String, String>();
					params.put("uname", parameters[0]);
					params.put("roomId", parameters[1]);
					return params;
				}
			};

			// Add the request to the RequestQueue.
			queue.add(jsObjRequest);
		}
	}

	public static Radio getInstance() {
		if (instance == null)
			instance = new Radio();
		return instance;
	}

	public void connect(final GameListener gl) {
		try {
			socket = IO.socket("http://lasquare.herokuapp.com");
			socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

				@Override
				public void call(Object... args) {
					System.out.println("Connection established");
				}

			}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

				@Override
				public void call(Object... args) {
					System.out.println("Connection dsiconnected");
				}

			}).on(Socket.EVENT_ERROR, new Emitter.Listener() {

				@Override
				public void call(Object... args) {

					System.out.println("SocketEvent error");
				}

			}).on("status", new Emitter.Listener() {

				@Override
				public void call(Object... args) {
					JSONObject obj = (JSONObject) args[0];
					if (obj != null) {
						gl.onStatus(obj);
					}
				}

			}).on("drawDrag", new Emitter.Listener() {

				@Override
				public void call(Object... args) {
					JSONObject obj = (JSONObject) args[0];
					if (obj != null) {
						// dl.drawDrag(obj);
					}
				}

			});
			socket.connect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void emit(String event, JSONObject arg) {

		if (socket != null) {
			socket.emit(event, arg);
		} else {
			System.out.println("socket null");
		}

	}

}
