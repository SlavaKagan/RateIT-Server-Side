package playground.layout;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.HashMap;
import java.util.Map;
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
import playground.logic.ElementEntity;
import playground.logic.ElementService;
import playground.logic.Location;
import playground.logic.UserService;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class WebUITestElements {
	@Autowired
	private ElementService elementservice;
	
	@Autowired
	private UserService userservice;
	private RestTemplate restTemplate;
	private String url;
	private NewUserForm form;
	private UserTO user;
	public static final String EMAIL = "rubykozel@gmail.com";
	
	@LocalServerPort
	private int port;
	
	private ObjectMapper jacksonMapper;
	
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/elements";
		form = new NewUserForm(EMAIL, "ruby", ":-)", "Manager");
		user = new UserTO(form);
		jacksonMapper = new ObjectMapper();
	}
	
	@Before
	public void setup () {
		
	}

	@After
	public void teardown() {
		this.userservice.cleanup();
		this.elementservice.cleanup();
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
		And there's an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Manager"
		
		When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with 
		{
			"type":"Messaging Board",
			"name":"Messaging Board"
		}
	 	
	 	Then the response is 200
	 	And the database contains the element:
	 	{
	 		"playground": "2019A.Kagan",
	 		"name": "Messaging Board",
	 		"type": "Messaging Board",
	 		"creatorPlayground": "2019A.Kagan",
	 		"creatorEmail": "rubykozel@gmail.com"
		}
	 * @throws Exception
	 */
	@Test
	public void testCreatingAnElementSuccessfully() throws Exception {
		//Given
		userservice.createUser(user.toEntity());
		
		//When
		ElementTO elementToPost = jacksonMapper.readValue("{\"type\":\"Messaging Board\", \"name\":\"Messaging Board\"}", ElementTO.class);
		this.restTemplate
				.postForObject(url + "/{userPlayground}/{email}", elementToPost,ElementTO.class, Constants.PLAYGROUND, EMAIL);
		
		//Then
		ElementEntity actualElementInDb = elementservice.getAllElements(Constants.PLAYGROUND, EMAIL).get(0);
		actualElementInDb.setLocation(new Location(0,0)); // For testing purposes
		assertThat(jacksonMapper.writeValueAsString(actualElementInDb))
		.isNotNull()
		.isEqualTo(jacksonMapper.writeValueAsString(
				jacksonMapper.readValue(""
						+ "{"
						+ "\"playground\": \"2019A.Kagan\","
						+ "\"name\": \"Messaging Board\","
						+ "\"expirationDate\": null,"
						+ "\"type\": \"Messaging Board\","
						+ "\"location\": {\"x\":0,\"y\":0},"
						+ "\"creatorPlayground\":\"2019A.Kagan\","
						+ "\"creatorEmail\":\"rubykozel@gmail.com\""
						+ "}", ElementEntity.class)));
	}
	
	/**
	 * 	Given the server is up
		And there's an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Reviewer",
		When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with 
		{
		 	"type":"Messaging Board", 
		 	"name":"Messaging Board"
		}
		Then the response is 500
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testCreatingAnElementWithAUserThatIsNotAManager() throws Exception {
		// Given
		user.setRole(Constants.REVIEWER);
		userservice.createUser(user.toEntity());

		// When
		ElementTO elementToPost = jacksonMapper.readValue("{\"type\":\"Messaging Board\", \"name\":\"Messaging Board\"}", ElementTO.class);
		this.restTemplate.postForObject(url + "/{userPlayground}/{email}", 
										elementToPost,		
										ElementTO.class, 
										Constants.PLAYGROUND,
										EMAIL);
			
	}
	
	/**
	 *  Given the server is up
		And there's an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Manager"
		When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with nothing
	 	Then the response is <> 2xx
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testCreatingAnElementWithoutDeliveringAnyValidJSON() throws Exception {
		// Given
		userservice.createUser(user.toEntity());
		
		// When
		this.restTemplate.postForObject(url + "/{userPlayground}/{email}", 
										null, 
										ElementTO.class, 
										Constants.PLAYGROUND,
										EMAIL);	
	}
	
	/**
	 * 	Given the server is up
		And there's an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Manager"
	 	When I POST "/playground/elements/2019A.Kagan/null" with 
	  	{
	  		"type":"Messaging Board", 
	  		"name":"Messaging Board"
	  	}
	  	Then the response is 500
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testCreatingAnElementWithEmailAsNull() throws Exception {
		// Given
		userservice.createUser(user.toEntity());
		
		// When
		ElementTO elementToPost = jacksonMapper.readValue("{\"type\":\"Messaging Board\", \"name\":\"Messaging Board\"}", ElementTO.class);
		this.restTemplate.postForObject(url + "/{userPlayground}/{email}", 
										elementToPost, 
										ElementTO.class,
										Constants.PLAYGROUND,
										null); 
	}
	
	/**
	 * 	Given the server is up
		And there's no manager in the database
	 	When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with '{}'
	 	Then the response is 500
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testCreatingAnElementWithEmptyJSON() throws Exception {
		// Given
		userservice.createUser(user.toEntity());
		
		// When
		this.restTemplate.postForObject(url + "/{userPlayground}/{email}",
				jacksonMapper.readValue("{}", ElementTO.class),
				ElementTO.class, Constants.PLAYGROUND, "rubykozel@gmail.com");
		 
	}
	
	@Test
	public void testChangeTheNameOfTheElement() throws Exception{
	/*
	 	Given the server is up
		And theres an element with playground: "2019A.Kagan", email: "rubykozel@gmail.com", playground: "2019A.Kagan", id: "2061451755",
	*/
		userservice.createUser(user.toEntity());
		ElementTO newElement= jacksonMapper.readValue("{\"type\":\"Messaging Board\", \"name\":\"Messaging Board\"}", ElementTO.class);
		elementservice.createElement(newElement.toEntity(), Constants.PLAYGROUND, EMAIL);
		
		/* When I PUT "/playground/elements/2019A.Kagan/rubykozel@gmail.com/2019A.Kagan/2061451755" with 
		 	{
		 		"playground": "2019A.Kagan",
		 		"id": "2061451755",
		 		"location": {
		 			"x": Any valid x,
		 			"y": Any valid y,
		 		},
		 		"name": "MyBoard",
		 		"creationDate": Any valid date,
		 		"expirationDate": null or any valid date,
		 		"type": "Messaging Board",
		 		"attributes": {
		 			"creatorsName": "Manager",
		 			"isActive": "True",
		 			"isAMovie": "False",
		 			"movieName": "Venom 2018"
		 		},
		 		"creatorPlayground": "2019A.Kagan",
		 		"creatorEmail": "rubykozel@gmail.com"
		 	}
		*/
		
		newElement.setName("MyBoard");
		this.restTemplate.put(url + "/{userPlayground}/{email}/{playground}/{id}", newElement, Constants.PLAYGROUND,EMAIL,Constants.PLAYGROUND,newElement.getId());
		//Then the reponse is "200 OK"
		
		
		assertThat(jacksonMapper.writeValueAsString(newElement))
		.isNotNull()
		.isEqualTo(jacksonMapper.writeValueAsString(
				jacksonMapper.readValue(""
						+ "{"
						+ "\"playground\": \"2019A.Kagan\","
						+ "\"name\": \"MyBoard\","
						+ "\"expirationDate\": null,"
						+ "\"type\": \"Messaging Board\","
						+ "\"location\": {\"x\":0,\"y\":0},"
						+ "\"creatorPlayground\":\"2019A.Kagan\","
						+ "\"creatorEmail\":\"rubykozel@gmail.com\""
						+ "}", ElementEntity.class)));
		
	}
	
	@Test (expected = Exception.class)
	public void testTryingToChangeTypeNameWithNull() throws Exception{
	/*
		Given the server is up
		And there is an element with playground: "2019A.Kagan", email: "rubykozel@gmail.com", playground: "2019A.Kagan", id: "567",
	*/
		userservice.createUser(user.toEntity());
		ElementTO newElement= new ElementTO("Messaging Board","Messaging Board",Constants.PLAYGROUND,EMAIL, new HashMap<>());
		elementservice.createElement(newElement.toEntity(), Constants.PLAYGROUND, form.getEmail());
		
		/* When I PUT "/playground/elements/2019A.Kagan/rubykozel@gmail.com/2019A.Kagan/567" with
		  	{
		 		"playground": "2019A.Kagan",
		 		"id": null,
		 		"location": {
		 			"x": Any valid x,
		 			"y": Any valid y,
		 		},
		 		"name": "Messaging Board",
		 		"creationDate": Any valid date,
		 		"expirationDate": null or any valid date,
		 		"type": "Messaging Board",
		 		"attributes": {
		 			"creatorsName": "Manager",
		 			"isActive": "True",
		 			"isAMovie": "False",
		 			"movieName": "Venom 2018"
		 		},
		 		"creatorPlayground": "2019A.Kagan",
		 		"creatorEmail": "rubykozel@gmail.com"
		 	}
		*/
		
		newElement.setType(null);
		
		this.restTemplate.put(url + "/{userPlayground}/{email}/{playground}/{id}", newElement, Constants.PLAYGROUND,EMAIL,Constants.PLAYGROUND,newElement.getId());
		
		//Then the reponse is 500 with message
	}
	
	/*@Test
	public void getElementsByAttributesValueSuccessfully() throws Exception {
		// Given the server is up - do nothing
		// And there are elements with playground: "2019A.Kagan", email: "rubykozel@gmail.com", attribute: "isAMovie", value:"False"
		userservice.createUser(user.toEntity());
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("isAMovie", "False");
		ElementTO newElement = new ElementTO();
		newElement.setAttributes(attributes);
		elementservice.createElement(newElement.toEntity(), Constants.PLAYGROUND, EMAIL);

//		When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/search/isAMovie/False"
		String userPlayground = "2019A.Kagan";
		String attributeName = "isAMovie";
		String value = "False";
		ActivityTO actualActivity = this.restTemplate.getForObject(this.url + "/{userPlayground}/{email}/search/{attributeName}/{value}", ActivityTO.class, userPlayground, EMAIL, attributeName, value);
//		Then the response is 200
		
//		And the output is '[{"playground":"2019A.Kagan","id":"1025028332","location":{"x": Any x ,"y": Any y },"name": Any name,"creationDate": Any valid date ,"expirationDate":null, "type": any type ,"attributes":{"creatorsName":"Manager","isActive":"True","isAMovie":"False","movieName":"Venom 2018"},"creatorPlayground":"2019A.Kagan","creatorEmail":"rubykozel@gmail.com"} ... ]'
		assertThat(actualActivity)
		.isNotNull(); //TODO

	}*/
}