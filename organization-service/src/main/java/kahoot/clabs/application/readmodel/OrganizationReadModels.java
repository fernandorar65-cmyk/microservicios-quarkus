package kahoot.clabs.application.readmodel;

import java.util.Collections;
import java.util.List;

import kahoot.clabs.application.event.OrganizationMemberPayload;
import kahoot.clabs.application.event.OrganizationProjectionSnapshot;
import kahoot.clabs.domain.aggregate.Organization;

public final class OrganizationReadModels {

    private OrganizationReadModels() {
    }

    public static OrganizationReadModel from(OrganizationProjectionSnapshot snapshot) {
        OrganizationReadModel model = new OrganizationReadModel();
        model.setId(snapshot.organizationId());
        model.setName(snapshot.name());
        model.setSlug(snapshot.slug());
        model.setDescription(snapshot.description());
        model.setLogoUrl(snapshot.logoUrl());
        model.setTimezone(snapshot.timezone());
        model.setLanguage(snapshot.language());
        model.setStatus(snapshot.status());
        model.setMembers(snapshot.members() == null
                ? Collections.emptyList()
                : snapshot.members().stream().map(OrganizationReadModels::toMemberReadModel).toList());
        model.setMemberCount(snapshot.memberCount());
        model.setCreatedAt(snapshot.createdAt());
        model.setUpdatedAt(snapshot.updatedAt());
        return model;
    }

    public static OrganizationReadModel from(Organization organization) {
        return from(OrganizationProjectionSnapshot.from(organization));
    }

    private static OrganizationMemberReadModel toMemberReadModel(OrganizationMemberPayload member) {
        OrganizationMemberReadModel model = new OrganizationMemberReadModel();
        model.setId(member.id());
        model.setUserId(member.userId());
        model.setRoleId(member.roleId());
        model.setStatus(member.status());
        model.setJoinedAt(member.joinedAt());
        return model;
    }

    public static List<OrganizationMemberReadModel> emptyMembers() {
        return Collections.emptyList();
    }
}
