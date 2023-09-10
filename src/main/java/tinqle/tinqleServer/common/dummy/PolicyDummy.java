package tinqle.tinqleServer.common.dummy;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tinqle.tinqleServer.domain.policy.model.Policy;
import tinqle.tinqleServer.domain.policy.repository.PolicyRepository;

@Component("policyDummy")
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PolicyDummy {

    private final PolicyRepository policyRepository;

    @PostConstruct
    public void init() {
        if (policyRepository.count() > 0) {
            log.info("[0] 정책이 이미 존재합니다");
            return;
        }
        Policy personal = Policy.builder()
                .name("personal")
                .title("개인정보처리방침")
                .content(personalContent)
                .build();

        Policy terms = Policy.builder()
                .name("use")
                .title("이용약관")
                .content(termsContent)
                .build();

        Policy marketing = Policy.builder()
                .name("marketing")
                .title("마케팅 수신동의")
                .content(marketingContent)
                .build();

        Policy age = Policy.builder()
                .name("age")
                .title("만 14세 이상 확인")
                .content(ageContent)
                .build();


        policyRepository.save(personal);
        policyRepository.save(terms);
        policyRepository.save(marketing);
        policyRepository.save(age);

        log.info("[0] 정책 더미 생성 완료");
    }

    private static String termsContent = "";
    private static String personalContent = "";
    private static String marketingContent = "";
    private static String ageContent = "";
}
