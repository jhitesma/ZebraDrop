package com.jthtml.zebraDrop;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Zebra {

	public static final int WIDTH = 52; 
	public static final int HEIGHT = 42;

	ZebraDropGame game;

	Vector2	position = new Vector2();
	Rectangle bounds = new Rectangle();
	boolean	facingLeft = true;
	float stateTime = 0;

	public Zebra(Vector2 position, ZebraDropGame gam) {
		game = gam;
		this.position = position;
		this.bounds.x = position.x;
		this.bounds.y = position.y;
		this.bounds.height = HEIGHT;
		this.bounds.width = WIDTH;
		this.stateTime = 0f;  	
	}
	
	public boolean isFacingLeft() {
		return facingLeft;
	}

	public void setFacingLeft(boolean facingLeft) {
		this.facingLeft = facingLeft;
	}

	public Vector2 getPosition() {
		return position;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public float getStateTime() {
		return stateTime;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
		this.bounds.setX(position.x);
		this.bounds.setY(position.y);
	}	

	public void setStateTime(float stateTime) {
		this.stateTime = stateTime;
	}

	public void update(float delta) {
		stateTime += delta;
	}
}