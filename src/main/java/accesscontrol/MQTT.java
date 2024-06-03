package accesscontrol;

import accesscontrol.controller.ExitController;
import accesscontrol.dto.DateTimeMessage;
import com.google.gson.Gson;

import javax.persistence.*;
import java.time.*;

import static accesscontrol.EntityManagerController.*;

public class MQTT {
    public static Gson gson = new Gson();
    public static EntityManagerFactory factory = Persistence.createEntityManagerFactory("accessControlDB");
    public static ExitController exitController = new ExitController();
    public static void main(String[] args) {



    }


}
