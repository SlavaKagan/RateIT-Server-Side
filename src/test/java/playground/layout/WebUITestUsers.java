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

import playground.logic.Constants;
import playground.logic.NewUserForm;
import playground.logic.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WebUITestUsers {
	@Autowired
	private UserService service;
	private RestTemplate restTemplate;
	private String url;
	private NewUserForm form;

	@LocalServerPort
	private int port;

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/users/";
		form = new NewUserForm("rubykozel@gmail.com", "ruby", ":-)", "Guest");
	}

	@Before
	public void setup() {

	}

	@After
	public void teardown() {
		this.service.cleanup();
	}

	@Test
	public void testServerInitializesProperly() throws Exception {

	}
	
	@Test
	public void testPostingNewUserSuccessfully() throws Exception {
		/*
		  	Given the server is up 
			When I POST "/playground/users" with '{"email":"rubykozel@gmail.com", "username":"ruby", "avatar":":-)", "role":"Guest"}' 
		 */
		
		UserTO actualUser = this.restTemplate.postForObject(url, form, UserTO.class);
		
		/*
		 	Then the response is 200 ok 
			And the output is 
			{
				"email": "rubykozel@gmail.com",
				"playground": "2019A.Kagan",
				"userName": "ruby",
				"avatar": ":-)", 
				"role": "Guest", 
				"points": 0
			}
		 */
		
		assertThat(actualUser)
		.isNotNull()
		.extracting(
				"email",
				"playground",
				"userName",
				"avatar",
				"role",
				"points")
		.containsExactly(
				"rubykozel@gmail.com",
				Constants.PLAYGROUND,
				"ruby",
				":-)",
				Constants.GUEST,
				0L);
	}
	
	@Test(expected = Exception.class)
	public void testPostingNewUserUnsuccessfully() throws Exception {
		/*
		  	Given the server is up 
			When I POST "/playground/users" with nothing 
		 */
		
		this.restTemplate.postForObject(url, null, UserTO.class);
		
		// Then the response is <> 2xx
	}
	
	@Test(expected = Exception.class)
	public void testPostingNewUserWithGivenEmailAsNull() throws Exception {
		/*
		  	Given the server is up 
			When I POST "/playground/users" with '{"email": null,"username":"ruby","avatar":":-)","role":"Guest"}' 
		 */
		
		form.setEmail(null);
		this.restTemplate.postForObject(url, form, UserTO.class);
		
		// Then the response is 500
	}
	
	@Test
	public void testConfirmingANewRegisteredUserSuccessfully() throws Exception {
		/*
		 	Given the server is up 
			And theres a user with playground: "2019A.Kagan", email: "rubykozel@gmail.com", code: "1234"
		 */
		service.createUser(form);

		// When I GET "/playground/users/confirm/2019A.Kagan/rubykozel@gmail.com/1234"
		UserTO actualUser = this.restTemplate.getForObject(url + "confirm/{playground}/{email}/{code}", UserTO.class,
				Constants.PLAYGROUND, "rubykozel@gmail.com", 1234);
		
		/*
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
		 */
		assertThat(actualUser)
			.isNotNull()
			.extracting(
					"email",
					"playground",
					"userName",
					"avatar",
					"role",
					"points")
			.containsExactly(
					"rubykozel@gmail.com",
					Constants.PLAYGROUND,
					"ruby",
					":-)",
					Constants.REVIEWER,
					0L);
	}

	@Test(expected = Exception.class)
	public void testConfirmingANewRegisteredUserUnsuccessfullyWithDifferentCode() throws Exception {
		/*
		 	Given the server is up 
			And theres an unconfirmed user with playground: "2019A.Kagan", email: "rubykozel@gmail.com", code: "1234"
		 */
		service.createUser(form);
		
		//	When I GET "/playground/users/confirm/2019A.Kagan/rubykozel@gmail.com/1235"
		this.restTemplate.getForObject(url + "confirm/{playground}/{email}/{code}", UserTO.class, Constants.PLAYGROUND,
				"rubykozel@gmail.com", 1235);
		
		// Then the response is 500
	}

	@Test(expected = Exception.class)
	public void testConfirmingAnExistingUser() throws Exception {
		
		/*
		 	Given the server is up 
			And theres a user with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Reviewer"
		 */
		service.createUser(form);
		service.confirmUser(Constants.PLAYGROUND, form.getEmail(), "1234");
		
		// When I GET "/playground/users/confirm/2019A.Kagan/rubykozel@gmail.com/Any_Code"
		this.restTemplate.getForObject(url + "confirm/{playground}/{email}/{code}", UserTO.class, Constants.PLAYGROUND,
				"rubykozel@gmail.com", 1111);
		
		// Then the response is 500
	}

	@Test
	public void testGettingAUserFromTheServerSuccessfully() throws Exception {
		
		/*
		 	Given the server is up 
			And theres a registered user with playground: "2019A.Kagan", email: "rubykozel@gmail.com"
		 */
		service.createUser(form);
		service.confirmUser(Constants.PLAYGROUND, form.getEmail(), "1234");
		
		// When I GET "/playground/users/login/2019A.Kagan/rubykozel@gmail.com"
		UserTO actualUser = this.restTemplate.getForObject(url + "login/{playground}/{email}", UserTO.class,
				Constants.PLAYGROUND, form.getEmail());
		
		/*
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
		 */
		assertThat(actualUser)
			.isNotNull()
			.extracting(
					"email",
					"playground",
					"userName",
					"avatar",
					"role",
					"points")
			.containsExactly(
					"rubykozel@gmail.com",
					Constants.PLAYGROUND,
					"ruby",
					":-)",
					Constants.REVIEWER,
					0L); //0L for long value
	}
	
	@Test(expected = Exception.class)
	public void testGettingAnUnconfirmedUser() throws Exception {
		/*
		 	Given the server is up 
			And there's an unconfirmed user with playground: "2019A.Kagan", email: "rubykozel@gmail.com" 
		 */
		service.createUser(form);
		
		// When I GET "/playground/users/login/2019A.Kagan/rubykozel@gmail.com" 
		this.restTemplate.getForObject(url + "login/{playground}/{email}", UserTO.class,
				Constants.PLAYGROUND, form.getEmail());
		
		// Then the response is 500
	}
	
	@Test(expected = Exception.class)
	public void testGettingAnUnregisteredUser() throws Exception {
		/*
		 	Given the server is up 
			And there are no accounts 
		 */
		
		/* No account was inserted */
		
		
		// When I GET "/playground/users/login/2019A.Kagan/rubykozel@gmail.com"
		this.restTemplate.getForObject(url + "login/{playground}/{email}", UserTO.class,
				Constants.PLAYGROUND, form.getEmail());
		
		// Then the response is 500
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