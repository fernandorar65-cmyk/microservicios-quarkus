package kahoot.clabs.application.readmodel;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import kahoot.clabs.domain.aggregate.Organization;
import kahoot.clabs.domain.entity.OrganizationMember;

public final class OrganizationReadModels {

    private OrganizationReadModels() {
    }

    public static OrganizationReadModel from(Organization organization) {
        List<OrganizationMember> members = organization.getMembers();
        OrganizationReadModel model = new OrganizationReadModel();
        model.setId(organization.getId());
        model.setName(organization.getName().value());
        model.setSlug(organization.getSlug().value());
        model.setDescription(organization.getDescription());
        model.setLogoUrl(organization.getLogo());
        model.setTimezone(organization.getTimezone());
        model.setLanguage(organization.getLanguage());
        model.setStatus(organization.getStatus().name());
        model.setMembers(members.stream().map(OrganizationReadModels::toMemberReadModel).toList());
        model.setMemberCount(members.size());
        model.setCreatedAt(toInstant(organization.getCreatedAt()));
        model.setUpdatedAt(toInstant(organization.getUpdatedAt()));
        return model;
    }

    private static OrganizationMemberReadModel toMemberReadModel(OrganizationMember member) {
        OrganizationMemberReadModel model = new OrganizationMemberReadModel();
        model.setId(member.getId());
        model.setUserId(member.getUserId());
        model.setRoleId(member.getRoleId());
        model.setStatus(member.getStatus().name());
        model.setJoinedAt(toInstant(member.getJoinedAt()));
        return model;
    }

    private static Instant toInstant(java.time.LocalDateTime value) {
        return value == null ? null : value.toInstant(ZoneOffset.UTC);
    }

    public static List<OrganizationMemberReadModel> emptyMembers() {
        return Collections.emptyList();
    }
}
