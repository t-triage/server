package com.clarolab.service;

import com.clarolab.model.CVSLog;
import com.clarolab.model.CVSRepository;
import com.clarolab.model.Product;
import com.clarolab.model.types.LogType;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.CVSRepositoryRepository;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import com.clarolab.view.KeyValuePair;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Log
public class CVSRepositoryService extends BaseService<CVSRepository> {

    // Commit details
    protected static Integer GIT_COMMIT_HASH = 0;
    protected static Integer GIT_COMMIT_DATE = 6;
    protected static Integer GIT_AUTHOR_EMAIL = 2;
    protected static Integer GIT_AUTHOR_NAME = 1;
    protected static Integer GIT_APPROVER_TEXT = 7;
    protected static Integer GIT_ADDED_LINES = 0;
    protected static Integer GIT_REMOVED_LINES = 1;
    protected static Integer GIT_LOCATION_PATH = 2;


    @Autowired
    private CVSRepositoryRepository cvsRepositoryRepository;

    @Autowired
    private LogService logService;

    @Override
    public BaseRepository<CVSRepository> getRepository() {
        return cvsRepositoryRepository;
    }

    public List<KeyValuePair> getCvsRepositoriesNames() {
        List<Object[]> list = cvsRepositoryRepository.findAllNames();
        return StringUtils.getKeyValuePairList(list);
    }

    public List<CVSLog> read(CVSRepository cvsRepository) {

        List<CVSLog> importedLogs = logService.gitLog(cvsRepository);

        if (importedLogs == null) {
            importedLogs = new ArrayList<>();
        }

        cvsRepository.setLastRead(DateUtils.now());
        update(cvsRepository);

        return importedLogs;
    }

    public List<CVSLog> read(String fileContent, Product product, LogType logType) {
        switch(logType) {
            case GIT:
                List<String> lines = Arrays.asList(fileContent.split("\\r?\\n"));
                return readGIT(lines, product, logType);
            case SVN:
                return readSVN(fileContent, product, logType);
            case MERCURIAL:
                return readMercurial(fileContent, product, logType);
        }
        return null;
    }

    public List<CVSLog> read(List<String> fileContent, Product product, LogType logType) {
        switch(logType) {
            case GIT:
                return readGIT(fileContent, product, logType);
            case SVN:
                StringBuilder sb = new StringBuilder();
                for(String s: fileContent)
                    sb.append(s);
                return readSVN(sb.toString(), product, logType);
            case MERCURIAL:
                StringBuilder builder = new StringBuilder();
                for(String s: fileContent)
                    builder.append(s);
                return readMercurial(builder.toString(), product, logType);
        }
        return null;
    }

