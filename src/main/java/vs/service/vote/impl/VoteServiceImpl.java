package vs.service.vote.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vs.dto.vote.QuestionStatsDto;
import vs.dto.vote.SyncRequest;
import vs.exception.EntityNotFoundException;
import vs.mapper.VoteMapper;
import vs.model.Answer;
import vs.model.Vote;
import vs.repository.AnswerRepository;
import vs.repository.VoteRepository;
import vs.service.vote.VoteService;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {
    private final VoteRepository voteRepository;
    private final AnswerRepository answerRepository;
    private final VoteMapper voteMapper;

    @Override
    public Map<Long, QuestionStatsDto> syncVotes(SyncRequest syncRequest) {
        deleteVotes(syncRequest.playerId(), syncRequest.lastValidSectionId());

        syncData(syncRequest);

        return getResultsForSection(syncRequest.targetSectionId());
    }

    private Map<Long, QuestionStatsDto> getResultsForSection(Long sectionId) {
        List<Answer> sectionAnswers = answerRepository.getAllByQuestionSectionId(sectionId);
        if (sectionAnswers.isEmpty()) {
            throw new EntityNotFoundException("Section with id: " + sectionId
                    + " does not exist!");
        }

        return sectionAnswers.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getQuestion().getId(),
                        Collectors.collectingAndThen(
                                Collectors.toList(), this::mapToQuestionStats)));
    }

    private void deleteVotes(String playerId, Long lastValidSectionId) {
        answerRepository.decrementFutureAnswerCounts(playerId, lastValidSectionId);

        voteRepository.deleteFutureVotes(playerId, lastValidSectionId);
    }

    private void syncData(SyncRequest syncRequest) {
        List<Vote> votes = syncRequest.sectionsToSync().stream()
                .flatMap(s -> s.voteDtos().stream())
                .map(v -> {
                    Vote vote = voteMapper.toModel(v);
                    vote.setPlayerId(syncRequest.playerId());
                    return vote;
                })
                .toList();
        voteRepository.saveAllAndFlush(votes);
        List<Long> answers = votes.stream()
                .map(v -> v.getAnswer().getId())
                .toList();
        answerRepository.incrementCounts(answers);
    }

    private QuestionStatsDto mapToQuestionStats(List<Answer> answers) {
        long total = answers.stream().mapToLong(Answer::getCount).sum();

        Map<Long, Double> percentages = answers.stream().collect(Collectors.toMap(
                Answer::getId,
                a -> {
                    double percentage = (double) a.getCount() * 100 / total;

                    return Math.round(percentage * 10.0) / 10.0;
                }));
        return new QuestionStatsDto(total, percentages);
    }
}
