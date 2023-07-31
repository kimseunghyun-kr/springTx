package hello.springtx.apply;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InitTxTest {

    @Autowired
    Hello hello;


    @Test
    void go(){
        //postconstruct method do not call
    }


    @TestConfiguration
    static class InitTxTestConfig {
        @Bean
        Hello hello() {
            return new Hello();
        }
    }


    @Slf4j
    static class Hello {

        @PostConstruct
        @Transactional
        public void initV1() {
            log.info("init v1");
            log.info("hello init @PostConstruct tx");
            printTxInfo();
        }

        @EventListener(ApplicationReadyEvent.class)
        @Transactional
        public void initV2() {
            log.info("init v2");
            log.info("hello init @EventListerner tx");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("is active = {}", isActive);
        }

    }
}
