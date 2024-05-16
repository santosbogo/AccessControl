package accesscontrol.queries;

import accesscontrol.model.AccessAttempt;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.*;
import java.util.List;

import static accesscontrol.EntityManagerController.*;


public class AccessAttempts {

    public AccessAttempts() {
    }

    public List<AccessAttempt> findAttemptsByUid(Long uid) {
        TypedQuery<AccessAttempt> query = entityManager().createQuery(
            "SELECT a FROM AccessAttempt a WHERE a.uid = :uid", AccessAttempt.class);
        query.setParameter("uid", uid);
        return query.getResultList();
    }

    public List<AccessAttempt> findAttemptsByDate(LocalDate date) {
        TypedQuery<AccessAttempt> query = entityManager().createQuery(
            "SELECT a FROM AccessAttempt a WHERE a.attemptDate = :date", AccessAttempt.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    public List<AccessAttempt> findAttemptsByStatus(boolean status) {
        TypedQuery<AccessAttempt> query = entityManager().createQuery(
            "SELECT a FROM AccessAttempt a WHERE a.attemptStatus = :status", AccessAttempt.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    public void persist(AccessAttempt attempt) {
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

    public List<AccessAttempt> findAttemptsByUserUid(Long userUid) {
        TypedQuery<AccessAttempt> query = entityManager().createQuery(
            "SELECT a FROM AccessAttempt a WHERE a.uid = :userUid", AccessAttempt.class);
        query.setParameter("userUid", userUid);
        return query.getResultList();
    }
}
