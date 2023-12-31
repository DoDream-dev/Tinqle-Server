package tinqle.tinqleServer.domain.friendship.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tinqle.tinqleServer.common.dto.ApiResponse;
import tinqle.tinqleServer.common.dto.SliceResponse;
import tinqle.tinqleServer.config.security.PrincipalDetails;
import tinqle.tinqleServer.domain.friendship.dto.request.FriendshipRequestDto.ChangeFriendNicknameRequest;
import tinqle.tinqleServer.domain.friendship.dto.request.FriendshipRequestDto.RequestFriendship;
import tinqle.tinqleServer.domain.friendship.dto.response.FriendshipResponseDto.CodeResponse;
import tinqle.tinqleServer.domain.friendship.dto.response.FriendshipResponseDto.FriendshipReqeustResponse;
import tinqle.tinqleServer.domain.friendship.dto.response.FriendshipResponseDto.ResponseFriendship;
import tinqle.tinqleServer.domain.friendship.service.FriendshipRequestService;
import tinqle.tinqleServer.domain.friendship.service.FriendshipService;

import static tinqle.tinqleServer.common.constant.SwaggerConstants.*;
import static tinqle.tinqleServer.common.dto.ApiResponse.success;
import static tinqle.tinqleServer.domain.friendship.dto.response.FriendshipResponseDto.*;


@RestController
@RequiredArgsConstructor
@Tag(name = TAG_FRIENDSHIP, description = TAG_FRIENDSHIP_DESCRIPTION)
@RequestMapping("/friendships")
public class FriendshipController {

    private final FriendshipService friendshipService;
    private final FriendshipRequestService friendshipRequestService;


    @Operation(summary = FRIENDSHIP_GET_MY_CODE)
    @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    @GetMapping
    public ApiResponse<CodeResponse> getMyCode(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return success(friendshipService.getCode(principalDetails.getId()));
    }

    @Operation(summary = FRIENDSHIP_REQUEST)
    @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    @PostMapping("/request")
    public ApiResponse<ResponseFriendship> friendshipRequest(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid RequestFriendship requestFriendship) {
        return success(friendshipRequestService.friendshipRequest(principalDetails.getId(), requestFriendship));
    }

    @Operation(summary = FRIENDSHIP_REQUEST_APPROVE)
    @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    @PostMapping("/request/{friendshipRequestId}/approval")
    public ApiResponse<FriendshipReqeustResponse> friendshipRequestApprove(
            @PathVariable Long friendshipRequestId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return success(friendshipRequestService.approveFriendshipRequest(principalDetails.getId(), friendshipRequestId));
    }

    @Operation(summary = FRIENDSHIP_REQUEST_REJECT)
    @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    @PostMapping("/request/{friendshipRequestId}/reject")
    public ApiResponse<FriendshipReqeustResponse> friendshipRequestRefuse(
            @PathVariable Long friendshipRequestId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return success(friendshipRequestService.rejectFriendRequest(principalDetails.getId(), friendshipRequestId));
    }

    @Operation(summary = FRIENDSHIP_MANAGE, description = PROFILE_IMAGE_URL_DESCRIPTION)
    @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    @GetMapping("/manage")
    public ApiResponse<SliceResponse<FriendshipCardResponse>> manage(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            Pageable pageable,
            @RequestParam(required = false) Long cursorId) {
        return success(friendshipService.getFriendshipManage(principalDetails.getId(), pageable, cursorId));
    }

    @Operation(summary = FRIENDSHIP_NICKNAME_CHANGE)
    @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    @PostMapping("/nickname/change")
    public ApiResponse<ChangeFriendNicknameResponse> changeFriendNickname(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid ChangeFriendNicknameRequest changeFriendNicknameRequest) {
        return success(friendshipService.changeFriendNickname(principalDetails.getId(), changeFriendNicknameRequest));
    }

    @Operation(summary = FRIENDSHIP_REQUEST_MESSAGE)
    @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    @GetMapping("/request/{friendshipRequestId}/message")
    public ApiResponse<FriendshipRequestMessageResponse> getMessage(
            @PathVariable Long friendshipRequestId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return success(friendshipRequestService.getMessage(principalDetails.getId(), friendshipRequestId));
    }

    @Operation(summary = DELETE_FRIENDSHIP)
    @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    @DeleteMapping("/{friendshipId}")
    public ApiResponse<DeleteFriendResponse> deleteFriend(
            @PathVariable Long friendshipId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return success(friendshipService.deleteFriend(principalDetails.getId(), friendshipId));
    }
}
