package com.armageddonas.crawler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

/**
 * Created by darknight on 20-Aug-15.
 */
public class MainForm {
    private JButton btnConfigure;
    private JButton btnConnection;
    private JButton btnStart;
    private JPanel pnlApp;
    private JPanel panelCrawler;
    private JPanel panelStatus;
    private JLabel lblCrawler;
    private JLabel lblStatus;
    private JLabel lblPort;
    private JProgressBar prgbrCrawling;
    private JLabel lblProgress;
    private JLabel lblStatusConnected;
    private JLabel lblStatusConnectedState;
    private JLabel lblHostname;
    private JTextField txtfldHostname;
    private JTextField txtfldPort;
    private JLabel lblUsername;
    private JLabel lblPassword;
    private JTextField txtfldUsername;
    private JTextField txtfldPassword;
    public JButton btnConnect;
    private JLabel lblWorkingOn;
    private JTextField txtfldUsersNumber;
    private JButton btnDropTables;

    Database db;
    Connection conn;

    public static void main(String[] args) {
        MainForm FormGui = new MainForm();
        JFrame frame = new JFrame("Yelp Crawler ;)");
        frame.setContentPane(FormGui.pnlApp);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        if (args != null && args.length > 0 && args[0].equals("debug")) {
            FormGui.btnConnect.doClick();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            FormGui.btnStart.doClick();
        }
    }

    public MainForm() {

        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new Thread() {
                    @Override
                    public synchronized void run() {
                        String hostname = txtfldHostname.getText();
                        String port = txtfldPort.getText();
                        String username = txtfldUsername.getText();
                        String password = txtfldPassword.getText();
                        db = new Database(hostname, port, username, password);

                        db.CreateDatabase();
                        if (db.connect() == true) {
                            lblStatusConnectedState.setText("<html><font color='green'>Connected</font></html>");
                            conn = db.getConnection();
                        } else {
                            lblStatusConnectedState.setText("<html><font color='red'>Connection error</font></html>");
                        }
                    }

                }.start();
            }
        });
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (lblStatusConnectedState.getText().equals("<html><font color='green'>Connected</font></html>")) {
                    db.CreateTables();
                    Crawler yelpCrawl = new Crawler(db);
                    new Thread(() -> {
                        yelpCrawl.Start(Integer.valueOf(txtfldUsersNumber.getText()), prgbrCrawling, lblWorkingOn);
                    }).start();
                }
            }
        });
        btnDropTables.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (lblStatusConnectedState.getText().equals("<html><font color='green'>Connected</font></html>")) {
                    db.DropTables();
                    lblWorkingOn.setText("Tables dropped");
                }
            }
        });
    }
}
