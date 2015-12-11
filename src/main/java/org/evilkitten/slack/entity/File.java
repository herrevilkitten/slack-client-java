package org.evilkitten.slack.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Data
public class File {
  private String id;
  private long created;
  private long timestamp;

  private String name;
  private String title;

  @JsonProperty("mimetype")
  private String mimeType;

  @JsonProperty("filetype")
  private String fileType;

  @JsonProperty("pretty_type")
  private String prettytype;

  @JsonProperty("user")
  private String userId;

  private Mode mode;
  private boolean editable;

  @JsonProperty("is_external")
  private boolean external;

  @JsonProperty("external_type")
  private String externalType;

  private long size;

  @JsonProperty("url_private")
  private URI urlPrivate;

  @JsonProperty("url_private_download")
  private URI urlPrivateDownload;

  @JsonProperty("thumb_64")
  private URI thumb64;

  @JsonProperty("thumb_80")
  private URI thumb80;

  @JsonProperty("thumb_360")
  private URI thumb360;

  @JsonProperty("thumb_360_gif")
  private URI thumb360Gif;

  @JsonProperty("thumb_360_w")
  private int thumb360Width;

  @JsonProperty("thumb_360_h")
  private int thumb360Height;

  private URI permanentLink;

  private URI editLink;

  private String preview;

  private String previewHighlight;

  private int lines;

  private int linesMore;

  private boolean published;
  private boolean publicUrlShared;

  private List<String> channelIds = new ArrayList<>();

  private List<String> groupIds = new ArrayList<>();

  private List<String> messageIds = new ArrayList<>();

  private String initialComment;

  private int numberOfStars;

  private boolean starred;

  private List<String> pinnedChannelIds = new ArrayList<>();

  private List<Reaction> reactions = new ArrayList<>();

  public enum Mode {
    HOSTED,
    EXTERNAL,
    SNIPPET,
    POST;

    public static Mode fromString(String name) {
      for (Mode mode : values()) {
        if (mode.toString().equalsIgnoreCase(name)) {
          return mode;
        }
      }

      throw new IllegalArgumentException(name + " is not a file mode");
    }
  }
}
