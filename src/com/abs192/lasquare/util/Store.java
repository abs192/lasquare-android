package com.abs192.lasquare.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.abs192.lasquare.game.Player;

public class Store {

	Context context;
	String ROOT;
	SharedPreferences sp;

	public Store(Context context) {
		this.context = context;
		sp = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public void setUsername(String uname) {
		Editor e = sp.edit();
		e.putString("uname", uname);
		e.commit();
	}

	public String getUsername() {
		return sp.getString("uname", "");
	}

	public String getRoomId() {
		return sp.getString("roomID", "");
	}

	public void setRoomId(String roomId) {
		Editor e = sp.edit();
		e.putString("roomID", roomId);
		e.commit();
	}

	public void setGameStatus(boolean b) {
		Editor e = sp.edit();
		e.putBoolean("gamestatus", b);
		e.commit();
	}

	public boolean getGameStatus() {
		return sp.getBoolean("gamestatus", false);
	}

	public void setPlayer(Player p) {
		overwriteObject(context, "player", p);
	}

	public Player getPlayer() {
		return (Player) readObject(context, "player");
	}

	private static void overwriteObject(Context context, String name, Object obj) {
		try {
			FileOutputStream fos = context.openFileOutput(name,
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.close();
		} catch (FileNotFoundException f) {
			Toast.makeText(context, "File Not Found Exception",
					Toast.LENGTH_SHORT).show();
			f.printStackTrace();

		} catch (IOException e) {
			Toast.makeText(context, "IO Exception", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

	}

	private static Object readObject(Context context, String name) {

		Object obj = null;
		FileInputStream fis;
		ObjectInputStream ois;
		try {
			fis = context.openFileInput(name);
			ois = new ObjectInputStream(fis);
			try {
				obj = ois.readObject();
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "";
		} catch (StreamCorruptedException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return obj;
	}

}
