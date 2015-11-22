package org.evilkitten.slack;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.evilkitten.slack.client.DefaultRtmWebSocketClient;
import org.evilkitten.slack.client.RtmWebSocketClient;
import org.evilkitten.slack.handler.RtmHandler;
import org.evilkitten.slack.message.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

public class SlackBot implements Closeable {
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

  private final List<RtmHandler> handlers = new ArrayList<>();
  private final String token;
  private final boolean autoReconnect;
  private final boolean autoMark;
  private final ObjectMapper objectMapper;
  private final long pingInterval;
  private final String agentName;

  private Session webSocketSession;
  private RtmWebSocketClient rtmWebSocketClient;

  private PingTimerTask pingTimerTask;
  private Timer pingTimer;

  private SlackBot(String token, boolean autoReconnect, boolean autoMark, ObjectMapper objectMapper, long pingInterval, String agentName, List<RtmHandler> handlers) {
    this.pingInterval = pingInterval;
    this.agentName = agentName;
    this.token = Objects.requireNonNull(token, "API token must be defined");
    this.autoReconnect = autoReconnect;
    this.autoMark = autoMark;
    this.objectMapper = Objects.requireNonNull(objectMapper, "A Jackson ObjectMapper must be provided")
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    this.handlers.addAll(handlers);
    this.rtmWebSocketClient = new DefaultRtmWebSocketClient(objectMapper, handlers);
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

  public void send(Object object) {
    try {
      send(objectMapper.writeValueAsString(object));
    } catch (JsonProcessingException e) {
      throw new SlackException(e);
    }
  }

  public void send(String text) {
    try {
      this.webSocketSession.getBasicRemote().sendText(text);
    } catch (IOException e) {
      throw new SlackException(e);
    }
  }

  public void connect() {
    Response response = api("rtm.start", new HashMap<>());
    WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
    try {
      webSocketSession = webSocketContainer.connectToServer(rtmWebSocketClient, response.getUrl());
      if (pingInterval > 0L) {
        pingTimerTask = new PingTimerTask();
        pingTimer = new Timer(true);
        pingTimer.scheduleAtFixedRate(pingTimerTask, 0, pingInterval);
      }
    } catch (DeploymentException | IOException e) {
      e.printStackTrace();
    }
  }

  public void disconnect() {
    try {
      webSocketSession.close();
      if (pingTimer != null) {
        pingTimer.cancel();
        pingTimer = null;
      }
    } catch (IOException e) {
      throw new SlackException(e);
    }
  }

  public boolean isConnected() {
    return webSocketSession.isOpen();
  }

  @Override
  public void close() throws IOException {
    disconnect();
  }

  public static class Builder {
    private String token = "";
    private boolean autoReconnect = false;
    private boolean autoMark = false;
    private ObjectMapper objectMapper = new ObjectMapper();
    private List<RtmHandler> handlers = new ArrayList<>();

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

    public Builder addHandler(RtmHandler handler) {
      this.handlers.add(handler);
      return this;
    }

    public SlackBot build() {
      if (StringUtils.isEmpty(token)) {
        throw new IllegalArgumentException("API token must be set");
      }
      return new SlackBot(token, autoReconnect, autoMark, objectMapper, pingInterval, agentName, handlers);
    }

    private String agentName = DEFAULT_AGENT_NAME;


    private long pingInterval = DEFAULT_PING_INTERVAL;


  }

  private class PingTimerTask extends TimerTask {
    @Override
    public void run() {
      try {
        webSocketSession.getBasicRemote().sendText(DEFAULT_PING_MESSAGE);
      } catch (IOException e) {
        LOGGER.warn("Unable to send ping", e);
        pingTimer.cancel();
      }
    }
  }
}
