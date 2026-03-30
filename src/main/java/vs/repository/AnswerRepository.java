package vs.repository;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vs.model.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Answer a SET a.count = a.count - 1 "
            + "WHERE a.id IN (SELECT v.answer.id FROM Vote v WHERE v.playerId = :playerId "
            + "AND v.question.id IN (SELECT q.id FROM Question q "
            + "WHERE q.sectionId > :lastSectionId))")
    void decrementFutureAnswerCounts(@Param("playerId") String playerId,
                                     @Param("lastSectionId") Long lastSectionId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Answer a SET a.count = a.count + 1 WHERE a.id IN :answerIds")
    void incrementCounts(List<Long> answerIds);

    @EntityGraph(attributePaths = {"question"})
    List<Answer> getAllByQuestionSectionIdIn(Set<Long> sectionIds);
}
