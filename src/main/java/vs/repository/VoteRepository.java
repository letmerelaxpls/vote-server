package vs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vs.model.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Vote v WHERE v.playerId = :playerId "
            + "AND v.question.id IN (SELECT q.id FROM Question q "
            + "WHERE q.sectionId > :lastSectionId)")
    int deleteFutureVotes(@Param("playerId") String playerId,
                          @Param("lastSectionId") Long lastSectionId);
}
