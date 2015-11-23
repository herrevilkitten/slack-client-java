package org.evilkitten.slack.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.jodah.typetools.TypeResolver;
import org.evilkitten.slack.handler.*;
import org.evilkitten.slack.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ClientEndpoint
public class DefaultRtmWebSocketClient implements RtmWebSocketClient {
  private final static Logger LOGGER = LoggerFactory.getLogger(DefaultRtmWebSocketClient.class);

  private final ObjectMapper objectMapper;
  private final List<RtmOpenHandler> openHandlers = new ArrayList<>();
  private final List<RtmCloseHandler> closeHandlers = new ArrayList<>();
  private final List<RtmMessageHandler> messageHandlers = new ArrayList<>();
  private final List<RtmErrorHandler> errorHandlers = new ArrayList<>();

  public DefaultRtmWebSocketClient(ObjectMapper objectMapper, List<RtmHandler> handlers) {
    this.objectMapper = objectMapper;
    for (RtmHandler handler : handlers) {
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
      Message messageObject = objectMapper.readValue(message, Message.class);
      for (RtmMessageHandler handler : messageHandlers) {
        Class<?> genericType = TypeResolver.resolveRawArguments(RtmMessageHandler.class, handler.getClass())[0];
        boolean isInstanceOf = genericType.isInstance(messageObject);

        if (isInstanceOf) {
          handler.onMessage(messageObject);
        }
      }
    } catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  @OnError
  public void onError(Session session, Throwable t) {
    LOGGER.error(t.getMessage(), t);
  }
}
