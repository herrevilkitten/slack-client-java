package org.evilkitten.slack.response.rtm;

import lombok.Data;
import org.evilkitten.slack.entity.Bot;

@Data
public class BotAddedEvent extends RtmEvent {
  private Bot bot;
}
