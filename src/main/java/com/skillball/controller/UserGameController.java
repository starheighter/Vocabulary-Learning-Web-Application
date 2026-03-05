package com.skillball.controller;

import com.skillball.entity.Game;
import com.skillball.entity.Status;
import com.skillball.entity.User;
import com.skillball.service.GamePlay;
import com.skillball.service.GameService;
import com.skillball.service.UserService;
import com.skillball.service.VocabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserGameController {
    @Autowired
    private UserService userService;
    @Autowired
    private GameService gameService;
    @Autowired
    private VocabService vocabService;
    @Autowired
    private GamePlay gamePlay;

    @GetMapping("/userGame")
    public String showUserGame(Model model) {
        adaptModel(model, "gameScreen");
        return "userGame";
    }

    @PostMapping("/userGame/continueLeaveGame")
    public String continueLeaveGame(HttpServletRequest req, Model model) {
        User user = userService.getCurrentUser();
        Game game = gameService.getCurrentGame();
        String button = req.getParameter("button");
        if (button.equals("delete")) {
            gamePlay.delete();
            model.addAttribute("welcome", "Hello " + user.getUsername() + "! Nice that You are here!");
            model.addAttribute("games", gameService.listRelevantGames(user));
            return "userHome";
        } else if (button.equals("leave")) {
            gameService.saveGame(game);
            gameService.setCurrentGame(null);
            model.addAttribute("welcome", "Hello " + user.getUsername() + "! Nice that You are here!");
            model.addAttribute("games", gameService.listRelevantGames(user));
            return "userHome";
        } else {
            switch (game.getStatus()) {
                case TOUCHDOWN:
                    gamePlay.touchdown();
                    if (game.isHomePossession()) {
                        adaptModel(model, "moveScreen");
                        model.addAttribute("moves", new String[] { "Extra Point", "Two Point Attempt" });
                        model.addAttribute("conversion", true);
                        return "userGame";
                    } else {
                        adaptModel(model, "gameScreen");
                        return "userGame";
                    }
                case SAFETY:
                    gamePlay.safety();
                    adaptModel(model, "gameScreen");
                    return "userGame";
                case EXTRAPOINT:
                case TWOPOINT:
                case FIELDGOAL:
                    gamePlay.extraPointTwoPointFieldGoal();
                    adaptModel(model, "answerScreen");
                    model.addAttribute("vocab", gamePlay.chooseVocab());
                    return "userGame";
                case KICKOFF:
                case PUNT:
                    gamePlay.kickoffPunt();
                    adaptModel(model, "gameScreen");
                    return "userGame";
                default:
                    if (game.getTime() == 0) {
                        gamePlay.endOfQuarter(user.getDurationQuarter());
                        if (game.getQuarter() == 5) {
                            model.addAttribute("welcome", "Hello " + user.getUsername() + "! Nice that You are here!");
                            model.addAttribute("games", gameService.listRelevantGames(user));
                            return "userHome";
                        }
                        adaptModel(model, "gameScreen");
                        return "userGame";
                    } else {
                        String[] moves = new String[] { "Run", "Pass", "Long Pass", "Punt", "Field Goal Attempt" };
                        if (game.isHomePossession()) {
                            adaptModel(model, "moveScreen");
                            model.addAttribute("moves", moves);
                            model.addAttribute("conversion", false);
                            return "userGame";
                        } else {
                            if (game.getDown() == 4 && game.getYard() < 55) {
                                game.setStatus(Status.PUNT);
                                adaptModel(model, "gameScreen");
                                return "userGame";
                            } else if (game.getDown() == 4 && game.getYard() > 60) {
                                game.setStatus(Status.FIELDGOAL);
                                adaptModel(model, "gameScreen");
                                return "userGame";
                            }
                            int index = (int) (Math.random() * 3) % 3;
                            game.setMove(moves[index]);
                            adaptModel(model, "answerScreen");
                            model.addAttribute("vocab", gamePlay.chooseVocab());
                            return "userGame";
                        }
                    }
            }
        }
    }

    @PostMapping("/userGame/userGameMove")
    public String move(HttpServletRequest req, Model model) {
        Game game = gameService.getCurrentGame();
        String move = req.getParameter("button");
        if (move.equals("Extra Point") || move.equals("Two Point Attempt")
                || move.equals("Field Goal Attempt") || move.equals("Punt")) {
            gamePlay.adaptMove(move);
            adaptModel(model, "gameScreen");
            return "userGame";
        }
        game.setMove(move);
        adaptModel(model, "answerScreen");
        model.addAttribute("vocab", gamePlay.chooseVocab());
        return "userGame";
    }

    @PostMapping("/userGame/userGameAnswer")
    public String answer(HttpServletRequest req, Model model) {
        Game game = gameService.getCurrentGame();
        String answer = req.getParameter("answer").toLowerCase();
        String english = vocabService.getCurrentVocab().getEnglish();
        if (!answer.equals(english) && (gamePlay.getResponseTime() <= gamePlay.THRESHOLD * 3.0
                || (int) (gamePlay.getResponseTime() / gamePlay.THRESHOLD - 5.0) <= game.getYard() / 5.0)) {
            adaptModel(model, "answerScreen");
            model.addAttribute("vocab", vocabService.getCurrentVocab());
            model.addAttribute("failed", true);
            return "userGame";
        }
        String comment = gamePlay.progress(gamePlay.getResponseTime());
        adaptModel(model, "gameScreen");
        // model.addAttribute("comment", comment);
        return "userGame";
    }

    @PostMapping("/userGame/vocabList")
    public String viewVocab(HttpServletRequest req, Model model) {
        User user = userService.getCurrentUser();
        vocabService.updateCurrentVocabList(user.getLanguage(), user.getLevel(), user.getIndex(), false);
        model.addAttribute("title", user.getLanguage() + " " + user.getLevel() + " " + user.getIndex());
        model.addAttribute("templateList", vocabService.createTemplateList(false));
        model.addAttribute("inGame", true);
        return "userVocab";
    }

    @PostMapping("/userVocab/userGame")
    public String userVocab(HttpServletRequest req, Model model) {
        adaptModel(model, "answerScreen");
        model.addAttribute("vocab", vocabService.getCurrentVocab());
        return "userGame";
    }

    private void adaptModel(Model model, String screen) {
        Game game = gameService.getCurrentGame();
        model.addAttribute("game", game);
        model.addAttribute(screen, true);
    }
}