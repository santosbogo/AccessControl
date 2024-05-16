package accesscontrol.queries;

import static accesscontrol.EntityManagerController.*;
import accesscontrol.model.Admin;

import javax.persistence.*;
import java.util.*;

public class Admins {

  public Admins(){

  }

  public Admin findAdminByUsernam(String username) {
    TypedQuery<Admin> query = entityManager().createQuery("SELECT a " +
      "FROM Admin a " +
      "WHERE a.username LIKE :username", Admin.class);
    query.setParameter("username", username);
    List<Admin> users = query.getResultList();
    if (users.isEmpty()) {
      return null;
    }
    return users.get(0);
  }
  public void persist(Admin admin) {
    entityManager().getTransaction().begin();
    entityManager().persist(admin);
    entityManager().getTransaction().commit();
  }

  public Admin findAdminByUsernameAndPassword(String username, String password) {
    TypedQuery<Admin> query = entityManager().createQuery("SELECT a " +
      "FROM Admin a " +
      "WHERE a.password LIKE :password AND a.username LIKE: username", Admin.class);
    query.setParameter("username", username);
    query.setParameter("password", password);
    return query.getSingleResult();
  }

}
