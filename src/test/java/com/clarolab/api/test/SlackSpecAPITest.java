package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.dto.SlackSpecDTO;
import com.clarolab.event.slack.SlackSpec;
import com.clarolab.event.slack.SlackSpecRepository;
import com.clarolab.event.slack.SlackSpecService;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.SlackSpecMapper;
import com.clarolab.model.Executor;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.serviceDTO.SlackSpecServiceDTO;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static com.clarolab.util.Constants.*;
import static io.restassured.RestAssured.given;

public class SlackSpecAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private SlackSpecMapper mapper;

    @Autowired
    SlackSpecServiceDTO slackSpecServiceDTO;

    @Autowired
    SlackSpecRepository slackSpecRepository;


    @Before
    public void clearProvider(){provider.clear();}


    @Test
    public void create(){
        SlackSpec spec = provider.getSlackSpec();
        spec.setContainer(provider.getContainer());
        slackSpecRepository.save(spec);

        SlackSpecDTO dto = mapper.convertToDTO(spec);
        dto.setExecutorId(provider.getExecutor().getId());

        dto.setDailyChannel("Daily channel");
        dto.setChannel("Common Channel");
        dto.setToken("xpf-jfjfjhsjsksks-utu2i48484jwi2i3");

        SlackSpecDTO answer = given()
                .body(dto)
                .post(API_PATH +  SLACK_INTEGRATION + CREATE_PATH)
                .then().statusCode(HttpStatus.SC_OK)
                .extract()
                .as(SlackSpecDTO.class);

        Assert.assertNotNull(answer);
        Assert.assertEquals(dto.getChannel(), answer.getChannel());
        Assert.assertEquals(dto.getDailyChannel(), answer.getDailyChannel());
        Assert.assertEquals(dto.getToken(), answer.getToken());

    }

    @Test
    public void update(){
        SlackSpec spec = provider.getSlackSpec();
        spec.setContainer(provider.getContainer());
        spec.setExecutor(provider.getExecutor());
        slackSpecRepository.save(spec);

        SlackSpecDTO dto = mapper.convertToDTO(spec);

        dto.setDailyChannel("Daily channel");
        dto.setChannel("Common Channel");
        dto.setToken("xpf-jfjfjhsjsksks-utu2i48484jwi2i3");

        SlackSpecDTO answer = given()
                .body(dto)
                .put(API_PATH +  SLACK_INTEGRATION + UPDATE_PATH)
                .then().statusCode(HttpStatus.SC_OK)
                .extract()
                .as(SlackSpecDTO.class);

        Assert.assertNotNull(answer);
        Assert.assertEquals(dto.getChannel(), answer.getChannel());
        Assert.assertEquals(dto.getDailyChannel(), answer.getDailyChannel());
        Assert.assertEquals(dto.getToken(), answer.getToken());

    }


}
