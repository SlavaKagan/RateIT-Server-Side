package playground.layout;

import static org.assertj.core.api.Assertions.assertThat;
import javax.annotation.PostConstruct;
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
	private UserTO user;
	
	@Value("${playground:Anonymous}")
	private String playground;
	
	@Value("${email:Anonymous}")
	private String email;
	
	private ObjectMapper jacksonMapper;

	@LocalServerPort
	private int port;

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/users/";
		form = new NewUserForm("rubykozel@gmail.com", "ruby", ":-)", "Guest");
		user = new UserTO(form);
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
		
		UserEntity actualUserInDb = this.service.getUser(playground + "@@" + email);
		
		// Then
		assertThat(jacksonMapper.writeValueAsString(actualUserInDb))
		.isNotNull()
		.isEqualTo(
				"{"
				+ "\"uniqueKey\":\"" + playground + "@@" + email + "\","
				+ "\"userName\":\"ruby\","
				+ "\"avatar\":\":-)\","
				+ "\"role\":\"Guest\","
				+ "\"points\":0,"
				+ "\"number\":\"" + actualUserInDb.getNumber() + "\""
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
		service.createUser(user.toEntity());
		
		// When
		this.restTemplate.getForObject(url + "confirm/{playground}/{email}/{code}", UserTO.class,
				playground, email, 1234);
		
		
		
		// Then
		String id = playground + "@@" + email;
		UserEntity actualUserInDb = this.service.getUser(id);
		
		assertThat(jacksonMapper.writeValueAsString(actualUserInDb))
		.isNotNull()
		.isEqualTo(
				"{"
				+ "\"uniqueKey\":\"" + playground + "@@" + email + "\","
				+ "\"userName\":\"ruby\","
				+ "\"avatar\":\":-)\","
				+ "\"role\":\"Reviewer\","
				+ "\"points\":0,"
				+ "\"number\":\"" + actualUserInDb.getNumber() + "\""
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
		service.createUser(user.toEntity());
		
		// When
		this.restTemplate.getForObject(url + "confirm/{playground}/{email}/{code}", UserTO.class, playground,
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
		UserEntity userToPost = user.toEntity();
		service.createUser(userToPost);
		service.confirmUser(playground, userToPost.getUniqueKey(), "1234");
		
		// When
		this.restTemplate.getForObject(url + "confirm/{playground}/{email}/{code}", UserTO.class, playground,
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
		service.createUser(user.toEntity());
		service.confirmUser(playground, email, "1234");
				
		// When
		
		UserTO actualUser = this.restTemplate.getForObject(url + "login/{playground}/{email}", UserTO.class,
				playground, email);
		
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
		service.createUser(user.toEntity());
		
		// When 
		this.restTemplate.getForObject(url + "login/{playground}/{email}", UserTO.class,
				playground, form.getEmail());
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
		@SuppressWarnings("unused")
		UserTO user = this.restTemplate.getForObject(url + "login/{playground}/{email}", UserTO.class,
				playground, form.getEmail());
		//Then the response is 500
	}
	
	
	/**
	 * Given the server is up 
	
		And there's an element with playground: "2019A.Kagan", email: "rubykozel@gmail.com", playground: "2019A.Kagan", id: "1025028332",
		
		When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/2019A.Kagan/1025028332"
		
		Then the response is 200 
		
		And the output is 
		{
			 "playground": "2019A.Kagan",
			 "id": "1586158061", 
			 "name": "Messaging Board", 
			 "expirationDate": null, 
			 "type": "Messaging Board", 
			 "attributes": {}, 
			 "creatorPlayground": "2019A.Kagan", 
			 "creatorEmail": "rubykozel@gmail.com" 
		}
	 * @throws Exception
	 */
	public void testGettingAnElementSuccessfully() throws Exception {
		service.createUser(user.toEntity());
		service.confirmUser(playground, form.getEmail(), "1234");
		
	this.restTemplate.getForObject(url + "{playground}/{email}/{userPlayground}/{id}", ElementTO.class, playground, "rubykozel@gmail.com", playground, "1025028332");
	}
	
	/**
	 * 	Given the server is up 
		And there is a confirmed user with playground: "2019A.Kagan", email: "rubykozel@gmail.com",
		When I PUT "/playground/users/2019A.Kagan/rubykozel@gmail.com" with
		 	{
		 		"email":"rubykozel@gmail.com",
		 		"playground": "2019A.Kagan",
		 		"userName": "rubson",
		 		"avatar": ":-)",
		 		"role": "Reviewer",
		 		"points": 0
		 	}
		Then the response is 200
	 * @throws Exception
	 */
	@Test
	public void testChangeTheUserNameSuccesfully() throws Exception {
		
		// Given
		service.createUser(user.toEntity());
		
		// When
		UserTO newUser = new UserTO(service.confirmUser(playground, form.getEmail(), "1234"));
		
		newUser.setUserName("rubson");
		this.restTemplate.put(url + "{playground}/{email}", newUser, playground,"rubykozel@gmail.com");
		
		//Then 
		assertThat(jacksonMapper.writeValueAsString(newUser))
		.isNotNull()
		.isEqualTo(
				"{"
				+ "\"email\":\"rubykozel@gmail.com\","
				+ "\"playground\":\"2019A.Kagan\","
				+ "\"userName\":\"rubson\","
				+ "\"avatar\":\":-)\","
				+ "\"role\":\"Reviewer\","
				+ "\"points\":0"
				+ "}");
	}
	
	/**
	 * 	Given the server is up
	 	And there is an unregistered user with playground: "2019A.Kagan", email: "rubykozel@gmail.com",
	 	When I PUT "/playground/users/2019A.Kagan/rubykozel@gmail.com" with 
		 	{
		 		"email":"ruby@gmail.com",
		 		"playground": "2019A.Kagan",
		 		"userName": "omer",
		 		"avatar": ":-)",
		 		"role": "Guest",
		 		"points": 0
		 	}
		 Then the response is 404
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testChangeUserNameOfUnregisteredUser() throws Exception {
		// Given
		UserTO newUser= new UserTO(form);
		newUser.setUserName("omer");
		
		// When
		this.restTemplate.put(url + "{playground}/{email}", newUser, playground,"rubykozel@gmail.com");
		
		//Then
	}
	
	/**
	 * 	Given the server is up
		And there's a user with playground: "2019A.Kagan", email: "rubykozel@gmail.com",
		When I PUT "/playground/users/2019A.Kagan/rubykozel@gmail.com" with 
		 	{
		 		"email":rubykozel@gmail.com,
		 		"playground": "2019A.Kagan",
		 		"username": null,
		 		"avatar": ":-)",
		 		"role": "Reviewer",
		 		"points": 0
		 	}
		Then the response is 500 	
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testChangeTheUserAvatarToNull() throws Exception {
		
		// Given
		service.createUser(user.toEntity());
		
		// When
		UserTO newUser= new UserTO(service.confirmUser(playground, form.getEmail(), "1234"));
		
		newUser.setAvatar(null);
		this.restTemplate.put(url + "{playground}/{email}", newUser, playground,"rubykozel@gmail.com");
		
		// Then
	}
}