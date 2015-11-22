package org.evilkitten.slack.client;

import javax.websocket.*;

@ClientEndpoint
public interface RtmWebSocketClient {

  @OnOpen
  public void onOpen(Session session, EndpointConfig endpointConfig);

  @OnClose
  public void onClose(Session session, CloseReason reason);

  @OnMessage
  public void onMessage(String message);

  @OnError
  public void onError(Session session, Throwable t);
}
