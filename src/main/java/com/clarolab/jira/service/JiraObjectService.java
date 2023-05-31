package com.clarolab.jira.service;


import com.clarolab.jira.model.JiraConfig;
import com.clarolab.jira.model.JiraObject;
import com.clarolab.jira.repository.JiraConfigRepository;
import com.clarolab.repository.BaseRepository;
import com.clarolab.service.BaseService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

import static com.clarolab.jira.util.Constants.*;
import static com.clarolab.util.Constants.COMMENT;
import static com.clarolab.util.Constants.TRANSITIONS;

@Log
@Service
public class JiraObjectService extends BaseService<JiraConfig> {

    final RestTemplate restTemplate;
    public final String postJiraSuccess = "\"ok\":true";
    public final String postJiraError = "\"ok\":false";
    public String finalToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ik16bERNemsxTVRoRlFVRTJRa0ZGT0VGRk9URkJOREJDTVRRek5EZzJSRVpDT1VKRFJrVXdNZyJ9.eyJodHRwczovL2F0bGFzc2lhbi5jb20vb2F1dGhDbGllbnRJZCI6InBZeU9mdWwwY0xCRlJ4dnVsdTFoTFJzQ2QxNG9BRUR4IiwiaHR0cHM6Ly9hdGxhc3NpYW4uY29tL2VtYWlsRG9tYWluIjoiZ21haWwuY29tIiwiaHR0cHM6Ly9hdGxhc3NpYW4uY29tL3N5c3RlbUFjY291bnRJZCI6IjYyYTA5ZmMwODc4M2U3MDA2ZjYxYzBmYSIsImh0dHBzOi8vYXRsYXNzaWFuLmNvbS9zeXN0ZW1BY2NvdW50RW1haWxEb21haW4iOiJjb25uZWN0LmF0bGFzc2lhbi5jb20iLCJodHRwczovL2F0bGFzc2lhbi5jb20vdmVyaWZpZWQiOnRydWUsImh0dHBzOi8vYXRsYXNzaWFuLmNvbS9maXJzdFBhcnR5IjpmYWxzZSwiaHR0cHM6Ly9hdGxhc3NpYW4uY29tLzNsbyI6dHJ1ZSwiaXNzIjoiaHR0cHM6Ly9hdGxhc3NpYW4tYWNjb3VudC1wcm9kLnB1czIuYXV0aDAuY29tLyIsInN1YiI6ImF1dGgwfDYyNzQyMGM1ZTAxYzE0MDA2YTUzMjM1ZCIsImF1ZCI6ImFwaS5hdGxhc3NpYW4uY29tIiwiaWF0IjoxNjU0NzAxMTA0LCJleHAiOjE2NTQ3MDQ3MDQsImF6cCI6InBZeU9mdWwwY0xCRlJ4dnVsdTFoTFJzQ2QxNG9BRUR4Iiwic2NvcGUiOiJyZWFkOm1lIn0.iQmN6A1PPS0w83rNLBB4dCOHq5TxuMK29CBvQj4TOEB_OSewRzlxNkZW2q6n0n_Um8VeWZVPYoPaEzlXiwZkyZ6QTisCNa0tjoEMGRSvM3I5rP3MD1l2X8oYj4790PyIkv9LgVgCKe6gNTeST5on10dA28d_MFNoTyaPonwm7i1P7aikl5BsnagoU6PaMAUZ2aZ3ElfdcRUWeFT8xxfCCzijyeL54t_WHY7MNCh1VXGe-oprV-6wfTt7qYTFocGU2FbYoBZ_OevCPA6r2aKCKz6fGQ5AHPoKO2rRWAS607q1WYzXSy7s6XPgs88z6AbS_D5GptQFtrQhzhuF5oBsUw";
    public final String clientJiraURL = "https://t-triage.atlassian.net/";

    @Autowired
    private JiraConfigRepository jiraConfigRepository;

    @Autowired
    private JiraOAuthService jiraOAuthService;

    @Autowired
    private JiraConfigService jiraConfigService;

