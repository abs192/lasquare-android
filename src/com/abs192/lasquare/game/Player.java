package com.abs192.lasquare.game;

import java.io.Serializable;

public class Player implements Serializable {

	private static final long serialVersionUID = -7077991686201736906L;
	int id, color;
	float x, y, width, height;
	String username, sid, room;

	public Player(int id, String sid, String username, String room) {
		super();
		this.id = id;
		this.username = username;
		this.sid = sid;
		this.room = room;
	}

	public Player(int id, String sid, float x, float y, float width,
			float height, int color, String username, String room) {
		this.id = id;
		this.sid = sid;
		this.color = color;
		this.width = width;
		this.height = height;
		this.username = username;
		this.room = room;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

}
