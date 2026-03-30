package vs.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vs.dto.question.QuestionStatsDto;
import vs.dto.vote.SyncRequest;
import vs.dto.vote.SyncResponse;
import vs.service.vote.VoteService;

@RestController
@RequestMapping("/vote")
@RequiredArgsConstructor
public class VoteController {
    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<SyncResponse> syncVotes(
            @RequestBody SyncRequest syncRequest) {
        return ResponseEntity.ok(voteService.syncVotes(syncRequest));
    }
}
