package vs.dto.vote;

import java.util.List;
import java.util.Map;
import vs.dto.question.QuestionStatsDto;

public record SyncResponse(
        Map<Long, List<QuestionStatsDto>> sectionStats) {
}
