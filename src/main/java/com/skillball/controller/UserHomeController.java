package com.skillball.controller;

import com.skillball.entity.Game;
import com.skillball.entity.Status;
import com.skillball.entity.User;
import com.skillball.service.GameService;
import com.skillball.service.TicketService;
import com.skillball.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserHomeController {

    @Autowired
    private UserService userService;
    @Autowired
    private GameService gameService;
    @Autowired
    private TicketService ticketService;

    @GetMapping("/userHome")
    public String showUserHome(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("welcome", "Hello " + user.getUsername() + "! Nice that You are here!");
        model.addAttribute("games", gameService.listRelevantGames(user));
        return "userHome";
    }

    @PostMapping("/userHome/sites")
    public String sites(HttpServletRequest req, Model model) {
        User user = userService.getCurrentUser();
        String site = req.getParameter("site");
        if (site.equals("Home")) {
            model.addAttribute("welcome", "Hello " + user.getUsername() + "! Nice that You are here!");
            model.addAttribute("games", gameService.listRelevantGames(user));
            return "userHome";
        } else if (site.equals("Settings")) {
            model.addAttribute("difficulty", user.getDifficulty());
            model.addAttribute("durationQuarter", user.getDurationQuarter());
            model.addAttribute("rsLength", user.getRsLength());
            model.addAttribute("title", user.getLanguage() + " " + user.getLevel() + " " + user.getIndex());
            return "userSettings";
        } else if (site.equals("Account")) {
            model.addAttribute("email", "E-Mail: " + user.getEmail());
            model.addAttribute("username", "Username: " + user.getUsername());
            return "userAccount";
        } else {
            model.addAttribute("ticketList", ticketService.listRelevantTickets(userService.getCurrentUser()));
            return "userSupport";
        }
    }

    @PostMapping("/userHome/newGame")
    public String newGame(HttpServletRequest req, Model model) {
        User user = userService.getCurrentUser();
        Game newGame = new Game();
        newGame.setUser(user);
        newGame.setTimeStamp();
        newGame.setGuest("Miami Dolphins");
        newGame.setHome("Buffalo Bills");
        newGame.setQuarter(1);
        newGame.setTime(user.getDurationQuarter());
        newGame.setScoreGuest(0);
        newGame.setScoreHome(0);
        newGame.setHomePossession(Math.random() > 0.5);
        newGame.setYard(65);
        newGame.setDown(1);
        newGame.setYellow(10);
        newGame.setStatus(Status.KICKOFF);
        newGame.setHomeStarted(newGame.isHomePossession());
        gameService.saveGame(newGame);
        gameService.setCurrentGame(newGame);
        model.addAttribute("game", newGame);
        model.addAttribute("gameScreen", true);
        return "userGame";
    }

    @PostMapping("/userHome/userGame")
    public String game(HttpServletRequest req, Model model) {
        int gameId = Integer.parseInt(req.getParameter("button"));
        Game game = gameService.getGameByGameId(gameId);
        gameService.setCurrentGame(game);
        model.addAttribute("game", game);
        model.addAttribute("gameScreen", true);
        return "userGame";
    }
}