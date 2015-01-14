package com.abs192.lasquare;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abs192.lasquare.game.GameListener;
import com.abs192.lasquare.game.Player;
import com.abs192.lasquare.util.ConnectionUpdateReceiver;
import com.abs192.lasquare.util.Radio;
import com.abs192.lasquare.util.Store;
import com.abs192.lasquare.util.URLStore;
import com.abs192.lasquare.util.Utilities;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class MainActivity extends Activity implements GameListener {

	private AlertDialog ad;
	private ConnectionUpdateReceiver cur;
	boolean isInternetConnected;
	TextView status;
	String uname, room;
	ProgressBar pg;
	GameCanvas game;
	private Player me;
	private ArrayList<Player> enemies;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		status = (TextView) findViewById(R.id.textInternet);
		pg = (ProgressBar) findViewById(R.id.progressBar);

		cur = new ConnectionUpdateReceiver(this);
		registerReceiver(cur, new IntentFilter(
				"android.net.conn.CONNECTIVITY_CHANGE"));
		isInternetConnected = ConnectionUpdateReceiver
				.checkConnection(getApplicationContext());
		if (!new Store(getApplicationContext()).getGameStatus()
				|| new Store(getApplicationContext()).getPlayer() == null)
			showDialog();
		else
			readyGame();
	}

	public void showDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		View e = factory.inflate(R.layout.dialog_room_enter, null);
		builder.setView(e);

		final EditText etU = (EditText) e.findViewById(R.id.etUser);
		final EditText etR = (EditText) e.findViewById(R.id.etRoom);
		String savedName = new Store(this).getUsername();
		etU.setText(savedName);
		String savedRoom = new Store(this).getRoomId();
		etR.setText(savedRoom);

		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// ...
				showDialog();
			}
		});

		Button b = (Button) e.findViewById(R.id.buttonCreateRoom);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				joinOrCreate(etU, etR, 1);
			}
		});

		ad = builder.create();
		ad.show();
	}

	protected void joinOrCreate(EditText etU, EditText etR, int i) {

		if (etU != null && etR != null) {
			room = etR.getText().toString();
			uname = etU.getText().toString();
			if (room == null || room.trim().equals("")) {
				Utilities.toast(this, "Room number field empty");
				return;
			}
			if (uname == null || uname.trim().equals("")) {
				Utilities.toast(this, "Username field empty");
				return;
			}
			// all clear
			registerAndPost(uname, room);
		}
	}

	private void registerAndPost(final String uname, final String room) {

		// for incorporating direct links to rooms later. separating global and
		// local values

		if (ConnectionUpdateReceiver.checkConnection(getApplicationContext())) {
			if (ad != null)
				ad.dismiss();

			pg.setVisibility(View.VISIBLE);
			String url = URLStore.USERCHECK_URL + "?username=" + uname
					+ "&room=" + room;
			Radio.getInstance().get(getApplicationContext(), url,
					new Listener<String>() {

						@Override
						public void onResponse(String response) {

							if (response.equals("1")) {

								Utilities.toast(MainActivity.this,
										"Joining room...");
								Radio.getInstance().postRoom(
										getApplicationContext(),
										new Listener<String>() {

											@Override
											public void onResponse(
													String response) {

												Utilities.toast(
														MainActivity.this,
														"Game on!");
												Store s = new Store(
														getApplicationContext());
												s.setRoomId(room);
												s.setUsername(uname);
												System.out.println(response);
												if (pg != null)
													pg.setVisibility(View.GONE);
												readyGame();
											}
										}, new ErrorListener() {

											@Override
											public void onErrorResponse(
													VolleyError error) {

												Utilities.toast(
														MainActivity.this,
														"Couldnt join room");
												System.out.println("error");
												System.out.println(error
														.toString());
												pg.setVisibility(View.GONE);
												showDialog();
											}
										}, uname, room);

							} else {

								pg.setVisibility(View.GONE);
								Utilities.toast(MainActivity.this,
										"User with same name already in room "
												+ room);
								showDialog();
							}
						}
					}, new ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							System.out.println("user check error");
							pg.setVisibility(View.GONE);
						}
					});

		} else {
			pg.setVisibility(View.GONE);
			Utilities.toast(this, "No internet connection");
		}

	}

	public void readyGame() {
		// game starts here
		new Store(getApplicationContext()).setGameStatus(true);
		game = (GameCanvas) findViewById(R.id.gamecanvas);
		Radio.getInstance().connect(this);
	}

	@Override
	public void onMove(JSONObject obj) {

	}

	@Override
	public void onJoin(JSONObject obj) {
		// enemy joins

	}

	@Override
	public void onMyId(JSONObject obj) {

	}

	@Override
	public void onStatus(JSONObject obj) {

		System.out.println("Status: " + obj);
		try {

			int id = Integer.parseInt(obj.getString("id"));
			String sid = obj.getString("sid");
			this.uname = obj.getString("username");
			this.room = obj.getString("room");

			me = new Player(id, sid, uname, room);
			draw(me);
			new Store(getApplicationContext()).setPlayer(me);
			enemies = new ArrayList<Player>();

			// now emit player by 'join'
			Radio.getInstance().emit("join", obj);
			// test^

		} catch (Exception e) {

			e.printStackTrace();
			Utilities.toast(this, "Error in game setup");
			finish();

		}
	}

	private void draw(Player p) {
		
	}

	@Override
	public void onKilled(JSONObject obj) {

	}

	@Override
	public void onAllPlayers(JSONObject obj) {

	}

	@Override
	public void onMyInfo(JSONObject obj) {

	}

	@Override
	public void onGameOver(JSONObject obj) {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		new Store(getApplicationContext()).setGameStatus(false);
		new Store(getApplicationContext()).setPlayer(null);
		unregisterReceiver(cur);
	}

	public void setConnectionUpdate(boolean isConnected) {
		this.isInternetConnected = isConnected;
		updateStatusBar(isConnected);
	}

	private void updateStatusBar(boolean b) {
		Animation anim;
		if (b) {
			anim = AnimationUtils.loadAnimation(getApplicationContext(),
					R.anim.navout);
			anim.setDuration(300);
			// fade out and hide
			status.setAnimation(anim);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					status.setVisibility(View.GONE);
				}
			}, 300);

		} else {

			anim = AnimationUtils.loadAnimation(getApplicationContext(),
					R.anim.navin);
			// fade in and show
			anim.setDuration(300);
			status.setAnimation(anim);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					status.setVisibility(View.VISIBLE);
				}
			}, 300);
		}

	}

}
