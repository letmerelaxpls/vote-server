package vs.dto.vote;

import java.util.List;

public record SectionSyncDto(Long sectionId, List<VoteDto> voteDtos) {
}
