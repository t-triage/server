package com.clarolab.functional;

import com.clarolab.service.ExecutorImportCSVService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ImportCSVReportFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private ExecutorImportCSVService executorImportCSVService;

    @Test
    public void importCSV () {

        String content = "Product;Container Name;Suite Name;Test Name;Test Path;Triage Notes;Suite Detail;Status;Executed Time;Severity;Error Description;Stack Trace;Standard Output;User\n" +
                "Databricks;Security Tools;sso.sonatype.com;something/else CVE-2017-13165;OS;An elevation of privilege vulnerability in the kernel file system. Product: Android. Versions: Android kernel. Android ID A-31269937.;linux-libc-dev,linux-tools-common;FALSO;2019-09-12 10:56:53.384;negligible;needed ;\"latest twistcli Ubuntu-xenial 46 linux 4.4.0-142.168 GPL-2 7.8 ip-20-12-56-456 {\"\"created\"\":1550606208,\"\"instruction\"\":\"\"RUN apt-get update apt-get upgrade -y\"\"} \";;\n" +
                ";;sso.sonatype.com;something/else CVE-2018-14616;OS;An issue was discovered in the Linux kernel through 4.17.10. There is a NULL pointer dereference in fscrypt_do_page_crypto() in fs/crypto/crypto.c when operating on a file in a corrupted f2fs image.;linux-libc-dev,linux-tools-common;FALSO;2019-09-12 10:56:53.384;low;fixed in 4.4.0-145.171 ;\"latest twistcli Ubuntu-xenial 46 linux 4.4.0-142.168 GPL-2 5.5 ip-20-12-56-457 {\"\"created\"\":1550606208,\"\"instruction\"\":\"\"RUN apt-get update apt-get upgrade -y\"\"} \";;";

        String separator = ";";

        String format = "";

        String result = executorImportCSVService.importReport(content, separator, format);

        Assert.assertEquals(result.length(), 0);
    }

    @Test
    public void importCSVWithFormat () {
        String content = "Product;Container Name;Suite Name;Test Name;Test Path;Triage Notes;Suite Detail;Status;Executed Time;Severity;Error Description;Stack Trace;Standard Output;User;Registry;Repository;Tag;Id;Scan Time;Pass;Type;Distro;Hostname;Layer;CVE ID;Compliance ID;Type;Severity;Packages;Source Package;Package Version;Package License;CVSS;Fix Status;Description;Cause;Published;Custom Labels\n" +
                "Databricks;Security Tools;sso.sonatype.com;something/else CVE-2017-13165;OS;An elevation of privilege vulnerability in the kernel file system. Product: Android. Versions: Android kernel. Android ID A-31269937.;linux-libc-dev,linux-tools-common;true;2019-09-12 10:56:53.384;negligible;needed ;\"latest twistcli Ubuntu-xenial 46 linux 4.4.0-142.168 GPL-2 7,8 ip-20-12-56-456 {\"\"created\"\":1550606208,\"\"instruction\"\":\"\"RUN apt-get update apt-get upgrade -y\"\"} \";;;sso.sonatype.com;something/else;latest;sha256:d5c68f8e13563f747404bbafe43aLO;2019-09-12 10:56:53.384;true;twistcli;Ubuntu-xenial;ip-20-12-56-456;\"{\"\"created\"\":1550606208,\"\"instruction\"\":\"\"RUN apt-get update apt-get upgrade -y\"\"}\";CVE-2017-13165;46;OS;negligible;linux-libc-dev,linux-tools-common;linux;4.4.0-142.168;GPL-2;7,8;needed;An elevation of privilege vulnerability in the kernel file system. Product: Android. Versions: Android kernel. Android ID A-31269937.;;2017-12-06 14:29:00.000;\n" +
                ";;sso.sonatype.com;something/else CVE-2018-14616;OS;An issue was discovered in the Linux kernel through 4.17.10. There is a NULL pointer dereference in fscrypt_do_page_crypto() in fs/crypto/crypto.c when operating on a file in a corrupted f2fs image.;linux-libc-dev,linux-tools-common;true;2019-09-12 10:56:53.384;low;fixed in 4.4.0-145.171 ;\"latest twistcli Ubuntu-xenial 46 linux 4.4.0-142.168 GPL-2 5,5 ip-20-12-56-457 {\"\"created\"\":1550606208,\"\"instruction\"\":\"\"RUN apt-get update apt-get upgrade -y\"\"} \";;;sso.sonatype.com;something/else;latest;sha256:d5c68f8e13563f747404bbafe43aLO;2019-09-12 10:56:53.384;true;twistcli;Ubuntu-xenial;ip-20-12-56-457;\"{\"\"created\"\":1550606208,\"\"instruction\"\":\"\"RUN apt-get update apt-get upgrade -y\"\"}\";CVE-2018-14616;46;OS;low;linux-libc-dev,linux-tools-common;linux;4.4.0-142.168;GPL-2;5,5;fixed in 4.4.0-145.171;An issue was discovered in the Linux kernel through 4.17.10. There is a NULL pointer dereference in fscrypt_do_page_crypto() in fs/crypto/crypto.c when operating on a file in a corrupted f2fs image.;;2018-07-27 04:29:00.000;\n" +
                ";;sso.sonatype.com;something/else CVE-2018-14614;OS;An issue was discovered in the Linux kernel through 4.17.10. There is an out-of-bounds access in __remove_dirty_segment() in fs/f2fs/segment.c when mounting an f2fs image.;linux-libc-dev,linux-tools-common;true;2019-09-12 10:56:53.384;low;fixed in 4.4.0-145.171 ;\"latest twistcli Ubuntu-xenial 46 linux 4.4.0-142.168 GPL-2 5,5 ip-20-12-56-458 {\"\"created\"\":1550606208,\"\"instruction\"\":\"\"RUN apt-get update apt-get upgrade -y\"\"} \";;;sso.sonatype.com;something/else;latest;sha256:d5c68f8e13563f747404bbafe43aLO;2019-09-12 10:56:53.384;true;twistcli;Ubuntu-xenial;ip-20-12-56-458;\"{\"\"created\"\":1550606208,\"\"instruction\"\":\"\"RUN apt-get update apt-get upgrade -y\"\"}\";CVE-2018-14614;46;OS;low;linux-libc-dev,linux-tools-common;linux;4.4.0-142.168;GPL-2;5,5;fixed in 4.4.0-145.171;An issue was discovered in the Linux kernel through 4.17.10. There is an out-of-bounds access in __remove_dirty_segment() in fs/f2fs/segment.c when mounting an f2fs image.;;2018-07-27 04:29:00.000;\n";

        String separator = ";";

        String format = ";;16;17,26;28;36;30;21;20;29;35,39;18,22,23,27,31,32,33,34,24,25,38;;";

        String result = executorImportCSVService.importReport(content, separator, format);

        Assert.assertEquals(result.length(), 0);
    }

    @Test
    public void importCSVWithInvalidFormat () {
        String content = "Product;Container Name;Suite Name;Test Name;Test Path;Triage Notes;Suite Detail;Status;Executed Time;Severity;Error Description;Stack Trace;Standard Output;User;Registry;Repository;Tag;Id;Scan Time;Pass;Type;Distro;Hostname;Layer;CVE ID;Compliance ID;Type;Severity;Packages;Source Package;Package Version;Package License;CVSS;Fix Status;Description;Cause;Published;Custom Labels\n" +
                "Databricks;Security Tools;sso.sonatype.com;something/else CVE-2017-13165;OS;An elevation of privilege vulnerability in the kernel file system. Product: Android. Versions: Android kernel. Android ID A-31269937.;linux-libc-dev,linux-tools-common;true;2019-09-12 10:56:53.384;negligible;needed ;\"latest twistcli Ubuntu-xenial 46 linux 4.4.0-142.168 GPL-2 7,8 ip-20-12-56-456 {\"\"created\"\":1550606208,\"\"instruction\"\":\"\"RUN apt-get update apt-get upgrade -y\"\"} \";;;sso.sonatype.com;something/else;latest;sha256:d5c68f8e13563f747404bbafe43aLO;2019-09-12 10:56:53.384;true;twistcli;Ubuntu-xenial;ip-20-12-56-456;\"{\"\"created\"\":1550606208,\"\"instruction\"\":\"\"RUN apt-get update apt-get upgrade -y\"\"}\";CVE-2017-13165;46;OS;negligible;linux-libc-dev,linux-tools-common;linux;4.4.0-142.168;GPL-2;7,8;needed;An elevation of privilege vulnerability in the kernel file system. Product: Android. Versions: Android kernel. Android ID A-31269937.;;2017-12-06 14:29:00.000;\n" +
                ";;sso.sonatype.com;something/else CVE-2018-14616;OS;An issue was discovered in the Linux kernel through 4.17.10. There is a NULL pointer dereference in fscrypt_do_page_crypto() in fs/crypto/crypto.c when operating on a file in a corrupted f2fs image.;linux-libc-dev,linux-tools-common;true;2019-09-12 10:56:53.384;low;fixed in 4.4.0-145.171 ;\"latest twistcli Ubuntu-xenial 46 linux 4.4.0-142.168 GPL-2 5,5 ip-20-12-56-457 {\"\"created\"\":1550606208,\"\"instruction\"\":\"\"RUN apt-get update apt-get upgrade -y\"\"} \";;;sso.sonatype.com;something/else;latest;sha256:d5c68f8e13563f747404bbafe43aLO;2019-09-12 10:56:53.384;true;twistcli;Ubuntu-xenial;ip-20-12-56-457;\"{\"\"created\"\":1550606208,\"\"instruction\"\":\"\"RUN apt-get update apt-get upgrade -y\"\"}\";CVE-2018-14616;46;OS;low;linux-libc-dev,linux-tools-common;linux;4.4.0-142.168;GPL-2;5,5;fixed in 4.4.0-145.171;An issue was discovered in the Linux kernel through 4.17.10. There is a NULL pointer dereference in fscrypt_do_page_crypto() in fs/crypto/crypto.c when operating on a file in a corrupted f2fs image.;;2018-07-27 04:29:00.000;\n" +
                ";;sso.sonatype.com;something/else CVE-2018-14614;OS;An issue was discovered in the Linux kernel through 4.17.10. There is an out-of-bounds access in __remove_dirty_segment() in fs/f2fs/segment.c when mounting an f2fs image.;linux-libc-dev,linux-tools-common;true;2019-09-12 10:56:53.384;low;fixed in 4.4.0-145.171 ;\"latest twistcli Ubuntu-xenial 46 linux 4.4.0-142.168 GPL-2 5,5 ip-20-12-56-458 {\"\"created\"\":1550606208,\"\"instruction\"\":\"\"RUN apt-get update apt-get upgrade -y\"\"} \";;;sso.sonatype.com;something/else;latest;sha256:d5c68f8e13563f747404bbafe43aLO;2019-09-12 10:56:53.384;true;twistcli;Ubuntu-xenial;ip-20-12-56-458;\"{\"\"created\"\":1550606208,\"\"instruction\"\":\"\"RUN apt-get update apt-get upgrade -y\"\"}\";CVE-2018-14614;46;OS;low;linux-libc-dev,linux-tools-common;linux;4.4.0-142.168;GPL-2;5,5;fixed in 4.4.0-145.171;An issue was discovered in the Linux kernel through 4.17.10. There is an out-of-bounds access in __remove_dirty_segment() in fs/f2fs/segment.c when mounting an f2fs image.;;2018-07-27 04:29:00.000;\n";

        String separator = ";";

        String format = ";;16;17;;26;28;36;30;21;20;29;35,39;18,22,23,27,31,32,33,34,24,25,38;;";

        String result = executorImportCSVService.importReport(content, separator, format);

        Assert.assertNotEquals(result.length(), 0);
    }

    @Test
    public void importCSVWithInvalidSeparator () {
        String content = "Product;Container Name;Suite Name;Test Name;Test Path;Triage Notes;Suite Detail;Status;Executed Time;Severity;Error Description;Stack Trace;Standard Output;User;Registry;Repository;Tag;Id;Scan Time;Pass;Type;Distro;Hostname;Layer;CVE ID;Compliance ID;Type;Severity;Packages;Source Package;Package Version;Package License;CVSS;Fix Status;Description;Cause;Published;Custom Labels\n" +
                "Databricks;Security Tools;sso.sonatype.com;something/else CVE-2017-13165;OS;An elevation of privilege vulnerability in the kernel file system. Product: Android. Versions: Android kernel. Android ID A-31269937.;linux-libc-dev,linux-tools-common;true;2019-09-12 10:56:53.384;negligible;needed ;\"latest twistcli Ubuntu-xenial 46 linux 4.4.0-142.168 GPL-2 7,8 ip-20-12-56-456 {\"\"created\"\":1550606208,\"\"instruction\"\":\"\"RUN apt-get update apt-get upgrade -y\"\"} \";;;sso.sonatype.com;something/else;latest;sha256:d5c68f8e13563f747404bbafe43aLO;2019-09-12 10:56:53.384;true;twistcli;Ubuntu-xenial;ip-20-12-56-456;\"{\"\"created\"\":1550606208,\"\"instruction\"\":\"\"RUN apt-get update apt-get upgrade -y\"\"}\";CVE-2017-13165;46;OS;negligible;linux-libc-dev,linux-tools-common;linux;4.4.0-142.168;GPL-2;7,8;needed;An elevation of privilege vulnerability in the kernel file system. Product: Android. Versions: Android kernel. Android ID A-31269937.;;2017-12-06 14:29:00.000;\n" +
                ";;sso.sonatype.com;something/else CVE-2018-14616;OS;An issue was discovered in the Linux kernel through 4.17.10. There is a NULL pointer dereference in fscrypt_do_page_crypto() in fs/crypto/crypto.c when operating on a file in a corrupted f2fs image.;linux-libc-dev,linux-tools-common;true;2019-09-12 10:56:53.384;low;fixed in 4.4.0-145.171 ;\"latest twistcli Ubuntu-xenial 46 linux 4.4.0-142.168 GPL-2 5,5 ip-20-12-56-457 {\"\"created\"\":1550606208,\"\"instruction\"\":\"\"RUN apt-get update apt-get upgrade -y\"\"} \";;;sso.sonatype.com;something/else;latest;sha256:d5c68f8e13563f747404bbafe43aLO;2019-09-12 10:56:53.384;true;twistcli;Ubuntu-xenial;ip-20-12-56-457;\"{\"\"created\"\":1550606208,\"\"instruction\"\":\"\"RUN apt-get update apt-get upgrade -y\"\"}\";CVE-2018-14616;46;OS;low;linux-libc-dev,linux-tools-common;linux;4.4.0-142.168;GPL-2;5,5;fixed in 4.4.0-145.171;An issue was discovered in the Linux kernel through 4.17.10. There is a NULL pointer dereference in fscrypt_do_page_crypto() in fs/crypto/crypto.c when operating on a file in a corrupted f2fs image.;;2018-07-27 04:29:00.000;\n" +
                ";;sso.sonatype.com;something/else CVE-2018-14614;OS;An issue was discovered in the Linux kernel through 4.17.10. There is an out-of-bounds access in __remove_dirty_segment() in fs/f2fs/segment.c when mounting an f2fs image.;linux-libc-dev,linux-tools-common;true;2019-09-12 10:56:53.384;low;fixed in 4.4.0-145.171 ;\"latest twistcli Ubuntu-xenial 46 linux 4.4.0-142.168 GPL-2 5,5 ip-20-12-56-458 {\"\"created\"\":1550606208,\"\"instruction\"\":\"\"RUN apt-get update apt-get upgrade -y\"\"} \";;;sso.sonatype.com;something/else;latest;sha256:d5c68f8e13563f747404bbafe43aLO;2019-09-12 10:56:53.384;true;twistcli;Ubuntu-xenial;ip-20-12-56-458;\"{\"\"created\"\":1550606208,\"\"instruction\"\":\"\"RUN apt-get update apt-get upgrade -y\"\"}\";CVE-2018-14614;46;OS;low;linux-libc-dev,linux-tools-common;linux;4.4.0-142.168;GPL-2;5,5;fixed in 4.4.0-145.171;An issue was discovered in the Linux kernel through 4.17.10. There is an out-of-bounds access in __remove_dirty_segment() in fs/f2fs/segment.c when mounting an f2fs image.;;2018-07-27 04:29:00.000;\n";

        String separator = ",";

        String format = ";;16;17,26;28;36;30;21;20;29;35,39;18,22,23,27,31,32,33,34,24,25,38;;";

        String result = executorImportCSVService.importReport(content, separator, format);

        Assert.assertNotEquals(result.length(), 0);
    }
}
