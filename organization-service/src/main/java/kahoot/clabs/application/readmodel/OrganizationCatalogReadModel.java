package kahoot.clabs.application.readmodel;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrganizationCatalogReadModel {

    private String id;
    private List<CatalogItemReadModel> departments;
    private List<CatalogItemReadModel> jobs;
    private List<CatalogItemReadModel> organizationStatuses;
    private List<CatalogItemReadModel> memberStatuses;
}
