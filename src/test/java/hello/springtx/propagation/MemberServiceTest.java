package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

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

    /**
     * MemberService @Transactional:OFF
     * MemberRepository @Transactional:ON
     * LogRepository @Transactional:ON
     *
     *
     */

    @Test
    void OuterTx0ff_success() {

//        given
        String username = "outerTx0ff_success";

//        when
        memberService.joinV1NTTT(username);

//        then : all data is saved normally
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }


    /**
     * MemberService @Transactional:OFF
     * MemberRepository @Transactional:ON
     * LogRepository @Transactional:ON Exception
     */
    @Test
    void OuterTx0ff_fail() {

//        given
        String username = "logException_OuterTx0ff_fail";

//        when
        assertThatThrownBy(()-> memberService.joinV1NTTT(username))
                .isInstanceOf(RuntimeException.class);

//        then : all data is saved normally
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }


    /**
     * MemberService @Transactional:ON
     * MemberRepository @Transactional:OFF
     * LogRepository @Transactional:OFF
     */
    @Test
    void singleTx() {

//        given
        String username = "singleTx";

//        when
        memberService.joinV1TNTNT(username);


//        then : all data is saved normally
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

//    all transactions will be used from here on JOINV1TTT.

//    this means
//     * MemberService @Transactional:ON
//     * MemberRepository @Transactional:ON
//     * LogRepository @Transactional:ON

//    as entry point is from JoinV1 with @Transaction { in MemberService }, the
//    @Transactional in the MemberRepository and the LogRepository will be
//    considered an extension and not a new transaction
//    MemberRespository and LogRepository still has the @Transaction
//    as we are given a hypothetical scenario where we assume that there exists other
//    methods which accesses the MemberRepository and LogRepository directly through a transaction
    @Test
    void OuterTx0n_success() {

//        given
        String username = "outerTx0n_success";

//        when
        memberService.joinV1TTT(username);

//        then : all data is saved normally
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * MemberService @Transactional:ON
     * MemberRepository @Transactional:ON
     * LogRepository @Transactional:ON Exception
     * both are empty as the rollbackOnly option within the TransactionManager must not be marked.
     * process similar to allMatch for the rollback option is called at the first initiating transac
     * commit
     *
     * the most evident sign of a rollbackOnly being marked while the initiating transaction commits
     * is the UnexpectedRollbackException that will be called.
     */
    @Test
    void OuterTx0n_fail() {

//        given
        String username = "logException_outerTx0n_fail";

//        when
        assertThatThrownBy(()-> memberService.joinV1TTT(username))
                .isInstanceOf(RuntimeException.class);

//        then : all data is rolled back
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }

//    assume the following requirement is added
//    even if the logging fails and triggers a rollback ,the registration of the member should work and be committed

    /**
     * MemberService @Transactional:ON
     * MemberRepository @Transactional:ON
     * LogRepository @Transactional:ON Exception
     */

//     what most amateur devs try to do to accomplish above but fails
    @Test
    void recoverException_fail() {
        //given
        String username = "logException_recoverException_fail";
        //when
        assertThatThrownBy(() -> memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);
        //then: 모든 데이터가 롤백된다.
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * MemberService @Transactional:ON
     * MemberRepository @Transactional:ON
     * LogRepository @Transactional(REQUIRES_NEW) Exception
     */

//    memberService.joinV2REQNEW(username);
//    does not produce a UnexpectedRollback exception as the logRepository now uses
//    a separate connection that is not committed by the initiating transaction in
//    MemberService joinV2ReqNew.
//    this is due to the propagation.requires_new creating a new transaction and rolling back on its own

//    actual solution;
//    use sparingly as this will cause transactionManager to handle 2 active connections concurrently
//    leading to performance issues

//    perhaps a Member Facade that functions as an entry point
//    completely separating the Log Repository and the MemberService
//    serializing each connection operation could be a viable solution
//    (though serializability may also mean performance bottlenecks but it does not seem to be the case here)

    @Test
    void recoverException_success() {
        //given
        String username = "logException_recoverException_success";
        //when
        memberService.joinV2REQNEW(username);
        //then: member 저장, log 롤백
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }


}
