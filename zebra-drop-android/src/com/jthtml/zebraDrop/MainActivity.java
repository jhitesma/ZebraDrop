package com.jthtml.zebraDrop;

import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.games.leaderboard.LeaderboardBuffer;
import com.google.android.gms.games.leaderboard.LeaderboardScoreBuffer;
import com.google.android.gms.games.leaderboard.OnLeaderboardScoresLoadedListener;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;


public class MainActivity extends AndroidApplication implements GameHelperListener, GoogleInterface {

	private GameHelper aHelper;
    private OnLeaderboardScoresLoadedListener theLeaderboardListener;

    public MainActivity() {
    	aHelper = new GameHelper(this);
    	aHelper.enableDebugLog(true, "MYTAG");
   
    	
    	//create a listener for getting raw data back from leaderboard
    	theLeaderboardListener = new OnLeaderboardScoresLoadedListener() {               
    		@Override
			public void onLeaderboardScoresLoaded(int arg0, LeaderboardBuffer arg1, LeaderboardScoreBuffer arg2) {
    			System.out.println("In call back");
    			for(int i = 0; i < arg2.getCount(); i++){
    				System.out.println(arg2.get(i).getScoreHolderDisplayName() + " : " + arg2.get(i).getDisplayScore());
    			}
    		}
    	};
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   
        aHelper.enableDebugLog(true, "zebraLog");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();

        // Disable hardware functions to save battery power.      
        cfg.useAccelerometer = false;
        cfg.useCompass = false;
        
        cfg.useGL20 = false;
        aHelper.setup(this);
        //initialize(new Game(this), cfg);        
        initialize(new ZebraDrop(this), cfg);
    }
    
    @Override
    public void onStart(){
    	super.onStart();
    	aHelper.onStart(this);
    }
   
    @Override
    public void onStop(){
    	super.onStop();
    	aHelper.onStop();
    }
   
    @Override
    public void onActivityResult(int request, int response, Intent data) {
    	super.onActivityResult(request, response, data);
    	aHelper.onActivityResult(request, response, data);
    }

	@Override
	public void onSignInFailed() {
		System.out.println("sign in failed");
	}
 
	@Override
	public void onSignInSucceeded() {
		System.out.println("sign in succeeded");		
	}
 
	@Override
	public void Login() {
		try {
			runOnUiThread(new Runnable(){
				//@Override
				@Override
				public void run(){
					aHelper.beginUserInitiatedSignIn();
				}
			});
        } 
		catch (final Exception ex){}
	}
 
	@Override
	public void LogOut() {
		System.out.println("Logging Out");
		try {
			runOnUiThread(new Runnable(){
				//@Override
				@Override
				public void run(){
					aHelper.signOut();
				}
			});
        }
		catch (final Exception ex){}               
	}
 
	@Override
	public boolean getSignedIn() {
		return aHelper.isSignedIn();
	}
 
	@Override
	public void submitScore(int _score) {
		System.out.println("in submit score");
		aHelper.getGamesClient().submitScore(getString(R.string.leaderboard_score), _score);       
	}
 
	@Override
	public void submitLevel(int _level) {
		System.out.println("in submit level");
		aHelper.getGamesClient().submitScore(getString(R.string.leaderboard_level), _level);       
	}

	@Override
	public void getScores() {
		startActivityForResult(aHelper.getGamesClient().getLeaderboardIntent(getString(R.string.leaderboard_score)), 105); 
	}
 
	@Override
	public void getScoresData() {
		aHelper.getGamesClient().loadPlayerCenteredScores(theLeaderboardListener,getString(R.string.leaderboard_score),1,1,25);
	}

	@Override
	public void unlockAchievement(String achievementId) {
		aHelper.getGamesClient().unlockAchievement(achievementId);
	}

	@Override
	public void incrementAchievement(String achievementId, int incBy) {
		aHelper.getGamesClient().incrementAchievement(achievementId, incBy);
	}

	@Override
	public void getAchievements() {
		startActivityForResult(aHelper.getGamesClient().getAchievementsIntent(), 13);
	}

	@Override
	public void getLevels() {
		startActivityForResult(aHelper.getGamesClient().getLeaderboardIntent(getString(R.string.leaderboard_level)), 105); 		
	}
}
