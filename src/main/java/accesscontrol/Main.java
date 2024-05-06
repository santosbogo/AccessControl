package accesscontrol;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main {
    public static void main(String[] args) {
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("accessControlDB");
        final EntityManager entityManager = factory.createEntityManager();

        entityManager.close();
        factory.close();
    }
}