package com.armageddonas.crawler;

import java.util.Arrays;

/**
 * Created by darknight on 21-Aug-15.
 */
public class User {
    String userid;

    @Override
    public String toString() {
        return "User{" +
                "userid='" + userid + '\'' +
                ", username='" + username + '\'' +
                ", friends=" + friends +
                ", reviews=" + reviews +
                ", reviewUpdates=" + reviewUpdates +
                ", firsts=" + firsts +
                ", followers=" + followers +
                ", photos=" + photos +
                ", lists=" + lists +
                ", location='" + location + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                ", hometown='" + hometown + '\'' +
                ", eliteOf=" + Arrays.toString(eliteOf) +
                '}';
    }

    String username;
    int friends;
    int reviews;
    int reviewUpdates;
    int firsts;
    int followers;
    int photos;
    int lists;
    Compliments complementsObj = new Compliments();
    String location;
    String registrationDate;
    String hometown;
    String eliteOf[];
    double popularityScore;
}