    @Autowired
    public JiraObjectService(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    @Override
    protected BaseRepository<JiraConfig> getRepository() {
        return jiraConfigRepository;
    }

    public String getJiraTicketStatus(String issueID, JiraConfig jiraConfig) {
        if (jiraConfig == null) {
            log.log(Level.WARNING, "Getting Ticket Status without config");
            return null;
        }
        try{
            final String url = JIRA_PRE_API_DOMAIN + jiraConfig.getCloudId() + JIRA_API_PATH_URI;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(jiraConfig.getFinalToken());
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String status = restTemplate.exchange(url + issueID, HttpMethod.GET, entity, String.class).getBody();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(status);
            log.log(Level.INFO, String.format("Getting Ticket Status with ticket %s and config %d successfully", issueID, jiraConfig.getId()));
            return jsonNode.get("fields").get("status").get("name").asText();
        }catch(Exception e){
            log.log(Level.SEVERE, String.format("Getting Ticket Status with ticket %s and config %d", issueID, jiraConfig.getId()), e);
            return null;
        }
    }

    public String jiraStatus(JiraConfig jiraConfig) {
        if(jiraConfig == null){
            log.log(Level.WARNING, "Getting Jira Status without config");
            return null;
        }
        try {
            jiraConfig = jiraOAuthService.refreshToken(jiraConfig);
            final String url = JIRA_PRE_API_DOMAIN + jiraConfig.getCloudId() + JIRA_API_PROJECT_STATUS + queryStatus(jiraConfig);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(jiraConfig.getFinalToken());
            HttpEntity<String> entity = new HttpEntity<>(headers);

            return restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
        } catch (Exception e) {
            if (e.getMessage().contains("401")) {
                log.log(Level.SEVERE, String.format("Getting Jira Status with config %d. Final token is expired", jiraConfig.getId()), e);
            } else {
                log.log(Level.SEVERE, String.format("Getting Jira Status with config %d", jiraConfig.getId()), e);
            }
            return null;
        }
    }

    public String getJiraObjects(String issueID) {
        try{
            final String url = clientJiraURL + JIRA_API_PATH_URI;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(finalToken);
            HttpEntity <String> entity = new HttpEntity<>(headers);

            return restTemplate.exchange(url + issueID, HttpMethod.GET, entity, String.class).getBody();
        }catch(Exception e){
            log.log(Level.SEVERE, String.format("Getting Jira Objects with ticket %s", issueID), e);
            return null;
        }
    }

    public void addJiraComment(JiraConfig jiraConfig, String issueID, String newComment) {
        if(jiraConfig == null){
            log.log(Level.WARNING, "Adding Jira Comment without config");
        }
        else{
            try{
                jiraConfig = jiraOAuthService.refreshToken(jiraConfig);
                final String url = JIRA_PRE_API_DOMAIN + jiraConfig.getCloudId() + "/rest/api/3/issue/" + issueID + COMMENT;
                JSONObject describeData= new JSONObject();
                JSONObject content = new JSONObject();
                JSONArray contentDataarray = new JSONArray();
                JSONArray contentDataarray2 = new JSONArray();
                JSONObject data = new JSONObject();
                JSONObject parentData = new JSONObject();

                data.put("text", newComment);
                data.put("type","text");
                contentDataarray.put(data);
                content.put("type", "paragraph");
                content.put("content", contentDataarray);
                contentDataarray2.put(content);
                describeData.put("type", "doc");
                describeData.put("version", 1);
                describeData.put("content",contentDataarray2);
                parentData.put("body", describeData);



                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(finalToken);
                HttpEntity<String> entity = new HttpEntity<>(parentData.toString(), headers);

                restTemplate.postForObject(url, entity, String.class);
                log.log(Level.INFO, String.format("Jira comment was added to ticket %s", issueID));
            }catch(Exception e){
                if (e.getMessage().contains("401")) {
                    log.log(Level.SEVERE, String.format("Adding Jira comment to ticket %s with config %d. Final token is expired", issueID, jiraConfig.getId()), e);
                } else {
                    log.log(Level.SEVERE, String.format("Adding Jira Comment to ticket %s with config %d", issueID, jiraConfig.getId()), e);
                }
            }

        }
    }

    public String addJiraCommentTest(String issueID, String comment) {
        try{
            final String url = clientJiraURL + JIRA_API_PATH_URI + issueID + COMMENT;
            JSONObject bodyInformation = new JSONObject();
            bodyInformation.put("body", comment);


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(finalToken);
            HttpEntity<String> entity = new HttpEntity<>(bodyInformation.toString(), headers);

            return restTemplate.postForObject(url, entity, String.class);
        }catch(Exception e){
            if (e.getMessage().contains("401")) {
                log.log(Level.SEVERE, String.format("Adding Jira comment to ticket %s. Final token is expired", issueID));
            } else {
                log.log(Level.SEVERE, String.format("Adding Jira Comment to ticket %s", issueID), e);
            }
            return null;
        }
    }

    public boolean transitionIssue(JiraConfig jiraConfig, String issueID, String columnID) {
        if(jiraConfig == null){
            log.log(Level.WARNING, "Transition Issue without config");
            return false;
        }
        else{
            try{
                jiraConfig = jiraOAuthService.refreshToken(jiraConfig);
                final String url = JIRA_PRE_API_DOMAIN + jiraConfig.getCloudId() + "/rest/api/3/issue/" + issueID + TRANSITIONS;
                String finalToken = jiraConfig.getFinalToken();
                JSONObject bodyInformation = new JSONObject();
                JSONObject newTransitionID = new JSONObject();
                bodyInformation.put("transition", newTransitionID.put("id", columnID));

                HttpHeaders headers = new HttpHeaders();
                List<MediaType> a = new ArrayList<>();
                a.add(MediaType.APPLICATION_JSON);
                headers.setAccept(a);
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(finalToken);
                HttpEntity<String> entity = new HttpEntity<>(bodyInformation.toString(), headers);

                restTemplate.postForObject(url, entity, String.class);
                log.log(Level.INFO, String.format("Transition of the ticket %s successfully.", issueID));
                return true;
            }catch(Exception e){
                if (e.getMessage().contains("401")) {
                    log.log(Level.SEVERE, String.format("Transition of the ticket %s with config %d. Final token is expired", issueID, jiraConfig.getId()), e);
                } else {
                    log.log(Level.SEVERE, String.format("Transition of the ticket %s with config %d", issueID, jiraConfig.getId()), e);
                }
                return false;
            }
        }
    }

    public String createJiraIssue(JiraConfig jiraConfig, String summary, String description) {
        return createJiraIssue(jiraConfig, summary, description, null);
    }

    public String createJiraIssue(JiraConfig jiraConfig, String summary, String description, String priority) {
        if(jiraConfig == null){
            log.log(Level.WARNING, "Creating Jira Issue without config");
            return null;
        }
        try {
            jiraConfig = jiraOAuthService.refreshToken(jiraConfig);
            String finalToken = jiraConfig.getFinalToken();
            final String url = JIRA_PRE_API_DOMAIN + jiraConfig.getCloudId() + "/rest/api/2/issue";

            //Creacion de los JSON objects:
            JSONObject bodyInformation = new JSONObject();
            JSONObject parentData = new JSONObject();
            JSONObject issuetypeData = new JSONObject();
            JSONObject projectData = new JSONObject();
            JSONObject update = new JSONObject();
            JSONArray labels = new JSONArray();

            //Childs y Subchilds del JSON w/ DataBase info:
            update.put("update", "{}");
            parentData.put("project", projectData.put("id", jiraConfig.getProjectKey()));
            parentData.put("summary", summary);
            parentData.put("description", description);
            parentData.put("issuetype", issuetypeData.put("id", jiraConfig.getIssueType()));

            //Aca se agregan todos los labels que querramos en el Issue.
            labels.put("automationIssue");
            labels.put("tTriage");
            parentData.put("labels", labels);

            if (!jiraConfig.getDefaultFieldsValues().isEmpty()) {
                JSONArray jiraFields = getJiraField(jiraConfig);
                JSONArray otherFields = new JSONArray(jiraConfig.getDefaultFieldsValues());
                for (int j = 0; j < otherFields.length(); j++) {
                    JSONObject jsonField = otherFields.getJSONObject(j);
                    String key = Arrays.stream(JSONObject.getNames(jsonField)).findFirst().get();
                    String idField = key;
                    for (int i = 0; i < jiraFields.length(); i++) {
                        if (jiraFields.getJSONObject(i).getString("name").equals(key)) {
                            idField = jiraFields.getJSONObject(i).getString("key");
                            log.log(Level.INFO, String.format("Getting Jira Field. Field %s is the name of %s id field", key, idField));
                        }
                    }
                    if (idField.equals(key)) {
                        log.log(Level.INFO, String.format("Getting Jira Field. Field %s not found in the list of id fields, asumming its a name of a field", key));
                    }
                    parentData.put(idField, jsonField.get(key));
                }
            }

            //Armado del JSON final para enviar como body.
            bodyInformation.put("fields", parentData);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            List<MediaType> a = new ArrayList<>();
            a.add(MediaType.APPLICATION_JSON);
            headers.setAccept(a);
            headers.setBearerAuth(finalToken);
            HttpEntity<String> entity = new HttpEntity<>(bodyInformation.toString(), headers);

            JiraObject response = restTemplate.postForObject(url, entity, JiraObject.class);
            assert response != null;
            log.log(Level.INFO, String.format("Created issue with key %s.", response.getKey()));
            return response.getKey();
        }catch(HttpClientErrorException e){
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED){
                log.log(Level.SEVERE, String.format("Creating Jira Issue with config %d. Final token is expired", jiraConfig.getId()));
            } else {
                log.log(Level.SEVERE, String.format("Creating Jira Issue with config %d.\n%s", jiraConfig.getId(), e.getResponseBodyAsString()));
            }
            return null;
        }catch(Exception e){
            log.log(Level.SEVERE, String.format("Creating Jira Issue with config %d.", jiraConfig.getId()), e);
            return null;
        }
    }

