package playground.layout;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.HashMap;
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
import playground.logic.ElementEntity;
import playground.logic.ElementService;
import playground.logic.Location;
import playground.logic.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class WebUITestElements  {
	
	@Autowired
	private ElementService elementservice;
	
	@Autowired
	private UserService userservice;
	
	@Value("${email:Anonymous}")
	private String email;
	
	@Value("${playground:Anonymous}") 
	private String playground;
	
	@Value("${delim:@@}")
	private String delim;
	
	@Value("${reviewer:Reviewer}")
	private String reviewer;
	
	private RestTemplate restTemplate;
	private String url;
	private UserTO user;
	private String elementJson;
	
	@LocalServerPort
	private int port;
	
	private ObjectMapper jacksonMapper;
	
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/elements";
		user = new UserTO(new NewUserForm(email, "ruby", ":-)", "Manager"));
		jacksonMapper = new ObjectMapper();
		elementJson = "{"
							+ "\"type\":\"Messaging Board\","
							+ "\"location\": {\"x\":0,\"y\":0},"
							+ "\"name\":\"Messaging Board\""
					+ "}";
	}
	
	@Before
	public void setup () throws Exception {
		user.setPlayground(playground);
		userservice.createUser(user.toEntity());
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
		And there's an account with playground: "2019A.Kagan", email: email, role: "Manager"
		
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
		//When

		ElementTO postedElement = this.restTemplate
				.postForObject(url + "/{userPlayground}/{email}", 
						jacksonMapper.readValue(elementJson, ElementTO.class),
						ElementTO.class, user.getPlayground(), user.getEmail());
		
		//Then
		ElementEntity actualElementInDb = elementservice.getElement(playground, email, postedElement.getId(), playground);
		
		actualElementInDb.setCreationDate(null);
		actualElementInDb.setX(0.0); // For testing purposes
		actualElementInDb.setY(0.0);
		
		assertThat(jacksonMapper.writeValueAsString(actualElementInDb))
		.isNotNull()
		.isEqualTo(jacksonMapper.writeValueAsString(
				jacksonMapper.readValue(""
						+ "{"
						+ "\"uniqueKey\": \"" + postedElement.getId() + "@@2019A.Kagan\","
						+ "\"name\": \"Messaging Board\","
						+ "\"expirationDate\": null,"
						+ "\"type\": \"Messaging Board\","
						+ "\"x\": 0.0,"
						+ "\"y\": 0.0,"
						+ "\"number\": " + actualElementInDb.getNumber() + ","
						+ "\"creatorPlayground\":\"2019A.Kagan\","
						+ "\"creatorEmail\":\"rubykozel@gmail.com\""
						+ "}", ElementEntity.class)));
	}	

	/**
	 * 	Given the server is up
		And there's an account with playground: "2019A.Kagan", email: email, role: "Reviewer",
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
		userservice.cleanup(); // specific for this test
		user.setRole(reviewer);
		userservice.createUser(user.toEntity());

		// When
		ElementTO elementToPost = jacksonMapper.readValue("{\"type\":\"Messaging Board\", \"name\":\"Messaging Board\"}", ElementTO.class);
		this.restTemplate.postForObject(url + "/{userPlayground}/{email}", 
										elementToPost,		
										ElementTO.class, 
										user.getPlayground(),
										user.getEmail());
			
	}
	
	/**
	 *  Given the server is up
		And there's an account with playground: "2019A.Kagan", email: email, role: "Manager"
		When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with nothing
	 	Then the response is <> 2xx
	 * @throws Exception
	 */
	
	@Test(expected = Exception.class)
	public void testCreatingAnElementWithoutDeliveringAnyValidJSON() throws Exception {
		// When
		this.restTemplate.postForObject(url + "/{userPlayground}/{email}", 
										null, 
										ElementTO.class, 
										user.getPlayground(),
										user.getEmail());	
	}
	
	/**
	 * 	Given the server is up
		And there's an account with playground: "2019A.Kagan", email: email, role: "Manager"
	 	When I POST "/playground/elements/2019A.Kagan/null" with 
	  	{
	  		"type":"Messaging Board", 
	  		"name":"Messaging Board"
	  	}
	  	Then the response is 500
	 * @throws Exception
	 */
	
	@Test(expected = Exception.class)
	public void testCreatingAnElementWithemailAsNull() throws Exception {
		// When
		ElementTO elementToPost = jacksonMapper.readValue(elementJson, ElementTO.class);
		this.restTemplate.postForObject(url + "/{userPlayground}/{email}", 
										elementToPost, 
										ElementTO.class,
										user.getPlayground(),
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
		// When
		this.restTemplate.postForObject(url + "/{userPlayground}/{email}",
				jacksonMapper.readValue("{}", ElementTO.class),
				ElementTO.class, user.getPlayground(), user.getEmail());
		 
	}
	
	/**
	 * Given the server is up
		And there's an element with playground: "2019A.Kagan", email: email, playground: "2019A.Kagan", id: "someID",
	 	When I PUT "/playground/elements/2019A.Kagan/rubykozel@gmail.com/2019A.Kagan/someID" with 
		 	
		 	{
		 		"playground": "2019A.Kagan",
		 		"name": "MyBoard",
		 		"type": "Messaging Board",
		 		"creatorPlayground": "2019A.Kagan",
		 		"creatorEmail": "rubykozel@gmail.com"
		 	}
		 	
		 Then the reponse is "200 OK"
	 * @throws Exception
	 */
	
	@Test
	public void testChangeTheNameOfTheElement() throws Exception{	
		// Given
		ElementTO elementToPost = jacksonMapper.readValue(elementJson, ElementTO.class);
		elementToPost.setPlayground(playground);
		elementservice.createElement(user.getPlayground(), user.getEmail(), elementToPost.toEntity());
		
		// When
		ElementTO elementToPut = jacksonMapper.readValue(
				"{"
						+ "\"playground\":\"2019A.Kagan\","
						+ "\"type\":\"Messaging Board\","
						+ "\"creatorPlayground\":\"2019A.Kagan\","
						+ "\"creatorEmail\":\"rubykozel@gmail.com\","
						+ "\"name\":\"MyBoard\""
				+ "}", ElementTO.class);
		
		this.restTemplate.put(url + "/{userPlayground}/{email}/{playground}/{id}",
								elementToPut, 
								user.getPlayground(),
								user.getEmail(),
								playground,
								elementToPost.getId());
		
		ElementEntity actualElementInDb = this.elementservice.getElement(playground, email, elementToPost.getId(), playground);
		actualElementInDb.setCreationDate(null);
		actualElementInDb.setX(0.0); // For testing purposes
		actualElementInDb.setY(0.0);
		
		//Then	
		assertThat(jacksonMapper.writeValueAsString(actualElementInDb))
		.isNotNull()
		.isEqualTo(jacksonMapper.writeValueAsString(
				jacksonMapper.readValue(""
						+ "{"
						+ "\"uniqueKey\": \"" + elementToPost.getId() + "@@2019A.Kagan\","
						+ "\"name\":\"MyBoard\","
						+ "\"expirationDate\": null,"
						+ "\"type\":\"Messaging Board\","
						+ "\"x\": 0.0,"
						+ "\"y\": 0.0,"
						+ "\"number\": " + actualElementInDb.getNumber() + ","
						+ "\"creatorPlayground\":\"2019A.Kagan\","
						+ "\"creatorEmail\":\"rubykozel@gmail.com\""
						+ "}", ElementEntity.class)));
		
	}
	
	/**
	 * 	Given the server is up
		And there is an element with playground: "2019A.Kagan", email: email, playground: "2019A.Kagan", id: "567",
	 	When I PUT "/playground/elements/2019A.Kagan/rubykozel@gmail.com/2019A.Kagan/567" with
		  	{
		 		"playground": "2019A.Kagan",
		 		"name": "Messaging Board",
		 		"type": null,
		 		"creatorPlayground": "2019A.Kagan",
		 		"creatoremail": email
		 	}
		Then the reponse is 500
	 * @throws Exception
	 */
	
	@Test (expected = Exception.class)
	public void testTryingToChangeTypeNameWithNull() throws Exception{
		
		//Given
		ElementTO newElement= jacksonMapper.readValue(elementJson, ElementTO.class);
		newElement.setPlayground(playground);
		elementservice.createElement(user.getPlayground(), user.getEmail(), newElement.toEntity());
		
		// When
		System.err.println("starting to set type to null");
		newElement.setType(null);
		System.err.println("ended set type to null");
		this.restTemplate.put(url + "/{userPlayground}/{email}/{playground}/{id}", 
				newElement, 
				user.getPlayground(), 
				user.getEmail(),
				playground,
				newElement.getId());
		
	}
	
	/**
	 * 	Given the server is up 
		And theres are elements with playground: "2019A.Kagan", email: email
		When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/search/name/Messaging Board"
		Then the response is 200
		And the output is 
		[{
			"playground":"2019A.Kagan",
			"id":"1025028332",
			"name":"Messaging Board"
			"creatorPlayground":"2019A.Kagan",
			"creatoremail":email
			} ... ]
	 */	
	
	@Test
	public void testGetElementsByAttributesValueSuccessfully() throws Exception {	
		
		// Given
		ElementTO newElement= jacksonMapper.readValue(elementJson, ElementTO.class);
		newElement.setPlayground(playground);
		elementservice.createElement(user.getPlayground(), user.getEmail(), newElement.toEntity());
		
		// When
		ElementTO[] actualElements = this.restTemplate
				.getForObject(this.url + "/{userPlayground}/{email}/search/{attributeName}/{value}", 
						ElementTO[].class, 
						user.getPlayground(),
						user.getEmail(), 
						"name", 
						"Messaging Board");
		
		// Then
		assertThat(actualElements)
			.isNotNull()
			.hasSize(1)
			.usingElementComparator((e1, e2)->e1.getName().toString().compareTo(e2.getName().toString()))
			.contains(newElement);

	}
	
	/**
	 * 	Given the server is up
		And theres are elements with playground: "2019A.Kagan", email: email
		When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/search/name/null"
		Then the response is 500
	 */
	
	@Test (expected = Exception.class)
	public void testGetElementsByNullAttributesValue() throws Exception {
		
		// Given
		ElementTO newElement= jacksonMapper.readValue(elementJson, ElementTO.class);
		newElement.setPlayground(playground);
		elementservice.createElement(user.getPlayground(), user.getEmail(), newElement.toEntity());
		
		// When
		this.restTemplate
		.getForObject(this.url + "/{userPlayground}/{email}/search/{attributeName}/{value}", 
				ElementTO[].class, 
				user.getPlayground(),
				user.getEmail(), 
				"name", 
				null);
		
	}
	
	/**
	 * 	Given the server is up
		And theres are no elements with playground: "2019A.Kagan", email: email, type: "something"
		When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/search/type/something"
		Then the response 200 
		And the output is []
	 */
	
	@Test
	public void testGetElementsByAttributesValueThatDoesNotExist() throws Exception {
		
		// Given
		ElementTO newElement= jacksonMapper.readValue(elementJson, ElementTO.class);
		newElement.setPlayground(playground);
		elementservice.createElement(user.getPlayground(), user.getEmail(), newElement.toEntity());
		
		// When
		ElementTO[] actualElements = this.restTemplate
		.getForObject(this.url + "/{userPlayground}/{email}/search/{attributeName}/{value}", 
				ElementTO[].class, 
				user.getPlayground(),
				user.getEmail(), 
				"type", 
				"something");
		
		assertThat(actualElements)
		.isNotNull()
		.hasSize(0);	
	}
	
	/**
	 * 	Given the server is up 
		And there's at list 1 element in the database with playground: "2019A.Kagan", email: email 
		When I GET "/playground/elements/2019A.Kagan/rubykoze@gmail.com/all?size=5&page=0" 
		Then the response 200 
		And the output is 
		[ { 
			"playground": "2019A.Kagan",
			 "id": "1025028332",
			 "location": { "x": Any, "y": Any },
			 "name": "Messaging Board", 
			 "creationDate": Any valid date, 
			 "expirationDate": null, 
			 "type": "Messaging Board",
			 attributes: {}, 
			 "creatorPlayground": "2019A.Kagan", 
			 "creatoremail": "rubykozel@gmail.com"
			} x 1 .. 5]'
	 * @throws Exception
	 */
	
	@Test
	public void testGettingElementsUsingPaginationSuccessfully() throws Exception {
		
		// Given
		ElementTO newElement= jacksonMapper.readValue(elementJson, ElementTO.class);
		newElement.setPlayground(playground);	
		elementservice.createElement(user.getPlayground(), user.getEmail(), newElement.toEntity());
		
		// When
		ElementTO[] actualElements = this.restTemplate
		.getForObject(this.url + "/{playground}/{email}/all?size={size}&page={page}", 
				ElementTO[].class, 
				user.getPlayground(),
				user.getEmail(), 5, 0);
		
		assertThat(actualElements)
		.isNotNull()
		.hasSize(1)
		.usingElementComparator((e1,e2)-> {
			if(e1.getType().compareTo(e2.getType()) != 0)
				return -1;
			return e1.getName().compareTo(e2.getName());
			})
		.contains(newElement);
	}
	
	/**
	 * 	Given the server is up 
		And theres at most 5 elements in the database with playground: "2019A.Kagan", email: email 
		When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/all?size=5&page=1" 
		Then the response is 200 
		And the output is '[]'
	 * @throws Exception
	 */
	
	@Test
	public void testGettingNoElementsFromPageWithNoElementsUsingPaginationSuccessfully() throws Exception {
		
		// Given	
		ElementTO newElement= jacksonMapper.readValue(elementJson, ElementTO.class);
		newElement.setPlayground(playground);	
		elementservice.createElement(user.getPlayground(), user.getEmail(), newElement.toEntity());
		
		// When
		ElementTO[] actualElements = this.restTemplate
				.getForObject(this.url + "/{playground}/{email}/all?size={size}&page={page}", 
						ElementTO[].class, 
						user.getPlayground(),
						user.getEmail(), 5, 1);
		
		// Then
		assertThat(actualElements)
			.isNotNull()
			.hasSize(0);
	}
	
	/**
		Given the server is up 
		And theres at list 1 element in the database with playground: "2019A.Kagan", email: email 
		When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/all?size=5&page=1" 
		Then the response  500
	 * @throws Exception 
	 */
	
	@Test(expected = Exception.class)
	public void testUsingBadPageNumberToRetreiveElements() throws Exception {
		
		ElementTO newElement= jacksonMapper.readValue(elementJson, ElementTO.class);
		newElement.setPlayground(playground);	
		elementservice.createElement(user.getPlayground(), user.getEmail(), newElement.toEntity());
		
		// When
		this.restTemplate.getForObject(this.url + "/{playground}/{email}/all?page={page}", 
						ElementTO[].class, 
						playground, 
						email, -1);
	}
	
	/**
	 * Given the server is up And there's an element with playground: "2019A.Kagan",
	 * email: email, playground: "2019A.Kagan", id: "1",
	 * 
	 * When I GET
	 * "/playground/elements/2019A.Kagan/rubykozel@gmail.com/2019A.Kagan/1"
	 * 
	 * Then the response is 200
	 * 
	 * And the output is 
	  {
	  		"playground": "2019A.Kagan", 
	  		"id": "1", 
	  		"location": {
	  			"x": Any, "y": Any 
	  		}, 
	  		"name": "Messaging Board", 
	  		"creationDate": Any valid date, 
	  		"expirationDate": null, 
	  		"type": "Messaging Board", 
	  		"attributes": {},
	 		"creatorPlayground": "2019A.Kagan", 
	  		"creatoremail": "rubykozel@gmail.com" 
	  }
	 * @throws Exception
	 */
	
	@Test
	public void testGettingAnElementSuccessfullyWithCorrectId() throws Exception {
		
		ElementTO elementToPost = jacksonMapper.readValue(elementJson, ElementTO.class);
	
		elementToPost.setId("1"); // For testing purposes
		elementToPost.setLocation(new Location(0,0));
		elementToPost.setPlayground(playground);
		elementservice.createElement(user.getPlayground(), user.getEmail(), elementToPost.toEntity());
		
		ElementTO actualElement = this.restTemplate.getForObject(url + "/{userPlayground}/{email}/{playground}/{id}",
				ElementTO.class, user.getPlayground(), user.getEmail(), playground, "1");
		
		actualElement.setCreationDate(null); // For testing purposes
		
		assertThat(jacksonMapper.writeValueAsString(actualElement))
		.isNotNull()
		.isEqualTo("{" 
						+ "\"playground\":\"2019A.Kagan\"," 
						+ "\"creationDate\":null," 
						+ "\"id\":\"1\","
						+ "\"location\":{\"x\":0.0,\"y\":0.0}," 
						+ "\"name\":\"Messaging Board\","
						+ "\"expirationDate\":null," 
						+ "\"type\":\"Messaging Board\","
						+ "\"attributes\":{}," 
						+ "\"creatorPlayground\":\"2019A.Kagan\","
						+ "\"creatorEmail\":\"rubykozel@gmail.com\"" 
						+ "}");
	}

	/**
	 * Given the server is up And theres an element with playground: "2019A.Kagan",
	 * email: email, playground: "2019A.Kagan", id: "1",
	 * 
	 * When I GET
	 * "/playground/elements/2019A.Kagan/rubykozel@gmail.com/2019A.Kagan/2"
	 * 
	 * Then the response is 404 with message: "Element does not exist"
	 * @throws Exception
	 */

	@Test (expected = Exception.class)
	public void testGettingAnElementUnsuccessfullyWithWrongId() throws Exception {

		ElementTO newElement = jacksonMapper.readValue(elementJson, ElementTO.class);
		newElement.setId("1"); // For testing purposes
		
		newElement.setPlayground(playground);	
		elementservice.createElement(user.getPlayground(), user.getEmail(), newElement.toEntity());

		this.restTemplate.getForObject(url + "/{userPlayground}/{email}/{playground}/{id}", ElementTO.class,
				user.getPlayground(), user.getEmail(), playground, "2");
	}
	
	/**
	 * Given the server is up And there are elements with playground: "2019A.Kagan",
	 * email: email
	 * 
	 * When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/all"
	 * 
	 * Then the response is 200
	 * 
	 * And the output is 
	  [ 
	  	{ 
	  		"playground": "2019A.Kagan", 
	  		"id": "1", 
	  		"location": {
	  			"x": Any, "y": Any 
	  		}, 
	  		"name": "Messaging Board", 
	  		"creationDate": Any valid date, 
	  		"expirationDate": null, 
	  		"type": "Messaging Board", 
	  		"attributes": {},
	  		"creatorPlayground": "2019A.Kagan", 
	  		"creatoremail": "rubykozel@gmail.com" 
	  } ...]'
	 * @throws Exception
	 */

	@Test
	public void testGettingAllElementsSuccessfulyBySpecificCreatoremailAndPlayground() throws Exception {

		ElementTO newElement = jacksonMapper.readValue(elementJson, ElementTO.class);
		newElement.setId("1"); // For testing purposes
		
		newElement.setPlayground(playground);	
		elementservice.createElement(user.getPlayground(), user.getEmail(), newElement.toEntity());
		
		ElementTO[] actualElements = this.restTemplate.getForObject(
				url + "/{userPlayground}/{email}/all?size={size}&page={page}", ElementTO[].class,
				user.getPlayground(), user.getEmail(), 5, 0);
		
		assertThat(actualElements).isNotNull().hasSize(1).usingElementComparator((e1, e2) -> {
			if (e1.getType().compareTo(e2.getType()) != 0)
				return -1;
			return e1.getName().compareTo(e2.getName());
		}).contains(newElement);
	}
	
	/**
	 * Given the server is up
	 * 
	 * And there are no elements with playground: "2019A.Kagan"
	 * 
	 * When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/all"
	 * 
	 * Then the response 200
	 * And the output is '[]'
	 * @throws Exception
	 */
	
	@Test
	public void testGettingNoneElementsWithPlaygroundThatHasNoElements() throws Exception {
		
		ElementTO[] elements = this.restTemplate.getForObject(
				url + "/{userPlayground}/{email}/all?size={size}&page={page}", ElementTO[].class,
				user.getPlayground(), user.getEmail(), 5, 0);
		
		assertThat(elements)
		.isNotNull()
		.hasSize(0);
	}
	
	/**
	 * Given the server is up And there is an element with playground:
	 * "2019A.Kagan", email: email, x: Any x that its distance from
	 * (0,0) is less then 10, y: Any y that its distance from (0,0) is less then 10
	 * 
	 * When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/near/0/0/10"
	 * 
	 * Then the response is 200
	 * 
	 * And the output is 
	  '[ 
	  	{ "playground": "2019A.Kagan", 
	  	  "id": "1",
	      "location": { 
	     	  "x": Any x that its distance from (0,0) is less then 10
	  		  "y": Any y that its distance from (0,0) is less then 10 
	  		}, 
	  	  "name": Any name,
	  	  "creationDate": Any valid date, "expirationDate": null, "type": Any type,
	  	  "attributes": {}, 
	  	  "creatorPlayground": "2019A.Kagan",
	     "creatoremail": "rubykozel@gmail.com" 
	    } ... ]'
	 * @throws Exception
	 */
	
	@Test
	public void testGettingAnElementSuccessfullyWithSpecificDistance() throws Exception {

		ElementTO newElement = new ElementTO("Messaging Board", "Messaging Board", playground, email,
				new HashMap<>());
		newElement.setLocation(new Location(1,1));
		newElement.setPlayground(playground);
		elementservice.createElement(user.getPlayground(), user.getEmail(), newElement.toEntity());
		
		ElementTO[] actualElements = this.restTemplate.getForObject(
				url + "/{userPlayground}/{email}/near/{x}/{y}/{distance}?size={size}&page={page}", ElementTO[].class,
				user.getPlayground(), user.getEmail(), 0, 0, 10, 5, 0);
		
		assertThat(actualElements)
		.isNotNull()
		.hasSize(1)
		.usingElementComparator((e1, e2) -> {
			if (e1.getType().compareTo(e2.getType()) != 0)
				return -1;
			return e1.getName().compareTo(e2.getName());
		})
		.contains(newElement);
	}
	
	
	/**
	 * When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/near/0/0/1"
	 * 
	 * Then the response 200
	 * And the output is '[]'
	 * @throws Exception
	 */
	@Test 
	public void testGettingAnElementWithSpecificDistanceWhenNoElementsInPlayground() throws Exception {
		
		ElementTO[] elements = this.restTemplate.getForObject(
				url + "/{userPlayground}/{email}/near/{x}/{y}/{distance}?size={size}&page={page}", ElementTO[].class,
				user.getPlayground(), user.getEmail(), 0, 0, 10, 5, 0);
		
		assertThat(elements)
		.isNotNull()
		.hasSize(0);
	}
}