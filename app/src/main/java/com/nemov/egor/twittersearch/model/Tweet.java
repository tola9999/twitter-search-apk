package com.nemov.egor.twittersearch.model;

/**
 * Created by egor.nemov on 03.02.16.
 */
public class Tweet {
    public String created_at;
    public String text;
    public TwitterUser user;

    @Override
    public String toString() {
        return "@" + user.screen_name + "\n[" + created_at + "]\n" + text;
    }
}
