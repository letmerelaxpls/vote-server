package vs.dto.vote;

import java.util.Map;

public record QuestionStatsDto(long totalVotes,
                               Map<Long, Double> answerPercentage) {
}
