package com.dorm.muro.dormitory.presentation.todo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TodoItem {
    private String title, commentary;
    private Date deadline;
    private boolean isPinned;

    TodoItem(String title, String commentary, Date deadline) {
        this.title = title;
        if (commentary != null && !commentary.isEmpty())
            this.commentary = commentary;
        else
            this.commentary = "";
        this.deadline = deadline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public String getDeadlineString(SimpleDateFormat formatter) {
        return formatter.format(deadline);
    }
}
