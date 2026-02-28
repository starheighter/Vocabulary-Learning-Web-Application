package com.skillball.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;

@Entity
public class Game {
    @Id
    @GeneratedValue
    private Integer gameId;
    @ManyToOne
    private User user;
    private Timestamp timeStamp;
    private String guest;
    private String home;
    private int quarter;
    private int time;
    private int scoreGuest;
    private int scoreHome;
    private boolean kickoff;
    private boolean touchdown;
    private boolean safety;
    private boolean fieldGoal;
    private boolean extraPoint;
    private boolean twoPoint;
    private boolean punt;
    private boolean homePossession;
    private int yard;
    private int down;
    private int yellow;
    private boolean homeStarted;
    private String move;

    public Integer getGameId() {
        return gameId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp() {
        this.timeStamp = new Timestamp(System.currentTimeMillis());
    }

    public String getGuest() {
        return guest;
    }

    public void setGuest(String guest) {
        this.guest = guest;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public int getQuarter() {
        return quarter;
    }

    public void setQuarter(int quarter) {
        this.quarter = quarter;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getScoreGuest() {
        return scoreGuest;
    }

    public void setScoreGuest(int scoreGuest) {
        this.scoreGuest = scoreGuest;
    }

    public int getScoreHome() {
        return scoreHome;
    }

    public void setScoreHome(int scoreHome) {
        this.scoreHome = scoreHome;
    }

    public boolean isKickoff() {
        return kickoff;
    }

    public void setKickoff(boolean kickoff) {
        this.kickoff = kickoff;
    }

    public boolean isTouchdown() {
        return touchdown;
    }

    public void setTouchdown(boolean touchdown) {
        this.touchdown = touchdown;
    }

    public boolean isSafety() {
        return safety;
    }

    public void setSafety(boolean safety) {
        this.safety = safety;
    }

    public boolean isFieldGoal() {
        return fieldGoal;
    }

    public void setFieldGoal(boolean fielGoal) {
        this.fieldGoal = fielGoal;
    }

    public boolean isExtraPoint() {
        return extraPoint;
    }

    public void setExtraPoint(boolean extraPoint) {
        this.extraPoint = extraPoint;
    }

    public boolean isTwoPoint() {
        return twoPoint;
    }

    public void setTwoPoint(boolean twoPoint) {
        this.twoPoint = twoPoint;
    }

    public boolean isPunt() {
        return punt;
    }

    public void setPunt(boolean punt) {
        this.punt = punt;
    }

    public boolean isHomePossession() {
        return homePossession;
    }

    public void setHomePossession(boolean homePossession) {
        this.homePossession = homePossession;
    }

    public int getYard() {
        return yard;
    }

    public void setYard(int yard) {
        this.yard = yard;
    }

    public int getDown() {
        return down;
    }

    public void setDown(int down) {
        this.down = down;
    }

    public int getYellow() {
        return yellow;
    }

    public void setYellow(int yellow) {
        this.yellow = yellow;
    }

    public boolean isHomeStarted() {
        return homeStarted;
    }

    public void setHomeStarted(boolean homeStarted) {
        this.homeStarted = homeStarted;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public String getGameInfo() {
        if (homePossession) {
            return guest + " " + scoreGuest + ":" + scoreHome + "- " + home + " | " + timeStamp;
        } else {
            return guest + " -" + scoreGuest + ":" + scoreHome + " " + home + " | " + timeStamp;
        }
    }

    public String getTopGameInfo() {
        if (homePossession) {
            return guest + " " + scoreGuest + ":" + scoreHome + "  - " + home;
        } else {
            return guest + " -  " + scoreGuest + ":" + scoreHome + " " + home;
        }
    }

    public String getBottomGameInfo() {
        String addition;
        if (kickoff) {
            addition = "KICKOFF";
        } else if (touchdown) {
            addition = "TOUCHDOWN";
        } else if (fieldGoal) {
            addition = "FIELD GOAL ATTEMPT";
        } else if (extraPoint) {
            addition = "EXTRA POINT";
        } else if (twoPoint) {
            addition = "TWO POINT ATTEMPT";
        } else if (punt) {
            addition = "PUNTING";
        } else if (safety) {
            addition = "SAFETY";
        } else {
            if (yard + yellow >= 100) {
                addition = down + getEnding(down) + " & " + "GOAL";
            } else {
                addition = down + getEnding(down) + " & " + yellow;
            }
        }
        return getQuarterTime() + " | " + addition;
    }

    private String getQuarterTime() {
        if (time == 0) {
            if (quarter == 1 || quarter == 3) {
                return "End of " + quarter + getEnding(quarter) + " quarter";
            } else if (quarter == 2) {
                return "Halftime";
            } else {
                return "Final";
            }
        }
        int minute = time / 60;
        int second = time % 60;
        if (second < 10) {
            return quarter + getEnding(quarter) + " | " + minute + ":0" + second;
        } else {
            return quarter + getEnding(quarter) + " | " + minute + ":" + second;
        }
    }

    private String getEnding(int number) {
        switch (number) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            case 4:
                return "th";
        }
        return null;
    }

    public String getPicturePath() {
        if (homePossession) {
            return "/images/" + ((int) (Math.round(yard / 5.0))) * 5 + ".jpg";
        } else {
            return "/images/" + ((int) (Math.round((100 - yard) / 5.0))) * 5 + ".jpg";
        }
    }
}