package org.evilkitten.slack;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlackBot {
    private final static String API_PROTOCOL = "https";
    private final static String API_HOST = "api.slack.com";
    private final static String API_PATH = "/api/";
    private final static String API_QUERY = API_PROTOCOL + "://" + API_HOST + API_PATH;

    private final static String TOKEN_KEY = "token";

    private final static String CONTENT_TYPE_HEADER = "Content-Type";
    private final static String CONTENT_TYPE = "application/x-www-form-urlencoded";

    private final static Logger LOGGER = LoggerFactory.getLogger(SlackBot.class);

    private final String token;
    private final boolean autoReconnect;
    private final boolean autoMark;
    private final Jackson

    private SlackBot(String token, boolean autoReconnect, boolean autoMark) {
        this.token = token;
        this.autoReconnect = autoReconnect;
        this.autoMark = autoMark;
    }

    private HttpEntity stringifyParameters(Map<String, String> parameters) throws UnsupportedEncodingException {
        List<NameValuePair> entries = new ArrayList<>();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            entries.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return new UrlEncodedFormEntity(entries);
    }

    private void api(String method, Map<String, String> parameters) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put(TOKEN_KEY, token);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(API_QUERY + method);
            httpPost.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE);
            httpPost.setEntity(stringifyParameters(parameters));

            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            StatusLine statusLine = httpResponse.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

            }
            HttpEntity entity = httpResponse.getEntity();
            EntityUtils.consume(entity);
        } catch (IOException e) {
            LOGGER.error(e.toString(), e);
            throw new SlackException(e);
        }
    }

    private class Builder {
        private String token = "";
        private boolean autoReconnect = false;
        private boolean autoMark = false;

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public Builder setAutoReconnect(boolean autoReconnect) {
            this.autoReconnect = autoReconnect;
            return this;
        }

        public Builder setAutoMark(boolean autoMark) {
            this.autoMark = autoMark;
            return this;
        }

        public SlackBot build() {
            if (StringUtils.isEmpty(token)) {
                throw new IllegalArgumentException("API token must be set");
            }
            return new SlackBot(token, autoReconnect, autoMark);
        }
    }
}
