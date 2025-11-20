package com.example.MCPServer.Service.dto;

public class Employee {
    private Long id;
    private String name;
    private String email;
    private String position;
    private int sickLeaves;
    private int casualLeaves;
    private int earnedLeaves;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public int getSickLeaves() { return sickLeaves; }
    public void setSickLeaves(int sickLeaves) { this.sickLeaves = sickLeaves; }

    public int getCasualLeaves() { return casualLeaves; }
    public void setCasualLeaves(int casualLeaves) { this.casualLeaves = casualLeaves; }

    public int getEarnedLeaves() { return earnedLeaves; }
    public void setEarnedLeaves(int earnedLeaves) { this.earnedLeaves = earnedLeaves; }
}
