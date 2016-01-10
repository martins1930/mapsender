package com.github.martins1930.mapsender.domain;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class ResponseTest {

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Test
  public void shouldSerializeToJson() throws Exception {
    String expectedJson = "{\"status\":\"ok\"}";
    assertEquals(expectedJson, OBJECT_MAPPER.writeValueAsString(Response.STATUS_OK));
  }

  @Test
  public void shouldDeSerializeFromJson() throws Exception {
    String responseAsJson = "{\"status\":\"ok\"}";
    assertEquals(Response.STATUS_OK, OBJECT_MAPPER.readValue(responseAsJson, Response.class));
  }
}