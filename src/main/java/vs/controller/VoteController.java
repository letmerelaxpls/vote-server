package vs.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vs.dto.vote.QuestionStatsDto;
import vs.dto.vote.SyncRequest;
import vs.service.vote.VoteService;

@RestController
@RequestMapping("/vote")
@RequiredArgsConstructor
public class VoteController {
    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<Map<Long, QuestionStatsDto>> syncVotes(
            @RequestBody SyncRequest syncRequest) {
        return ResponseEntity.ok(voteService.syncVotes(syncRequest));
    }
}
