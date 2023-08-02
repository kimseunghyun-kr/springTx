package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Slf4j
public class BasicTxTest {

    @Autowired
    PlatformTransactionManager txManager;

    @TestConfiguration
    static class Config {
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Test
    void commit(){
        log.info("transaction start");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("transaction commit");
        txManager.commit(status);
        log.info("transaction committed");
    }


    @Test
    void rollback(){
        log.info("transaction start");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("transaction rollback");
        txManager.rollback(status);
        log.info("transaction rolled back");
    }

    @Test
    void double_commit(){
        log.info("transaction 1 start");
        TransactionStatus status1 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("transaction 1 commit");
        txManager.commit(status1);
        log.info("transaction 1 committed");


        log.info("transaction 2 start");
        TransactionStatus status2 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("transaction 2 commit");
        txManager.commit(status2);
        log.info("transaction 2 committed");
    }

    @Test
    void commit_then_rollback(){
        log.info("transaction 1 start");
        TransactionStatus status1 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("transaction 1 commit");
        txManager.commit(status1);
        log.info("transaction 1 committed");


        log.info("transaction 2 start");
        TransactionStatus status2 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("transaction 2 rollback");
        txManager.rollback(status2);
        log.info("transaction 2 rolled back");
    }

//    required -> all required to commit for entirety to commit ( final state = commit ).
//    like allMatch
    @Test
    void inner_commit() {
        log.info("external(outer) transaction start");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction() = {}", outer.isNewTransaction());

        log.info("inner transaction start(participating in existing transaction -> increasing transaction scope");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner.isNewTransaction() = {}", inner.isNewTransaction());

        log.info("transaction inner commit");
        txManager.commit(inner);
        log.info("transaction inner is not actually committed. a transaction can only commit once" +
                "this is a dummy code of sorts to allow mental mindmapping for" +
                "devs");

        log.info("transaction outer commit");
        txManager.commit(outer);
        log.info("transaction committed");
    }
    //    like allMatch

    @Test
    void outer_rollback() {
        log.info("external(outer) transaction start");
        TransactionStatus outer = txManager.getTransaction(new
                DefaultTransactionAttribute());
        log.info("inner transaction start");
        TransactionStatus inner = txManager.getTransaction(new
                DefaultTransactionAttribute());
        log.info("transaction inner commit");
        txManager.commit(inner);
        log.info("transaction outer rollback");
        txManager.rollback(outer);
    }


    //    like allMatch

    @Test
    void inner_rollback() {
        log.info("external(outer) transaction start");
        TransactionStatus outer = txManager.getTransaction(new
                DefaultTransactionAttribute());
        log.info("inner transaction start");
        TransactionStatus inner = txManager.getTransaction(new
                DefaultTransactionAttribute());
        log.info("transaction inner rollback");
        txManager.rollback(inner);
        log.info("transaction outer commit");
        assertThatThrownBy(() -> txManager.commit(outer))
                .isInstanceOf(UnexpectedRollbackException.class);
    }

    @Test
    void inner_rollback_requires_new() {

        log.info("external(outer) transaction start");
        TransactionStatus outer = txManager.getTransaction(new
                DefaultTransactionAttribute());
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction());

        log.info("inner transaction start");


        log.info("suspends current transaction, inititates new transaction for the inner. " +
                "different connection between inner and outer");

        log.info("can be seen from inner having Acquired Connection [HikariProxyConnection@**** wrapping conn1" +
                "while outer has Acquired Connection [HikariProxyConnection@**** wrapping conn0");
        DefaultTransactionAttribute definition = new DefaultTransactionAttribute();

        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus inner = txManager.getTransaction(definition );

        log.info("inner.isNewTransaction()={}", inner.isNewTransaction());

        log.info("transaction inner rollback");
        txManager.rollback(inner);

        log.info("transaction outer commit");
        txManager.commit(outer);

    }


//    some side effect on TransactionSynchronisationManager

//    @Test
//    void inner_commit_requires_new() {
//
//        log.info("external(outer) transaction start");
//        TransactionStatus outer = txManager.getTransaction(new
//                DefaultTransactionAttribute());
//        log.info("outer.isNewTransaction()={}", outer.isNewTransaction());
//
//        DefaultTransactionAttribute definition = new DefaultTransactionAttribute();
//
//        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
//        TransactionStatus inner = txManager.getTransaction(definition );
//
//        log.info("inner.isNewTransaction()={}", inner.isNewTransaction());
//
//        log.info("transaction inner commit");
//        txManager.commit(outer);
//
//        log.info("transaction outer commit");
//        txManager.commit(inner);
//
//    }
}
