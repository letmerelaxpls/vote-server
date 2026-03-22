package vs.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vs.config.MapperConfig;
import vs.dto.vote.VoteDto;
import vs.model.Vote;

@Mapper(config = MapperConfig.class)
public interface VoteMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "question.id", source = "questionId")
    @Mapping(target = "answer.id", source = "answerId")
    Vote toModel(VoteDto voteDto);
}
