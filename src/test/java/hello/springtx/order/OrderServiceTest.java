package hello.springtx.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Slf4j
@SpringBootTest
public class OrderServiceTest {

    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    void complete() throws NotEnoughMoneyException {
        //given
        Order order = new Order();
        order.setUsername("normal");

        //when
        orderService.order(order);

        //then

        Order findOrder = orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getUsername()).isEqualTo("normal");
    }

    @Test
    void runtimeException() {
        //given
        Order order = new Order();
        order.setUsername("exception");
        //when, then
        assertThatThrownBy(() -> orderService.order(order))
                .isInstanceOf(RuntimeException.class);
        //then: rolled back, no data should be present
        Optional<Order> orderOptional =
                orderRepository.findById(order.getId());
        assertThat(orderOptional.isEmpty()).isTrue();
    }
    @Test
    void bizException() {
        //given
        Order order = new Order();
        order.setUsername("insufficient funds");
        //when
        try {
            orderService.order(order);
            fail("NotenoughMoneyException needs to pop up");
        } catch (NotEnoughMoneyException e) {
            log.info("insuff funds, use diff account with suff funds");
        }
        //then
        Order findOrder = orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getPayStatus()).isEqualTo("wait");
    }

}
