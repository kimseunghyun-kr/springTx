package hello.springtx.propagation;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class LogRepository {

    private final EntityManager em;

    @Transactional
    public void save(Log logMessage) {
        log.info("saving log");
        em.persist(logMessage);

        if (logMessage.getMessage().contains("logException")) {
            log.info("Exception occur if log saved");
            throw new RuntimeException("Log exception occured");
        }
    }


    public void NTsave(Log logMessage) {
        log.info("saving log");
        em.persist(logMessage);

        if (logMessage.getMessage().contains("logException")) {
            log.info("Exception occur if log saved");
            throw new RuntimeException("Log exception occured");
        }
    }


    public Optional<Log> find (String message) {
        return em.createQuery("select l from Log l where l.message = :message", Log.class)
                    .setParameter("message", message)
                    .getResultList().stream().findAny();
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveREQNEW(Log logMessage) {
        log.info("saving log");
        em.persist(logMessage);

        if (logMessage.getMessage().contains("logException")) {
            log.info("Exception occur if log saved");
            throw new RuntimeException("Log exception occured");
        }
    }
}
