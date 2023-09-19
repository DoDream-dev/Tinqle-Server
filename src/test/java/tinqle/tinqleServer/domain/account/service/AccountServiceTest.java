package tinqle.tinqleServer.domain.account.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tinqle.tinqleServer.common.exception.StatusCode;
import tinqle.tinqleServer.domain.account.dto.response.AccountResponseDto.MyAccountInfoResponse;
import tinqle.tinqleServer.domain.account.dto.response.AccountResponseDto.OthersAccountInfoResponse;
import tinqle.tinqleServer.domain.account.dto.response.AccountResponseDto.UpdateNicknameResponse;
import tinqle.tinqleServer.domain.account.dto.response.AccountResponseDto.UpdateStatusResponse;
import tinqle.tinqleServer.domain.account.exception.AccountException;
import tinqle.tinqleServer.domain.account.model.Account;
import tinqle.tinqleServer.domain.account.model.Status;
import tinqle.tinqleServer.domain.account.repository.AccountRepository;
import tinqle.tinqleServer.domain.account.template.AccountTemplate;
import tinqle.tinqleServer.domain.friendship.model.Friendship;
import tinqle.tinqleServer.domain.friendship.repository.FriendshipRepository;
import tinqle.tinqleServer.domain.friendship.template.FriendshipTemplate;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    AccountService accountService;
    @Mock
    AccountRepository accountRepository;
    @Mock
    FriendshipRepository friendshipRepository;

    private static final Account dummyAccountA = AccountTemplate.createDummyAccountA();
    private static final Account dummyAccountB = AccountTemplate.createDummyAccountB();

    @Test
    @DisplayName("내 정보 조회 - 성공")
    public void getMyAccountInfo() throws Exception {
        //given
        given(accountRepository.findById(any())).willReturn(Optional.ofNullable(dummyAccountA));

        //when
        MyAccountInfoResponse responseDto = accountService.getMyAccountInfo(dummyAccountA.getId());

        //then
        assertThat(responseDto.nickname()).isEqualTo("test1");
        assertThat(responseDto.statusImageUrl()).isEqualTo(Status.HAPPY.getStatusImageUrl());
    }

    @Test
    @DisplayName("내 정보 조회 - 실패")
    public void getMyAccountInfoFail() throws Exception {
        //given
        given(accountRepository.findById(any())).willReturn(Optional.empty());

        //when - then
        assertThatThrownBy(() -> accountService.getMyAccountInfo(any()))
                .isInstanceOf(AccountException.class)
                .hasMessage(StatusCode.NOT_FOUND_ACCOUNT.getMessage());
    }

    @Test
    @DisplayName("다른 계정 정보 조회(친구 닉네임 변경 안했을 시) - 성공")
    public void getOthersAccountInfo() throws Exception {
        //given
        Friendship dummyFriendship = FriendshipTemplate.createDummyFriendship(dummyAccountA, dummyAccountB, false);
        given(accountRepository.findById(1L)).willReturn(Optional.ofNullable(dummyAccountA));
        given(accountRepository.findById(2L)).willReturn(Optional.ofNullable(dummyAccountB));
        given(friendshipRepository.findByAccountSelfAndAccountFriend(dummyAccountA, dummyAccountB))
                .willReturn(Optional.ofNullable(dummyFriendship));
        //when
        OthersAccountInfoResponse responseDto = accountService.getOthersAccountInfo(1L, 2L);

        //then
        assertThat(responseDto.nickname()).isEqualTo("test2");
        assertThat(responseDto.statusImageUrl()).isEqualTo(Status.SAD.getStatusImageUrl());
        assertThat(responseDto.isFriend()).isTrue();

    }

    @Test
    @DisplayName("다른 계정 정보 조회(친구 닉네임 변경 시) - 성공")
    public void getOthersAccountInfoChangeNickname() throws Exception {
        //given
        Friendship dummyFriendship = FriendshipTemplate.createDummyFriendship(dummyAccountA, dummyAccountB, true);
        given(accountRepository.findById(1L)).willReturn(Optional.ofNullable(dummyAccountA));
        given(accountRepository.findById(2L)).willReturn(Optional.ofNullable(dummyAccountB));
        given(friendshipRepository.findByAccountSelfAndAccountFriend(dummyAccountA, dummyAccountB))
                .willReturn(Optional.ofNullable(dummyFriendship));

        //when
        OthersAccountInfoResponse responseDto = accountService.getOthersAccountInfo(1L, 2L);

        //then
        assertThat(responseDto.nickname()).isEqualTo("바꾼 닉네임");
        assertThat(responseDto.statusImageUrl()).isEqualTo(Status.SAD.getStatusImageUrl());
        assertThat(responseDto.isFriend()).isTrue();
    }

    @Test
    @DisplayName("다른 계정 정보 조회(친구가 아님) - 성공")
    public void getOthersAccountInfoNotFriend() throws Exception {
        //given
        given(accountRepository.findById(1L)).willReturn(Optional.ofNullable(dummyAccountA));
        given(accountRepository.findById(2L)).willReturn(Optional.ofNullable(dummyAccountB));
        given(friendshipRepository.findByAccountSelfAndAccountFriend(dummyAccountA, dummyAccountB)).
                willReturn(Optional.empty());

        //when
        OthersAccountInfoResponse responseDto = accountService.getOthersAccountInfo(1L, 2L);

        //then
        assertThat(responseDto.nickname()).isEqualTo("test2");
        assertThat(responseDto.statusImageUrl()).isEqualTo(Status.SAD.getStatusImageUrl());
        assertThat(responseDto.isFriend()).isFalse();
    }
    
    @Test
    @DisplayName("다른 계정 정보 조회 - 성공(동일 아이디 요청)")
    public void getOthersAccountInfoSameId() throws Exception {
        //given
        given(accountRepository.findById(1L)).willReturn(Optional.ofNullable(dummyAccountA));

        //when
        OthersAccountInfoResponse responseDto = accountService.getOthersAccountInfo(1L, 1L);
        //then
        assertThat(responseDto.accountId()).isEqualTo(1L);
        assertThat(responseDto.nickname()).isEqualTo("test1");
        assertThat(responseDto.statusImageUrl()).isEqualTo(Status.HAPPY.getStatusImageUrl());
        assertThat(responseDto.isFriend()).isNull();
    }

    @Test
    @DisplayName("코드 검색 - 성공(회원 존재, 친구X)")
    public void searchByCodeNotFriend() throws Exception {
        //given
        given(accountRepository.findById(1L)).willReturn(Optional.ofNullable(dummyAccountA));
        given(accountRepository.findByCode(any())).willReturn(Optional.ofNullable(dummyAccountB));
        given(accountRepository.findById(2L)).willReturn(Optional.ofNullable(dummyAccountB));
        given(friendshipRepository.findByAccountSelfAndAccountFriend(dummyAccountA, dummyAccountB)).
                willReturn(Optional.empty());

        //when
        OthersAccountInfoResponse responseDto = accountService.searchByCode(1L, "random code");

        //then
        assertThat(responseDto.nickname()).isEqualTo("test2");
        assertThat(responseDto.statusImageUrl()).isEqualTo(Status.SAD.getStatusImageUrl());
        assertThat(responseDto.isFriend()).isFalse();
    }

    @Test
    @DisplayName("코드 검색 - 성공(회원 존재, 친구O)")
    public void searchByCodeFriend() throws Exception {
        //given
        Friendship dummyFriendship = FriendshipTemplate.createDummyFriendship(dummyAccountA, dummyAccountB, true);
        given(accountRepository.findById(1L)).willReturn(Optional.ofNullable(dummyAccountA));
        given(accountRepository.findByCode(any())).willReturn(Optional.ofNullable(dummyAccountB));
        given(accountRepository.findById(2L)).willReturn(Optional.ofNullable(dummyAccountB));
        given(friendshipRepository.findByAccountSelfAndAccountFriend(dummyAccountA, dummyAccountB)).
                willReturn(Optional.ofNullable(dummyFriendship));

        //when
        OthersAccountInfoResponse responseDto = accountService.searchByCode(1L, "random code");

        //then
        assertThat(responseDto.nickname()).isEqualTo("바꾼 닉네임");
        assertThat(responseDto.statusImageUrl()).isEqualTo(Status.SAD.getStatusImageUrl());
        assertThat(responseDto.isFriend()).isTrue();
    }

    @Test
    @DisplayName("코드 검색 - 실패(코드 존재 x)")
    public void searchByCodeFail() throws Exception {
        //given
        given(accountRepository.findById(1L)).willReturn(Optional.ofNullable(dummyAccountA));
        given(accountRepository.findByCode(any())).willReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> accountService.searchByCode(1L, "random code"))
                .isInstanceOf(AccountException.class)
                .hasMessage(StatusCode.NOT_FOUND_ACCOUNT_CODE.getMessage());
    }
    
    @Test
    @DisplayName("닉네임 변경 - 성공")
    public void updateNickname() throws Exception {
        //given
        given(accountRepository.findById(1L)).willReturn(Optional.ofNullable(dummyAccountA));

        //when
        UpdateNicknameResponse responseDto = accountService.updateNickname(1L, "changeNickname");

        //then
        assertThat(responseDto.nickname()).isEqualTo("changeNickname");
    }

    @Test
    @DisplayName("닉네임 변경 - 실패(같은 닉네임)")
    public void updateNicknameFail() throws Exception {
        //given
        given(accountRepository.findById(1L)).willReturn(Optional.ofNullable(dummyAccountA));

        //when - then
        assertThatThrownBy(() -> accountService.updateNickname(1L, "test1"))
                .isInstanceOf(AccountException.class)
                .hasMessage(StatusCode.SAME_NICKNAME_ERROR.getMessage());
    }

    @Test
    @DisplayName("Status 변경 - 성공")
    public void updateStatus() throws Exception {
        //given
        given(accountRepository.findById(1L)).willReturn(Optional.ofNullable(dummyAccountA));

        //when
        UpdateStatusResponse responseDto = accountService.updateStatus(1L, "sad");

        //then
        assertThat(responseDto.statusImageUrl()).isEqualTo(Status.SAD.getStatusImageUrl());
    }

    @Test
    @DisplayName("Status 변경 - 실패(paramStatus 존재 x)")
    public void updateStatusFailNotExistStatus() throws Exception {
        //given
        given(accountRepository.findById(1L)).willReturn(Optional.ofNullable(dummyAccountA));

        //when
        assertThatThrownBy(() -> accountService.updateStatus(1L, "오류"))
                .isInstanceOf(AccountException.class)
                .hasMessage(StatusCode.NOT_FOUND_STATUS.getMessage());
    }

    @Test
    @DisplayName("Status 변경 - 실패(accountStatus = requestStatus)")
    public void updateStatusFailSameStatus() throws Exception {
        //given
        given(accountRepository.findById(1L)).willReturn(Optional.ofNullable(dummyAccountA));

        //when
        assertThatThrownBy(() -> accountService.updateStatus(1L, "sad"))
                .isInstanceOf(AccountException.class)
                .hasMessage(StatusCode.SAME_STATUS_ERROR.getMessage());
    }
}