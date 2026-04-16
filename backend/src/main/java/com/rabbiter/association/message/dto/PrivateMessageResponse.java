package com.rabbiter.association.message.dto;

public class PrivateMessageResponse {
    private final String id;
    private final String senderId;
    private final String senderName;
    private final String receiverId;
    private final String receiverName;
    private final String clubId;
    private final String clubName;
    private final String content;
    private final String createTime;
    private final boolean mine;

    public PrivateMessageResponse(String id,
                                  String senderId,
                                  String senderName,
                                  String receiverId,
                                  String receiverName,
                                  String clubId,
                                  String clubName,
                                  String content,
                                  String createTime,
                                  boolean mine) {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.clubId = clubId;
        this.clubName = clubName;
        this.content = content;
        this.createTime = createTime;
        this.mine = mine;
    }

    public String getId() { return id; }
    public String getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getReceiverId() { return receiverId; }
    public String getReceiverName() { return receiverName; }
    public String getClubId() { return clubId; }
    public String getClubName() { return clubName; }
    public String getContent() { return content; }
    public String getCreateTime() { return createTime; }
    public boolean isMine() { return mine; }
}
