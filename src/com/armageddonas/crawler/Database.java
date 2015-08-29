package com.armageddonas.crawler;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by darknight on 21-Aug-15.
 */
public class Database {

    String hostname;
    String port;
    String username;
    String password;

    PreparedStatement userExistsStatement;
    PreparedStatement insertUserStatement;
    PreparedStatement insertEliteOfStatement;

    private Connection conn = null;

    Database(String hostname, String port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void CreateTables() {
        String dbconstr = "jdbc:mysql://" + hostname + ":" + port +
                "?user=" + username + "&password=" + password;
        System.out.println(dbconstr);
        try {
            Connection conn = DriverManager.getConnection(dbconstr);
            Statement stmt = conn.createStatement();

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `Yelp`.`User` (\n" +
                    "  `idUser` VARCHAR(100) NOT NULL,\n" +
                    "  `Username` VARCHAR(80) NULL,\n" +
                    "  `Friends` INT NULL,\n" +
                    "  `Reviews` INT NULL,\n" +
                    "  `ReviewUpdates` INT NULL,\n" +
                    "  `Firsts` INT NULL,\n" +
                    "  `Follower` INT NULL,\n" +
                    "  `Photos` INT NULL,\n" +
                    "  `Lists` INT NULL,\n" +
                    "  `Location` VARCHAR(100) NULL,\n" +
                    "  `RegistrationDate` VARCHAR(100) NULL,\n" +
                    "  `Hometown` VARCHAR(100) NULL,\n" +
                    "  `ComplimentProfile` INT NULL,\n" +
                    "  `ComplimentCute` INT NULL,\n" +
                    "  `ComplimentFunny` INT NULL,\n" +
                    "  `ComplimentPlain` INT NULL,\n" +
                    "  `ComplimentWriter` INT NULL,\n" +
                    "  `ComplimentList` INT NULL,\n" +
                    "  `ComplimentNote` INT NULL,\n" +
                    "  `ComplimentPhotos` INT NULL,\n" +
                    "  `ComplimentHot` INT NULL,\n" +
                    "  `ComplimentCool` INT NULL,\n" +
                    "  `ComplimentMore` INT NULL,\n" +
                    "  `ComplimentsTotal` INT NULL,\n" +
                    "  `PopularityScore` DOUBLE NULL,\n" +
                    "  PRIMARY KEY (`idUser`))");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `Yelp`.`EliteOf` (\n" +
                    "  `Year` INT NOT NULL,\n" +
                    "  `idUser` VARCHAR(100) NOT NULL,\n" +
                    "  PRIMARY KEY (`Year`, `idUser`),\n" +
                    "  INDEX `FK_User_idx` (`idUser` ASC),\n" +
                    "  CONSTRAINT `FK_User`\n" +
                    "    FOREIGN KEY (`idUser`)\n" +
                    "    REFERENCES `Yelp`.`User` (`idUser`)\n" +
                    "    ON DELETE NO ACTION\n" +
                    "    ON UPDATE NO ACTION)");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `Yelp`.`Queue` (\n" +
                    "  `idQueue` INT NOT NULL AUTO_INCREMENT,\n" +
                    "  `idUser` VARCHAR(100) NULL,\n" +
                    "  `FriendOf` VARCHAR(100) NULL,\n" +
                    "  PRIMARY KEY (`idQueue`),\n" +
                    "  UNIQUE INDEX `idUser_UNIQUE` (`idUser` ASC))");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean CreateDatabase() {
        try {
            String dbconstr = "jdbc:mysql://" + hostname + ":" + port +
                    "?user=" + username + "&password=" + password;
            System.out.println(dbconstr);
            Connection conn = DriverManager.getConnection(dbconstr);

            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS Yelp");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean connect() {
        try {
            //region Connect to database
            String conString = "jdbc:mysql://" + hostname + ":" + port +
                    "/Yelp?user=" + username + "&password=" + password;
            System.out.println(conString);
            conn = DriverManager.getConnection(conString);
            //endregion

            //region Initialize prepared statements
            userExistsStatement = conn.prepareStatement("SELECT COUNT(idUser) AS Count FROM User WHERE idUser=?");

            String insertUserQuery = "INSERT INTO User(idUser, Username, Friends, Reviews, ReviewUpdates, Firsts, Follower, Photos, Lists, " +
                    "Location, RegistrationDate, Hometown, ComplimentProfile, ComplimentCute, ComplimentFunny, ComplimentPlain, " +
                    "ComplimentWriter, ComplimentList, ComplimentNote, ComplimentPhotos, ComplimentHot, ComplimentCool, ComplimentMore, ComplimentsTotal, PopularityScore) " +
                    "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            insertUserStatement = conn.prepareStatement(insertUserQuery);

            String insertEliteOfQuery = "INSERT INTO EliteOf(Year,idUser) VALUES(?,?)";
            insertEliteOfStatement = conn.prepareStatement(insertEliteOfQuery);
            //endregion

            return true;
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

        return false;
    }

    public boolean UserExists(String userid) {
        try {
            userExistsStatement.setString(1, userid);

            ResultSet rs = userExistsStatement.executeQuery();
            rs.next();
            if (rs.getInt("Count") == 1) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    public void SaveUser(User user) {
        try {
            //region Insert user's info
            insertUserStatement.setString(1, user.userid);
            insertUserStatement.setString(2, user.username);
            insertUserStatement.setInt(3, user.friends);
            insertUserStatement.setInt(4, user.reviews);
            insertUserStatement.setInt(5, user.reviewUpdates);
            insertUserStatement.setInt(6, user.firsts);
            insertUserStatement.setInt(7, user.followers);
            insertUserStatement.setInt(8, user.photos);
            insertUserStatement.setInt(9, user.lists);
            insertUserStatement.setString(10, user.location);
            insertUserStatement.setString(11, user.registrationDate);
            insertUserStatement.setString(12, user.hometown);
            insertUserStatement.setInt(13, user.complementsObj.profile);
            insertUserStatement.setInt(14, user.complementsObj.cute);
            insertUserStatement.setInt(15, user.complementsObj.funny);
            insertUserStatement.setInt(16, user.complementsObj.plain);
            insertUserStatement.setInt(17, user.complementsObj.writer);
            insertUserStatement.setInt(18, user.complementsObj.list);
            insertUserStatement.setInt(19, user.complementsObj.note);
            insertUserStatement.setInt(20, user.complementsObj.photos);
            insertUserStatement.setInt(21, user.complementsObj.hot);
            insertUserStatement.setInt(22, user.complementsObj.cool);
            insertUserStatement.setInt(23, user.complementsObj.more);
            insertUserStatement.setDouble(24, user.complementsObj.total);
            insertUserStatement.setDouble(25, user.popularityScore);


            insertUserStatement.executeUpdate();
            //endregion

            //region Insert elite of years
            for (int i = 0; i < user.eliteOf.length; i++) {
                insertEliteOfStatement.setInt(1, Integer.valueOf(user.eliteOf[i]));
                insertEliteOfStatement.setString(2, user.userid);

                insertEliteOfStatement.executeUpdate();
            }
            //endregion
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public String[] GetQueue() {
        List<String> queue = new ArrayList<>();
        try {
            PreparedStatement getQueueStm = conn.prepareStatement("SELECT idUser FROM Queue");
            getQueueStm.executeQuery();
            ResultSet rs = getQueueStm.getResultSet();
            while (rs.next()) {
                queue.add(rs.getString("idUser"));
            }
            if (queue.size() == 0) {
                return null;
            }
            String[] tempArray = new String[queue.size()];
            return queue.toArray(tempArray);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean SaveToQueue(String userid, String friendid) {
        try {
            PreparedStatement queueQueryStr = conn.prepareStatement("INSERT INTO Queue(idUser,FriendOf) VALUES(?,?)");
            queueQueryStr.setString(1, userid);
            queueQueryStr.setString(2, friendid);
            queueQueryStr.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Connection getConnection() {
        return conn;
    }

    public void DropTables() {
        String dbconstr = "jdbc:mysql://" + hostname + ":" + port +
                "?user=" + username + "&password=" + password;
        System.out.println(dbconstr);
        try {
            Connection conn = DriverManager.getConnection(dbconstr);
            Statement stmt = conn.createStatement();

            stmt.executeUpdate("DROP TABLE yelp.EliteOf");
            stmt.executeUpdate("DROP TABLE yelp.Queue");
            stmt.executeUpdate("DROP TABLE yelp.User");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
