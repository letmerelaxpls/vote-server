package vs.dto.vote;

import java.util.List;

public record SyncRequest(
        String playerId,
        Long lastValidSectionId,
        List<SectionSyncDto> sectionsToSync) {
}
