package vs.service.vote.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vs.dto.question.QuestionStatsDto;
import vs.dto.vote.SectionSyncDto;
import vs.dto.vote.SyncRequest;
import vs.dto.vote.SyncResponse;
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
    public SyncResponse syncVotes(SyncRequest syncRequest) {
        deleteVotes(syncRequest.playerId(), syncRequest.lastValidSectionId());

        syncData(syncRequest);

        Set<Long> sectionIds = syncRequest.sectionsToSync().stream()
                .map(SectionSyncDto::sectionId)
                .collect(Collectors.toSet());
        return getSyncResponse(sectionIds);
    }

    private SyncResponse getSyncResponse(Set<Long> sectionIds) {
        List<Answer> sectionToSyncAnswers = answerRepository.getAllByQuestionSectionIdIn(sectionIds);

        Map<Long, List<QuestionStatsDto>> sectionsStats =  sectionToSyncAnswers.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getQuestion().getSectionId(),
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(Answer::getQuestion),
                                questionMap -> questionMap.entrySet().stream()
                                        .map(entry -> {
                                            Long questionId = entry.getKey().getId();
                                            List<Answer> answers = entry.getValue();
                                            return mapToQuestionStats(questionId, answers);
                                        }).toList()
                        )
                )
        );
        return new SyncResponse(sectionsStats);
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

        voteRepository.saveAll(votes);

        List<Long> answers = votes.stream()
                .map(v -> v.getAnswer().getId())
                .toList();

        answerRepository.incrementCounts(answers);
    }

    private QuestionStatsDto mapToQuestionStats(Long questionId, List<Answer> answers) {
        long totalVotes = answers.stream().mapToLong(Answer::getCount).sum();

        Map<Long, Double> percentages = answers.stream().collect(Collectors.toMap(
                Answer::getId,
                a -> {
                    double percentage = (double) a.getCount() * 100 / totalVotes;

                    return Math.round(percentage * 10.0) / 10.0;
                }));
        return new QuestionStatsDto(questionId, totalVotes, percentages);
    }
}
