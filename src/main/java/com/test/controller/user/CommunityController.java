package com.test.controller.user;

import com.test.database.dto.CommunityDto;
import com.test.database.dto.CommunityListDto;
import com.test.database.dto.UserDto;
import com.test.database.model.Member;
import com.test.database.model.enums.MemberRole;
import com.test.database.requests.CreateCommunityRequest;
import com.test.service.CommunityService;
import com.test.service.MembershipService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/communities")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final MembershipService membershipService;

    @GetMapping
    public ResponseEntity<List<CommunityListDto>> getAllCommunities() {
        List<CommunityListDto> communityList = communityService.getAllCommunityList();
        return ResponseEntity.ok(communityList);
    }

    @PostMapping
    public ResponseEntity<CommunityDto> createCommunity(@Valid @RequestBody CreateCommunityRequest request) {
        CommunityDto communityDto = membershipService.createCommunity(request.getName(), request.getDescription());
        return ResponseEntity.ok(communityDto);
    }

    @PostMapping("/{communityId}/join")
    public ResponseEntity<Void> joinCommunity(@PathVariable("communityId") Long communityId) {
        membershipService.joinCommunity(communityId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{communityId}/leave")
    public ResponseEntity<Void> leaveCommunity(@PathVariable("communityId") Long communityId) {
        membershipService.leaveCommunity(communityId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{communityId}/delete")
    public ResponseEntity<String> deleteCommunity(@PathVariable Long communityId) {
        membershipService.deleteCommunityByOwner(communityId);
        return ResponseEntity.ok("Сообщество успешно удалено");
    }

    @GetMapping("/{communityId}/members")
    public ResponseEntity<List<Member>> getCommunityMembers(@PathVariable("communityId") Long communityId) {
        List<Member> members = communityService.getCommunityMembers(communityId);
        return ResponseEntity.ok(members);
    }


    @PostMapping("/{communityId}/members/{memberId}/update-role")
    public ResponseEntity<String> updateMemberRole(@PathVariable("communityId") Long communityId,
                                                   @PathVariable("memberId") Long memberId,
                                                   @RequestParam("newRole") @NotNull MemberRole newRole) {
        membershipService.updateMemberRole(communityId, memberId, newRole);
        return ResponseEntity.ok("Роль участника изменена на " + newRole);
    }
}
