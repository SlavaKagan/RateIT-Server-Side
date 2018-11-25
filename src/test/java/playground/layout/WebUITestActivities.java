package playground.layout;

import javax.annotation.PostConstruct;

import static org.assertj.core.api.Assertions.assertThat;
import javax.annotation.PostConstruct;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import playground.logic.Constants;

import playground.logic.ActivityService;
import playground.logic.Constants;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WebUITestActivities {
	
	@Autowired
	private ActivityService service;
	
	private RestTemplate restTemplate;
	private String url;
	private NewUserForm form;
	public static final String EMAIL = "rubykozel@gmail.com";
	private ObjectMapper jacksonMapper;
	
	@LocalServerPort
	private int port;

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/activities";
		jacksonMapper = new ObjectMapper();
	}
	
	@Before
	public void setup () {
		
	}
	
	@After
	public void teardown() {
		this.service.cleanup();
	}
	
	@Test
	public void testServerInitializesProperly() throws Exception {
		
	}
	
	@Test
	public void testPostANewActivitySuccessfuly() throws Exception {
		/**
		 * 	Given the server is up 
			When I POST "/playground/activities/2019A.Kagan/rubykozel@gmail.com" with '{"elementId": "1025028355","elementPlayground": "2019A.Kagan","type": "x"}'
			Then the response is 200
			And the output is '{"email":"rubykozel@gmail.com","playground":"2019A.Kagan","userName":"ruby","avatar":":-)","role":"Guest","points":0}'
		 */
		
		ElementTO element = new ElementTO();
		element.setId("1025028355");
		element.setPlayground("2019A.Kagan");
		ActivityTO activity = new ActivityTO(element, "x");
		
		Object returnedObject = this.restTemplate.postForObject(this.url + "/{userPlayground}/{email}", activity, ActivityTO.class, Constants.PLAYGROUND, EMAIL);
		String userJson = "{\"email\":\"rubykozel@gmail.com\",\"playground\":\"2019A.Kagan\",\"userName\":\"ruby\",\"avatar\":\":-)\",\"role\":\"Guest\",\"points\":0}";
		UserTO newUser = this.jacksonMapper.readValue(userJson, UserTO.class);
		
		assertThat(jacksonMapper.writeValueAsString((UserTO)returnedObject)).isEqualTo(jacksonMapper.writeValueAsString(newUser));

	}
	
	@Test(expected=Exception.class)
	public void testPostANewActivityWithNullEmailEnsuccessfuly() throws Exception {
		/**
		 * 	Given the server is up
			When I POST "/playground/activities/2019A.Kagan/null" with '{"elementId": "1025028355","elementPlayground": "2019A.Kagan","type": "x"}'
			Then the response is 500 with message: "One of the paramters provided was null"
		 */
		
		ElementTO element = new ElementTO();
		element.setId("1025028355");
		element.setPlayground("2019A.Kagan");
		ActivityTO activity = new ActivityTO(element, "x");
		Object returnedObject = this.restTemplate.postForObject(this.url + "/{userPlayground}/{email}", activity, ActivityTO.class, Constants.PLAYGROUND, "null");
		
	}
	
	@Test(expected=Exception.class)
	public void testPostANewActivityWithAnEmptyJSONUnsuccessfuly() throws Exception {
		/**
		 * 	Given the server is up
			When I POST "/playground/activities/2019A.Kagan/rubykozel@gmail.com" with '{}'
			Then the response is 500 
		 */
		
		ElementTO element = new ElementTO();
		element.setId("1025028355");
		element.setPlayground("2019A.Kagan");
		Object emptyJson = jacksonMapper.readValue("{}", Object.class);
		Object returnedObject = this.restTemplate.postForObject(this.url + "/{userPlayground}/{email}", emptyJson, ActivityTO.class, Constants.PLAYGROUND, EMAIL);
	}
}