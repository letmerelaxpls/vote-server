package vs.dto.question;

import java.util.Map;

public record QuestionStatsDto(long questionId,
                               long totalVotes,
                               Map<Long, Double> answerPercentage) {
}
