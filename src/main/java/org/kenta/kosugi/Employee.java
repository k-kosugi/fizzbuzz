package org.kenta.kosugi;

import java.util.Date;

public class Employee {

    private String id;

    private String firstName;

    private String lastName;

    private Employee boss;

    private Date hiredDate;

    public static void main(String[] args) {

    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Employee getBoss() {
        return boss;
    }

    public Date getHiredDate() {
        return hiredDate;
    }
}
