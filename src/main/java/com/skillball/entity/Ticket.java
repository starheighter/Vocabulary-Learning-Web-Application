package com.skillball.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;

@Entity
public class Ticket {
    @Id
    @GeneratedValue
    private Integer ticketId;
    @ManyToOne
    private User requester;
    private Timestamp timeStamp;
    private String title;
    private String text;
    private String answer;
    private boolean deletedByUser;
    private boolean deletedByAdmin;

    public Integer getTicketId() {
        return ticketId;
    }

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp() {
        this.timeStamp = new Timestamp(System.currentTimeMillis());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isDeletedByUser() {
        return deletedByUser;
    }

    public boolean isDeletedByAdmin() {
        return deletedByAdmin;
    }

    public void setDeletedByUser(boolean deletedByUser) {
        this.deletedByUser = deletedByUser;
    }

    public void setDeletedByAdmin(boolean deletedByAdmin) {
        this.deletedByAdmin = deletedByAdmin;
    }
}