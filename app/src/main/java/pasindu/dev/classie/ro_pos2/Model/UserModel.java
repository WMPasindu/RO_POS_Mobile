package pasindu.dev.classie.ro_pos2.Model;

public class UserModel {
    private String userId;
    private String userName;
    private String address;
    private String phone;
    private String IMIE;

    public UserModel() {
    }

    public UserModel(String userId, String userName, String address, String phone, String IMIE) {
        this.userId = userId;
        this.userName = userName;
        this.address = address;
        this.phone = phone;
        this.IMIE = IMIE;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIMIE() {
        return IMIE;
    }

    public void setIMIE(String IMIE) {
        this.IMIE = IMIE;
    }
}
