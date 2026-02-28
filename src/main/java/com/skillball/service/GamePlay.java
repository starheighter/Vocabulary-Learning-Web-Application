package com.skillball.service;

import com.skillball.entity.Game;
import com.skillball.entity.User;
import com.skillball.entity.Vocab;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GamePlay {
    @Autowired
    private UserService userService;
    @Autowired
    private GameService gameService;
    @Autowired
    private VocabService vocabService;

    private long gameClock;
    public final double THRESHOLD = 3.0;

    public void delete() {
        Game game = gameService.getCurrentGame();
        gameService.deleteGame(game);
        gameService.setCurrentGame(null);
    }

    public void touchdown() {
        Game game = gameService.getCurrentGame();
        game.setTouchdown(false);
        if (game.isHomePossession()) {
            game.setScoreHome(game.getScoreHome() + 6);
        } else {
            game.setScoreGuest(game.getScoreGuest() + 6);
            if (game.getScoreHome() > game.getScoreGuest()
                    && (game.getScoreHome() - game.getScoreGuest() - 2) % 8 == 0) {
                game.setTwoPoint(true);
                game.setYard(98);
            } else {
                game.setExtraPoint(true);
                game.setYard(85);
            }
        }
    }

    public void extraPointTwoPointFieldGoal() {
        Game game = gameService.getCurrentGame();
        if (game.isExtraPoint()) {
            game.setMove("Extra Point");
        } else if (game.isTwoPoint()) {
            game.setMove("Two Point Attempt");
        } else {
            game.setMove("Field Goal Attempt");
        }
    }

    public void kickoffPunt() {
        Game game = gameService.getCurrentGame();
        game.setYard(20);
        game.setDown(1);
        game.setYellow(10);
        if (game.isKickoff()) {
            game.setKickoff(false);
        } else {
            game.setPunt(false);
            game.setHomePossession(!game.isHomePossession());
        }
    }

    public void endOfQuarter(int durationQuarter) {
        Game game = gameService.getCurrentGame();
        game.setQuarter(game.getQuarter() + 1);
        game.setTime(durationQuarter);
        if (game.getQuarter() == 3) {
            game.setKickoff(true);
            game.setYard(65);
            game.setHomePossession(!game.isHomeStarted());
        } else if (game.getQuarter() == 5) {
            delete();
        }
    }

    public void adaptMove(String move) {
        Game game = gameService.getCurrentGame();
        if (move.equals("Extra Point")) {
            game.setExtraPoint(true);
            game.setYard(85);
        } else if (move.equals("Two Point Attempt")) {
            game.setTwoPoint(true);
            game.setYard(98);
        }
        game.setFieldGoal(move.equals("Field Goal Attempt"));
        game.setPunt(move.equals("Punt"));
    }

    public Vocab chooseVocab() {
        if (vocabService.getCurrentVocabList(false) == null || vocabService.getCurrentVocabList(false).isEmpty()) {
            User user = userService.getCurrentUser();
            vocabService.updateCurrentVocabList(user.getLanguage(), user.getLevel(), user.getIndex(), false);
        }
        int index = (int) (Math.random() * vocabService.getCurrentVocabList(false).size())
                % vocabService.getCurrentVocabList(false).size();
        vocabService.setCurrentVocab(vocabService.getCurrentVocabList(false).remove(index));
        activateGameClock();
        return vocabService.getCurrentVocab();
    }

    public String progress(double responseTime) {
        Game game = gameService.getCurrentGame();
        int progressYard = 0;
        boolean inTime = game.isHomePossession() && responseTime < THRESHOLD
                || !game.isHomePossession() && responseTime > THRESHOLD;
        boolean sack = game.isHomePossession() && responseTime > THRESHOLD * 2.0
                || !game.isHomePossession() && responseTime < THRESHOLD / 2.0;
        boolean farToQuick = responseTime < THRESHOLD / 3.0;
        boolean farToSlow = responseTime > THRESHOLD * 3.0;
        boolean runThrough = game.isHomePossession() && farToQuick || !game.isHomePossession() && farToSlow;
        boolean turnover = game.isHomePossession() && farToSlow || !game.isHomePossession() && farToQuick;
        if (turnover) {
            if (farToQuick) {
                progressYard = (int) Math.min(((5.0 - THRESHOLD / responseTime) * 5.0), 75 - game.getYard());
            } else {
                progressYard = (int) Math.min(((5.0 - responseTime / THRESHOLD) * 5.0), 75 - game.getYard());
            }
        }
        String comment = null;
        switch (game.getMove()) {
            case "Run":
                if (inTime) {
                    if (game.isHomePossession()) {
                        progressYard = (int) (10.0 - (5.0 * responseTime));
                    } else {
                        progressYard = (int) ((responseTime - THRESHOLD) * 2.5);
                    }
                }
                comment = excecuteProgress(progressYard, sack, runThrough, turnover);
                break;
            case "Pass":
                if (inTime) {
                    if (game.isHomePossession()) {
                        progressYard = (int) (30.0 - (10.0 * responseTime));
                    } else {
                        progressYard = (int) ((responseTime - THRESHOLD) * 5.0 + 10.0);
                    }
                }
                comment = excecuteProgress(progressYard, sack, runThrough, turnover);
                break;
            case "Long Pass":
                if (inTime) {
                    if (game.isHomePossession()) {
                        progressYard = (int) (50.0 - (15.0 * responseTime));
                    } else {
                        progressYard = (int) ((responseTime - THRESHOLD) * 7.5 + 20.0);
                    }
                }
                comment = excecuteProgress(progressYard, sack, runThrough, turnover);
                break;
            case "Extra Point":
                if (inTime) {
                    if (game.isHomePossession()) {
                        game.setScoreHome(game.getScoreHome() + 1);
                    } else {
                        game.setScoreGuest(game.getScoreGuest() + 1);
                    }
                    comment = "Extra Point is good!";
                }
                game.setExtraPoint(false);
                game.setKickoff(true);
                game.setYard(65);
                game.setHomePossession(!game.isHomePossession());
                break;
            case "Two Point Attempt":
                if (inTime) {
                    if (game.isHomePossession()) {
                        game.setScoreHome(game.getScoreHome() + 2);
                    } else {
                        game.setScoreGuest(game.getScoreGuest() + 2);
                    }
                    comment = "Two Point Attempt is good!";
                }
                game.setTwoPoint(false);
                game.setKickoff(true);
                game.setYard(65);
                game.setHomePossession(!game.isHomePossession());
                break;
            case "Field Goal Attempt":
                if (inTime) {
                    if (game.isHomePossession()) {
                        game.setScoreHome(game.getScoreHome() + 3);
                    } else {
                        game.setScoreGuest(game.getScoreGuest() + 3);
                    }
                    game.setKickoff(true);
                    game.setYard(65);
                    comment = "Kick is good!";
                } else {
                    game.setYard(100 - game.getYard());
                    game.setDown(1);
                    game.setYellow(10);
                    comment = "Kick is no good!";
                }
                game.setFieldGoal(false);
                game.setHomePossession(!game.isHomePossession());
                break;
        }
        game.setTime(Math.max(game.getTime() - (int) (8 + Math.random() * 5), 0));
        gameService.saveGame(game);
        return comment;
    }

    private String excecuteProgress(int progressYard, boolean sack, boolean runThrough, boolean turnover) {
        Game game = gameService.getCurrentGame();
        if (sack && !turnover) {
            progressYard = (int) (Math.random() * -6);
        }
        if (runThrough) {
            progressYard *= 5;
        }
        if (game.getYard() + progressYard > 110) {
            progressYard = 105 - game.getYard();
        }
        if (game.getYard() + progressYard < -10) {
            progressYard = -5 - game.getYard();
        }
        String comment = progressYard + " yards Progress!";
        game.setYard(game.getYard() + progressYard);
        game.setDown(game.getDown() + 1);
        game.setYellow(game.getYellow() - progressYard);
        if (sack) {
            comment = "Sack!";
        }
        if (turnover) {
            if (Math.random() > 0.5) {
                comment = "Fumble!";
            } else {
                comment = "Intercepted!";
            }
        }
        if (game.getYellow() <= 0 || game.getYard() >= 100 || game.getYard() <= 0) {
            if (game.getYard() >= 100 || turnover && game.getYard() <= 0) {
                game.setTouchdown(true);
            } else if (game.getYard() <= 0) {
                game.setSafety(true);
            }
            game.setDown(1);
            game.setYellow(10);
        }
        if (game.getDown() > 4 || turnover) {
            game.setYard(100 - game.getYard());
            game.setDown(1);
            game.setYellow(10);
            game.setHomePossession(!game.isHomePossession());
        }
        return comment;
    }

    private double adaptResponseTime(double responseTime) {
        User user = userService.getCurrentUser();
        Game game = gameService.getCurrentGame();
        if (game.isHomePossession()) {
            switch (game.getMove()) {
                case "Extra Point":
                    return responseTime / 2.0;
                case "Two Point Attempt":
                    return responseTime;
                case "Field Goal Attempt":
                    return responseTime + ((100 - game.getYard()) - 35) / 10.0 * THRESHOLD;
                case "Run":
                    responseTime = responseTime / 2.0;
                    break;
                case "Pass":
                    responseTime = responseTime;
                    break;
                case "Long Pass":
                    responseTime = responseTime * 1.5;
                    break;
            }
        } else {
            switch (game.getMove()) {
                case "Extra Point":
                    return responseTime * 2.0;
                case "Two Point Attempt":
                    return responseTime;
                case "Field Goal Attempt":
                    return Math.min(responseTime + (35 - (100 - game.getYard())) / 10.0 * THRESHOLD,
                            responseTime + 1.5);
                case "Run":
                    responseTime = responseTime * 1.5;
                    break;
                case "Pass":
                    responseTime = responseTime;
                    break;
                case "Long Pass":
                    responseTime = responseTime / 2.0;
                    break;
            }
        }
        switch (user.getDifficulty()) {
            case "Very Easy":
                return responseTime / 2.0;
            case "Easy":
                return responseTime / 1.5;
            case "Normal":
                return responseTime;
            case "Hard":
                return responseTime * 1.5;
            case "Very Hard":
                return responseTime * 2.0;
        }
        return 0;
    }

    public void safety() {
        Game game = gameService.getCurrentGame();
        if (game.isHomePossession()) {
            game.setScoreHome(game.getScoreGuest() + 2);
        } else {
            game.setScoreGuest(game.getScoreHome() + 2);
        }
        game.setSafety(false);
        game.setKickoff(true);
        game.setYard(65);
        game.setHomePossession(!game.isHomePossession());
    }

    public void activateGameClock() {
        this.gameClock = System.currentTimeMillis();
    }

    public double getResponseTime() {
        return adaptResponseTime((System.currentTimeMillis() - gameClock) / 1000.0);
    }
}