package accesscontrol;

import accesscontrol.model.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("accessControlDB");
        final EntityManager entityManager = factory.createEntityManager();

        createAdminUser(entityManager);
        createUsers(entityManager);
        createAccess(entityManager);
        createExit(entityManager);

        entityManager.close();
        factory.close();
    }

    public static void createAdminUser(EntityManager entityManager){
        Admin adminUser = new Admin("Fernando", "Lichtschein", "taylor", "swift");
        entityManager.getTransaction().begin();
        entityManager.persist(adminUser);
        entityManager.getTransaction().commit();
    }

    public static void createUsers(EntityManager entityManager){
        List<User> users = new ArrayList<>();
        users.add(new User("ea 85 b6 b2 ", "Santos", "Bogo"));
        users.add(new User("ea 85 b6 b4 ", "Juan", "Perez"));
        users.add(new User("hougiyf", "Maria", "Gomez"));
        users.add(new User("jugkyey54", "Pedro", "Rodriguez"));
        users.add(new User("srw654hiuth9", "Ana", "Martinez"));
        users.add(new User("a3 53 e8 f4", "Carlos", "Lopez"));



        entityManager.getTransaction().begin();
        for (User user : users) {
            entityManager.persist(user);
        }
        entityManager.getTransaction().commit();
    }

    public static void createAccess(EntityManager entityManager){
        AccessAttempt accessAttempt = new AccessAttempt("qwrfjsw12", java.time.LocalDate.now(), LocalTime.now(), true);
        entityManager.getTransaction().begin();
        entityManager.persist(accessAttempt);
        entityManager.getTransaction().commit();
        AccessAttempt accessAttempt1 = new AccessAttempt("hougiyf", java.time.LocalDate.now(), LocalTime.of(11, 9,30), true);
        entityManager.getTransaction().begin();
        entityManager.persist(accessAttempt1);
        entityManager.getTransaction().commit();
        AccessAttempt accessAttempt2 = new AccessAttempt("jugkyey54", java.time.LocalDate.now(), LocalTime.of(5, 12,30), true);
        entityManager.getTransaction().begin();
        entityManager.persist(accessAttempt2);
        entityManager.getTransaction().commit();
        AccessAttempt accessAttempt3 = new AccessAttempt("srw654hiuth9", java.time.LocalDate.now(), LocalTime.now(), true);
        entityManager.getTransaction().begin();
        entityManager.persist(accessAttempt3);
        entityManager.getTransaction().commit();
        AccessAttempt accessAttempt4 = new AccessAttempt("a3 53 e8 f4", java.time.LocalDate.of(2024, 06, 05), LocalTime.now(), true);
        entityManager.getTransaction().begin();
        entityManager.persist(accessAttempt4);
        entityManager.getTransaction().commit();
    }

    public static void createExit(EntityManager entityManager){
        ExitButton exitButton = new ExitButton(java.time.LocalDate.now(), LocalTime.now());
        entityManager.getTransaction().begin();
        entityManager.persist(exitButton);
        entityManager.getTransaction().commit();
        ExitButton exitButton1 = new ExitButton(java.time.LocalDate.now(), LocalTime.of(10, 30, 30));
        entityManager.getTransaction().begin();
        entityManager.persist(exitButton1);
        entityManager.getTransaction().commit();

    }




}