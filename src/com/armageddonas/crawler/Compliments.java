package com.armageddonas.crawler;

/**
 * Created by darknight on 21-Aug-15.
 */
public class Compliments {
    int profile;
    int cute;
    int funny;
    int plain;
    int writer;
    int list;
    int note;
    int photos;
    int hot;
    int cool;
    int more;

    double total;

    public void CalculateTotal() {
        total = 0;
        total += profile;
        total += cute;
        total += funny;
        total += plain;
        total += writer;
        total += list;
        total += note;
        total += photos;
        total += hot;
        total += cool;
        total += more;
    }
}
