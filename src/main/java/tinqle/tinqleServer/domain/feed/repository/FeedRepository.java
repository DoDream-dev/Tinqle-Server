package tinqle.tinqleServer.domain.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tinqle.tinqleServer.domain.feed.model.Feed;

import java.time.LocalDateTime;
import java.util.List;


public interface FeedRepository extends JpaRepository<Feed, Long>, FeedRepositoryCustom {

    List<Feed> findAllByCreatedAtBeforeAndVisibilityIsTrue(LocalDateTime beforeTime);
}
