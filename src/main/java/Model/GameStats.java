package Model;

public class GameStats {
    public int year;
    public int week;

    //Special Teams
    public int returnYards;
    public int returnTDs;

    //Kicking
    public int extraPointAttempts;
    public int extraPointMakes;
    public int fieldGoalAttempts;
    public int fieldGoalMakes;
    public int fieldGoalYards;

    //Passing
    public int passingAttempts;
    public int passingCompletions;
    public int passingTDs;
    public int passingYards;

    //Receiving
    public int receptions;
    public int receivingYards;
    public int receivingTDs;

    //Rushing
    public int rushingAttempts;
    public int rushingYards;
    public int rushingTDs;

    //Turnovers
    public int fumbles;
    public int interceptions;

    public GameStats(int year, int week) {
        this.year = year;
        this.week = week;
    }

    public GameStats() {

    }

    public void updateXPStats(String stat) {
        this.extraPointMakes = Integer.parseInt(stat.substring(0, stat.indexOf("/")));
        this.extraPointAttempts = Integer.parseInt(stat.substring(stat.indexOf("/") + 1));
    }

    public void updateFGStats(String stat) {
        this.fieldGoalMakes = Integer.parseInt(stat.substring(0, stat.indexOf("/")));
        this.fieldGoalAttempts = Integer.parseInt(stat.substring(stat.indexOf("/") + 1));
    }
}
