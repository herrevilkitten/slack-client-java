package org.evilkitten.slack.response.rtm;

import lombok.Data;
import org.evilkitten.slack.entity.Plan;

@Data
public class TeamPlanChangedEvent extends RtmEvent {
  private Plan plan;
}
