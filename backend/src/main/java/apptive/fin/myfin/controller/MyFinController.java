package apptive.fin.myfin.controller;

import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.myfin.dto.MyfinRequestDto;
import apptive.fin.myfin.dto.MyfinResponseDto;
import apptive.fin.myfin.service.MyFinService;
import apptive.fin.search.dto.SearchRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorites")
public class MyFinController {

    private final MyFinService myFinService;

    // 찜 목록 조회 (프로필 기반 재계산)
    @PostMapping("/list")
    public ResponseEntity<MyfinResponseDto.List_> getFavoritesWithProfile(
            @RequestBody(required = false) SearchRequestDto request,
            @AuthenticationPrincipal AuthUserDetails userDetails
    ) {
        return ResponseEntity.ok(myFinService.getFavorites(userDetails.getId(), request));
    }

    // 찜 목록 조회 (기본)
    @GetMapping
    public ResponseEntity<MyfinResponseDto.List_> getFavorites(
            @AuthenticationPrincipal AuthUserDetails userDetails
    ) {
        return ResponseEntity.ok(myFinService.getFavorites(userDetails.getId()));
    }

    // 찜 추가
    @PostMapping
    public ResponseEntity<Void> addFavorite(
            @RequestBody MyfinRequestDto request,
            @AuthenticationPrincipal AuthUserDetails userDetails
    ) {
        myFinService.addFavorite(userDetails.getId(), request.productPropertyId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 찜 삭제
    @DeleteMapping("/{productPropertyId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long productPropertyId,
            @AuthenticationPrincipal AuthUserDetails userDetails
    ) {
        myFinService.removeFavorite(userDetails.getId(), productPropertyId);
        return ResponseEntity.noContent().build();
    }

    // 찜 여부 확인
    @GetMapping("/{productPropertyId}/status")
    public ResponseEntity<Boolean> checkFavoriteStatus(
            @PathVariable Long productPropertyId,
            @AuthenticationPrincipal AuthUserDetails userDetails
    ) {
        return ResponseEntity.ok(myFinService.isFavorite(userDetails.getId(), productPropertyId));
    }

    // 찜 개수 조회
    @GetMapping("/count")
    public ResponseEntity<Integer> getFavoriteCount(
            @AuthenticationPrincipal AuthUserDetails userDetails
    ) {
        return ResponseEntity.ok(myFinService.getFavoriteCount(userDetails.getId()));
    }
}
