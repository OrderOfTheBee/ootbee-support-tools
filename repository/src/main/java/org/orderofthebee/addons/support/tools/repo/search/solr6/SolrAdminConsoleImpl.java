package org.orderofthebee.addons.support.tools.repo.search.solr6;

import org.alfresco.httpclient.HttpClientFactory;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.orderofthebee.addons.support.tools.repo.search.SolrAdminConsole;

import java.io.IOException;
import java.net.URLEncoder;

public class SolrAdminConsoleImpl implements SolrAdminConsole {

    private HttpClientFactory solrHttpClientFactory;

    private String baseUrl;

    private HttpClient httpClient;

    public void init() {
        ParameterCheck.mandatory("baseUrl", baseUrl);
        httpClient = solrHttpClientFactory.getHttpClient();
    }

    @Override
    public int getCascadeTrackerPendingCount(String coreName) {

        GetMethod getMethod = new GetMethod(
                httpClient.getHostConfiguration().getHostURL() + "/" + baseUrl + "/" + coreName + "/select?"
                        + "?fl=" + URLEncoder.encode("*,[cached]")
                        + "&q=" + URLEncoder.encode("{!term f=int@s_@cascade}1")
                        + "&wt=json");
        try {
            httpClient.executeMethod(getMethod);
            JSONObject json = (JSONObject) new JSONParser().parse(getMethod.getResponseBodyAsString());
            JSONObject response = (JSONObject) json.get("response");
            return (int) response.get("numFound");

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    public void setSolrHttpClientFactory(HttpClientFactory solrHttpClientFactory) {
        this.solrHttpClientFactory = solrHttpClientFactory;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

}
