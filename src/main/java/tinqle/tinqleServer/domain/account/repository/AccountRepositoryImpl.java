package tinqle.tinqleServer.domain.account.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import tinqle.tinqleServer.domain.account.model.Account;
import tinqle.tinqleServer.domain.comment.model.Comment;
import tinqle.tinqleServer.domain.feed.model.Feed;

import java.util.List;

import static tinqle.tinqleServer.domain.account.model.QAccount.account;
import static tinqle.tinqleServer.domain.comment.model.QComment.comment;

@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Account> findCommentAuthorByFeedDistinctExceptFeedAuthor(Feed feed, Account feedAuthor) {
        JPAQuery<Account> query = queryFactory.select(account).distinct()
                .from(comment)
                .join(comment.account, account)
                .where(comment.feed.id.eq(feed.getId())
                        .and(comment.visibility.isTrue())
                        .and(comment.account.id.notIn(feedAuthor.getId())));

        return query.fetch();
    }

    @Override
    public List<Account> findChildCommentAuthorByParentCommentDistinctExceptAuthors(
            Comment parentComment, Account feedAuthor, Account parentCommentAuthor, Account childCommentAuthor) {
        JPAQuery<Account> query = queryFactory.select(account).distinct()
                .from(comment)
                .leftJoin(comment.account, account)
                .where(comment.parent.id.eq(parentComment.getId())
                        .and(comment.visibility.isTrue())
                        .and(account.id.notIn(feedAuthor.getId(), parentCommentAuthor.getId(), childCommentAuthor.getId())));

        return query.fetch();
    }
}
