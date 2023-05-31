package com.clarolab.connectors.impl.qtest;

//import com.clarolab.qtest.client.QTestApiClient;
//import com.clarolab.qtest.client.QTestTestCycleClient;
//import com.clarolab.qtest.entities.QTestTestCycle;

public class QTestCLService {

//    private QTestTestCycleClient qTestTestCycleClient;
//
//    @Builder
//    private QTestCLService(QTestApiClient qTestApiClient){
//        qTestTestCycleClient = QTestTestCycleClient.builder().qTestApiClient(qTestApiClient).build();
//    }
//
//    public List<Container> getAllCycleLifeAsContainers() throws ContainerServiceException {
//        try{
//            List<Container> containers = Lists.newArrayList();
//            qTestTestCycleClient.getAllTestCycleOnAllProject().forEach(testCycle -> containers.add(createContainer(testCycle)));
//            return containers;
//        } catch (Exception e) {
//            throw new ContainerServiceException(String.format("[getAllCycleLifeForProjectAsContainers] : An error occurred trying to get containers"), e);
//        }
//    }
//
//    public List<Container> getAllCycleLifeForProjectAsContainers(String projectName) throws ContainerServiceException {
//        try {
//            List<Container> containers = Lists.newArrayList();
//            qTestTestCycleClient.getAllTestCycleOnProject(projectName).forEach(testCycle -> containers.add(createContainer(testCycle)));
//            return containers;
//        } catch (Exception e) {
//            throw new ContainerServiceException(String.format("[getAllCycleLifeForProjectAsContainers] : An error occurred trying to get containers"), e);
//        }
//    }
//
//    public Container getCycleLifeCrossAllProjectsAsContainer(String name) throws ContainerServiceException {
//        try {
//            if(name.startsWith("http"))
//                throw new ContainerServiceException("get Container From URL not yet implemented.");
//            return createContainer(qTestTestCycleClient.findTestCycleCrossAllProjects(name));
//        } catch (Exception e) {
//            throw new ContainerServiceException(String.format("[getCycleLifeCrossAllProjectsAsContainer] : An error occurred trying to get Container(%s)" , name), e);
//        }
//    }
//
//    private Container createContainer(QTestTestCycle testCycle){
//        return Container.builder()
//                .name(testCycle.getName())
//                .url(testCycle.getUrl())
//                .enabled(true)
//                .timestamp(DateUtils.now())
//                .build();
//    }
}
