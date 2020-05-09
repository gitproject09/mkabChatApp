package org.mkab.chatapp.model;

public class Information {
    private String title;
    private String type;
    private String fileName;

    public Information(String title, String type, String fileName) {
        super();

        this.title = title;
        this.type = type;
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
