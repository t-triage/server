package com.clarolab.integration.test.connectors;

import com.clarolab.connectors.CIConnector;
import com.clarolab.connectors.services.exceptions.ContainerServiceException;
import com.clarolab.integration.BaseIntegrationTest;
import com.clarolab.model.Container;
import lombok.extern.java.Log;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

@Log
public class BambooConnectorTest extends BaseIntegrationTest {

    @Ignore
    @Test
    public void getContainersTest() throws ContainerServiceException {
        CIConnector connector = ciConnectors.get("BAMBOO_CLAROLAB").connect();
        List<Container> containers = connector.getAllContainers();
        log.info("Size: " + containers.size());
        containers.forEach(container -> log.info(container.getName()
                + " ; "
                + container.getRawHiddenData()
                + " ; "
                + container.getUrl()
                + " ; "
                + container.getDescription()
        ));
        MatcherAssert.assertThat(containers.size(), Matchers.greaterThan(0));
    }
}
