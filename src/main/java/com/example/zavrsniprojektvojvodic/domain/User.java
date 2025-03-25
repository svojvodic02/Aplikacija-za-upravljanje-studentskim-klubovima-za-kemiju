package com.example.zavrsniprojektvojvodic.domain;

import java.util.Objects;

public class User extends NamedEntity{
    private String lastName;
    private String email;

    private String password;

    private String OIB;

    private String isTheUserAnAdmin;

    public User(String firstname, String lastName, String username, String password, String isTheUserAnAdmin, Long id,String OIB) {
        super(firstname,id);
        this.lastName = lastName;
        this.email = username;
        this.password = password;
        this.isTheUserAnAdmin = isTheUserAnAdmin;
        this.OIB=OIB;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIsTheUserAnAdmin() {
        return isTheUserAnAdmin;
    }

    public void setIsTheUserAnAdmin(String isTheUserAnAdmin) {
        this.isTheUserAnAdmin = isTheUserAnAdmin;
    }

    public String getOIB() {
        return OIB;
    }

    public void setOIB(String OIB) {
        this.OIB = OIB;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(lastName, user.lastName) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(OIB, user.OIB) && Objects.equals(isTheUserAnAdmin, user.isTheUserAnAdmin);
    }

}
