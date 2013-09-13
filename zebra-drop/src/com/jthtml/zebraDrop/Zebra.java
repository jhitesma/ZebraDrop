package com.jthtml.zebraDrop;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Sine;


import com.badlogic.gdx.math.MathUtils;
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
	int rotation;
	
	public Zebra() {
		this.bounds.x = -10;
		this.bounds.y = -10;
		this.bounds.height = HEIGHT;
		this.bounds.width = WIDTH;
		this.stateTime = 0f;  	
		this.rotation = MathUtils.random(-60,60);
//		Tween.call(zebraCallback).start(game.tweenManager);
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

    private final TweenCallback zebraCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) { 
            Tween.to(this, ZebraAccessor.ROTATION, MathUtils.random(500,1500))
                .target(MathUtils.random(-60,60))
                .ease(Sine.INOUT)
                .repeatYoyo(-1, 0)
                .setCallback(zebraCallback)
                .start(game.tweenManager);
        }
    };

}