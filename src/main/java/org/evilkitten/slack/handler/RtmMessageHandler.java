package org.evilkitten.slack.handler;

import org.evilkitten.slack.message.Message;

public interface RtmMessageHandler<T extends Message> extends RtmHandler {
  void onMessage(T message);
}
