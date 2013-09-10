package com.jthtml.zebraDrop;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.math.Vector2;

public class ZebraAccessor implements TweenAccessor<Zebra>{
	 // The following lines define the different possible tween types.
    // It's up to you to define what you need :-)

    public static final int POSITION_X = 1;
    public static final int POSITION_Y = 2;
    public static final int POSITION_XY = 3;
    public static final int ROTATION = 4;

    // TweenAccessor implementation

    @Override
    public int getValues(Zebra target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION_X: returnValues[0] = target.position.x; return 1;
            case POSITION_Y: returnValues[0] = target.position.y; return 1;
            case POSITION_XY:
                returnValues[0] = target.position.x;
                returnValues[1] = target.position.y;
                return 2;
            case ROTATION:
            	returnValues[0] = target.rotation;
            	return 1;
            default: assert false; return -1;
        }
    }
    
    @Override
    public void setValues(Zebra target, int tweenType, float[] newValues) {
    	Vector2 newpos = target.position;
    	switch (tweenType) {
            case POSITION_X: 
            	newpos.x = newValues[0];
            	newpos.y = target.position.y;
            	target.setPosition(newpos); 
            	break;
            case POSITION_Y: 
            	newpos.x = target.position.x;
            	newpos.y = newValues[1];
            	target.setPosition(newpos); 
            	break;
            case POSITION_XY:
            	newpos.x = newValues[0];
            	newpos.y = newValues[1];
            	target.setPosition(newpos);
                break;
            case ROTATION:
            	target.rotation = (int) newValues[0];
            	break;
            default: assert false; break;
        }
    }
}
