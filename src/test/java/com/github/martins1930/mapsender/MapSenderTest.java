package com.github.martins1930.mapsender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.martins1930.mapsender.domain.Request;
import com.github.martins1930.mapsender.domain.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MapSenderApplication.class)
@WebIntegrationTest(randomPort = true)
public class MapSenderTest {

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  // TODO create the broker with a random port
  public static final String BROKER_URL = "tcp://localhost:61616";

  @Value("${local.server.port}")
  private int port;

  private String mapSenderEndpoint;
  private RestTemplate restClient;
  private BrokerService broker;
  private HttpHeaders requestHeaders;
  private String queueName;

  @Before
  public void setUp() throws Exception {
    mapSenderEndpoint = "http://localhost:" + port + "/send";
    restClient = new TestRestTemplate();

    createActiveMqBroker();

    requestHeaders = new HttpHeaders();
    requestHeaders.setContentType(MediaType.APPLICATION_JSON);

    queueName = UUID.randomUUID().toString();
  }

  private void createActiveMqBroker() throws Exception {
    broker = new BrokerService();
    broker.setPersistent(false);
    broker.addConnector(BROKER_URL);
    broker.start();
  }

  @After
  public void tearDown() throws Exception {
    broker.stop();
  }

  @Test
  public void shouldTestRestController() throws Exception {

    Map<String, Object> requestParams = new HashMap<>();
    requestParams.put("int", 34);
    requestParams.put("string", "string 1 test");
    requestParams.put("long", 45L);
    requestParams.put("boolean", true);
    Request request = new Request(BROKER_URL, queueName, requestParams);

    HttpEntity<String> httpEntity = new HttpEntity<>(
        OBJECT_MAPPER.writeValueAsString(request), requestHeaders
    );

    Response apiResponse = restClient.postForObject(mapSenderEndpoint, httpEntity, Response.class);
    assertEquals(Response.STATUS_OK, apiResponse);

    Map<String, Object> consumerMessage = consumeMessage();
    assertEquals(requestParams, consumerMessage);
  }

  private Map<String, Object> consumeMessage() throws JMSException {
    // Create a ConnectionFactory
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);

    // Create a Connection
    Connection connection = connectionFactory.createConnection();
    connection.start();

    // Create a Session
    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

    // Create the destination (Topic or Queue)
    Destination destination = session.createQueue(queueName);

    // Create a MessageConsumer from the Session to the Topic or Queue
    MessageConsumer consumer = session.createConsumer(destination);

    // Wait for a message
    Message message = consumer.receive(1000);

    Map<String, Object> map = new HashMap<>();
    if (message instanceof MapMessage) {
      MapMessage mapMessage = (MapMessage) message;
      map.put("int", mapMessage.getInt("int"));
      map.put("string", mapMessage.getString("string"));
      map.put("boolean", mapMessage.getBoolean("boolean"));
      map.put("long", mapMessage.getLong("long"));
    } else {
      fail("The message should have the type MapMessage");
    }

    consumer.close();
    session.close();
    connection.close();
    return map;
  }
}
