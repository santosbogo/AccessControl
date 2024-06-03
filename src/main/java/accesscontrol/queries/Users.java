package accesscontrol.queries;

import accesscontrol.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static accesscontrol.EntityManagerController.*;

public class Users {

    public Users() {

    }

    public User findUserByUid(Long uid) {
        return entityManager().find(User.class, uid);
    }

    public List<User> findAllUsers() {
        TypedQuery<User> query = entityManager().createQuery("SELECT u FROM User u", User.class);
        return query.getResultList();
    }

    public List<User> findUsersByFirstName(String firstName) {
        TypedQuery<User> query = entityManager().createQuery(
            "SELECT u FROM User u WHERE u.firstName = :firstName", User.class);
        query.setParameter("firstName", firstName);
        return query.getResultList();
    }

    public List<User> findUsersByLastName(String lastName) {
        TypedQuery<User> query = entityManager().createQuery(
            "SELECT u FROM User u WHERE u.lastName = :lastName", User.class);
        query.setParameter("lastName", lastName);
        return query.getResultList();
    }

    public List<User> findAllActive() {
        return entityManager().createQuery("SELECT u FROM User u WHERE u.state = true", User.class).getResultList();
    }
    public void persist(User user) {
        entityManager().getTransaction().begin();
        if (user.getUid() == null) { // Nuevo usuario
            entityManager().persist(user);
        } else { // Usuario existente
            entityManager().merge(user);
        }
        entityManager().getTransaction().commit();
    }

    public void delete(User user) {
        entityManager().getTransaction().begin();
        if (!entityManager().contains(user)) {
            user = entityManager().merge(user);
        }
        entityManager().remove(user);
        entityManager().getTransaction().commit();
    }

}