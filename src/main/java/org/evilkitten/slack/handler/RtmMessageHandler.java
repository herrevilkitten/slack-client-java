package org.evilkitten.slack.handler;

import org.evilkitten.slack.message.Message;

public interface RtmMessageHandler extends RtmHandler {
  void onMessage(Message message);
}
