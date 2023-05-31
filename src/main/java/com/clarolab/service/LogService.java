package com.clarolab.service;

import com.clarolab.dto.LogCommitsPerDayDTO;
import com.clarolab.dto.LogCommitsPerPersonAndPerDayDTO;
import com.clarolab.dto.LogCommitsPerPersonDTO;
import com.clarolab.model.*;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.LogRepository;
import com.clarolab.util.DateUtils;
import lombok.extern.java.Log;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.StringUtils;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Service
@Log
public class LogService extends BaseService<CVSLog> {

    private final String PATH_TEST = "src/test/java";

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private ProductService productService;

    private Repository repository;
    private Git git;
    private CredentialsProvider credentialsProvider;

    @Override
    public BaseRepository<CVSLog> getRepository() {
        return logRepository;
    }

    public List<CVSLog> gitLog(CVSRepository cvsRepository) {
        List<CVSLog> cvsLogs = readLog(cvsRepository);

        processLog(cvsLogs);

        return cvsLogs;
    }

    public List<CVSLog> processLog(List<CVSLog> cvsLogs) {
        List<CVSLog> processedLogs = new ArrayList<>();
        log.log(Level.INFO, String.format("Starting to process %s logs.", cvsLogs.size()));

        if (cvsLogs != null || !cvsLogs.isEmpty()) {
            for (CVSLog cvsLog : cvsLogs) {
                CVSLog actualLog = processLog(cvsLog);
                if (actualLog != null) {
                    processedLogs.add(actualLog);
                }
            }
        }

        log.log(Level.INFO, String.format("Log processing finished. Saving %s of %s logs.", processedLogs.size(), cvsLogs.size()));
        return processedLogs;
    }

    private CVSLog processLog(CVSLog cvsLog) {
        // find the user
        if (cvsLog.getAuthor() == null && !StringUtils.isEmptyOrNull(cvsLog.getAuthorText())) {
            User user = null;

            if (com.clarolab.util.StringUtils.isValidEmailAddress(cvsLog.getAuthorText()))
                user = userService.findByUsername(cvsLog.getAuthorText());

            if (user == null && !StringUtils.isEmptyOrNull(cvsLog.getAuthorRealname()))
                user = userService.findByRealname(cvsLog.getAuthorRealname());

            cvsLog.setAuthor(user);
        }

        // find the test
        String path = FilenameUtils.getPath(cvsLog.getLocationPath());
        String fileName = FilenameUtils.getBaseName(cvsLog.getLocationPath());

        String[] splittedPath = path.split("\\.|/");
        String slashedPath = fileName;
        String dottedPath = fileName;

        if (cvsLog.getTest() == null && !StringUtils.isEmptyOrNull(path)) {
            List<TestCase> testCases;

            for (int i = splittedPath.length - 1; i > 0; i--) {
                testCases = testCaseService.findAllByLocationPathContains(dottedPath);
                if (testCases.isEmpty())
                    testCases = testCaseService.findAllByLocationPathContains(slashedPath);

                if (testCases.isEmpty())
                    break;

                cvsLog.setTest(testCases.get(0)); //TODO remove and get the test by name

                if (testCases.size() == 1)
                    break;
                else {
                    dottedPath = splittedPath[i] + "." + dottedPath;
                    slashedPath = splittedPath[i] + "/" + slashedPath;
                }
            }
        }

        //find the product
        if (cvsLog.getProduct() == null) {
            List<Product> products;
            slashedPath = splittedPath[splittedPath.length - 1];
            dottedPath = splittedPath[splittedPath.length - 1];

            for (int i = splittedPath.length - 2; i > 0; i--) {
                products = productService.findProductsByPackageNames(slashedPath);
                if (products.isEmpty())
                    products = productService.findProductsByPackageNames(dottedPath);

                if (products.size() == 1) {
                    cvsLog.setProduct(products.get(0));
                    break;
                } else {
                    if (products.isEmpty()) {
                        slashedPath = splittedPath[i];
                        dottedPath = splittedPath[i];
                    } else {
                        dottedPath = splittedPath[i] + "." + dottedPath;
                        slashedPath = splittedPath[i] + "/" + slashedPath;
                    }
                }
            }
        }

        //if the csvLog is valid it has to be saved in the database
        CVSLog existingLog = findTopByCommitHashAndByTest(cvsLog.getCommitHash(), cvsLog.getTest());
        if (existingLog != null) {
            existingLog.setAuthor(cvsLog.getAuthor());
            existingLog.setProduct(cvsLog.getProduct());
            existingLog.setTest(cvsLog.getTest());
            update(existingLog);

            return null;
        }

        if (cvsLog.getTest() == null)
            return null;

        return cvsLog;
    }

