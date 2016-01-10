package com.github.martins1930.mapsender.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class Request {

  private final String amqUrl;
  private final String queueName;
  private final Map<String, Object> params;

  @JsonCreator
  public Request(@JsonProperty("amqUrl") String amqUrl,
                 @JsonProperty("queueName") String queueName,
                 @JsonProperty("params") Map<String, Object> params) {
    this.amqUrl = amqUrl;
    this.queueName = queueName;
    this.params = params;
  }

  public String getAmqUrl() {
    return amqUrl;
  }

  public String getQueueName() {
    return queueName;
  }

  public Map<String, Object> getParams() {
    return params;
  }

  @Override
  public String toString() {
    return "Request{" +
           "amqUrl='" + amqUrl + '\'' +
           ", queueName='" + queueName + '\'' +
           ", params=" + params +
           '}';
  }
}
