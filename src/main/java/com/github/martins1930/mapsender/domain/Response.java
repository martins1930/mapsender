package com.github.martins1930.mapsender.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Response {

  public static final Response STATUS_OK = new Response("ok");

  private final String status;

  @JsonCreator
  private Response(@JsonProperty("status") String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Response response = (Response) o;

    return status != null ? status.equals(response.status) : response.status == null;

  }

  @Override
  public int hashCode() {
    return status != null ? status.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Response{" +
           "status='" + status + '\'' +
           '}';
  }
}
