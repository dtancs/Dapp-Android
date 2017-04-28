package com.tancs.dapp.models;

/**
 * Created by tancs on 4/28/17.
 */

public class User {
    private String  id,
                    name,
                    email,
                    created_at,
                    updated_at,
                    password,
                    password_confirm;

    public User(){
        this.id = "";
        this.name = "";
        this.email = "";
        this.created_at = "";
        this.updated_at = "";
        this.password = "";
        this.password_confirm = "";
    }

    public User(String id,String name,String email,String created_at,String updated_at){
        this.id = id;
        this.name = name;
        this.email = email;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.password = "";
        this.password_confirm = "";
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword_confirm() {
        return password_confirm;
    }

    public void setPassword_confirm(String password_confirm) {
        this.password_confirm = password_confirm;
    }
}
