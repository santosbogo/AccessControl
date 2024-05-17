package accesscontrol;

import accesscontrol.model.Admin;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("accessControlDB");
        final EntityManager entityManager = factory.createEntityManager();

        createAdminUser(entityManager);

        entityManager.close();
        factory.close();
    }

    public static void createAdminUser(EntityManager entityManager){
        Admin adminUser = new Admin("Fernando", "Lichtschein", "taylor", "swift");
        entityManager.getTransaction().begin();
        entityManager.persist(adminUser);
        entityManager.getTransaction().commit();
    }



}