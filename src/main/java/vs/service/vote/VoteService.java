package vs.service.vote;

import java.util.Map;
import vs.dto.vote.QuestionStatsDto;
import vs.dto.vote.SyncRequest;

public interface VoteService {
    Map<Long, QuestionStatsDto> syncVotes(SyncRequest syncRequest);
}
