package org.evilkitten.slack.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.jodah.typetools.TypeResolver;
import org.evilkitten.slack.SlackBot;
import org.evilkitten.slack.handler.*;
import org.evilkitten.slack.response.PostProcessing;
import org.evilkitten.slack.response.rtm.RtmEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.util.ArrayList;
import java.util.List;

@ClientEndpoint
public class DefaultRtmWebSocketClient implements RtmWebSocketClient {
  private final static Logger LOGGER = LoggerFactory.getLogger(DefaultRtmWebSocketClient.class);

  private final SlackBot slackBot;
  private final ObjectMapper objectMapper;

  private final List<RtmOpenHandler> openHandlers = new ArrayList<>();
  private final List<RtmCloseHandler> closeHandlers = new ArrayList<>();
  private final List<RtmMessageHandler<?>> messageHandlers = new ArrayList<>();
  private final List<RtmErrorHandler> errorHandlers = new ArrayList<>();

  public DefaultRtmWebSocketClient(ObjectMapper objectMapper, SlackBot slackBot) {
    this.objectMapper = objectMapper;
    this.slackBot = slackBot;
  }
/*
  public void addHandler(RtmMessageHandler<?> handler) {
    messageHandlers.add(handler);

  }
*/
    public void addHandler(RtmHandler handler) {
    if (handler instanceof RtmOpenHandler) {
      openHandlers.add((RtmOpenHandler) handler);
    }

    if (handler instanceof RtmCloseHandler) {
      closeHandlers.add((RtmCloseHandler) handler);
    }

    if (handler instanceof RtmMessageHandler) {
      messageHandlers.add((RtmMessageHandler<?>) handler);
    }

    if (handler instanceof RtmErrorHandler) {
      errorHandlers.add((RtmErrorHandler) handler);
    }
  }

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
      RtmEvent messageObject = objectMapper.readValue(message, RtmEvent.class);
      messageObject.setRawResponse(message);
      LOGGER.info("Object:  {}", messageObject);
      if (messageObject instanceof PostProcessing) {
        ((PostProcessing) messageObject).postProcess(slackBot);
      }

      for (RtmMessageHandler handler : messageHandlers) {
        Class<?> genericType = TypeResolver.resolveRawArguments(RtmMessageHandler.class, handler.getClass())[0];
        boolean isInstanceOf = genericType.isInstance(messageObject);

        LOGGER.info("Handler: {} {}", genericType.getTypeName(), isInstanceOf);
        if (isInstanceOf) {
          handler.onMessage(messageObject);
        }
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  @OnError
  public void onError(Session session, Throwable t) {
    LOGGER.error(t.getMessage(), t);
  }
}
