package com.rabbiter.association.message.dto;

public class MessageContactResponse {
    private final String userId;
    private final String userName;
    private final String displayName;
    private final String role;
    private final String clubId;
    private final String clubName;
    private final String lastMessage;
    private final String lastMessageTime;
    private final int unreadCount;

    public MessageContactResponse(String userId,
                                  String userName,
                                  String displayName,
                                  String role,
                                  String clubId,
                                  String clubName,
                                  String lastMessage,
                                  String lastMessageTime,
                                  int unreadCount) {
        this.userId = userId;
        this.userName = userName;
        this.displayName = displayName;
        this.role = role;
        this.clubId = clubId;
        this.clubName = clubName;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;
    }

    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getDisplayName() { return displayName; }
    public String getRole() { return role; }
    public String getClubId() { return clubId; }
    public String getClubName() { return clubName; }
    public String getLastMessage() { return lastMessage; }
    public String getLastMessageTime() { return lastMessageTime; }
    public int getUnreadCount() { return unreadCount; }
}
