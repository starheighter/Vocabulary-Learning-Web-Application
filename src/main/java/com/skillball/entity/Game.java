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
    private boolean homePossession;
    private int yard;
    private int down;
    private int yellow;
    private boolean homeStarted;
    private String move;
    private Status status;
    private String comment;

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getGameInfo() {
        if (homePossession) {
            return guest + " " + scoreGuest + ":" + scoreHome + " - " + home + " | " + timeStamp;
        } else {
            return guest + " - " + scoreGuest + ":" + scoreHome + " " + home + " | " + timeStamp;
        }
    }

    public String getTopGameInfo() {
        if (homePossession) {
            return guest + " " + scoreGuest + ":" + scoreHome + " - " + home;
        } else {
            return guest + " - " + scoreGuest + ":" + scoreHome + " " + home;
        }
    }

    public String getBottomGameInfo() {
        String addition;
        switch (status) {
            case KICKOFF:
                addition = "KICKOFF";
                break;
            case TOUCHDOWN:
                addition = "TOUCHDOWN";
                break;
            case FIELDGOAL:
                addition = "FIELD GOAL ATTEMPT";
                break;
            case EXTRAPOINT:
                addition = "EXTRA POINT";
                break;
            case TWOPOINT:
                addition = "TWO POINT ATTEMPT";
                break;
            case PUNT:
                addition = "PUNTING";
                break;
            case SAFETY:
                addition = "SAFETY";
                break;
            default:
                if (yard + yellow >= 100) {
                    addition = down + getEnding(down) + " & " + "GOAL";
                } else {
                    addition = down + getEnding(down) + " & " + yellow;
                }
        }
        return getQuarterTime() + " | " + addition + " | " + (comment != null ? comment : "");
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