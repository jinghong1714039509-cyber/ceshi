package com.rabbiter.association.user.dto;

public class UserResponse {

    private final String id;
    private final String userName;
    private final String name;
    private final String gender;
    private final Integer age;
    private final String phone;
    private final String address;
    private final Integer status;
    private final String statusLabel;
    private final Integer type;
    private final String roleLabel;
    private final String createTime;

    public UserResponse(String id, String userName, String name, String gender, Integer age, String phone,
                        String address, Integer status, String statusLabel, Integer type, String roleLabel,
                        String createTime) {
        this.id = id;
        this.userName = userName;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.phone = phone;
        this.address = address;
        this.status = status;
        this.statusLabel = statusLabel;
        this.type = type;
        this.roleLabel = roleLabel;
        this.createTime = createTime;
    }

    public String getId() { return id; }
    public String getUserName() { return userName; }
    public String getName() { return name; }
    public String getGender() { return gender; }
    public Integer getAge() { return age; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public Integer getStatus() { return status; }
    public String getStatusLabel() { return statusLabel; }
    public Integer getType() { return type; }
    public String getRoleLabel() { return roleLabel; }
    public String getCreateTime() { return createTime; }
}
