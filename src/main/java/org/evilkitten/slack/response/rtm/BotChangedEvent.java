package org.evilkitten.slack.response.rtm;

import lombok.Data;
import org.evilkitten.slack.entity.Bot;

@Data
public class BotChangedEvent extends RtmEvent {
  private Bot bot;
}