    public List<CVSLog> readMercurial(String fileContent, Product product, LogType logType) {
        if (product == null || org.eclipse.jgit.util.StringUtils.isEmptyOrNull(product.getPackageNames()))
            return null;

        List<CVSLog> importedLogs = new ArrayList<>();
        String packageName = product.getPackageNames();

        if (packageName.contains("."))
            packageName = packageName.replaceAll("\\.", "/");

        String commitHash;
        String authorText;
        String authorRealname;
        String approverText;
        String locationPath;
        Long commitDate;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(IOUtils.toInputStream(fileContent, "UTF-8"));
            NodeList logs = doc.getElementsByTagName("logentry");

            for (int i=0; i<logs.getLength(); i++) {
                Node logEntry = logs.item(i);
                if (logEntry.getNodeType() == Node.ELEMENT_NODE) {
                    Element node = (Element) logEntry;
                    commitHash = node.getAttribute("node");
                    authorText = node.getElementsByTagName("author").item(0).getAttributes().item(0).getNodeValue();
                    authorRealname = node.getElementsByTagName("author").item(0).getFirstChild().getNodeValue();
                    approverText = node.getElementsByTagName("msg").item(0).getFirstChild().getNodeValue();
                    commitDate = DateUtils.convertDate(node.getElementsByTagName("date").item(0).getFirstChild().getNodeValue(), "yyyy-MM-dd'T'HH:mm:ss");

                    if (approverText.contains("\n"))
                        approverText = approverText.substring(0, approverText.indexOf("\n"));

                    NodeList pathList = node.getElementsByTagName("paths").item(0).getChildNodes();
                    for (int j=0; j<pathList.getLength(); j++)
                        if (pathList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            Element path = (Element) pathList.item(j);
                            if (!path.getFirstChild().getNodeValue().contains(packageName))
                                continue;
                            CVSLog log = new CVSLog();
                            locationPath = path.getFirstChild().getNodeValue();
                            if (locationPath.contains("\n"))
                                locationPath = locationPath.substring(0, locationPath.indexOf("\n"));
                            log.setEnabled(true);
                            log.setTimestamp(DateUtils.now());
                            log.setUpdated(DateUtils.now());
                            log.setCommitHash(commitHash);
                            log.setAuthorText(authorText);
                            log.setAuthorRealname(authorRealname);
                            log.setApproverText(approverText);
                            log.setCommitDate(commitDate);
                            log.setCommitDay(DateUtils.covertToString(commitDate, DateUtils.DATE_SMALL));
                            log.setLocationPath(locationPath);
                            log.setLogType(logType);
                            log.setUpdatedLines(1);
                            importedLogs.add(log);
                        }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return importedLogs;
    }

    public List<CVSLog> readSVN(String fileContent, Product product, LogType logType) {
        if (product == null || org.eclipse.jgit.util.StringUtils.isEmptyOrNull(product.getPackageNames()))
            return null;

        List<CVSLog> importedLogs = new ArrayList<>();
        String packageName = product.getPackageNames();

        if (packageName.contains("."))
            packageName = packageName.replaceAll("\\.", "/");

        String commitHash;
        String authorText;
        String approverText;
        String locationPath;
        Long commitDate;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(IOUtils.toInputStream(fileContent, "UTF-8"));
            NodeList logs = doc.getElementsByTagName("logentry");

            for (int i=0; i<logs.getLength(); i++) {
                Node logEntry = logs.item(i);
                if (logEntry.getNodeType() == Node.ELEMENT_NODE) {
                    Element node = (Element) logEntry;
                    commitHash = node.getAttribute("revision");
                    authorText = node.getElementsByTagName("author").item(0).getFirstChild().getNodeValue();
                    approverText = node.getElementsByTagName("msg").item(0).getFirstChild().getNodeValue();
                    commitDate = DateUtils.convertDate(node.getElementsByTagName("date").item(0).getFirstChild().getNodeValue(), "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");

                    if (approverText.contains("\n"))
                        approverText = approverText.substring(0, approverText.indexOf("\n"));

                    NodeList pathList = node.getElementsByTagName("paths").item(0).getChildNodes();
                    for (int j=0; j<pathList.getLength(); j++)
                        if (pathList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            Element path = (Element) pathList.item(j);
                            if (!path.getFirstChild().getNodeValue().contains(packageName))
                                continue;
                            CVSLog log = new CVSLog();
                            locationPath = path.getFirstChild().getNodeValue();
                            if (locationPath.contains("\n"))
                                locationPath = locationPath.substring(0, locationPath.indexOf("\n"));
                            log.setEnabled(true);
                            log.setTimestamp(DateUtils.now());
                            log.setUpdated(DateUtils.now());
                            log.setCommitHash(commitHash);
                            log.setAuthorText(authorText);
                            log.setApproverText(approverText);
                            log.setCommitDate(commitDate);
                            log.setCommitDay(DateUtils.covertToString(commitDate, DateUtils.DATE_SMALL));
                            log.setLocationPath(locationPath);
                            log.setLogType(logType);
                            log.setUpdatedLines(1);
                            importedLogs.add(log);
                        }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return importedLogs;
    }

    public List<CVSLog> readGIT(List<String> filecontent, Product product, LogType logType) {
        if (product == null || org.eclipse.jgit.util.StringUtils.isEmptyOrNull(product.getPackageNames()))
            return null;

        List<CVSLog> importedLogs = new ArrayList<>();
        String packageName = product.getPackageNames();

        if (packageName.contains("."))
            packageName = packageName.replaceAll("\\.", "/");

        String commitHash = "";
        String authorText = "";
        String authorName = "";
        String approverText = "";
        Long commitDate = DateUtils.now();

        for (String line: filecontent) {
            String[] details = line.split(" \\| ");

            if (details.length > 1) {
                commitHash = details[GIT_COMMIT_HASH];
                authorText = details[GIT_AUTHOR_EMAIL];
                authorName = details[GIT_AUTHOR_NAME];
                commitDate = DateUtils.convertDate(details[GIT_COMMIT_DATE], "yyyy-MM-dd HH:mm:ss Z");
                approverText = details[GIT_APPROVER_TEXT];
            } else if (!org.eclipse.jgit.util.StringUtils.isEmptyOrNull(line)) {
                details = line.split("\\t");

                if (details[GIT_ADDED_LINES].equals("-") || !details[GIT_LOCATION_PATH].contains(packageName))
                    continue;

                CVSLog log = new CVSLog();

                log.setUpdatedLines(Integer.parseInt(details[GIT_ADDED_LINES]) + Integer.parseInt(details[GIT_REMOVED_LINES]));
                log.setLocationPath(details[GIT_LOCATION_PATH]);

                log.setEnabled(true);
                log.setTimestamp(DateUtils.now());
                log.setUpdated(DateUtils.now());
                log.setAuthorText(authorText);
                log.setAuthorRealname(authorName);
                log.setCommitHash(commitHash);
                log.setApproverText(approverText);
                log.setCommitDate(commitDate);
                log.setCommitDay(DateUtils.covertToString(commitDate, DateUtils.DATE_SMALL));
                log.setProduct(product);
                log.setLogType(logType);

                importedLogs.add(log);
            }
        }

        return importedLogs;
    }

    public List<CVSLog> readAndProcess(String filecontent, Product product, LogType logType) {
        return logService.processLog(read(filecontent, product, logType));
    }

    public List<CVSLog> readAndProcess(List<String> filecontent, Product product, LogType logType) {
        return logService.processLog(read(filecontent, product, logType));
    }

    public boolean initConnection(CVSRepository cvsRepository) {
        return logService.isValidRepository(cvsRepository);
    }
}
