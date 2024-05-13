package accesscontrol;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class EntityManagerController {
  final static private ThreadLocal<EntityManager> entityManagerThread = new ThreadLocal<>();

  private static EntityManagerFactory factory;

  public static void setFactory(EntityManagerFactory factory) {
    EntityManagerController.factory = factory;
  }

  public static EntityManager entityManager() {
    final EntityManager entityManager = entityManagerThread.get();
    if (entityManager == null) {
      entityManagerThread.set(factory.createEntityManager());
    }
    return entityManagerThread.get();
  }

  public static void closeEntityManager() {
    final EntityManager entityManager = entityManagerThread.get();
    if (entityManager != null) {
      entityManager.close();
      entityManagerThread.remove();
    }
  }
}
