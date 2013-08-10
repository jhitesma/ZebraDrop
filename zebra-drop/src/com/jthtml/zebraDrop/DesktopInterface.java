package com.jthtml.zebraDrop;

public class DesktopInterface implements GoogleInterface{
	 
    @Override
    public void Login() {
            System.out.println("Desktop: would of logged in here");
    }

    @Override
    public void LogOut() {
            System.out.println("Desktop: would of logged out here");
    }

    @Override
    public boolean getSignedIn() {
            System.out.println("Desktop: getSignIn()");
            return false;
    }
   
    public void submitScore(int score){
            System.out.println("Desktop: submitScore: " +score);
    }

    @Override
    public void getScores() {
            System.out.println("Desktop: getScores()");
    }

    @Override
    public void getScoresData() {
            System.out.println("Desktop: getScoresData()");
    }

	@Override
	public void unlockAchievement(String achievementId) {
			System.out.println("Desktop: unlockAchievement(" + achievementId + ")");
	}

	@Override
	public void incrementAchievement(String achievementId, int incBy) {
		System.out.println("Desktop: incrementAchievement(" + achievementId + "," + incBy + ")");		
	}

	@Override
	public void getAchievements() {
		System.out.println("Desktop: getAchievements()");
	}

	@Override
	public void getLevels() {
		System.out.println("Desktop: getLevels()");
	}

	@Override
	public void submitLevel(int level) {
		System.out.println("Desktop: submitLevel");
		
	}

}