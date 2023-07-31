package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
public class RollbackTest {
    @Autowired
    RollbackService rollbackService;

    @Test
    void  runtimeException() {
        assertThatThrownBy(() -> rollbackService.runtimeException()).isInstanceOf(RuntimeException.class);
//        rolled back
//        2023-07-31T14:49:30.873+08:00 DEBUG 19152 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction rollback
    }

    @Test
    void checkedException() {
        assertThatThrownBy(() -> rollbackService.checkedException()).isInstanceOf(RollbackService.MyException.class);
//        committed
//        2023-07-31T14:59:19.926+08:00 DEBUG 22284 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
    }

    @Test
    void rolledBackFor() {
        assertThatThrownBy(() -> rollbackService.rollBackFor()).isInstanceOf(RollbackService.MyException.class);
//        rolled back
//        2023-07-31T14:59:58.094+08:00 TRACE 9184 --- [           main] o.s.t.i.TransactionInterceptor           : Completing transaction for [hello.springtx.apply.RollbackTest$RollbackService.rollBackFor] after exception: hello.springtx.apply.RollbackTest$RollbackService$MyException
//2023-07-31T14:59:58.094+08:00 DEBUG 9184 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction rollback

    }

    @TestConfiguration
    static class RollbackServiceTestConfig {
        @Bean
        RollbackService rollbackService(){
            return new RollbackService();
        }
    }

    @Slf4j
    static class RollbackService {
        //runtime exception
        @Transactional
        void runtimeException() {
            log.info("call RuntimeException");
            throw new RuntimeException();
        }
        //checked exception
        @Transactional
        void checkedException() throws MyException {
            log.info("call checkedException");
            throw new MyException();
        }

//        rolled back checkedException
        @Transactional(rollbackFor = MyException.class)
        void rollBackFor() throws MyException {
            log.info("call rollBackFor");
            throw new MyException();
        }


        private class MyException extends Exception {
        }
    }
}