    public JSONArray getJiraField(JiraConfig jiraConfig){
        String url = JIRA_PRE_API_DOMAIN + jiraConfig.getCloudId() + "/rest/api/3/field";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<MediaType> a = new ArrayList<>();
        a.add(MediaType.APPLICATION_JSON);
        headers.setAccept(a);
        headers.setBearerAuth(jiraConfig.getFinalToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String fields = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
        assert fields != null;
        return new JSONArray(fields);
    }

    public void updatePriority(JiraConfig jiraConfig, String issueID, String id) {
        if(jiraConfig == null){
            log.log(Level.WARNING, "Updating Priority of Jira Issue without config");
        }
        else{
            try{
                jiraConfig = jiraOAuthService.refreshToken(jiraConfig);

                String url = JIRA_PRE_API_DOMAIN + jiraConfig.getCloudId() + JIRA_API_PATH_URI + issueID;
                JSONObject updateData = new JSONObject();
                JSONObject setData = new JSONObject();
                JSONObject priorityIdData = new JSONObject();
                JSONObject priority = new JSONObject();
                JSONArray setArrayData = new JSONArray();

                //falta el array del set
                priorityIdData.put("id",id);
                setData.put("set",priorityIdData);
                setArrayData.put(setData);
                priority.put("priority",setArrayData);
                updateData.put("update",priority);

                HttpHeaders headers = new HttpHeaders();
                List<MediaType> a = new ArrayList<>();
                a.add(MediaType.APPLICATION_JSON);
                headers.setAccept(a);
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(jiraConfig.getFinalToken());
                HttpEntity<String> entity = new HttpEntity<>(updateData.toString(), headers);

                restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
                log.log(Level.INFO, String.format("Updated issue priority of ticket %s.", issueID));
            }catch(Exception e){
                log.log(Level.SEVERE, "Updating Jira Priority Issue. Final token is expired");
            }
        }
    }

    public String createJiraIssueTest(String summary, String description) {
        final String url = clientJiraURL + "/rest/api/2/issue/";
        String jiraObjectResultAsJsonStr = "";
        //Creacion de los JSON objects:
        JSONObject bodyInformation = new JSONObject();
        JSONObject parentData = new JSONObject();
        JSONObject issuetypeData = new JSONObject();
        JSONObject projectData = new JSONObject();
        JSONObject reporterData = new JSONObject();
        JSONArray labels = new JSONArray();

        //Childs y Subchilds del JSON HARDCODED:
        parentData.put("project", projectData.put("key", "SEC"));
        parentData.put("summary", summary);
        parentData.put("description", description);
        parentData.put("issuetype", issuetypeData.put("name", "Bug"));
        parentData.put("reporter", reporterData.put("accountId", "557058:f58131cb-b67d-43c7-b30d-6b58d40bd077"));
        //Modificar la linea siguiente si queremos tener un Assignee especifico.(Agregar el id del user)
        //parentData.put("assignee", assigneeData.put("id", "60ba730e48549e006952ab7d"));


        //Aca se agregan todos los labels que querramos en el Issue.
        labels.put("securityCheck");
        labels.put("tTriage");
        parentData.put("labels", labels);

        //Armado del JSON final para enviar como body.
        bodyInformation.put("fields", parentData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(finalToken);
        HttpEntity<String> entity = new HttpEntity<>(bodyInformation.toString(), headers);

        try {
            jiraObjectResultAsJsonStr = restTemplate.postForObject(url, entity, String.class);
        } catch (Exception ex) {
            if (ex.getMessage().contains("401")) {
                log.log(Level.FINE, "Final token needs to be updated !");
                finalToken = jiraOAuthService.refreshJiraTokenTest();
                headers.setBearerAuth(finalToken);
                HttpEntity<String> newEntity = new HttpEntity<>(bodyInformation.toString(), headers);
                try {
                    jiraObjectResultAsJsonStr = restTemplate.postForObject(url, newEntity, String.class);
                } catch (Exception exception) {
                    log.log(Level.SEVERE, "Error trying to create new Jira issue..." + ex.getMessage());
                    return postJiraError;
                }
            } else {
                log.log(Level.SEVERE, "Error trying to create new Jira issue..." + ex.getMessage());
            }
        }

        return postJiraSuccess + " " + jiraObjectResultAsJsonStr;
    }

    public String getProjectList(Long id){
        JiraConfig jiraConfig = jiraConfigRepository.findByProductIdAndEnabledTrue(id);
        if(jiraConfig == null){
            log.log(Level.WARNING, "Getting Jira Projects List with invalid config ID");
            return null;
        }
        try{
            jiraConfig = jiraOAuthService.refreshToken(jiraConfig);
            jiraConfig = validationsCloudId(jiraConfig, jiraConfig.getFinalToken());
            String url = JIRA_PRE_API_DOMAIN + jiraConfig.getCloudId()+ JIRA_API_SEARCH_PROJECTS ;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(jiraConfig.getFinalToken());
            HttpEntity<String> entity = new HttpEntity<>(headers);
            return restTemplate.exchange(url , HttpMethod.GET, entity, String.class).getBody();
        }catch (Exception e) {
            if (e.getMessage().contains("401")) {
                log.log(Level.SEVERE, String.format("Getting Jira Projects List of project with config %d. Final token is expired", id), e);
            } else {
                log.log(Level.SEVERE, String.format("Getting Jira Projects List of project with config %d", id), e);
            }
            return null;
        }
    }

    public JiraConfig validationsCloudId(JiraConfig jiraConfig, String finalToken){
        if(jiraConfig == null){
            log.log(Level.WARNING, "Getting Jira Cloud Id without config");
            return null;
        }
        else{
            try {
                jiraConfig = jiraOAuthService.refreshToken(jiraConfig);
                JiraConfig jiraConfig1 = new JiraConfig();
                if (jiraConfig == null){
                    jiraConfig1 = jiraOAuthService.getJiraCloudId(jiraConfig1 , finalToken);
                    jiraConfig1.setFinalToken(finalToken);
                    return jiraConfig1;
                }
                if (jiraConfig.getCloudId().isEmpty()){
                    jiraConfig = jiraOAuthService.getJiraCloudId(jiraConfig,jiraConfig.getFinalToken());
                }
                return jiraConfig;
            }catch (Exception e){
                if (e.getMessage().contains("401")) {
                    log.log(Level.SEVERE, String.format("Getting Jira Cloud Id with config %d. Final token is expired", jiraConfig.getId()));
                } else {
                    log.log(Level.SEVERE, String.format("Getting Jira Cloud Id with config %d", jiraConfig.getId()), e);
                }
                return null;
            }
        }
    }

    public String getProjectStatus(Long productId, String projectKey){
        JiraConfig jiraConfig = jiraConfigRepository.findByProductIdAndEnabledTrue(productId);
        if(jiraConfig == null){
            log.log(Level.WARNING, "Getting Jira Project Status without config");
            return null;
        }
        else{
            try{
                jiraConfig = validationsCloudId(jiraConfig,jiraConfig.getFinalToken());
                String url = JIRA_PRE_API_DOMAIN + jiraConfig.getCloudId() + JIRA_API_PROJECTS + projectKey + "/statuses";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(jiraConfig.getFinalToken());
                HttpEntity<String> entity = new HttpEntity<>(headers);
                return restTemplate.exchange(url , HttpMethod.GET, entity, String.class).getBody();
            }catch(Exception e){
                if (e.getMessage().contains("401")) {
                    log.log(Level.SEVERE, String.format("Getting Jira Project Status with config %d. Final token is expired", jiraConfig.getId()), e);
                } else {
                    log.log(Level.SEVERE, String.format("Getting Jira Project Status with config %d", jiraConfig.getId()), e);
                }
                return null;
            }
        }
    }

    public String jiraTaskId(String projectKey){
        JiraConfig jiraConfig = jiraConfigRepository.findByProjectKey(projectKey);
        if(jiraConfig == null){
            log.log(Level.WARNING, "Getting Jira Task Id with invalid Project Key");
            return null;
        }
        else{
            try{
                jiraConfig = jiraOAuthService.refreshToken(jiraConfig);
                jiraConfig = validationsCloudId(jiraConfig,jiraConfig.getFinalToken());
                String url = JIRA_PRE_API_DOMAIN + jiraConfig.getCloudId() + JIRA_API_PROJECTS + projectKey + "/statuses";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(jiraConfig.getFinalToken());
                HttpEntity<String> entity = new HttpEntity<>(headers);
                JsonNode jsonNode = Objects.requireNonNull(restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class).getBody()).get(0);
                return jsonNode.get("id").textValue();
            }catch(Exception e){
                if (e.getMessage().contains("401")) {
                    log.log(Level.SEVERE, String.format("Getting Jira Task Id with config %d. Final token is expired", jiraConfig.getId()), e);
                } else {
                    log.log(Level.SEVERE, String.format("Getting Jira Task Id with config %d", jiraConfig.getId()), e);
                }
                return null;
            }
        }
    }

    public String queryStatus(JiraConfig jiraConfig){
        return "id=" + jiraConfig.getInitialStateId() + "&id=" + jiraConfig.getReopenStateId() + "&id=" + jiraConfig.getResolvedStateId() + "&id=" + jiraConfig.getClosedStateId();
    }

    public String searchIssueType(Long productId, String projectId){
        JiraConfig jiraConfig = jiraConfigRepository.findByProductIdAndEnabledTrue(productId);
        if(jiraConfig == null){
            log.log(Level.WARNING, "Getting Jira Issue Types with invalid Product Id");
            return null;
        }
        else{
            try{
                jiraConfig = jiraOAuthService.refreshToken(jiraConfig);
                jiraConfig = validationsCloudId(jiraConfig,jiraConfig.getFinalToken());
                String url = JIRA_PRE_API_DOMAIN + jiraConfig.getCloudId() + SEARCH_ISSUETYPE + "?projectId=" + projectId;
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(jiraConfig.getFinalToken());
                HttpEntity<String> entity = new HttpEntity<>(headers);
                return restTemplate.exchange(url , HttpMethod.GET, entity, String.class).getBody();
            }catch(Exception e){
                if (e.getMessage().contains("401")) {
                    log.log(Level.SEVERE, String.format("Getting Jira Issue Types with config %d. Final token is expired", jiraConfig.getId()), e);
                } else {
                    log.log(Level.SEVERE, String.format("Getting Jira Issue Types with config %d", jiraConfig.getId()), e);
                }
                return null;
            }
        }
    }
}
