package hello.springtx.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

//    Jpa represents Order data at commit
    @Transactional
    public void order(Order order) throws NotEnoughMoneyException {
        log.info("order  = {}", order);

        orderRepository.save(order);
        log.info("transaction start");
        if(order.getUsername().equals("exception")){
            log.info("RuntimeException");
            throw new RuntimeException();
        } else if (order.getUsername().equals("insufficient funds")) {
            log.info("NotenoughMoneyException");
            order.setPayStatus("wait");
            throw new NotEnoughMoneyException("insufficient funds");
        } else {
            log.info("normal transac");
            order.setPayStatus("complete");
        }
        log.info("fin Transaction");
    }

}
