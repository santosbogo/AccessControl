package accesscontrol.queries;

import static accesscontrol.EntityManagerController.*;
import accesscontrol.model.Admin;

public class Admins {

  public Admins(){

  }
  public void persist(Admin admin) {
    entityManager().getTransaction().begin();
    entityManager().persist(admin);
    entityManager().getTransaction().commit();
  }

}
