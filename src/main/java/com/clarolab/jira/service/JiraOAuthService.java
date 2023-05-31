package com.clarolab.jira.service;

import com.clarolab.jira.model.JiraConfig;
import com.clarolab.jira.model.JiraObject;
import com.clarolab.jira.repository.JiraConfigRepository;
import com.clarolab.service.ApplicationDomainService;
import com.clarolab.util.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.java.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;

import static com.clarolab.jira.util.Constants.*;


@Log
@Service
public class JiraOAuthService {

    @Autowired
    private JiraConfigRepository jiraConfigRepository;

    @Autowired
    private JiraConfigService jiraConfigService;

    @Autowired
    private ApplicationDomainService applicationDomainService;

    final RestTemplate restTemplate;
    public static final String jiraAuthorize = "offline_access%20read%3Ajira-user%20manage%3Ajira-project%20manage%3Ajira-configuration%20write%3Ajira-work%20manage%3Ajira-webhook%20read%3Ajira-work%20manage%3Ajira-data-provider";

    @Autowired
    public JiraOAuthService(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public String getFirstLoginCode() {
        try {
            String callback = StringUtils.concatURL(applicationDomainService.getURL(),REDIRECT_URL);
            callback = StringUtils.encodeURL(callback);

            String jiraEndpoint = JIRA_CLOUD_PATH_URI + "authorize?audience=api.atlassian.com&client_id=" + CLIENT_ID + "&scope=" + jiraAuthorize + "&response_type=code&redirect_uri=" + callback;
            HttpClient httpclient = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(jiraEndpoint);

            HttpResponse response = httpclient.execute(httpget);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public JiraObject  getRefreshCode(String code, Long productId) {
        JiraObject jiraResult = JiraObject.builder().build();
        JiraConfig jiraConfig = jiraConfigRepository.findByProductIdAndEnabledTrue(productId);
        if(jiraConfig == null){
            log.log(Level.WARNING, "Getting Refresh Token of Jira Config with invalid ID");
            return null;
        }
        try {
            JSONObject bodyInformation = new JSONObject();
            bodyInformation.put("grant_type", "authorization_code");
            bodyInformation.put("client_id", jiraConfig.getClientID());
            bodyInformation.put("client_secret", jiraConfig.getClientSecret());
            bodyInformation.put("code", code);
            bodyInformation.put("redirect_uri", StringUtils.concatURL(applicationDomainService.getURL(), API_JIRA_CODE_AUTH));
            //bodyInformation.put("redirect_uri", "http://localhost:8080/auth/jira");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(bodyInformation.toString(), headers);
            jiraResult = restTemplate.postForObject(JIRA_AUTH_URL, entity, JiraObject.class);
        }catch(HttpClientErrorException e){
            log.log(Level.SEVERE, String.format("Error trying to request Authorization token %d.\n%s", jiraConfig.getId(), e.getResponseBodyAsString()), e);
            jiraResult.setError(true);
            return jiraResult;
        }catch(Exception e){
            log.log(Level.SEVERE, "Error trying to request Authorization token", e);
            jiraResult.setError(true);
            return jiraResult;
        }
        assert jiraResult != null;
        jiraConfig= getJiraCloudId(jiraConfig,jiraResult.getAccess_token());
        jiraConfig.setFinalToken(jiraResult.getAccess_token());
        jiraConfig.setRefreshToken(jiraResult.getRefresh_token());
        jiraConfigService.save(jiraConfig);
        return jiraResult;
    }


    public JiraObject refreshJiraToken(JiraConfig jiraConfig) {

        JSONObject bodyInformation = new JSONObject();
        JiraObject jiraObjectResult = new JiraObject();
        bodyInformation.put("grant_type", "refresh_token");
        bodyInformation.put("refresh_token", jiraConfig.getRefreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(jiraConfig.getClientID(), jiraConfig.getClientSecret());
        HttpEntity<String> entity = new HttpEntity<>(bodyInformation.toString(), headers);

        try {
            //jiraObjectResult = restTemplate.postForObject(url, entity, String.class);
            jiraObjectResult = restTemplate.exchange(JIRA_AUTH_URL, HttpMethod.POST, entity, JiraObject.class).getBody();
            log.log(Level.FINE, "Final Token updated successfully! ");
            assert jiraObjectResult != null;

        } catch (Exception ex) {
            if (ex.getMessage().contains("400")) {
                log.log(Level.SEVERE, "Couldn't get new Final token.. ", ex);

            }
        }

        return jiraObjectResult;
    }

    public JiraConfig getJiraCloudId(JiraConfig jiraConfig, String token){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        JsonNode jsonNode = Objects.requireNonNull(restTemplate.exchange(JIRA_RESOURCES_URL, HttpMethod.GET, entity, JsonNode.class).getBody()).get(0);
        String cloudId = jsonNode.get("id").textValue();
        jiraConfig.setCloudId(cloudId);
        return jiraConfig;
    }

    public JiraConfig refreshToken(JiraConfig jiraConfig){
        JSONObject bodyInformation = new JSONObject();
        bodyInformation.put("grant_type", "refresh_token");
        bodyInformation.put("client_id", jiraConfig.getClientID());
        bodyInformation.put("client_secret", jiraConfig.getClientSecret());
        bodyInformation.put("refresh_token", jiraConfig.getRefreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(bodyInformation.toString(), headers);
        JsonNode jsonNode = restTemplate.exchange(JIRA_AUTH_URL, HttpMethod.POST, entity, JsonNode.class).getBody();
        assert jsonNode != null;
        String accessToken = jsonNode.get("access_token").textValue();
        String refreshToken = jsonNode.get("refresh_token").textValue();
        jiraConfig.setFinalToken(accessToken);
        jiraConfig.setRefreshToken(refreshToken);
        return jiraConfigService.save(jiraConfig);
    }
    public String refreshJiraTokenTest() {
        JSONObject bodyInformation = new JSONObject();
        bodyInformation.put("grant_type", "refresh_token");
        bodyInformation.put("refresh_token", "NZLIhg85L6rvTHHxSaQqp6eeVlP3DxW4VEjt8PbE22qYU");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
        HttpEntity<String> entity = new HttpEntity<>(bodyInformation.toString(), headers);

        return Objects.requireNonNull(restTemplate.exchange(JIRA_AUTH_URL, HttpMethod.POST, entity, JiraObject.class).getBody()).getAccess_token();
    }


    public HttpEntity<String> getJiraProjectEntity(Long productId) {

        JiraConfig jiraConfig = jiraConfigRepository.findByProductIdAndEnabledTrue(productId);
        String bearer_token = jiraConfig.getFinalToken();

        HttpHeaders jiraProjectHeaders = new HttpHeaders();
        jiraProjectHeaders.setBearerAuth(bearer_token);

        return new HttpEntity<>(jiraProjectHeaders);
    }


    public JsonNode getJiraResources(Long productId) {
        HttpEntity<String> jiraProjectEntity = getJiraProjectEntity(productId);
        return Objects.requireNonNull(restTemplate.exchange(JIRA_RESOURCES_URL, HttpMethod.GET, jiraProjectEntity, JsonNode.class).getBody()).get(0);
    }

    public JsonNode getJiraBoard(Long productId) {
        HttpEntity<String> jiraProjectEntity = getJiraProjectEntity(productId);
        JsonNode jiraResources = getJiraResources(productId);
        String cloudId = jiraResources.get("id").textValue();
        String jiraBoardURL = (JIRA_BOARD_FIRST + "/" + cloudId + JIRA_BOARD_SECOND);
        return restTemplate.exchange(jiraBoardURL, HttpMethod.GET, jiraProjectEntity, JsonNode.class).getBody();
    }


    public ArrayList getProjectKeys(Long productId) {

        JsonNode jiraResources = getJiraResources(productId);
        JsonNode jiraBoard = getJiraBoard(productId);
        ArrayList responseArray = new ArrayList();
        ArrayList projectKeys = new ArrayList();

        String jiraServer = jiraResources.get("url").textValue();

        for (int i = 0; i < jiraBoard.get("projects").size(); i++) {
            projectKeys.add(jiraBoard.get("projects").get(i).get("key").textValue());
        }
        responseArray.add(jiraServer);
        responseArray.add(projectKeys);

        return responseArray;
    }

    public ArrayList getProjectStates(Long productId, String projectKey) {

        JsonNode jiraBoard = getJiraBoard(productId);

        ArrayList responseArray = new ArrayList();
        for (int i = 0; i < jiraBoard.get("projects").size(); i++) {
            if (jiraBoard.get("projects").get(i).get("key").textValue().equals(projectKey)) {
                JsonNode aux = jiraBoard.get("projects").get(i).get("issuetypes");
                for (int n = 0; n < aux.size(); n++) {
                    responseArray.add(aux.get(n).get("id").textValue());
                }
            }
        }

        return responseArray;
    }

    public Boolean validateToken(JiraConfig jiraConfig) {
        try{
            refreshToken(jiraConfig);
        }catch (Exception ex){
            return false;
        }
        return true;
    }
}
