package org.evilkitten.slack;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.evilkitten.slack.entity.Message;
import org.evilkitten.slack.entity.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

public class SlackBot {
  private final static String API_PROTOCOL = "https";
  private final static String API_HOST = "api.slack.com";
  private final static String API_PATH = "/api/";
  private final static String API_QUERY = API_PROTOCOL + "://" + API_HOST + API_PATH;

  private final static String AGENT_KEY = "agent";
  private final static String TOKEN_KEY = "token";

  private final static String DEFAULT_AGENT_NAME = "java-slack";

  private final static String DEFAULT_PING_MESSAGE = "{\"type\": \"ping\"}";
  private final static long DEFAULT_PING_INTERVAL = 5000L;

  private final static String CONTENT_TYPE_HEADER = "Content-Type";
  private final static String CONTENT_TYPE = "application/x-www-form-urlencoded";

  private final static Logger LOGGER = LoggerFactory.getLogger(SlackBot.class);

  private final String token;
  private final boolean autoReconnect;
  private final boolean autoMark;
  private final ObjectMapper objectMapper;
  private final long pingInterval;
  private final String agentName;

  private Session webSocketSession;

  private PingTimerTask pingTimerTask;
  private Timer pingTimer;

  private SlackBot(String token, boolean autoReconnect, boolean autoMark, ObjectMapper objectMapper, long pingInterval, String agentName) {
    this.pingInterval = pingInterval;
    this.agentName = agentName;
    this.token = Objects.requireNonNull(token, "API token must be defined");
    this.autoReconnect = autoReconnect;
    this.autoMark = autoMark;
    this.objectMapper = Objects.requireNonNull(objectMapper, "A Jackson ObjectMapper must be provided")
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  private HttpEntity stringifyParameters(Map<String, String> parameters) throws UnsupportedEncodingException {
    List<NameValuePair> entries = parameters.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    return new UrlEncodedFormEntity(entries);
  }

  private Response api(String method, Map<String, String> parameters) {
    if (parameters == null) {
      parameters = new HashMap<>();
    }
    parameters.put(TOKEN_KEY, token);
    if (!StringUtils.isEmpty(agentName)) {
      parameters.put(AGENT_KEY, agentName);
    }

    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpPost httpPost = new HttpPost(API_QUERY + method);
      httpPost.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE);
      httpPost.setEntity(stringifyParameters(parameters));

      CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
      StatusLine statusLine = httpResponse.getStatusLine();
      HttpEntity entity = httpResponse.getEntity();
      String response = EntityUtils.toString(entity);
      LOGGER.debug("Received status code {}", statusLine);
      LOGGER.debug("Received response: {}", response);
      Response success = objectMapper.readValue(response, Response.class);
      LOGGER.debug("json {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(success));
      if (!success.isOk()) {
        throw new SlackException(success.getError());
      }

      return success;
    } catch (IOException e) {
      LOGGER.error(e.toString(), e);
      throw new SlackException(e);
    }
  }

  public void start() throws NoSuchAlgorithmException, KeyManagementException {
    Response response = api("rtm.start", new HashMap<>());
    WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
    try {
      webSocketSession = webSocketContainer.connectToServer(new RtmClient(), response.getUrl());
      if (pingInterval > 0L) {
        pingTimerTask = new PingTimerTask();
        pingTimer = new Timer(true);
        pingTimer.scheduleAtFixedRate(pingTimerTask, 0, pingInterval);
      }
    } catch (DeploymentException | IOException e) {
      e.printStackTrace();
    }
  }

  public static class Builder {
    private String token = "";
    private boolean autoReconnect = false;
    private boolean autoMark = false;
    private ObjectMapper objectMapper = new ObjectMapper();

    public Builder(String token) {
      this.token = Objects.requireNonNull(token, "API token must be set");
    }

    public Builder setAutoReconnect(boolean autoReconnect) {
      this.autoReconnect = autoReconnect;
      return this;
    }

    public Builder setAutoMark(boolean autoMark) {
      this.autoMark = autoMark;
      return this;
    }

    private long pingInterval = DEFAULT_PING_INTERVAL;

    public Builder setObjectMapper(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
      return this;
    }

    public Builder setPingInterval(long pingInterval) {
      this.pingInterval = pingInterval;
      return this;
    }

    public Builder setAgentName(String agentName) {
      this.agentName = agentName;
      return this;
    }

    private String agentName = DEFAULT_AGENT_NAME;

    public SlackBot build() {
      if (StringUtils.isEmpty(token)) {
        throw new IllegalArgumentException("API token must be set");
      }
      return new SlackBot(token, autoReconnect, autoMark, objectMapper, pingInterval, agentName);
    }


  }

  private class PingTimerTask extends TimerTask {
    long pingCount = 0;

    @Override
    public void run() {
      try {
        LOGGER.debug("Sending ping {}", ++pingCount);
        webSocketSession.getBasicRemote().sendText(DEFAULT_PING_MESSAGE);
      } catch (IOException e) {
        LOGGER.warn("Unable to send ping", e);
        pingTimer.cancel();
      }
    }
  }

  @ClientEndpoint
  public class RtmClient {

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
      LOGGER.trace("RtmClient session opened");
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
      LOGGER.trace("RtmClient session closed because {}", reason.getReasonPhrase());
    }

    @OnMessage
    public void onMessage(String message) {
      LOGGER.info("Message: {}", message);

      try {
        Message messageObject = objectMapper.readValue(message, Message.class);
      } catch (IOException e) {
        LOGGER.error(e.getMessage(), e);
      }
    }

    @OnError
    public void onError(Session session, Throwable t) {
      LOGGER.error(t.getMessage(), t);
    }
  }
}
