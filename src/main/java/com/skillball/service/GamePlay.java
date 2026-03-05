package com.skillball.service;

import com.skillball.entity.Game;
import com.skillball.entity.Status;
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

    private long playClock;
    public final double THRESHOLD = 3.0;

    public void delete() {
        Game game = gameService.getCurrentGame();
        gameService.deleteGame(game);
        gameService.setCurrentGame(null);
    }

    public void touchdown() {
        Game game = gameService.getCurrentGame();
        game.setStatus(Status.OTHER);
        if (game.isHomePossession()) {
            game.setScoreHome(game.getScoreHome() + 6);
        } else {
            game.setScoreGuest(game.getScoreGuest() + 6);
            if (game.getScoreHome() > game.getScoreGuest()
                    && (game.getScoreHome() - game.getScoreGuest() - 2) % 8 == 0) {
                game.setStatus(Status.TWOPOINT);
                game.setYard(98);
            } else {
                game.setStatus(Status.EXTRAPOINT);
                game.setYard(85);
            }
        }
    }

    public void extraPointTwoPointFieldGoal() {
        Game game = gameService.getCurrentGame();
        if (game.getStatus() == Status.EXTRAPOINT) {
            game.setMove("Extra Point");
        } else if (game.getStatus() == Status.TWOPOINT) {
            game.setMove("Two Point Attempt");
        } else {
            game.setMove("Field Goal Attempt");
        }
    }

    public void safety() {
        Game game = gameService.getCurrentGame();
        if (game.isHomePossession()) {
            game.setScoreGuest(game.getScoreGuest() + 2);
        } else {
            game.setScoreHome(game.getScoreHome() + 2);
        }
        game.setStatus(Status.KICKOFF);
        game.setYard(65);
        game.setHomePossession(!game.isHomePossession());
    }

    public void kickoffPunt() {
        Game game = gameService.getCurrentGame();
        game.setYard(20);
        game.setDown(1);
        game.setYellow(10);
        if (game.getStatus() != Status.KICKOFF) {
            game.setHomePossession(!game.isHomePossession());
        }
        game.setStatus(Status.OTHER);
    }

    public void endOfQuarter(int durationQuarter) {
        Game game = gameService.getCurrentGame();
        game.setQuarter(game.getQuarter() + 1);
        game.setTime(durationQuarter);
        if (game.getQuarter() == 3) {
            game.setStatus(Status.KICKOFF);
            game.setYard(65);
            game.setHomePossession(!game.isHomeStarted());
        } else if (game.getQuarter() == 5) {
            delete();
        }
    }

    public void adaptMove(String move) {
        Game game = gameService.getCurrentGame();
        if (move.equals("Extra Point")) {
            game.setStatus(Status.EXTRAPOINT);
            game.setYard(85);
        } else if (move.equals("Two Point Attempt")) {
            game.setStatus(Status.TWOPOINT);
            game.setYard(98);
        }
        if (move.equals("Field Goal Attempt")) {
            game.setStatus(Status.FIELDGOAL);
        }
        if (move.equals("Punt")) {
            game.setStatus(Status.PUNT);
        }
    }

    public Vocab chooseVocab() {
        if (vocabService.getCurrentVocabList(false) == null || vocabService.getCurrentVocabList(false).isEmpty()) {
            User user = userService.getCurrentUser();
            vocabService.updateCurrentVocabList(user.getLanguage(), user.getLevel(), user.getIndex(), false);
        }
        int index = (int) (Math.random() * vocabService.getCurrentVocabList(false).size())
                % vocabService.getCurrentVocabList(false).size();
        vocabService.setCurrentVocab(vocabService.getCurrentVocabList(false).remove(index));
        activatePlayClock();
        return vocabService.getCurrentVocab();
    }

    public String progress(double responseTime) {
        Game game = gameService.getCurrentGame();
        int progressYard = 0;
        boolean completed = game.isHomePossession() && responseTime < THRESHOLD
                || !game.isHomePossession() && responseTime > THRESHOLD;
        boolean sack = game.isHomePossession() && responseTime > THRESHOLD * 1.5
                || !game.isHomePossession() && responseTime < THRESHOLD / 1.5;
        String comment = null;
        switch (game.getMove()) {
            case "Run":
                if (game.isHomePossession()) {
                    progressYard = (int) (THRESHOLD - responseTime);
                } else {
                    progressYard = (int) (responseTime - THRESHOLD);
                }
                game.setComment(excecuteProgress(progressYard));
                break;
            case "Pass":
                if (completed) {
                    if (game.isHomePossession()) {
                        progressYard = (int) ((THRESHOLD - responseTime) * 10.0);
                    } else {
                        progressYard = (int) ((responseTime - THRESHOLD) * 10.0);
                    }
                } else if (sack) {
                    if (game.isHomePossession()) {
                        progressYard = (int) (THRESHOLD - responseTime);
                    } else {
                        progressYard = (int) (responseTime - THRESHOLD);
                    }
                } else {
                    progressYard = 0;
                }
                game.setComment(excecuteProgress(progressYard));
                break;
            case "Long Pass":
                if (completed) {
                    if (game.isHomePossession()) {
                        progressYard = (int) ((THRESHOLD - responseTime) * 30.0);
                    } else {
                        progressYard = (int) ((responseTime - THRESHOLD) * 30.0);
                    }
                } else if (sack) {
                    if (game.isHomePossession()) {
                        progressYard = (int) (THRESHOLD - responseTime);
                    } else {
                        progressYard = (int) (responseTime - THRESHOLD);
                    }
                } else {
                    progressYard = 0;
                }
                game.setComment(excecuteProgress(progressYard));
                break;
            case "Extra Point":
                if (completed) {
                    if (game.isHomePossession()) {
                        game.setScoreHome(game.getScoreHome() + 1);
                    } else {
                        game.setScoreGuest(game.getScoreGuest() + 1);
                    }
                    game.setComment("Extra Point is good!");
                } else {
                    game.setComment("Extra Point is no good!");
                }
                game.setStatus(Status.KICKOFF);
                game.setYard(65);
                game.setHomePossession(!game.isHomePossession());
                break;
            case "Two Point Attempt":
                if (completed) {
                    if (game.isHomePossession()) {
                        game.setScoreHome(game.getScoreHome() + 2);
                    } else {
                        game.setScoreGuest(game.getScoreGuest() + 2);
                    }
                    game.setComment("Two Point Attempt is good!");
                } else {
                    game.setComment("Two Point Attempt is no good!");
                }
                game.setStatus(Status.KICKOFF);
                game.setYard(65);
                game.setHomePossession(!game.isHomePossession());
                break;
            case "Field Goal Attempt":
                if (completed) {
                    game.setComment("Kick is good!");
                    game.setStatus(Status.KICKOFF);
                    if (game.isHomePossession()) {
                        game.setScoreHome(game.getScoreHome() + 3);
                    } else {
                        game.setScoreGuest(game.getScoreGuest() + 3);
                    }
                    game.setYard(65);

                } else {
                    game.setComment("Kick is no good!");
                    game.setStatus(Status.OTHER);
                    game.setYard(100 - game.getYard());
                    game.setDown(1);
                    game.setYellow(10);
                }
                game.setHomePossession(!game.isHomePossession());
                break;
        }
        game.setTime(Math.max(game.getTime() - (int) (8 + Math.random() * 5), 0));
        gameService.saveGame(game);
        return comment;
    }

    private String excecuteProgress(int progressYard) {
        Game game = gameService.getCurrentGame();
        if (game.getYard() + progressYard > 110) {
            progressYard = 105 - game.getYard();
        }
        if (game.getYard() + progressYard < -10) {
            progressYard = -5 - game.getYard();
        }
        game.setYard(game.getYard() + progressYard);
        game.setDown(game.getDown() + 1);
        game.setYellow(game.getYellow() - progressYard);
        if (game.getYellow() <= 0 || game.getYard() >= 100 || game.getYard() <= 0) {
            if (game.getYard() >= 100) {
                game.setStatus(Status.TOUCHDOWN);
            } else if (game.getYard() <= 0) {
                game.setStatus(Status.SAFETY);
            }
            game.setDown(1);
            game.setYellow(10);
        }
        if (game.getDown() > 4) {
            game.setYard(100 - game.getYard());
            game.setDown(1);
            game.setYellow(10);
            game.setHomePossession(!game.isHomePossession());
        }
        return (progressYard > 0 ? "+" : "") + progressYard;
    }

    public void activatePlayClock() {
        this.playClock = System.currentTimeMillis();
    }

    public double getResponseTime() {
        return (System.currentTimeMillis() - playClock) / 1000.0;
    }
}