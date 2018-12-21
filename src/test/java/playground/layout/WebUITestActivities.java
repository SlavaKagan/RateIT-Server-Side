package playground.layout;

import javax.annotation.PostConstruct;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import playground.logic.ActivityService;
import playground.logic.ElementService;
import playground.logic.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WebUITestActivities {
	
	@Autowired
	private ActivityService service;
	
	@Autowired
	private ElementService elementService;
	
	@Autowired
	private UserService userService;
	
	@Value("${playground:Anonymous}")
	private String playground;
	
	@Value("${email:email}")
	private String email;
	
	private RestTemplate restTemplate;
	private String url;
	private ObjectMapper jacksonMapper;
	private String activityJson;
	
	@LocalServerPort
	private int port;
	
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/activities";
		jacksonMapper = new ObjectMapper();
		
		activityJson = "{"
							+ "\"elementPlayground\":\"2019A.Kagan\","
							+ "\"elementId\":0,"
							+ "\"type\":\"PostReview\","
							+ "\"attributes\": {\"review\":\"This is a review\"}"
						+ "}";
	}

	
	@Before
	public void setup () throws Exception {
		UserTO user = new UserTO(new NewUserForm(email, "ruby", ":-)", "Manager"));
		user.setPlayground(playground);
		userService.createUser(user.toEntity());
		ElementTO element = jacksonMapper.readValue(
			"{"
				+ "\"type\":\"Movie Page\","
				+ "\"location\": {\"x\":0,\"y\":0},"
				+ "\"name\":\"Saw 3 movie\","
				+ "\"attributes\": {}"
			+ "}", ElementTO.class);
		element.setPlayground(playground);
		elementService.createElement(playground, email, element.toEntity());
	}
	
	@After
	public void teardown() {
		this.service.cleanup();
		this.elementService.cleanup();
		this.userService.cleanup();
	}
	
	@Test
	public void testServerInitializesProperly() throws Exception {
		
	}
	
	/**
	 * 	Given the server is up
	 * 	And there's a confirmed user in the database
	  	And there's an element with id: 0
		When I POST "/playground/activities/2019A.Kagan/rubykozel@gmail.com" with 
		{
			"elementPlayground":"2019A.Kagan",
			"elementId":0,
			"type":"PostReview",
			"attributes": {
				"review":"This is a review"
			}
		}
		Then the response is 200
		And the output is 
		{
    		"playground": "2019A.Kagan",
    		"playerPlayground": "2019A.Kagan",
    		"playerEmail": "admin@gmail.com",
    		"id": "0",
    		"elementPlayground": "2019A.Kagan",
    		"elementId": "0",
    		"type": "PostReview",
    		"attributes": {
        		"review": "This is a review"
    		}
		}
	 * @throws Exception
	 */
	
	@Test
	public void testPostANewActivitySuccessfuly() throws Exception {
		
		// When
		ActivityTO activityTO = this.restTemplate.postForObject(
				url + "/{userPlayground}/{email}",
				jacksonMapper.readValue(activityJson, ActivityTO.class),
				ActivityTO.class, playground, email);
		
		
		assertThat(jacksonMapper.writeValueAsString(activityTO))
		.isNotNull()
		.isEqualTo(
				"{"
				+ "\"playground\":\"2019A.Kagan\","
				+ "\"playerPlayground\":\"2019A.Kagan\","
				+ "\"playerEmail\":\""+ email +"\","
				+ "\"id\":\""+ activityTO.getId() +"\","
				+ "\"elementPlayground\":\"2019A.Kagan\","
				+ "\"elementId\":\"0\","
				+ "\"type\":\"PostReview\","
				+ "\"attributes\":{\"review\":\"This is a review\"}"
				+ "}");

	}
	
	/**
	 * 	Given the server is up
	 * 	And there's a valid user in the database
	 	And there's an element with id: 0
		When I POST "/playground/activities/2019A.Kagan/null" with
		{
			"elementPlayground":"2019A.Kagan",
			"elementId":0,
			"type":"PostReview",
			"attributes": {
				"review":"This is a review"
			}
		}
		Then the response is 500
	 * @throws Exception
	 */
	
	@Test(expected=Exception.class)
	public void testPostANewActivityWithNullEmailUnsuccessfuly() throws Exception {
		// When
		this.restTemplate.postForObject(
				url + "/{userPlayground}/{email}",
				jacksonMapper.readValue(activityJson, ActivityTO.class),
				ActivityTO.class, playground, null);
		
	}
	
	/**
	 * 	Given the server is up
	 * 	And there's a valid user in the database
	 	And there's an element with id: 0
		When I POST "/playground/activities/2019A.Kagan/rubykozel@gmail.com" with '{}'
		Then the response is 500
	 * @throws Exception
	 */
	
	@Test(expected=Exception.class)
	public void testPostANewActivityWithAnEmptyJSONUnsuccessfuly() throws Exception {
		
		// When
		this.restTemplate.postForObject(
				url + "/{userPlayground}/{email}",
				jacksonMapper.readValue("{}", ActivityTO.class),
				ActivityTO.class, playground, email);
	}
	
	/**
	 * 	Given the server is up
	 * 	And there's a valid user in the database
	 	And there's an element with id: 0
		When I POST "/playground/activities/2019A.Kagan/rubykozel@gmail.com" with
		{
			"elementPlayground":"2019A.Kagan",
			"elementId":0,
			"type": unsupported type,
			"attributes": {
				"review":"This is a review"
			}
		}
		Then the response is 500
	 * @throws Exception
	 */
	
	@Test(expected=Exception.class)
	public void testPostANewActivityWithAnUnSupportedTypeUnsuccessfuly() throws Exception {
		
		// When
		this.restTemplate.postForObject(
				url + "/{userPlayground}/{email}",
				jacksonMapper.readValue("{"
						+ "\"elementPlayground\":\"2019A.Kagan\","
						+ "\"elementId\":0,"
						+ "\"type\":\"Unsupported\","
						+ "\"attributes\": {\"review\":\"This is a review\"}"
					+ "}", ActivityTO.class),
				ActivityTO.class, playground, email);
	}
}