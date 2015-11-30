package org.evilkitten.slack.handler;

import org.evilkitten.slack.response.rtm.RtmEvent;

public interface RtmMessageHandler<T extends RtmEvent> extends RtmHandler {
  void onMessage(T message);
}
