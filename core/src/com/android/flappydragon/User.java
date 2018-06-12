package com.android.flappydragon;

/**
 * Created by Rodrigo on 26/05/2018.
 */

public class User {

    private Double score;
    private String nickName, password;

    public User (){

    }
    public User (String nickName, String password, Double score){
        this.nickName = nickName;
        this.password = password;
        this.score = score;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
