package com.skillball.service;

import com.skillball.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private UserService userService;
    @Autowired
    private VocabService vocabService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Role admin = new Role();
        admin.setRolename("ROLE_ADMIN");
        roleService.saveRole(admin);

        Role user = new Role();
        user.setRolename("ROLE_USER");
        roleService.saveRole(user);

        User alice = new User();
        alice.setEmail("st155220@stud.uni-stuttgart.de");
        alice.setUsername("alice");
        alice.setPassword(passwordEncoder.encode("alice"));
        alice.setLanguage("German");
        alice.setLevel("Basic");
        alice.setIndex(1);
        alice.setDifficulty("Normal");
        alice.setDurationQuarter(180);
        alice.setRsLength(7);
        alice.setRole(roleService.getRoleByRolename("ROLE_ADMIN"));
        alice.setEmailConfirmed(true);
        alice.setEnabled(true);
        userService.saveUser(alice);

        User bob = new User();
        bob.setEmail("dominik.larche@gmail.com");
        bob.setUsername("bob");
        bob.setPassword(passwordEncoder.encode("bob"));
        bob.setLanguage("German");
        bob.setLevel("Basic");
        bob.setIndex(1);
        bob.setDifficulty("Normal");
        bob.setDurationQuarter(180);
        bob.setRsLength(7);
        bob.setRole(roleService.getRoleByRolename("ROLE_USER"));
        bob.setEmailConfirmed(true);
        bob.setEnabled(true);
        userService.saveUser(bob);

        for (int i = 0; i < 100; i++) {
            Vocab vocab = new Vocab();
            vocab.setLanguage("German");
            vocab.setLevel("Basic");
            vocab.setIndex(1);
            int a = (int) (Math.random() * 100);
            int b = (int) (Math.random() * 100);
            int c = a + b;
            vocab.setEnglish(c + "");
            vocab.setTranslation(a + " + " + b);
            vocab.setPosition(1);
            vocabService.saveVocab(vocab);
        }

        Ticket login = new Ticket();
        login.setRequester(bob);
        login.setTimeStamp();
        login.setTitle("Login");
        login.setText("I am not able to login.");
        login.setDeletedByUser(false);
        login.setDeletedByAdmin(false);
        ticketService.saveTicket(login);

        Ticket register = new Ticket();
        register.setRequester(bob);
        register.setTimeStamp();
        register.setTitle("Register");
        register.setText("I am not able to register.");
        register.setDeletedByUser(false);
        register.setDeletedByAdmin(false);
        ticketService.saveTicket(register);
    }
}