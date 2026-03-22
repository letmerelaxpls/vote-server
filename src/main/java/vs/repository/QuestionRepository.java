package vs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vs.model.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
}
