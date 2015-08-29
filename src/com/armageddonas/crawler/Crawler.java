package com.armageddonas.crawler;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by darknight on 21-Aug-15.
 */
public class Crawler {
    String startUrl = "http://www.yelp.com/browse/reviews/picks?edition_id=c6HT44PKCaXqzN_BdgKPCw";
    String pageUrl = "http://www.yelp.com";
    String UserProfileURL = "http://www.yelp.com/user_details?userid=";
    String UserFriendsURL = "http://www.yelp.com/user_details_friends?userid=";
    int USERS_DATABASE_TOTAL;
    //ArrayList<String> usersQueue = new ArrayList();
    Database db;
    JProgressBar pgrBar;
    JLabel lblWorkingOn;

    Crawler(Database db) {
        this.db = db;
    }

    public void Start(int usersNumber, JProgressBar pgrBar, JLabel lblWorkingOn) {
        this.USERS_DATABASE_TOTAL = usersNumber;
        this.pgrBar = pgrBar;
        this.lblWorkingOn = lblWorkingOn;

        String queue[];

        pgrBar.setStringPainted(true);
        pgrBar.setValue(0);

        try {
            //region Create database
            if (db.GetQueue() == null) {
                lblWorkingOn.setText("Creating queue");
                Connection.Response res = null;
                res = Jsoup
                        .connect(startUrl)
                        .userAgent("Mozilla/17.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .timeout(30000)
                        .method(Connection.Method.GET)
                        .execute();
                String firstUserid = res.parse().select("a[class=user-display-name]").first().attr("abs:href").replace("http://www.yelp.com/user_details?userid=", "");
                System.out.println(firstUserid);
                CreateQueue(firstUserid);
            }
            //endregion

            queue = db.GetQueue();
            for (int i = 0; i < queue.length; i++) {
                //region Gui elements
                lblWorkingOn.setText("Crawling user's " + (i + 1) + " of " + queue.length);
                pgrBar.setValue((int) ((double) (i + 1) / USERS_DATABASE_TOTAL * 100));
                //endregion
                if (db.UserExists(queue[i])) {
                    System.out.println("User '" + queue[i] + "' is already in database");
                } else {
                    //region Save user to database
                    User user = GetUserInfo(queue[i]);
                    if (user != null) {
                        db.SaveUser(user);
                    }
                    //endregion
                }
            }
        } catch (HttpStatusException e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        lblWorkingOn.setText("Finished");
    }

    public void Start() {
        int userInDatabase = 0;
        String queue[];

        try {
            //region Create database
            if (db.GetQueue() == null) {
                //todo: gui message start
                Connection.Response res = null;
                res = Jsoup
                        .connect(startUrl)
                        .userAgent("Mozilla/17.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .timeout(30000)
                        .method(Connection.Method.GET)
                        .execute();
                String firstUserid = res.parse().select("a[class=user-display-name]").first().attr("abs:href").replace("http://www.yelp.com/user_details?userid=", "");
                System.out.println(firstUserid);
                CreateQueue(firstUserid);
                //todo: gui message end
            }
            //endregion

            queue = db.GetQueue();
            for (int i = 0; i < queue.length; i++) {
                if (db.UserExists(queue[i])) {
                    System.out.println("User '" + queue[i] + "' is already in database");
                } else {
                    //region Save user to database
                    User user = GetUserInfo(queue[i]);
                    if (user != null) {
                        db.SaveUser(user);
                        userInDatabase++;
                    }
                    //endregion
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Function that extracts the number from user's stats
     *
     * @param res       User's profile page
     * @param className Html class name
     * @return Number of Friends, Reviews etc
     */
    private int sanitizeElementNumber(Connection.Response res, String className) {
        String tempStr = null;
        try {
            if (res.parse().select("#user_stats").select("[class=" + className + "]").size() == 0) {
                return 0;
            }
            tempStr = res.parse().select("#user_stats").select("[class=" + className + "]").text();

            if (!tempStr.equals("")) return Integer.valueOf(tempStr.split(" ")[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Function that extracts the number from user's compliments
     *
     * @param res       User's profile page
     * @param className Html class name
     * @return Value of compliment
     */
    private int sanitizeCompliments(Connection.Response res, String className) throws NullPointerException {
        String tempStr = null;
        try {
            Element temp = res.parse().select("#about_user_column").first();
            if (temp.select("[class=" + className + "]").size() == 0) {
                return 0;
            }
            temp = temp.select("[class=" + className + "]").first().parent();
            tempStr = temp.select("[class=compliment-count]").text();

            if (!tempStr.equals("")) return Integer.valueOf(tempStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;

    }

    /**
     * Get user's info
     *
     * @param userid User's unique string
     * @return User's info
     */
    private User GetUserInfo(String userid) {
        /*try {
            Thread.sleep(4 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        //System.out.println("User: " + userid);
        User curUser = new User();
        try {

            Connection.Response res = Jsoup
                    //.connect("http://www.yelp.com/user_details?userid=wGT4oguaI_FgODlFQ9-QPA").timeout(10 * 1000)
                    .connect(UserProfileURL + userid).timeout(20 * 1000)
                    .method(Connection.Method.GET)
                    .execute();

            //curUser.userid = profileURL.replace("http://www.yelp.com/user_details?userid=", "");
            curUser.userid = userid;

            curUser.username = res.parse().select("[class=about-connections]").select("h1").text();

            //region Get user stats
            curUser.friends = sanitizeElementNumber(res, "i-wrap ig-wrap-common i-friends-green-common-wrap");

            curUser.reviews = sanitizeElementNumber(res, "i-wrap ig-wrap-common i-star-orange-common-wrap");

            curUser.reviewUpdates = sanitizeElementNumber(res, "i-wrap ig-wrap-common i-refresh-orange-small-common-wrap");

            curUser.firsts = sanitizeElementNumber(res, "i-wrap ig-wrap-common i-first-burst-common-wrap");

            curUser.followers = sanitizeElementNumber(res, "i-wrap ig-wrap-common i-pink-heart-common-wrap");

            curUser.photos = sanitizeElementNumber(res, "i-wrap ig-wrap-common i-camera-common-wrap");

            curUser.lists = sanitizeElementNumber(res, "i-wrap ig-wrap-common i-list-common-wrap");
            //endregion

            //region Get compliments
            curUser.complementsObj.profile = sanitizeCompliments(res, "i ig-compliments i-24x24_profile-compliments");

            curUser.complementsObj.cute = sanitizeCompliments(res, "i ig-compliments i-24x24_cute-compliments");

            curUser.complementsObj.funny = sanitizeCompliments(res, "i ig-compliments i-24x24_funny-compliments");

            curUser.complementsObj.plain = sanitizeCompliments(res, "i ig-compliments i-24x24_plain-compliments");

            curUser.complementsObj.writer = sanitizeCompliments(res, "i ig-compliments i-24x24_writer-compliments");

            curUser.complementsObj.list = sanitizeCompliments(res, "i ig-compliments i-24x24_list-compliments");

            curUser.complementsObj.note = sanitizeCompliments(res, "i ig-compliments i-24x24_note-compliments");

            curUser.complementsObj.photos = sanitizeCompliments(res, "i ig-compliments i-24x24_photos-compliments");

            curUser.complementsObj.hot = sanitizeCompliments(res, "i ig-compliments i-24x24_hot-compliments");

            curUser.complementsObj.cool = sanitizeCompliments(res, "i ig-compliments i-24x24_cool-compliments");

            curUser.complementsObj.more = sanitizeCompliments(res, "i ig-compliments i-24x24_more-compliments");

            curUser.complementsObj.CalculateTotal();
            //endregion

            //region Get profile questions
            Elements profileQuestions = res.parse().select("#profile_questions").first().children();
            for (int i = 0; i < profileQuestions.size(); i += 2) {
                String titleText = profileQuestions.get(i).select("span").text();
                if (titleText.equals("Location")) {
                    curUser.location = profileQuestions.get(i + 1).text();
                } else if (titleText.equals("Yelping Since")) {
                    curUser.registrationDate = profileQuestions.get(i + 1).text();
                } else if (titleText.equals("My Hometown")) {
                    curUser.hometown = profileQuestions.get(i + 1).text();
                }
            }
            //endregion

            //region Get elite years
            Elements eliteYears = res.parse().select("#user-badges").select("a");
            String eliteYearsStr[] = new String[eliteYears.size()];
            for (int i = 0; i < eliteYears.size(); i++) {
                eliteYearsStr[i] = eliteYears.get(i).attr("title").replaceAll("\\D+", "");
            }
            curUser.eliteOf = eliteYearsStr;
            //endregion

            //Calculate popularity score
            curUser.popularityScore = curUser.friends * 0.05 + curUser.reviews * 0.1 + curUser.firsts * 0.05 +
                    curUser.followers * 0.3 + curUser.photos * 0.05 + curUser.complementsObj.total * 0.15 + curUser.eliteOf.length * 0.3;

            System.out.println(curUser.toString());

            return curUser;
        } catch (HttpStatusException e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("User url:" + UserProfileURL + userid);
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("Nullpointerexception - User url:" + UserProfileURL + userid);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Add user's friends to queue
     *
     * @param userid
     */
    private void CreateQueue(String userid) {
        int usersQueueNum = 0;
        int index = 0;
        List<String> queue = new ArrayList<>();
        queue.add(userid);
        try {
            do {
                Connection.Response res = Jsoup
                        .connect(UserFriendsURL + queue.get(index))
                        .method(Connection.Method.GET)
                        .execute();

                //Get user's friends as elements
                Elements allFriendsElem = res.parse().select("#friends_box_list").select("[class=photo-box pb-ss]");
                //region Parse user's friends
                for (int i = 0; i < allFriendsElem.size() && usersQueueNum < USERS_DATABASE_TOTAL; i++) {
                    //Get the users from image url
                    String friendUserid = allFriendsElem.get(i).select("a").attr("href").replace(UserProfileURL, "");

                    if (db.SaveToQueue(friendUserid, queue.get(index)) == true) {
                        queue.add(friendUserid);
                        usersQueueNum++;

                        System.out.println("Create Queue " + usersQueueNum + " of " + USERS_DATABASE_TOTAL);
                        lblWorkingOn.setText("Create Queue " + usersQueueNum + " of " + USERS_DATABASE_TOTAL);
                        pgrBar.setValue((int) ((double) usersQueueNum / USERS_DATABASE_TOTAL * 100));
                    } else {
                        System.out.println("Could not add " + friendUserid);
                    }
                }
                //endregion
                //region Sleep to limit requests
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //endregion
                index++;
            } while (usersQueueNum < USERS_DATABASE_TOTAL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
