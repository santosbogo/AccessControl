package accesscontrol.model;

import com.google.gson.Gson;

import javax.persistence.*;

@Entity
public class User{
    @Id
    @GeneratedValue(generator = "userGen", strategy = GenerationType.SEQUENCE)
    private Long userId;

    public Long getId() {
        return userId;
    }

    public void setId(Long id) {
        this.userId = id;
    }

    @Id
    @Column(nullable = false, unique = true)
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String cardId) {
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

    @Column(nullable = false)
    private String Username;

    @Column()
    private boolean state;

    public void activate(){
        this.state = true;
    }

    public void deactivate(){
        this.state = false;
    }

    public boolean state(){
        return state;
    }

    public User(String uid, String firstName, String lastName){
        setUid(uid);
        setLastName(lastName);
        setFirstName(firstName);
        activate();
    }

    public User(){

    }

    public String asJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}