    private void initService(CVSRepository cvsRepository) {
        try {
            this.repository = new FileRepository(
                    cvsRepository.getLocalPath() + "/.git");
            this.credentialsProvider = new UsernamePasswordCredentialsProvider(
                    cvsRepository.getUsername(),
                    cvsRepository.getPassword());
            this.git = new Git(repository);
            if (Files.notExists(Paths.get(cvsRepository.getLocalPath() + "/.git"))) {
                clone(cvsRepository);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, String.format("Error connecting cvs repository: %d", cvsRepository.getId()), e);
        }
    }

    private String getTestName(String testName) {
        String[] name = testName.split(" ");
        for (int i = 0; i < name.length; i++) {
            if (name[i].contains("(")) {
                String result = name[i].split("\\(")[0];
                return result;
            }
        }
        return null;
    }

    private List<TestCase> findTestInsert(DiffEntry diffEntry, EditList editList) throws IOException {
        List<TestCase> testCases = new ArrayList<>();

        for (int i = 0; i < editList.size(); i++) {
            int inicio = editList.get(i).getBeginB();
            int fin = editList.get(i).getEndB();

            List<String> lines = Files.readAllLines(Paths.get(diffEntry.getNewPath()));

            if (lines.size() >= fin) {
                while (inicio < fin) {
                    String line = lines.get(inicio).trim();
                    if (line.startsWith("@Test")) {
                        TestCase testCase = new TestCase();
                        testCase.setLocationPath(getPackage(diffEntry.getNewPath()));
                        int k = inicio + 1;
                        while (!lines.get(k).trim().startsWith("public")) {
                            k++;
                        }

                        String testName = getTestName(lines.get(k).trim());
                        if (testName != null) {
                            testCase.setName(testName);
                            testCases.add(testCase);
                        }
                    }
                    inicio++;
                }
            }
        }
        return testCases;
    }

    private TestCase findTestModify(DiffEntry diffEntry, EditList editList) throws IOException {

        for (int i = 0; i < editList.size(); i++) {
            int inicio = editList.get(i).getBeginB();

            List<String> lines = Files.readAllLines(Paths.get(diffEntry.getNewPath()));

            if (lines.size() > inicio) {
                while (inicio > 0) {
                    String line = lines.get(inicio).trim();
                    if (line.startsWith("public")) {
                        TestCase testCase = new TestCase();
                        testCase.setLocationPath(getPackage(diffEntry.getNewPath()));
                        int k = inicio - 1;
                        while (lines.get(k).trim().startsWith("@")) {
                            if (!lines.get(k).trim().startsWith("@Test")) {
                                k--;
                            } else {
                                String testName = getTestName(lines.get(inicio).trim());
                                if (testName != null) {
                                    testCase.setName(testName);
                                    return testCase;
                                }
                            }
                        }
                        return null;
                    } else if (line.startsWith("private")) {
                        return null;
                    } else if (line.startsWith("import")) {
                        return null;
                    } else if (line.startsWith("@Test")) {
                        return null;
                    }
                    inicio--;
                }
            }
        }
        return null;
    }

