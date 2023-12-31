package tinqle.tinqleServer.domain.friendship.dto.response;

import lombok.Builder;
import tinqle.tinqleServer.domain.friendship.model.Friendship;

public class FriendshipResponseDto {

    public record CodeResponse(
            String code
    ) {}

    public record ResponseFriendship(
            Long friendshipRequestId
    ) {}

    public record FriendshipReqeustResponse(
            boolean result
    ) {
        public static FriendshipReqeustResponse of(boolean result) {
            return new FriendshipReqeustResponse(!result);
        }
    }

    public record FriendshipCardResponse(
            Long accountId,
            Long friendshipId,
            String profileImageUrl,
            String friendNickname,
            String status
    ) {
        @Builder
        public FriendshipCardResponse{}

        public static FriendshipCardResponse of(Friendship friendship) {
            if (friendship.isChangeFriendNickname()) {
                return FriendshipCardResponse.builder()
                        .accountId(friendship.getAccountFriend().getId())
                        .profileImageUrl(friendship.getAccountFriend().getProfileImageUrl())
                        .friendshipId(friendship.getId())
                        .friendNickname(friendship.getFriendNickname())
                        .status(friendship.getAccountFriend().getStatus().toString())
                        .build();
            } else {
                return FriendshipCardResponse.builder()
                        .accountId(friendship.getAccountFriend().getId())
                        .profileImageUrl(friendship.getAccountFriend().getProfileImageUrl())
                        .friendshipId(friendship.getId())
                        .friendNickname(friendship.getAccountFriend().getNickname())
                        .status(friendship.getAccountFriend().getStatus().toString())
                        .build();
            }
        }
    }

    public record ChangeFriendNicknameResponse(String friendNickname) {}

    public record FriendshipRequestMessageResponse(String message) {}

    public record DeleteFriendResponse(boolean result) {}
}
