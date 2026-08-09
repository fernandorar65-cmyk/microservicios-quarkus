package kahoot.clabs.infrastructure.persistence.mongo.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;

import kahoot.clabs.application.readmodel.LeaderboardReadModel;
import kahoot.clabs.infrastructure.persistence.mongo.document.LeaderboardEntryEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.document.LeaderboardReadDocument;

@ApplicationScoped
public class LeaderboardReadMapper {

    public LeaderboardReadModel toReadModel(LeaderboardReadDocument document) {
        if (document == null) {
            return null;
        }

        LeaderboardReadModel model = new LeaderboardReadModel();
        model.setId(document.getId());
        model.setSessionId(document.getSessionId());
        model.setOrganizationId(document.getOrganizationId());
        model.setUpdatedAt(document.getUpdatedAt());
        model.setRanking(toRanking(document.getRanking()));
        return model;
    }

    private List<LeaderboardReadModel.LeaderboardEntry> toRanking(List<LeaderboardEntryEmbed> embeds) {
        if (embeds == null || embeds.isEmpty()) {
            return Collections.emptyList();
        }

        return embeds.stream().map(this::toEntry).collect(Collectors.toList());
    }

    private LeaderboardReadModel.LeaderboardEntry toEntry(LeaderboardEntryEmbed embed) {
        LeaderboardReadModel.LeaderboardEntry entry = new LeaderboardReadModel.LeaderboardEntry();
        entry.setPosition(embed.getPosition());
        entry.setSessionPlayerId(embed.getSessionPlayerId());
        entry.setUserId(embed.getUserId());
        entry.setNickname(embed.getNickname());
        entry.setScore(embed.getScore());
        return entry;
    }
}