    private List<CVSLog> readLog(CVSRepository cvsRepository) {

        initService(cvsRepository);

        List<CVSLog> cvsLogs = new ArrayList<>();
        CVSLog cvsLog;
        User author;

        try {
            git.pull().setCredentialsProvider(credentialsProvider).call();

            RevWalk rw = new RevWalk(repository);
            ObjectId branchId = repository.resolve(cvsRepository.getBranch());
            LogCommand command = git.log().add(branchId);
            if (!cvsRepository.getPackages().isEmpty()) {
                for (String path : cvsRepository.getPackages()) {
                    command.addPath(PATH_TEST + "/" + path);
                }
            } else {
                command.addPath(PATH_TEST);
            }
            Iterable<RevCommit> commits = command.call();

            for (RevCommit commit : commits) {

                cvsLog = new CVSLog();
                cvsLog.setCvsRepository(cvsRepository);

                //log.info("COMMIT NUM: " + commit.getName() + " - AUTHOR: " + commit.getAuthorIdent().getName());

                if (cvsRepository.getLastRead() > commit.getCommitTime()) {
                    break;
                }

                cvsLog.setAuthorText(commit.getAuthorIdent().getName());
                cvsLog.setApproverText(commit.getCommitterIdent().getName());
                cvsLog.setCommitHash(commit.getName());
                cvsLog.setCommitDate(commit.getCommitTime());

                author = new User();
                author.setUsername(commit.getAuthorIdent().getEmailAddress());
                author.setRealname(commit.getAuthorIdent().getName());

                cvsLog.setAuthor(author);

                if (commit.getParentCount() != 0) {
                    RevCommit parent = rw.parseCommit(commit.getParent(0).getId());

                    DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
                    df.setRepository(repository);
                    df.setDiffComparator(RawTextComparator.DEFAULT);
                    df.setDetectRenames(true);
                    List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());

                    for (DiffEntry diff : diffs) {
                        //SI CORRESPONDE A TEST --> ADD list
                        if (pathInPackage(diff.getNewPath(), cvsRepository)) {
                            cvsLog.setCodeModified(diff.getNewPath());

                            FileHeader fileHeader = df.toFileHeader(diff);
                            EditList editList = fileHeader.toEditList();

                            for (int i = 0; i < editList.size(); i++) {
                                if (editList.get(i).getType() == Edit.Type.DELETE) {
                                    editList.remove(i);
                                }
                            }

                            List<TestCase> testCases = findTestInsert(diff, editList);
                            if (!testCases.isEmpty()) {
                                for (TestCase testCase : testCases) {
                                    cvsLog.setTest(testCase);

                                    log.info("COMMIT NUM: " + commit.getName() + " - AUTHOR: " + commit.getAuthorIdent().getName());
                                    log.info("test name: " + testCase.getName() + " - url: " + testCase.getLocationPath());

                                    cvsLogs.add(cvsLog);
                                }
                            }
                            //si no es un nuevo test puede ser una modificacion
                            TestCase testCase = findTestModify(diff, editList);
                            if (testCase != null) {
                                cvsLog.setTest(testCase);

                                log.info("COMMIT NUM: " + commit.getName() + " - AUTHOR: " + commit.getAuthorIdent().getName());
                                log.info("test name: " + testCase.getName() + "- url: " + testCase.getLocationPath());

                                cvsLogs.add(cvsLog);
                            }
                        }
                    }
                }
            }
        } catch (IncorrectObjectTypeException e) {
            log.log(Level.SEVERE, String.format("Error reading csv log for: %d", cvsRepository.getId()), e);
        } catch (AmbiguousObjectException e) {
            log.log(Level.SEVERE, String.format("Error reading csv log for: %d", cvsRepository.getId()), e);
        } catch (MissingObjectException e) {
            log.log(Level.SEVERE, String.format("Error reading csv log for: %d", cvsRepository.getId()), e);
        } catch (NoSuchFileException e) {
            log.log(Level.SEVERE, String.format("Error reading csv log for: %d", cvsRepository.getId()), e);
        } catch (IOException e) {
            log.log(Level.SEVERE, String.format("Error reading csv log for: %d", cvsRepository.getId()), e);
        } catch (NoHeadException e) {
            log.log(Level.SEVERE, String.format("Error reading csv log for: %d", cvsRepository.getId()), e);
        } catch (GitAPIException e) {
            log.log(Level.SEVERE, String.format("Error reading csv log for: %d", cvsRepository.getId()), e);
        }

