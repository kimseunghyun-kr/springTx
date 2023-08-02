package hello.springtx.propagation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final LogRepository logRepository;

// JoinV1 + {service Transaction} + {memberRepositoryTransaction} + {LogRepositoryTransaction}
    public void joinV1NTTT(String username) {
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("== memberRepository call begin");
        memberRepository.save(member);
        log.info("== memberRepository call completed");

        log.info("== LogRepository call begin");
        logRepository.save(logMessage);
        log.info("== LogRepository call completed");

    }

    @Transactional
    public void joinV1TTT(String username) {
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("== memberRepository call begin");
        memberRepository.save(member);
        log.info("== memberRepository call completed");

        log.info("== LogRepository call begin");
        logRepository.save(logMessage);
        log.info("== LogRepository call completed");

    }

    @Transactional
    public void joinV1TNTNT(String username) {
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("== memberRepository call begin");
        memberRepository.NTsave(member);
        log.info("== memberRepository call completed");

        log.info("== LogRepository call begin");
        logRepository.NTsave(logMessage);
        log.info("== LogRepository call completed");

    }

    @Transactional
    public void joinV2(String username) {
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("== memberRepository call begin");
        memberRepository.save(member);
        log.info("== memberRepository call completed");

        log.info("== LogRepository call begin");
        try {
            logRepository.save(logMessage);
        } catch (RuntimeException e) {
            log.info("log save failed, logMessage = {}", logMessage.getMessage());
            log.info("returning to normal control flow");
        }
        log.info("== LogRepository call completed");

    }

    @Transactional
    public void joinV2REQNEW(String username) {
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("== memberRepository call begin");
        memberRepository.save(member);
        log.info("== memberRepository call completed");

        log.info("== LogRepository call begin");
        try {
            logRepository.saveREQNEW(logMessage);
        } catch (RuntimeException e) {
            log.info("log save failed, logMessage = {}", logMessage.getMessage());
            log.info("returning to normal control flow");
        }
        log.info("== LogRepository call completed");

    }
}
