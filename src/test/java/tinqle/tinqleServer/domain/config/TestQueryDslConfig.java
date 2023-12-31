package tinqle.tinqleServer.domain.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import tinqle.tinqleServer.domain.account.repository.AccountRepositoryImpl;
import tinqle.tinqleServer.domain.feed.repository.FeedRepositoryImpl;
import tinqle.tinqleServer.domain.friendship.repository.FriendshipRepositoryImpl;
import tinqle.tinqleServer.domain.notification.repository.NotificationRepositoryImpl;

@TestConfiguration
public class TestQueryDslConfig {
    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public AccountRepositoryImpl accountRepositoryImpl() {
        return new AccountRepositoryImpl(jpaQueryFactory());
    }

    @Bean
    public FriendshipRepositoryImpl friendshipRepositoryImpl() {
        return new FriendshipRepositoryImpl(jpaQueryFactory());
    }

    @Bean
    public FeedRepositoryImpl feedRepositoryImpl() {
        return new FeedRepositoryImpl(jpaQueryFactory());
    }

    @Bean
    public NotificationRepositoryImpl notificationRepositoryImpl() {
        return new NotificationRepositoryImpl(jpaQueryFactory());
    }
}