package eu.dnetlib.dhp.model.mdstore;

import java.io.Serializable;


/**
 * @author Sandro La Bruzzo
 *
 * Provenace class models the provenance of the record in the metadataStore
 * It contains the identifier and the name of the datasource that gives the
 * record
 *
 */
public class Provenance implements Serializable {

    private String datasourceId;


    private String datasourceName;

    private String nsPrefix;

    public Provenance() {

    }

    public Provenance(String datasourceId, String datasourceName, String nsPrefix) {
        this.datasourceId = datasourceId;
        this.datasourceName = datasourceName;
        this.nsPrefix = nsPrefix;
    }

    public String getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(String datasourceId) {
        this.datasourceId = datasourceId;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public String getNsPrefix() {
        return nsPrefix;
    }

    public void setNsPrefix(String nsPrefix) {
        this.nsPrefix = nsPrefix;
    }
}
