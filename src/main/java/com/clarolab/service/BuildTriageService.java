/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;


import com.clarolab.event.process.ApplicationEventBuilder;
import com.clarolab.event.process.ApplicationEventType;
import com.clarolab.model.*;
import com.clarolab.model.types.StateType;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.BuildTriageRepository;
import com.clarolab.util.Constants;
import com.clarolab.util.DateUtils;
import lombok.extern.java.Log;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.clarolab.util.Constants.DEFAULT_MAX_TESTCASES_PER_DAY;
import static com.clarolab.util.Constants.MAX_TESTCASES_PER_DAY;
import static com.google.common.base.Strings.isNullOrEmpty;

@Service
@Log
public class BuildTriageService extends BaseService<BuildTriage> {

    @Autowired
    private BuildTriageRepository buildTriageRepository;

    @Autowired
    private BuildTriageService buildTriageService;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private TriageDeadlineService triageDeadlineService;

    @Autowired
    private ApplicationEventBuilder applicationEventBuilder;

    @Autowired
    private TriageSpecService triageSpecService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PropertyService propertyService;

    private Cache buildCache;

    @Override
    public BaseRepository<BuildTriage> getRepository() {
        return buildTriageRepository;
    }

    public BuildTriage getLastTriageOf(Executor executor) {
        List<Long> buildIds = buildTriageRepository.findOngoing(executor);
        List<BuildTriage> list =  buildTriageRepository.findAllByIdInOrderByRankDesc(buildIds);
        // List<BuildTriage> list = buildTriageRepository.findTopByExecutorAndEnabledAndTriagedOrderByIdDesc(executor, true, false);

        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public BuildTriage getByBuildIs(Build build) {
        return buildTriageRepository.getByBuildIsAndEnabled(build, true);
    }

    public BuildTriage find(Build build) {
        return buildTriageRepository.getFirstByBuild(build);
    }

    private String getBuildCacheKey(User user, long timestamp) {
        return user.getId() + String.valueOf(timestamp);
    }

    public void testWasTriaged(TestTriage testTriage) {
        evictBuildCache(testTriage.getTriager());
        evictBuildCache(testTriage.getBuildTriage().getTriager());
    }

    private void evictBuildCache(User user, long timestamp) {
        buildCache.evict(getBuildCacheKey(user, timestamp));
    }

    private void evictBuildCache(User user) {
        long tomorrow = DateUtils.beginDay(1);
        evictBuildCache(user, tomorrow);
    }


    public BuildTriage markBuildAsTriaged(User user, Long buildId, String theNote) {
        BuildTriage buildTriage = find(buildId);
        return markBuildAsTriagedAndTests(user, buildTriage, theNote);
    }

    public BuildTriage markBuildAsTriagedAndTests(User user, BuildTriage buildTriage, String theNote) {
        List<TestTriage> testTriages = testTriageService.findAllPendingByBuild(buildTriage.getBuild());

        for (TestTriage testTriage : testTriages){
            if (!testTriage.isTriaged()) {
                testTriage.setTriager(user);
                testTriage.setTriaged();
                testTriageService.update(testTriage);
            }
        }

        return markBuildAsTriaged(user, buildTriage, theNote);
    }

    public BuildTriage markBuildAsTriaged(User user, BuildTriage buildTriage, String theNote) {
        buildTriage.setTriaged();
        buildTriage.setTriager(user);
        buildTriage.setNote(getNoteFrom(buildTriage, user, theNote));

        notifyBuildTriaged(buildTriage);

        return update(buildTriage);
    }

    public BuildTriage markBuildAsInvalid(User user, Long buildId, String theNote) {
        BuildTriage buildTriage = find(buildId);
        invalidateBuild(user, buildTriage, theNote);
        BuildTriage previousTriage = activatePreviousBuildTriage(buildTriage);

        return buildTriage;
    }

    private BuildTriage activatePreviousBuildTriage(BuildTriage buildTriage) {
        BuildTriage previousTriage = getPreviousTriage(buildTriage);

        if (previousTriage != null) {
            previousTriage.activate();

            previousTriage = update(previousTriage);

            testTriageService.activateTests(previousTriage);
        }

        return previousTriage;
    }

    private BuildTriage invalidateBuild(User user, BuildTriage buildTriage, String theNote) {
        List<TestTriage> testTriages = testTriageService.findAllByBuild(buildTriage.getBuild());

        for (TestTriage testTriage : testTriages){
            testTriage.setTriager(user);
            testTriage.invalidate();
            testTriageService.update(testTriage);
        }

        buildTriage.invalidate();
        buildTriage.setTriager(user);
        buildTriage.setNote(getNoteFrom(buildTriage, user, theNote));
        update(buildTriage);

        return buildTriage;
    }

    public BuildTriage markBuildAsDisabled(User user, Long buildId, String theNote) {
        log.info("I'm in the correct site");
        BuildTriage buildTriage = find(buildId);
        buildTriage.getExecutor().disable();
        buildTriage.disable();
        buildTriage.setNote(getNoteFrom(buildTriage, user, theNote));
        executorService.update(buildTriage.getExecutor());
        buildTriageService.update(buildTriage);
        return buildTriage;
    }

    public BuildTriage markBuildAsEnabled(User user, Long buildId) {
        BuildTriage buildTriage = find(buildId);
        buildTriage.getExecutor().enable();
        buildTriage.activate();
        executorService.update(buildTriage.getExecutor());
        this.update(buildTriage);
        return buildTriage;
    }

    private Note getNoteFrom(BuildTriage buildTriage, User user, String theNote) {
        if (theNote == null || theNote.isEmpty()) {
            // There isn't a new note. we keep the old one
            return buildTriage.getNote();
        } else {
            if (buildTriage.getNote() == null || buildTriage.getNote().getDescription().isEmpty()) {
                // There isn't a new note. we build a new one
                Note note = Note.builder()
                        .description(theNote)
                        .author(user)
                        .build();
                note = noteService.save(note);
                return note;
            } else {
                if (!buildTriage.getNote().equals(theNote)) {
                    // We have a new text for the existing note
                    Note note = buildTriage.getNote();
                    note.setDescription(theNote);
                    note = noteService.update(note);
                    return note;
                } else {
                    // The note didn't changed, answer the current one
                    return buildTriage.getNote();
                }
            }
        }
    }

    public List<BuildTriage> getPendingTriage(Container container){
        return buildTriageRepository.findByContainerAndTriagedAndExpiredAndEnabledAndExecutor_Enabled(container, false, false, true, true);
    }

    public List<BuildTriage> getPendingTriage(Container container, Long prev, Long now){
        return buildTriageRepository.findByContainerAndTriagedAndExpiredAndEnabledAndExecutor_EnabledAndTimestampBetween(container, false, false, true,true, prev, now);
    }    

    public List<BuildTriage> getAlreadyTriaged(Container container){
        return buildTriageRepository.findByContainerAndTriagedAndEnabledAndExecutor_Enabled(container, true,  true, true);
    }

    public List<BuildTriage> getPendingTriage(List<Executor> executors){
        return buildTriageRepository.findAllByExecutorInAndTriagedAndEnabledAndExpired(executors, false, true, false);
    }

    public List<BuildTriage> getBuildTriageOf(List<Executor> executors){
        return buildTriageRepository.findAllByExecutorInAndEnabledAndExpired(executors, true, false);
    }

    public List<BuildTriage> getBuildTriageBy(String search) {
        return getBuildTriageBy(search, null);
    }

    public List<BuildTriage> getBuildTriageBy(String search, Container container) {
        if(isNullOrEmpty(search))
            return getBuildTriagedBy(container);

        List<Executor> executors = container!= null ? executorService.findAllWithTestAndExecutorName(search, container) : executorService.findAllWithTestAndExecutorName(search);
        
        List<BuildTriage> buildTriages = executors.stream()
                .map( executor -> this.getLastTriageOf(executor ))
                .collect(Collectors.toList());
        return buildTriages;
    }

    public List<BuildTriage> getBuildTriagedBy(Container container) {
        List<Long> buildIds = container != null ? buildTriageRepository.findOngoing(container) : buildTriageRepository.findOngoing();
        return buildTriageRepository.findAllByIdInOrderByRankDesc(buildIds);
    }

    public List<BuildTriage> findWithBuilds(List<Build> builds) {
        return buildTriageRepository.findAllByBuildIn(builds);
    }
    
    

    // I think this is deprecated
    public List<BuildTriage> getPendingBuildTriages(Container container, boolean triaged) {
        List<BuildTriage> builds;
        if (container == null) {
            builds = buildTriageRepository.findByTriagedAndExpiredAndEnabled(triaged, false, true);
        } else {
            builds = getPendingTriage(container);
        }
        return builds;
    }

    public List<BuildTriage> pendingBigBuilds(User user, long fromDate) {
        // Start getting the list of future builds that are big enough to start working on now
        List<BuildTriage> futureBuilds = buildTriageRepository.findAllByEnabledAndTriagerAndTriagedAndDeadlineGreaterThanOrderByDeadlineAsc(true, user, false, fromDate);

        List<BuildTriage> bigBuilds = new ArrayList<>();
        int daysFromToday = 0;
        long pendingTests = 0;
        Integer testsPerDay = propertyService.valueOf(MAX_TESTCASES_PER_DAY, DEFAULT_MAX_TESTCASES_PER_DAY);

        for (BuildTriage build : futureBuilds) {
            daysFromToday = Math.max(1, DateUtils.daysFromToday(build.getDeadline(), 24));
            pendingTests = testTriageService.countPendingToTriage(build);
            if (pendingTests > testsPerDay * daysFromToday ) {
                bigBuilds.add(build);
            }
        }

        return bigBuilds;
    }

    public int expireOldEvents(long timestamp) {
        // Set enabled = false & triaged = true
        return buildTriageRepository.expireByDeadlineLessThan(timestamp);
    }

    // Expires all the build triages except the last one
    public int expireAllExcept(Build build) {
        // Set enabled = false & triaged = true
        // return buildTriageRepository.expireAllExcept(build, build.getExecutor());

        List<BuildTriage> builds = buildTriageRepository.findAllByExecutorAndNumberLessThanAndEnabledAndTriagedAndExpiredOrderByNumberDesc(build.getExecutor(), build.getNumber(), true, false, false);

        for (BuildTriage deprecatedBuilds: builds) {
            deprecatedBuilds.expire();
            update(deprecatedBuilds);
        }

        return builds.size();
    }

    public List<BuildTriage> findAllOlderThan(long timestamp) {
        return buildTriageRepository.findAllByDeadlineLessThanAndEnabled(timestamp, true);
    }

    public List<BuildTriage> findAllByExecutor(Executor executor) {
        this.findAll();
        executorService.findAll();
        return buildTriageRepository.findAllByExecutorOrderByNumberAsc(executor);
    }

    private List<User> findUsersCanTriage(BuildTriage build, int limit) {
        List<User> usersByExecutor = buildTriageRepository.findUserByExecutor(build.getExecutor());

        if (usersByExecutor.size() >= limit) {
            return usersByExecutor;
        }

        List<User> answer = new ArrayList<>(limit);
        answer.addAll(usersByExecutor);

        List<User> usersByContainers = buildTriageRepository.findUserByContainer(build.getContainer());

        for (User userId : usersByContainers) {
            if (!answer.contains(userId)) {
                addUser(answer, userId);
            }
            if (answer.size() >= limit) {
                return answer;
            }
        }
        return answer;

    }

    public BuildTriage getPreviousTriage(BuildTriage buildTriage) {
        if (buildTriage == null) {
            return null;
        }
        if (buildTriage.getBuild() == null || buildTriage.getExecutor() == null) {
            return null;
        }
        List<BuildTriage> builds = buildTriageRepository.findTopByExecutorAndNumberLessThanAndEnabledOrderByNumberDesc(buildTriage.getExecutor(), buildTriage.getBuild().getNumber(), true);
        if (builds.isEmpty()) {
            return null;
        } else {
            return builds.get(0);
        }
    }

    /**
     * Order: Logged user, current assignee, executor assignee, container assignee,
     * other users that have triaged the executor and then the ones that has triaged the container
     *
     * @param buildTriage
     * @param loggedUser
     * @return
     */
    public LinkedHashSet<User> findSuggestedAssignee(BuildTriage buildTriage, User loggedUser) {
        int maxAmount = Constants.UI_SUGGESTION_SIZE;
        LinkedHashSet<User> answer = new LinkedHashSet<>(maxAmount);
        User suggestedUser;

        answer.add(loggedUser);

        // Add the selected triager
        suggestedUser = buildTriage.getTriager();
        addUser(answer, suggestedUser);

        // Add the spec triager associated to the executor
        TriageSpec spec = buildTriage.getSpec();
        suggestedUser = buildTriage.getTriager();
        addUser(answer, suggestedUser);

        // Add the spec triager associated to the container
        if (spec.getExecutor() != null) {
            // Let's find the default one
            spec = triageSpecService.geTriageFlowSpecByContainer(buildTriage.getContainer());
            suggestedUser = buildTriage.getTriager();
            addUser(answer, suggestedUser);
        }

        if (answer.size() < maxAmount) {
            // Add other users that have triaged the executor and then the ones that has triaged the container
            List<User> otherUsers = findUsersCanTriage(buildTriage, maxAmount * 2);

            int i = 0;
            while (answer.size() < maxAmount && i < otherUsers.size()) {
                suggestedUser = otherUsers.get(i);
                addUser(answer, suggestedUser);
                i++;
            }
        }

        return answer;
    }

    public void notifyBuildTriaged(BuildTriage buildTriage) {
        ApplicationEvent event = applicationEventBuilder.newEvent();
        event.setType(ApplicationEventType.BUILD_TRIAGED);
        event.setExtraParameter(String.valueOf(buildTriage.getId()));
        applicationEventBuilder.saveUnique(event, true);
    }

    private void addUser(Collection<User> list, User user) {
        if (user.isEnabled()) {
            list.add(user);
        }
    }

    public BuildTriage setAssigneeToBuild(User user, Long buildId) {
        BuildTriage buildTriage = find(buildId);
        buildTriage.setTriager(user);

        changeAssigneeAtSpec(buildTriage.getExecutor(), user);

        List<TestTriage> triages = testTriageService.find(buildTriage);
        triages.forEach(testTriage -> {
            testTriageService.setAssigneeToTest(user.getId(), testTriage.getId());
        });

        return update(buildTriage);
    }

    private TriageSpec changeAssigneeAtSpec(Executor executor, User user) {
        TriageSpec spec = triageSpecService.geTriageFlowSpecByExecutor(executor);
        if (spec == null) {
            TriageSpec specContainer = triageSpecService.geTriageFlowSpecByContainer(executor.getContainer());
            // There isn't any spec configuration for the executor. creating a new one to override the container spec
            spec = triageSpecService.buildNewSpec(specContainer, executor);
            spec.setTriager(user);

            spec = triageSpecService.save(spec);

        } else {
            // the configuration for the container exist, so updating it
            spec.setTriager(user);
            triageSpecService.update(spec);
        }
        return spec;
    }

    private TriageSpec changePriorityAtSpec(BuildTriage triage, int priority) {
        TriageSpec spec = triageSpecService.geTriageFlowSpecByExecutor(triage.getExecutor());
        if (spec == null) {
            TriageSpec specContainer = triageSpecService.geTriageFlowSpecByContainer(triage.getContainer());
            // There isn't any spec configuration for the executor. creating a new one to override the container spec
            spec = triageSpecService.buildNewSpec(specContainer, triage.getExecutor());
            spec.setPriority(priority);

            spec = triageSpecService.save(spec);

            triage.setSpec(spec);
            update(triage);
        } else {
            // the configuration for the container exist, so updating it
            spec.setPriority(priority);
            triageSpecService.update(spec);
        }
        return spec;
    }

    @Autowired
    public void setBuildCache(CacheManager cacheManager) {
        this.buildCache = Objects.requireNonNull(cacheManager.getCache("buildCache"));
    }

    public List<BuildTriage> findTodaysBuilds(User user, long tomorrow) {
        return buildTriageRepository.findAllByEnabledAndTriagerAndTriagedAndDeadlineLessThanEqualOrderByRankDesc(true, user, false, tomorrow);
    }

    public int assignPriority(BuildTriage buildTriage, int priority) {
        TriageSpec spec = changePriorityAtSpec(buildTriage, priority);
        return spec.getPriority();
    }

    public BuildTriage create(BuildTriage buildTriage) {
        buildTriage.initialize();

        TriageDeadline triageDeadline = triageDeadlineService.computeTriageDeadline(buildTriage);
        buildTriage.setTriageDeadline(triageDeadline);

        buildTriage.setTriager(triageSpecService.getTriager(buildTriage));

        buildTriage = save(buildTriage);

        return buildTriage;
    }

    public long countTriagesCompleted() {
        return buildTriageRepository.countByTriagedAndExpiredAndEnabled(true, false, true);
    }

    public List<BuildTriage> findAllByTimestampBetween(long timestampFrom, long timestampTo) {
        return buildTriageRepository.findAllByTimestampBetween(timestampFrom, timestampTo);
    }

    public Map<User, List<BuildTriage>> getBuildTriageByGroupedByUser(Product product, boolean triaged) {
        return getBuildTriageBy(product, triaged)
                .stream()
                .collect(Collectors.groupingBy(BuildTriage::getTriager));
    }

    public Map<User, List<BuildTriage>> getBuildTriageByGroupedByUser(Product product, boolean triaged, Long timeStampFrom, Long timeStampTo) {
        return getBuildTriageBy(product, triaged, timeStampFrom, timeStampTo)
                .stream()
                .collect(Collectors.groupingBy(BuildTriage::getTriager));
    }

    public List<BuildTriage> getBuildTriageBy(Product product, boolean triaged) {
        return buildTriageRepository.findAllByProduct(product, triaged);
    }

    public List<BuildTriage> getBuildTriageBy(Product product, boolean triaged, Long timeStampFrom, Long timeStampTo) {
        return buildTriageRepository.findAllByProduct(product, triaged, timeStampFrom, timeStampTo);
    }    

    public List<BuildTriage> getBuildTriageBy(User user, boolean triaged) {
        return buildTriageRepository.findAllByTriagerAndTriaged(user, triaged);
    }

    public List<BuildTriage> getBuildTriageBy(User user, boolean triaged, Long timeStampFrom, Long timeStampTo) {
        return buildTriageRepository.findAllByTriagerAndTriagedAndTimestampBetween(user, triaged, timeStampFrom, timeStampTo);
    }    

    public List<TestTriage> getTextDetail(BuildTriage buildTriage) {
        List<TestTriage> answer = testTriageService.findAllByBuildAndStateNot(buildTriage.getBuild(), StateType.PASS);
        Hibernate.unproxy(answer);
        return answer;
    }

    public List<BuildTriage> findAllByContainerProductAndUpdatedGreatedThanTwoMonth(Product product){
        Calendar twoMonthAgo = Calendar.getInstance();
        twoMonthAgo.add(Calendar.MONTH, 2);
        return buildTriageRepository.findDistinctAllByContainerProductAndUpdatedGreaterThan(product, Calendar.getInstance().getTimeInMillis()-twoMonthAgo.getTimeInMillis());
    }

}
