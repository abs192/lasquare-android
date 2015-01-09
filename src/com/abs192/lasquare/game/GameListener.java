package com.abs192.lasquare.game;

import org.json.JSONObject;

public interface GameListener {

	public void onMove(JSONObject obj);

	public void onJoin(JSONObject obj);

	public void onMyId(JSONObject obj);

	public void onStatus(JSONObject obj);

	public void onKilled(JSONObject obj); 

	public void onAllPlayers(JSONObject obj);

	public void onMyInfo(JSONObject obj);

	public void onGameOver(JSONObject obj);

}
