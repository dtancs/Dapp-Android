package com.tancs.dapp.models;

/**
 * Created by tancs on 4/30/17.
 */

public class Micropost {
    private String  id,
            user_id,
            content,
            created_at,
            updated_at,
            created_time_ago;

    public Micropost(){
        this.id = "";
        this.user_id = "";
        this.content = "";
        this.created_at = "";
        this.updated_at = "";
        this.created_time_ago = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_time_ago() {
        return created_time_ago;
    }

    public void setCreated_time_ago(String created_time_ago) {
        this.created_time_ago = created_time_ago;
    }
}
