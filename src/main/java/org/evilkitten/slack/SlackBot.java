package org.evilkitten.slack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.Singular;
import net.jodah.typetools.TypeResolver;
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
import org.evilkitten.slack.entity.*;
import org.evilkitten.slack.handler.RtmHandler;
import org.evilkitten.slack.handler.RtmMessageHandler;
import org.evilkitten.slack.response.PostProcessing;
import org.evilkitten.slack.response.Response;
import org.evilkitten.slack.response.api.RtmStartResponse;
import org.evilkitten.slack.response.rtm.RtmEvent;
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

@Data
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
  private final ObjectMapper objectMapper;
  private final long pingInterval;
  private final String agentName;

  @Singular
  private final Map<String, User> users = new HashMap<>();

  @Singular
  private final Map<String, Channel> channels = new HashMap<>();
  // private final Map<String, Group> groups = new HashMap<>();
  // private final Map<String, Im> ims = new HashMap<>();
  // Subteams

  @Singular
  private final Map<String, Bot> bots = new HashMap<>();

  @Setter(AccessLevel.NONE)
  private Session webSocketSession;

  @Setter(AccessLevel.NONE)
  private RtmWebSocketClient rtmWebSocketClient;

  @Setter(AccessLevel.NONE)
  private RtmStartResponse rtmRtmStartResponse;

  @Setter(AccessLevel.NONE)
  private PingTimerTask pingTimerTask;

  @Setter(AccessLevel.NONE)
  private Timer pingTimer;

  @Setter(AccessLevel.NONE)
  private long currentEventId = 0;

  private SlackBot(String token, ObjectMapper objectMapper, long pingInterval, String agentName) {
    this.pingInterval = pingInterval;
    this.agentName = agentName;
    this.token = Objects.requireNonNull(token, "API token must be defined");
    this.objectMapper = Objects.requireNonNull(objectMapper, "A Jackson ObjectMapper must be provided")
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    this.rtmWebSocketClient = new DefaultRtmWebSocketClient(objectMapper, this);
  }

  private HttpEntity stringifyParameters(Map<String, String> parameters) throws UnsupportedEncodingException {
    List<NameValuePair> entries = parameters.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    return new UrlEncodedFormEntity(entries);
  }

  private ApiResponse api(String method, Map<String, String> parameters, Class<? extends Response> responseClass) {
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
      String responseText = EntityUtils.toString(entity);

      LOGGER.debug("Received status code {}", statusLine);
      LOGGER.debug("Received response: {}", responseText);

      Response responseObject = objectMapper.readValue(responseText, responseClass);
      if (responseObject instanceof PostProcessing) {
        ((PostProcessing) responseObject).postProcess(this);
      }

      return new ApiResponse(statusLine.getStatusCode(), statusLine.getReasonPhrase(), responseText, responseObject);
    } catch (IOException e) {
      LOGGER.error(e.toString(), e);
      throw new SlackException(e);
    }
  }

  synchronized public void send(RtmEvent event) {
    try {
      event.setId(currentEventId++);
      send(objectMapper.writeValueAsString(event));
    } catch (JsonProcessingException e) {
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

  synchronized public void send(String text) {
    try {
      this.webSocketSession.getBasicRemote().sendText(text);
    } catch (IOException e) {
      throw new SlackException(e);
    }
  }

  public void connect() {
    try {
      ApiResponse apiResponse = api("rtm.start", new HashMap<>(), RtmStartResponse.class);
      rtmRtmStartResponse = (RtmStartResponse) apiResponse.getResponseObject();
      LOGGER.debug("json {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rtmRtmStartResponse));
      if (!rtmRtmStartResponse.isOk()) {
        throw new SlackException(rtmRtmStartResponse.getError());
      }

      WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
      webSocketSession = webSocketContainer.connectToServer(rtmWebSocketClient, rtmRtmStartResponse.getUrl());

      if (pingInterval > 0L) {
        pingTimerTask = new PingTimerTask();
        pingTimer = new Timer(true);
        pingTimer.scheduleAtFixedRate(pingTimerTask, 0, pingInterval);
      }
    } catch (DeploymentException | IOException e) {
      throw new SlackException(e);
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
    return webSocketSession != null && webSocketSession.isOpen();
  }

  public Self getSelf() {
    if (rtmRtmStartResponse != null) {
      return rtmRtmStartResponse.getSelf();
    }
    return null;
  }

  public Team getTeam() {
    if (rtmRtmStartResponse != null) {
      return rtmRtmStartResponse.getTeam();
    }
    return null;
  }

  public Channel getChannel(String channelId) {
    if (!channels.containsKey(channelId)) {
      channels.put(channelId, new Channel());
    }
    return channels.get(channelId);
  }

  public User getUser(String userId) {
    if (!users.containsKey(userId)) {
      users.put(userId, new User());
    }
    return users.get(userId);
  }

  @Override
  public void close() throws IOException {
    disconnect();
  }
/*
  public void addHandler(RtmMessageHandler<?> handler) {
    this.handlers.add(handler);
    if (rtmWebSocketClient instanceof DefaultRtmWebSocketClient) {
      ((DefaultRtmWebSocketClient) rtmWebSocketClient).addHandler(handler);
    }
    return this;
  }
*/
  public SlackBot addHandler(RtmHandler handler) {
    this.handlers.add(handler);
    if (rtmWebSocketClient instanceof DefaultRtmWebSocketClient) {
      ((DefaultRtmWebSocketClient) rtmWebSocketClient).addHandler(handler);
    }
    return this;
  }

  public static class Builder {
    private String token = "";
    private ObjectMapper objectMapper = new ObjectMapper();

    public Builder(String token) {
      this.token = Objects.requireNonNull(token, "API token must be set");
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

    public SlackBot build() {
      if (StringUtils.isEmpty(token)) {
        throw new IllegalArgumentException("API token must be set");
      }
      return new SlackBot(token, objectMapper, pingInterval, agentName);
    }

    private String agentName = DEFAULT_AGENT_NAME;


    private long pingInterval = DEFAULT_PING_INTERVAL;
  }

  @Data
  public class ApiResponse {
    private int statusCode;
    private String statusLine;
    private String responseText;
    private Response responseObject;

    public ApiResponse(int statusCode, String statusLine, String responseText, Response responseObject) {
      this.statusCode = statusCode;
      this.statusLine = statusLine;
      this.responseText = responseText;
      this.responseObject = responseObject;
    }
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
