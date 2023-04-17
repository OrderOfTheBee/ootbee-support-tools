package org.orderofthebee.addons.support.tools.repo.search.solr6;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.orderofthebee.addons.support.tools.repo.search.SolrAdminNativeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * SOLR Admin HTTP Client for native SOLR services.
 * This class provides access to requests not covered by the <a href="https://docs.alfresco.com/search-services/latest/admin/restapi/">Alfresco REST API for SOLR</a>
 *
 * @author Angel Borroy
 */
public class SolrAdminNativeClientImpl implements SolrAdminNativeClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolrAdminNativeClientImpl.class);

    private HttpClient httpClient;

    private String baseUrl;

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCascadeTrackerPendingCount(String coreName) {
        try {
            GetMethod getMethod = new GetMethod(
                    httpClient.getHostConfiguration().getHostURL() + baseUrl + "/" + coreName + "/select"
                            + "?fl=" + URLEncoder.encode("*,[cached]", StandardCharsets.UTF_8.toString())
                            + "&q=" + URLEncoder.encode("{!term f=int@s_@cascade}1", StandardCharsets.UTF_8.toString())
                            + "&wt=json");
            httpClient.executeMethod(getMethod);
            JSONObject json = (JSONObject) new JSONParser().parse(getMethod.getResponseBodyAsString());
            JSONObject response = (JSONObject) json.get("response");
            return (long) response.get("numFound");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return -1;
        }
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

}
