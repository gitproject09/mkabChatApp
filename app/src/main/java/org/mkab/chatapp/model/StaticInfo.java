package org.mkab.chatapp.model;

public class StaticInfo {

    public static String BaseEndPoint = "https://mkabchatapp.firebaseio.com";
    public static String EndPoint     = "https://mkabchatapp.firebaseio.com";
    public static String MessagesEndPoint = BaseEndPoint + "/messages";
    public static String FriendsURL = BaseEndPoint + "/friends";
    public static String UsersURL = BaseEndPoint + "/users";
    public static String DoctorsURL = BaseEndPoint + "/doctors";
    public static String InfoURL = BaseEndPoint + "/informations";
    public static String UserCurrentChatFriendEmail = "";
    public static String TypingStatus = "TypingStatus";

    public static String NotificationEndPoint = BaseEndPoint + "/notifications";
    public static String FriendRequestsEndPoint = BaseEndPoint + "/friendrequests";

    public static int ChatAciviityRequestCode = 101;

}
