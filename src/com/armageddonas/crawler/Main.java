package com.armageddonas.crawler;

/**
 * Created by darknight on 25-Aug-15.
 */
public class Main {

    public static void main(String[] args) {

        if (args.length > 0 && args[0].equals("nogui")) {
            String hostname = args[1];
            String port = "3306";
            String username = "root";
            String password = args[2];
            Database db;
            db = new Database(hostname, port, username, password);

            db.connect();
            db.CreateDatabase();
            Crawler yelpCrawl = new Crawler(db);
            yelpCrawl.Start();
        } else if (args.length > 0 && args[0].equals("debug")) {
            String tmp[] = new String[1];
            tmp[0] = "debug";
            MainForm mf = new MainForm();
            mf.main(tmp);
        } else {
            MainForm mf = new MainForm();
            mf.main(null);
        }
    }
}
