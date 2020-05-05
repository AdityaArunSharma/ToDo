package com.asquarestudios.todo.Model;

public class NewItem
{
    private String tittle;
    private String task;
    private String date;
    private String priority;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public NewItem(String tittle, String task) {
        this.tittle = tittle;
        this.task = task;
    }

    public NewItem() {
    }
}
