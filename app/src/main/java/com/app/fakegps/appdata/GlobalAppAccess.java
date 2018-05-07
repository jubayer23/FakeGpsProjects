package com.app.fakegps.appdata;



public class GlobalAppAccess {


    public static final String APP_NAME = "TimeSetter";
    public static String BaseUrl = "http://198.204.244.250:8084/";
    //public static String BaseUrl = "https://b5e99a4d.ngrok.io/bgb/";
    public static final String URL_GET_TIMES =  BaseUrl + "getTimes.jsp";
    public static final String URL_INSERT_TIME = BaseUrl +  "insertTime.jsp";
    public static final String URL_DELETE_TIME = BaseUrl +  "deleteTime.jsp";


    public static final  int SUCCESS = 1;
    public static  final  int ERROR = 0;



    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;


    public static final String KEY_CALL_FROM = "call_from";
    public static final String KEY_NOTIFICATION_ID = "notification_id";

    public static final String TAG_ALARM_RECEIVER = "alarm_receiver";

    public static final String[] reminder_time_options = {"Select a time", "Before time expires", "15 mins before",
            "30 mins before","1 hour before"};

    public static final String[] rideshares_options = {"Uber", "Lyft", "Uber or Lyft"};

    public static final String[] seats_options = {"4 Seats", "5 Seats"};

}
