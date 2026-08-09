package kahoot.clabs.infrastructure.persistence.mongo.document;

import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "organization_catalogs")
@Getter
@Setter
@NoArgsConstructor
public class OrganizationCatalogReadDocument {

    @BsonId
    private String id;

    private List<CatalogItemEmbed> departments;
    private List<CatalogItemEmbed> jobs;
    private List<CatalogItemEmbed> organizationStatuses;
    private List<CatalogItemEmbed> memberStatuses;
}
