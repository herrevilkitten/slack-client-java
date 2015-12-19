package org.evilkitten.slack.response.rtm.mesage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.evilkitten.slack.entity.ItemType;
import org.evilkitten.slack.response.rtm.MessageEvent;

@Data
public class  UnpinnedItemEvent extends MessageEvent {
  @JsonProperty("item_type")
  private ItemType itemType;

  private Object item;

}
