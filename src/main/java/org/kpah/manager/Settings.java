package org.kpah.manager;

import org.kpah.utils.ConfigLoader;

public class Settings {
    // Game settings
    public static final int PORT_SERVER;
    public static final int MAX_PLAYER;
    public static final String NAME_SERVER;

    // Database settings
    public static final String DATABASE;
    public static final String HOST;
    public static final String USER;
    public static final String PASS;

    public static final byte[] KEYS = "kpah".getBytes();
    public static final byte DAY_WAIT_FOR_DELETE = 7;

    public static final String NUMBER_SUPPORT = "nhập sđt";
    public static final String URL = "localhost";

    public static final int SECOND_WAIT_LOGIN = 1;
    public static final int MILISECOND_WAIT_LOGIN = 1000;
    public static final short SECOND_REVIVE_PLAYER = 30;
    public static final int MILISECOND_REVIVE_PLAYER = 30000;
    public static final byte LEVEL_CAN_AUTO_REVIVE = 10;

    public static final int EXP_DONATE; // gấp 100 lần EXP
    public static final byte PERCENT_EXP_PARTY = 5;
    public static final byte MAX_PLAYER_IN_PARTY = 10;

    public static final int MILISECOND_UPDATE_DATABASE = 12000; // 2 minute

    public static final int TIME_LIVE_MOB = 8000;
    public static final short DISTANCE_MOB_CAN_ATTACK = 90;

    public static final int MILISECOND_WAIT_KICK_SESSION = 60000;
    public static final int MILISECOND_WAIT_KICK_PLAYER = 600000;

    public static final String[] ICON = {};

    public static final String[] LOGO_GAME = {};

    static {
        ConfigLoader config = new ConfigLoader("config.properties");

        PORT_SERVER = config.getInt("PORT_SERVER", 19129);
        NAME_SERVER = config.getString("NAME_SERVER", "KPAH");
        MAX_PLAYER = config.getInt("MAX_PLAYER", 40000);

        DATABASE = config.getString("DATABASE", "kpah");
        HOST = config.getString("HOST", "127.0.0.1:3306");
        USER = config.getString("USER", "root");
        PASS = config.getString("PASS", "password");

        EXP_DONATE = config.getInt("EXP_DONATE", 100);
    }
}
