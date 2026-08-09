package kahoot.clabs.infrastructure.persistence.mongo.adapter;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import kahoot.clabs.application.port.out.OrganizationProjectionPort;
import kahoot.clabs.application.readmodel.OrganizationMemberReadModel;
import kahoot.clabs.application.readmodel.OrganizationReadModel;
import kahoot.clabs.infrastructure.persistence.mongo.document.OrganizationMemberEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.document.OrganizationReadDocument;
import kahoot.clabs.infrastructure.persistence.mongo.repository.OrganizationMongoRepository;

@ApplicationScoped
public class OrganizationProjectionAdapter implements OrganizationProjectionPort {

    @Inject
    OrganizationMongoRepository repository;

    @Override
    public void save(OrganizationReadModel readModel) {
        repository.persistOrUpdate(toDocument(readModel));
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private OrganizationReadDocument toDocument(OrganizationReadModel readModel) {
        OrganizationReadDocument document = new OrganizationReadDocument();
        document.setId(readModel.getId());
        document.setName(readModel.getName());
        document.setSlug(readModel.getSlug());
        document.setLogoUrl(readModel.getLogoUrl());
        document.setDescription(readModel.getDescription());
        document.setTimezone(readModel.getTimezone());
        document.setLanguage(readModel.getLanguage());
        document.setStatus(readModel.getStatus());
        document.setMembers(toMemberEmbeds(readModel.getMembers()));
        document.setMemberCount(readModel.getMemberCount());
        document.setCreatedAt(readModel.getCreatedAt());
        document.setUpdatedAt(readModel.getUpdatedAt());
        return document;
    }

    private List<OrganizationMemberEmbed> toMemberEmbeds(List<OrganizationMemberReadModel> members) {
        if (members == null || members.isEmpty()) {
            return Collections.emptyList();
        }
        return members.stream().map(this::toMemberEmbed).toList();
    }

    private OrganizationMemberEmbed toMemberEmbed(OrganizationMemberReadModel member) {
        OrganizationMemberEmbed embed = new OrganizationMemberEmbed();
        embed.setId(member.getId());
        embed.setUserId(member.getUserId());
        embed.setRoleId(member.getRoleId());
        embed.setStatus(member.getStatus());
        embed.setJoinedAt(member.getJoinedAt());
        return embed;
    }
}
