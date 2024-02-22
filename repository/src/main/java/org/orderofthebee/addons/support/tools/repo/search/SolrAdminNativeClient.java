package org.orderofthebee.addons.support.tools.repo.search;

/**
 * SOLR Admin HTTP Client for native SOLR services.
 * This interface describes operations not covered by the <a href="https://docs.alfresco.com/search-services/latest/admin/restapi/">Alfresco REST API for SOLR</a>
 *
 * @author Angel Borroy
 */
public interface SolrAdminNativeClient {

    /**
     * Get count of documents that require path indexing in SOLR.
     * @param coreName name of the core: alfresco, archive
     * @return Number of documents that require path indexing
     */
    long getCascadeTrackerPendingCount(String coreName);

}
