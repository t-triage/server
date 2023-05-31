package com.clarolab.event.slack;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;
import com.clarolab.model.TriageSpec;
import com.clarolab.model.User;
import com.clarolab.service.TriageSpecService;
import com.clarolab.service.UserService;
import com.clarolab.util.StringUtils;
import lombok.extern.java.Log;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
@Component
public class SlackService {

    @Autowired
    private UserService userService;

    @Autowired
    private SlackSpecService slackSpecService;

    @Autowired
    private TriageSpecService triageSpecService;

    public final String slackError = "\"ok\":false";
    public final String slackSuccess = "\"ok\":true";

    public String sendMessage(SlackSpec spec, String channel, String message, boolean includeInText) {
        log.info(String.format("Sending Message to Slack channel: %s token: %s and message: %s ", channel, spec.getFinalToken(), message));

        if (message == null || message.isEmpty()) {
            return slackError + " Empty message";
        }

        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost("https://slack.com/api/chat.postMessage");

            // Request parameters and other properties.
            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("token", spec.getFinalToken()));
            params.add(new BasicNameValuePair("channel", channel));
            params.add(new BasicNameValuePair("attachments", message));
            if (includeInText) {
                params.add(new BasicNameValuePair("text", message));
            }

            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            //Execute and get the response.
            HttpResponse response = httpclient.execute(httppost);
            try {
                String responseText = EntityUtils.toString(response.getEntity());
                if (!StringUtils.isEmpty(responseText) && responseText.contains(slackError)) {
                    log.log(Level.SEVERE, String.format("Couldnt send message, error: %s", responseText));
                    return responseText;
                }
            } catch (Exception ex) {
                log.log(Level.SEVERE, "Couldnt read Slack response message");
            }

            return slackSuccess;
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR sending a message to slackId: " + spec.getId(), e);
        }

        return slackSuccess;
    }

    public boolean sendMessageToUser(SlackSpec spec, String message) {
        log.info(String.format("Sending Message to Slack user with token: %s and message: %s ", spec.getFinalToken(), message));

        if (message == null || message.isEmpty()) {
            return false;
        }

        try {
            TriageSpec triageSpec = triageSpecService.geTriageFlowSpecByContainer(spec.getContainer());
            User containerAssignee = triageSpec.getTriager();

            if (containerAssignee != null && !StringUtils.isEmpty(containerAssignee.getSlackId())) {
                log.info(String.format("Sending Message to Slack user: %s", containerAssignee.getSlackId()));

                HttpClient httpclient = HttpClients.createDefault();
                HttpPost httppost = new HttpPost("https://slack.com/api/chat.postMessage");

                // Request parameters and other properties.
                List<NameValuePair> params = new ArrayList<NameValuePair>(2);
                params.add(new BasicNameValuePair("token", spec.getFinalToken()));
                params.add(new BasicNameValuePair("channel", containerAssignee.getSlackId()));
                params.add(new BasicNameValuePair("attachments", message));
                // params.add(new BasicNameValuePair("as_user", "true"));

                httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

                //Execute and get the response.
                HttpResponse response = httpclient.execute(httppost);

                try {
                    String responseText = EntityUtils.toString(response.getEntity());
                    if (!StringUtils.isEmpty(responseText) && responseText.contains(slackError)) {
                        log.log(Level.SEVERE, String.format("Couldnt send message, error: %s", responseText));
                        return false;
                    }
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Couldnt read Slack response message");
                }

                return true;
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR sending a message to slackId: " + spec.getId(), e);
        }

        return true;
    }




    public void setSlackUserIds() {
        List<User> pendingUsers = userService.findUsersWithoutSlack();

        Set<String> tokens = slackSpecService.findAllEnabled().stream().map(slackSpec -> slackSpec.getToken()).collect(Collectors.toSet());

        // Do it with a strict email comparison
        for (String token : tokens) {
            try {
                SlackWebApiClient webApiClient = SlackClientFactory.createWebApiClient(token);
                List<allbegray.slack.type.User> userList = webApiClient.getUserList();

                for (User pendingUser : pendingUsers) {
                    allbegray.slack.type.User user = userList.stream()
                            .filter(usr -> !usr.getDeleted() && usr.getProfile().getEmail() != null && usr.getProfile().getEmail().contains(pendingUser.getUsername()))
                            .findFirst()
                            .orElse(null);
                    if (user != null) {
                        pendingUser.setSlackId(user.getId());
                        userService.update(pendingUser);
                    } else {
                        user = userList.stream()
                                .filter(usr -> !usr.getDeleted() && StringUtils.containsSameValue(StringUtils.getUsername(usr.getProfile().getEmail()), StringUtils.getUsername(pendingUser.getUsername())))
                                .findFirst()
                                .orElse(null);
                        if (user != null) {
                            pendingUser.setSlackId(user.getId());
                            userService.update(pendingUser);
                        } else {
                            log.log(Level.SEVERE, "User is not in Slack: " + pendingUser.getUsername());
                        }
                    }
                }
            } catch (Exception ex) {
                log.log(Level.SEVERE, "Error trying to set Slack IDs " + token, ex);
            }
        }
    }

    public boolean sendMessageNow(SlackSpec spec, String message) {
        return sendMessageNow(spec, spec.getFinalChannel(), message);
    }

    public boolean sendMessageNow(SlackSpec spec, String channel, String message) {
        String answer = sendMessage(spec, channel, message, false);
        if (StringUtils.isEmpty(answer)) {
            return false;
        } else if (answer.contains(slackSuccess)) {
            return true;
        } else {
            return false;
        }
    }
    
}
