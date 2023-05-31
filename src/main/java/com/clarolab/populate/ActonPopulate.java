package com.clarolab.populate;

import com.clarolab.model.Connector;
import com.clarolab.model.Container;
import com.clarolab.model.User;
import com.clarolab.model.types.ConnectorType;
import com.clarolab.model.types.ReportType;
import com.clarolab.model.types.StatusType;
import com.clarolab.service.ConnectorService;
import com.clarolab.service.ContainerService;
import com.clarolab.service.TriageSpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActonPopulate {

    @Autowired
    private RealDataProvider realDataProvider;

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private TriageSpecService triageSpecService;

    public void populate() {
        configureProductActOn();
    }
    private void configureProductActOn() {
        User oldUser = provider.getUser();

        // CREATES A NEW USER
        provider.clear();
        provider.setUseRandom(false);

        provider.setName("Todd Brochu");
        provider.getUser();

        provider.setName("Act-On");
        provider.getProduct();

        Connector tConnector = Connector.builder()
                .name("Bamboo3")
                .url("http://bamboo3.via.act-on.net:8085/")
                .type(ConnectorType.BAMBOO)
                .enabled(true)
                .build();
        tConnector = connectorService.save(tConnector);
        provider.setConnector(tConnector);
        Container tContainer;

        tContainer = Container.builder()
                .name("QA")
                .connector(tConnector)
                .url("http://bamboo3.via.act-on.net:8085/browse/QA")
                .product(provider.getProduct())
                .reportType(ReportType.PROTRACTOR_STEPS)
                .build();
        tContainer = containerService.save(tContainer);
        tConnector.add(tContainer);
        provider.setContainer(tContainer);
        provider.getTriageSpec();

        provider.clear();
        provider.setUser(oldUser);

    }

    // =CONCATENAR("{ 'testCaseName': '";B2; "', 'status': '";C2; "', 'errorDetails': '" ;J2;"'},")
    // find replace 'Always Append' to > Always Append
    private List<TestTriagePopulate> getTests() {
        List<TestTriagePopulate> tests = realDataProvider.getTests("acton_tests.txt");
        for (TestTriagePopulate test : tests) {
            if (test.getStatusText() != null && test.getStatusText().equals("Passed")) {
                test.setAs(StatusType.PASS, 0, 2);
            }
        }

        return tests;
    }

}
