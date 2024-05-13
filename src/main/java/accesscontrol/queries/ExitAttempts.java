package accesscontrol.queries;

import accesscontrol.model.AccessAttempt;
import accesscontrol.model.ExitButton;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class ExitAttempts {

    private final EntityManager entityManager;

    public ExitAttempts(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<ExitButton> findExitsByDate(String date) {
        TypedQuery<ExitButton> query = entityManager.createQuery(
                "SELECT a FROM ExitButton a WHERE a.exitDate = :date", ExitButton.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    public void persist(ExitButton attempt) {
        entityManager.getTransaction().begin();
        entityManager.persist(attempt);
        entityManager.getTransaction().commit();
    }

    public void delete(AccessAttempt attempt) {
        entityManager.getTransaction().begin();
        if (!entityManager.contains(attempt)) {
            attempt = entityManager.merge(attempt);
        }
        entityManager.remove(attempt);
        entityManager.getTransaction().commit();
    }
}
