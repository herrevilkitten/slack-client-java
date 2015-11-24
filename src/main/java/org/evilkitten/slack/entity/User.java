package org.evilkitten.slack.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class User {
  private String id;
  private String name;
  private boolean deleted;
  private String color;

  @JsonProperty("is_admin")
  private boolean admin;

  @JsonProperty("is_owner")
  private boolean owner;

  @JsonProperty("is_primary_owner")
  private boolean primaryOwner;

  @JsonProperty("is_restricted")
  private boolean restricted;

  @JsonProperty("is_ultra_restricted")
  private boolean ultraRestricted;

  @JsonProperty("has_2fa")
  private boolean multiFactor;

  @JsonProperty("has_files")
  private boolean files;

  @JsonProperty("two_factor_type")
  private String twoFactorType;

  @Data
  public class Profile {
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("real_name")
    private String realName;
    private String email;
    private String skype;
    private String phone;

    @JsonProperty("image_24")
    private String image24;

    @JsonProperty("image_32")
    private String image32;

    @JsonProperty("image_48")
    private String image48;

    @JsonProperty("image_72")
    private String image72;

    @JsonProperty("image_192")
    private String image192;
  }
}
