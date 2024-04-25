package accesscontrol.model;

import com.google.gson.Gson;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User{

    @Id
    @Column(nullable = false, unique = true)
    private Long uid;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long cardId) {
        this.uid = cardId;
    }

    @Column(nullable = false)
    private String firstName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(nullable = false)
    private String lastName;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public User(Long uid, String firstName, String lastName){
        setUid(uid);
        setLastName(lastName);
        setFirstName(firstName);
    }

    public User(){

    }

    public String asJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
