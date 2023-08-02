package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Slf4j
@SpringBootTest
public class MemberServiceTest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    LogRepository logRepository;

    @Test
    void OuterTx0ff_success() {

//        given
        String username = "outerTx0ff_success";

//        when
        memberService.joinV1(username);

//        then : all data is saved normally
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }


    @Test
    void OuterTx0ff_fail() {

//        given
        String username = "logException_OuterTx0ff_fail";

//        when
        assertThatThrownBy(()-> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

//        then : all data is saved normally
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }

    @Test
    void singleTx() {

//        given
        String username = "singleTx";

//        when
        memberService.joinV1(username);


//        then : all data is saved normally
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    @Test
    void OuterTx0n_success() {

//        given
        String username = "outerTx0n_success";

//        when
        memberService.joinV1T(username);

//        then : all data is saved normally
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * MemberService @Transactional:ON
     * MemberRepository @Transactional:ON
     * LogRepository @Transactional:ON Exception
     */
    @Test
    void OuterTx0n_fail() {

//        given
        String username = "logException_outerTx0n_fail";

//        when
        assertThatThrownBy(()-> memberService.joinV1T(username))
                .isInstanceOf(RuntimeException.class);

//        then : all data is rolled back
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }

}
