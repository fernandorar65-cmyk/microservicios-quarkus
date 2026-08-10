package kahoot.clabs.infrastructure.persistence.mongo.mapper;

import java.util.Collections;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import kahoot.clabs.application.readmodel.OrganizationMemberReadModel;
import kahoot.clabs.application.readmodel.OrganizationMemberUserReadModel;
import kahoot.clabs.application.readmodel.OrganizationReadModel;
import kahoot.clabs.infrastructure.persistence.mongo.document.OrganizationMemberEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.document.OrganizationMemberUserEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.document.OrganizationReadDocument;

@ApplicationScoped
public class OrganizationReadMapper {

    public OrganizationReadModel toReadModel(OrganizationReadDocument document) {
        OrganizationReadModel model = new OrganizationReadModel();
        model.setId(document.getId());
        model.setName(document.getName());
        model.setSlug(document.getSlug());
        model.setLogoUrl(document.getLogoUrl());
        model.setDescription(document.getDescription());
        model.setTimezone(document.getTimezone());
        model.setLanguage(document.getLanguage());
        model.setStatus(document.getStatus());
        model.setMembers(toMemberReadModels(document.getMembers()));
        model.setMemberCount(document.getMemberCount());
        model.setCreatedAt(document.getCreatedAt());
        model.setUpdatedAt(document.getUpdatedAt());
        return model;
    }

    private List<OrganizationMemberReadModel> toMemberReadModels(List<OrganizationMemberEmbed> members) {
        if (members == null) {
            return Collections.emptyList();
        }
        return members.stream().map(this::toMemberReadModel).toList();
    }

    private OrganizationMemberReadModel toMemberReadModel(OrganizationMemberEmbed member) {
        OrganizationMemberReadModel model = new OrganizationMemberReadModel();
        model.setId(member.getId());
        model.setUserId(member.getUserId());
        model.setUser(toMemberUserReadModel(member.getUser()));
        model.setRoleId(member.getRoleId());
        model.setStatus(member.getStatus());
        model.setJoinedAt(member.getJoinedAt());
        return model;
    }

    private OrganizationMemberUserReadModel toMemberUserReadModel(OrganizationMemberUserEmbed user) {
        if (user == null) {
            return null;
        }
        OrganizationMemberUserReadModel model = new OrganizationMemberUserReadModel();
        model.setFullName(user.getFullName());
        model.setEmail(user.getEmail());
        return model;
    }
}