        return cvsLogs;
    }

    private boolean pathInPackage(String path, CVSRepository cvsRepository) {
        for (String packageName : cvsRepository.getPackages()) {
            if (path.startsWith(PATH_TEST + "/" + packageName)) {
                return true;
            }
        }
        if (path.startsWith(PATH_TEST)) {
            return true;
        }
        return false;
    }

    private String getPackage(String path) {
        String name = path.split("\\.")[0];
        String strPackage = name.substring(PATH_TEST.length() + 1);

        return strPackage.replace("/", ".");
    }

    public boolean isValidRepository(CVSRepository cvsRepository) {
        boolean isSuccessful = false;
        this.initService(cvsRepository);
        try {
            isSuccessful = git.pull().setCredentialsProvider(credentialsProvider).call().isSuccessful();

        } catch (WrongRepositoryStateException e) {
            log.log(Level.SEVERE, String.format("Error reading csv log for: %d", cvsRepository.getId()), e);
        } catch (InvalidConfigurationException e) {
            log.log(Level.SEVERE, String.format("Error reading csv log for: %d", cvsRepository.getId()), e);
        } catch (CanceledException e) {
            log.log(Level.SEVERE, String.format("Error reading csv log for: %d", cvsRepository.getId()), e);
        } catch (InvalidRemoteException e) {
            log.log(Level.SEVERE, String.format("Error reading csv log for: %d", cvsRepository.getId()), e);
        } catch (TransportException e) {
            log.log(Level.SEVERE, String.format("Error reading csv log for: %d", cvsRepository.getId()), e);
        } catch (RefNotFoundException e) {
            log.log(Level.SEVERE, String.format("Error reading csv log for: %d", cvsRepository.getId()), e);
        } catch (NoHeadException e) {
            log.log(Level.SEVERE, String.format("Error reading csv log for: %d", cvsRepository.getId()), e);
        } catch (RefNotAdvertisedException e) {
            log.log(Level.SEVERE, String.format("Error reading csv log for: %d", cvsRepository.getId()), e);
        } catch (GitAPIException e) {
            log.log(Level.SEVERE, String.format("Error reading csv log for: %d", cvsRepository.getId()), e);
        } finally {
            return isSuccessful;
        }
    }

    public void clone(CVSRepository cvsRepository) {
        File file = new File(cvsRepository.getLocalPath());
        try {
            this.git.cloneRepository()
                    .setDirectory(file)
                    .setURI(cvsRepository.getUrl())
                    .setCloneAllBranches(true)
                    .setCredentialsProvider(credentialsProvider)
                    .call();

        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    public List<LogCommitsPerPersonDTO> getCommitsPerPerson() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_MONTH, -2);
        long prev = cal.getTimeInMillis();
        long now = DateUtils.now();
        int maxLimit = 5;

        return logRepository.countCommitsByAuthor(prev, now).stream().limit(maxLimit).collect(Collectors.toList());
    }


    public List<LogCommitsPerDayDTO> getCommitsPerDay() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        long prev = cal.getTimeInMillis();
        long now = DateUtils.now();
        List<LogCommitsPerDayDTO> commits = logRepository.countCommitsByDay(prev, now);
        List<LogCommitsPerDayDTO> answer = new ArrayList<>();

        for (LogCommitsPerDayDTO c : commits) {

            if (answer.isEmpty()) {
                answer.add(c);
            } else {

                // Check on list whether it is already a commit on that day
                boolean contains = answer.stream().anyMatch(o -> o.getCommitDay().equals(c.getCommitDay()));

                // If it exists get that object
                if (contains) {
                    LogCommitsPerDayDTO present = answer.stream().filter(o -> o.getCommitDay().equals(c.getCommitDay())).findFirst().get();
                    present.setCommitCount(present.getCommitCount() + c.getCommitCount());
                } else {
                    answer.add(c);
                }
            }
        }

        return answer;
    }

    public List<LogCommitsPerPersonAndPerDayDTO> getCommitsPerPersonAndPerDay() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        long prev = cal.getTimeInMillis();
        long now = DateUtils.now();
        int maxAuthors = 7;

        List<LogCommitsPerPersonAndPerDayDTO> commits = logRepository.countCommitsByAuthorAndByDay(prev, now);
        List<LogCommitsPerPersonDTO> topCommitters = logRepository.countCommitsByAuthor(prev, now).stream().limit(maxAuthors).collect(Collectors.toList());
        List<LogCommitsPerPersonAndPerDayDTO> answer = new ArrayList<>();

        List<LogCommitsPerPersonAndPerDayDTO> groupedByDay = commits;

        for (LogCommitsPerPersonAndPerDayDTO a : commits) {
            Long prevAuthor = a.getAuthorId();
            String prevDate = a.getCommitDay();
            long prevTimestamp = a.getCommitDate();
            long count = 0;

            for (LogCommitsPerPersonAndPerDayDTO b : groupedByDay) {
                if (b.getAuthorId() == prevAuthor) {
                    count++;
                    if (b.getCommitDay().equals(prevDate) && !b.getCommitDate().equals(prevTimestamp)) {
                        a.setCommitCount(a.getCommitCount() + b.getCommitCount());
                        b.setCommitCount(0);
                    } else if (count == 0) {
                        continue;
                    }
                }

            }
        }
        commits.removeIf(c -> (c.getCommitCount() == 0));

        // If the commit author is on the top 7 add it
        for (LogCommitsPerPersonAndPerDayDTO c : commits) {
            if (topCommitters.stream().anyMatch(o -> o.getAuthorId() == c.getAuthorId()))
                answer.add(c);
        }

        return answer;
    }

    public long deleteOld(long timestamp) {
        long deletedCount = logRepository.deleteByTimestampLessThan(timestamp);
        log.log(Level.INFO, String.format("ApplicationEvent: Finish deleting %d tests", deletedCount));
        return deletedCount;
    }

    public CVSLog findTopByCommitHashAndByTest(String commitHash, TestCase test) {
        return logRepository.findTopByCommitHashAndTest(commitHash, test);
    }

    public Boolean exists(String commitHash) {
        return !logRepository.findByCommitHashAndEnabledTrue(commitHash).isEmpty();
    }

    public List<CVSLog> findAllByTimestampBetween(long prev, long now) {
        return logRepository.findAllBetweenGroupByAuthor(prev, now);
    }

}
