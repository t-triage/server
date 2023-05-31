package com.clarolab.integration.test.container;

import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class ContainerVersionTest {

    private List<String> files = Lists.newArrayList("version/appVersion.json", "version/appVersion2.json", "version/appVersion3.json", "version/appVersion4.json", "version/appVersion5.json");

    @Test
    public void checkVersion() throws IOException {
        String content = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(files.get(0)), Charset.defaultCharset());
        JSONObject jsonObj = new JSONObject(content);
        MatcherAssert.assertThat(jsonObj.has("version"), Matchers.is(true));
        MatcherAssert.assertThat(jsonObj.isNull("version"), Matchers.is(false));
        MatcherAssert.assertThat(StringUtils.isEmpty(jsonObj.getString("version")), Matchers.is(false));
        MatcherAssert.assertThat(StringUtils.truncateStringAtLong(StringUtils.cleanup(jsonObj.getString("version")), 255), Matchers.equalTo("19.10-release_r1911212221_35ed5ff77335144788d0563ae38758325fcfba95"));
    }

    @Test
    public void checkVersionInvalidField() throws IOException {
        String content = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(files.get(1)), Charset.defaultCharset());
        JSONObject jsonObj = new JSONObject(content);
        MatcherAssert.assertThat(jsonObj.has("version"), Matchers.is(false));
    }

    @Test
    public void checkVersionTruncated() throws IOException {
        String content = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(files.get(2)), Charset.defaultCharset());
        JSONObject jsonObj = new JSONObject(content);
        MatcherAssert.assertThat(jsonObj.has("version"), Matchers.is(true));
        MatcherAssert.assertThat(jsonObj.isNull("version"), Matchers.is(false));
        MatcherAssert.assertThat(StringUtils.isEmpty(jsonObj.getString("version")), Matchers.is(false));
        MatcherAssert.assertThat(StringUtils.truncateStringAtLong(StringUtils.cleanup(jsonObj.getString("version")), 255).length(), Matchers.equalTo(255));
    }

    @Test
    public void checkVersionEmptyValue() throws IOException {
        String content = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(files.get(3)), Charset.defaultCharset());
        JSONObject jsonObj = new JSONObject(content);
        MatcherAssert.assertThat(jsonObj.has("version"), Matchers.is(true));
        MatcherAssert.assertThat(jsonObj.isNull("version"), Matchers.is(false));
        MatcherAssert.assertThat(StringUtils.isEmpty(jsonObj.getString("version")), Matchers.is(true));
    }

    @Test
    public void checkVersionNullValue() throws IOException {
        String content = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(files.get(4)), Charset.defaultCharset());
        JSONObject jsonObj = new JSONObject(content);
        MatcherAssert.assertThat(jsonObj.has("version"), Matchers.is(true));
        MatcherAssert.assertThat(jsonObj.isNull("version"), Matchers.is(true));
    }
}
