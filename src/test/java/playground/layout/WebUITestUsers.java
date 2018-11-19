package playground.layout;

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
import playground.logic.UserEntity;
import playground.logic.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WebUITestUsers {
	@Autowired
	private UserService service;
	private RestTemplate restTemplate;
	private String url;
	private NewUserForm form;
	
	private ObjectMapper jacksonMapper;

	@LocalServerPort
	private int port;

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/users/";
		form = new NewUserForm("rubykozel@gmail.com", "ruby", ":-)", "Guest");
		jacksonMapper = new ObjectMapper();
	}

	@Before
	public void setup() {

	}

	@After
	public void teardown() {
		this.service.cleanup();
	}
	
	/**
	 * Given nothing
	 * When nothing
	 * Then the server is loading properly
	 * @throws Exception
	 */
	@Test
	public void testServerInitializesProperly() throws Exception {

	}
	
	/**
	 * 	Given the server is up 
		When I POST "/playground/users" with 
		{
			"email":"rubykozel@gmail.com",
			"username":"ruby", 
			"avatar":":-)", 
			"role":"Guest"
		}
	 	Then the response is 200 OK
	 	And the database contains the user:
	 	{
			"email": "rubykozel@gmail.com",
			"playground": "2019A.Kagan",
			"userName": "ruby",
			"avatar": ":-)", 
			"role": "Guest", 
			"points": 0
		}
	 * @throws Exception
	 */
	@Test
	public void testPostingNewUserSuccessfully() throws Exception {
		// Given 
		
		/*nothing*/
		
		// When
		this.restTemplate.postForObject(url, form, UserTO.class);
		
		UserEntity actualUserInDb = this.service.getUser(form.getEmail());
		
		// Then
		assertThat(jacksonMapper.writeValueAsString(actualUserInDb))
		.isNotNull()
		.isEqualTo(
				"{"
				+ "\"email\":\"rubykozel@gmail.com\","
				+ "\"playground\":\"2019A.Kagan\","
				+ "\"userName\":\"ruby\","
				+ "\"avatar\":\":-)\","
				+ "\"role\":\"Guest\","
				+ "\"points\":0"
				+ "}");
	}
	
	
	/**
	 * 	Given the server is up 
		When I POST "/playground/users" with nothing
	 	Then the response is <> 2xx
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testPostingNewUserUnsuccessfully() throws Exception {	
		//	When
		this.restTemplate.postForObject(url, null, UserTO.class); 
	}
	
	
	/**
	 * 	Given the server is up 
		When I POST "/playground/users" with '{"email": null,"username":"ruby","avatar":":-)","role":"Guest"}' 
	 	Then the response is 500
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testPostingNewUserWithGivenEmailAsNull() throws Exception {
		// When
		form.setEmail(null);
		this.restTemplate.postForObject(url, form, UserTO.class);
	}
	
	
	/**
	 * 	Given the server is up 
		And theres a user with playground: "2019A.Kagan", email: "rubykozel@gmail.com", code: "1234"
		When I GET "/playground/users/confirm/2019A.Kagan/rubykozel@gmail.com/1234"
		Then the response is 200 
		And the database contains the user 
		{
			"email": "rubykozel@gmail.com",
			"playground": "2019A.Kagan", 
			"userName": "ruby", 
			"avatar": ":-)", 
			"role": "Reviewer", 
			"points": 0
		}
	 * @throws Exception
	 */
	@Test
	public void testConfirmingANewRegisteredUserSuccessfully() throws Exception {
		// Given
		service.createUser(form);
		
		// When
		this.restTemplate.getForObject(url + "confirm/{playground}/{email}/{code}", UserTO.class,
				Constants.PLAYGROUND, "rubykozel@gmail.com", 1234);
		
		
		
		// Then
		UserEntity actualUserInDb = this.service.getUser(form.getEmail());
		
		assertThat(jacksonMapper.writeValueAsString(actualUserInDb))
		.isNotNull()
		.isEqualTo(
				"{"
				+ "\"email\":\"rubykozel@gmail.com\","
				+ "\"playground\":\"2019A.Kagan\","
				+ "\"userName\":\"ruby\","
				+ "\"avatar\":\":-)\","
				+ "\"role\":\"Reviewer\","
				+ "\"points\":0"
				+ "}");
	}
	
	/**
	 * 	Given the server is up 
		And theres an unconfirmed user with playground: "2019A.Kagan", email: "rubykozel@gmail.com", code: "1234"
		When I GET "/playground/users/confirm/2019A.Kagan/rubykozel@gmail.com/1235"
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testConfirmingANewRegisteredUserUnsuccessfullyWithDifferentCode() throws Exception {
		// Given
		service.createUser(form);
		
		// When
		this.restTemplate.getForObject(url + "confirm/{playground}/{email}/{code}", UserTO.class, Constants.PLAYGROUND,
				"rubykozel@gmail.com", 1235);
	}
	
	/**
	 * 	Given the server is up 
		And there's a user with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Reviewer"
	 	When I GET "/playground/users/confirm/2019A.Kagan/rubykozel@gmail.com/Any_Code"
	 	Then the response is 500
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testConfirmingAnExistingUser() throws Exception {
		// Given
		service.createUser(form);
		service.confirmUser(Constants.PLAYGROUND, form.getEmail(), "1234");
		
		// When
		this.restTemplate.getForObject(url + "confirm/{playground}/{email}/{code}", UserTO.class, Constants.PLAYGROUND,
				"rubykozel@gmail.com", 1111); 
	}
	
	/**
	 * 	Given the server is up 
		And theres a registered user with playground: "2019A.Kagan", email: "rubykozel@gmail.com"
	 	When I GET "/playground/users/login/2019A.Kagan/rubykozel@gmail.com"
	 	Then the response is 200 
		And the output is 
		{
			"email": "rubykozel@gmail.com",
			"playground": "2019A.Kagan",
			"userName": "ruby",
			"avatar": ":-)",
			"role": "Reviewer",
			"points": 0
		}
	 * @throws Exception
	 */
	@Test
	public void testGettingARegisteredUserFromTheServerSuccessfully() throws Exception {
		// Given
		service.createUser(form);
		service.confirmUser(Constants.PLAYGROUND, form.getEmail(), "1234");
		
		// When
		UserTO actualUser = this.restTemplate.getForObject(url + "login/{playground}/{email}", UserTO.class,
				Constants.PLAYGROUND, form.getEmail());

		// Then
		assertThat(jacksonMapper.writeValueAsString(actualUser))
			.isNotNull()
			.isEqualTo(
					"{"
					+ "\"email\":\"rubykozel@gmail.com\","
					+ "\"playground\":\"2019A.Kagan\","
					+ "\"userName\":\"ruby\","
					+ "\"avatar\":\":-)\","
					+ "\"role\":\"Reviewer\","
					+ "\"points\":0"
					+ "}");
	}
	
	/**
	 * 	Given the server is up 
		And there's an unconfirmed user with playground: "2019A.Kagan", email: "rubykozel@gmail.com" 
	 	When I GET "/playground/users/login/2019A.Kagan/rubykozel@gmail.com" 
	 	Then the response is 500
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testGettingAnUnconfirmedUser() throws Exception {
		// Given
		service.createUser(form);
		
		// When 
		this.restTemplate.getForObject(url + "login/{playground}/{email}", UserTO.class,
				Constants.PLAYGROUND, form.getEmail());
	}
	
	/**
	 * 	Given the server is up 
		And there are no accounts 
		When I GET "/playground/users/login/2019A.Kagan/rubykozel@gmail.com"
		Then the response is 500
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testGettingAnUnregisteredUser() throws Exception {
		// Given
		
		/* nothing */
		
		// When
		this.restTemplate.getForObject(url + "login/{playground}/{email}", UserTO.class,
				Constants.PLAYGROUND, form.getEmail());
	}
	
	@Test
	public void testChangeTheUserEmailSuccesfully() throws Exception {
	/*
	 	Given the server is up 
		And there is a confirmed user with playground: "2019A.Kagan", email: "rubykozel@gmail.com", 
	 */
		service.createUser(form);
		service.confirmUser(Constants.PLAYGROUND, form.getEmail(), "1234");

		/* When I PUT "/playground/users/2019A.Kagan/rubykozel@gmail.com" with
		 	{
		 		"email":"ruby@gmail.com",
		 		"playground": "2019A.Kagan",
		 		"userName": "ruby",
		 		"avatar": ":-)",
		 		"role": "Reviewer",
		 		"points": 0
		 	}		
		 */
		UserTO newUser= new UserTO(form);
		newUser.setEmail("ruby@gmail.com");
		
		this.restTemplate.put(url + "{playground}/{email}", newUser, Constants.PLAYGROUND,"rubykozel@gmail.com");
	 	//Then the response is 200 
	}
	
	@Test(expected = Exception.class)
	public void testChangeEmailOfUnregisteredUser() throws Exception {
	/*
	 	Given the server is up
	 	And there is an unregistered user with playground: "2019A.Kagan", email: "rubykozel@gmail.com", 
	 */
		
		/*When I PUT "/playground/users/2019A.Kagan/rubykozel@gmail.com" with 
		 	{
		 		"email":"ruby@gmail.com",
		 		"playground": "2019A.Kagan",
		 		"userName": "ruby",
		 		"avatar": ":-)",
		 		"role": "Guest",
		 		"points": 0
		 	}
		
		*/
		UserTO newUser= new UserTO(form);
		newUser.setEmail("ruby@gmail.com");
		
		this.restTemplate.put(url + "{playground}/{email}", newUser, Constants.PLAYGROUND,"rubykozel@gmail.com");
		
		//Then the response is 404 with message: "This is an unregistered account"
	}
	
	@Test(expected = Exception.class)
	public void testChangeTheUserEmailToNull() throws Exception {
	/*
		Given the server is up
		And theres a user with playground: "2019A.Kagan", email: "rubykozel@gmail.com",
	*/
		service.createUser(form);
		service.confirmUser(Constants.PLAYGROUND, form.getEmail(), "1234");
		
		/* When I PUT "/playground/users/2019A.Kagan/rubykozel@gmail.com" with 
		 	{
		 		"email":null,
		 		"playground": "2019A.Kagan",
		 		"username": "ruby",
		 		"avatar": ":-)",
		 		"role": "Reviewer",
		 		"points": 0
		 	}		
		
		*/
		UserTO newUser= new UserTO(form);
		newUser.setEmail(null);
		
		this.restTemplate.put(url + "{playground}/{email}", newUser, Constants.PLAYGROUND,"rubykozel@gmail.com");
		//Then the response is 500 with null exception
	}
}