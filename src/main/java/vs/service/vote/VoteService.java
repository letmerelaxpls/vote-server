package vs.service.vote;

import vs.dto.vote.SyncRequest;
import vs.dto.vote.SyncResponse;

public interface VoteService {
    SyncResponse syncVotes(SyncRequest syncRequest);
}
