package org.evilkitten.slack.handler;

import lombok.Data;
import org.evilkitten.slack.response.rtm.RtmEvent;

@Data
public class RtmMessage<T extends RtmEvent> {
  private final String raw;
  private final T message;
  private final long timestamp;

  public RtmMessage(String raw, T message) {
    this.raw = raw;
    this.message = message;
    this.timestamp = System.currentTimeMillis();
  }
}
