package com.raghusaripalli.attendancemanager;

public class Attendance {
    private String subject;
    private int present;
    private int total;


    // parametrized constructor
    public Attendance(String subject,int present, int total){
        this.present = present;
        this.subject =subject;
        this.total = total;
    }

    public String getSubject(){
        return subject;
    }

    public int getPresent(){
        return present;
    }

    public int getTotal(){
        return  total;
    }
}
