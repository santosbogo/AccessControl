package accesscontrol.queries;

import accesscontrol.model.AccessAttempt;
import accesscontrol.model.ExitButton;

import javax.persistence.TypedQuery;
import java.time.*;
import java.util.List;
import static accesscontrol.EntityManagerController.entityManager;


public class ExitAttempts {


    public ExitAttempts() {

    }

    public List<ExitButton> findExitsByDate(LocalDate date) {
        TypedQuery<ExitButton> query = entityManager().createQuery(
                "SELECT a FROM ExitButton a WHERE a.exitDate = :date ORDER BY a.exitTime", ExitButton.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    public void persist(ExitButton attempt) {
        entityManager().getTransaction().begin();
        entityManager().persist(attempt);
        entityManager().getTransaction().commit();
    }

    public void delete(AccessAttempt attempt) {
        entityManager().getTransaction().begin();
        if (!entityManager().contains(attempt)) {
            attempt = entityManager().merge(attempt);
        }
        entityManager().remove(attempt);
        entityManager().getTransaction().commit();
    }
}
