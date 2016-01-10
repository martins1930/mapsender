package com.github.martins1930.mapsender.web;

import com.github.martins1930.mapsender.domain.Request;
import com.github.martins1930.mapsender.domain.Response;
import java.util.Map;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MapSenderController {

  @RequestMapping(value = "/send", method = RequestMethod.POST,
      consumes = "application/json", produces = "application/json")
  @ResponseBody
  public Response sendMessage(@RequestBody Request request) throws JMSException {

    ActiveMQConnectionFactory
        connectionFactory =
        new ActiveMQConnectionFactory(request.getAmqUrl());

    // Create a Connection
    Connection connection = connectionFactory.createConnection();
    connection.start();

    // Create a Session
    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

    // Create the destination (Topic or Queue)
    Destination destination = session.createQueue(request.getQueueName());

    // Create a MessageProducer from the Session to the Topic or Queue
    MessageProducer producer = session.createProducer(destination);
    producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

    // Create a messages
    MapMessage mapMessage = session.createMapMessage();
    for (Map.Entry<String, Object> paramsEntry : request.getParams().entrySet()) {
      String key = paramsEntry.getKey();
      Object value = paramsEntry.getValue();
      if (value instanceof Integer) {
        mapMessage.setInt(key, (Integer) value);
      } else if (value instanceof String) {
        mapMessage.setString(key, (String) value);
      } else if (value instanceof Long) {
        mapMessage.setLong(key, (Long) value);
      } else if (value instanceof Boolean) {
        mapMessage.setBoolean(key, (Boolean) value);
      }
    }

    producer.send(mapMessage);

    // Clean up
    session.close();
    connection.close();

    return Response.STATUS_OK;
  }

  @RequestMapping("/mapSender")
  public String displayMainPage() {
    return "mapSender";
  }


}
