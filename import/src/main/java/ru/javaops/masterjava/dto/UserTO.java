package ru.javaops.masterjava.dto;


public class UserTO {
    private String name;
    private String email;
    private String flag;

    public UserTO(String name, String email, String flag) {
        this.name = name;
        this.email = email;
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "UserTO{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", flag='" + flag + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserTO)) return false;

        UserTO userTO = (UserTO) o;

        if (name != null ? !name.equals(userTO.name) : userTO.name != null) return false;
        if (email != null ? !email.equals(userTO.email) : userTO.email != null) return false;
        return flag != null ? flag.equals(userTO.flag) : userTO.flag == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (flag != null ? flag.hashCode() : 0);
        return result;
    }
}
