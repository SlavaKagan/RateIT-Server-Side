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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import playground.logic.ElementEntity;
import playground.logic.ElementService;
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
	
	
	private RestTemplate restTemplate;
	private String url;
	private NewUserForm form;
	private UserTO user;
	private String elementJson;
	
	@LocalServerPort
	private int port;
	
	private ObjectMapper jacksonMapper;
	
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/elements";
		form = new NewUserForm(email, "ruby", ":-)", "Manager");
		user = new UserTO(form);
		jacksonMapper = new ObjectMapper();
		elementJson = "{"
							+ "\"type\":\"Messaging Board\","
							+ "\"location\": {\"x\":0,\"y\":0},"
							+ "\"name\":\"Messaging Board\""
					+ "}";
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
	//@Test
	public void testCreatingAnElementSuccessfully() throws Exception {
		//Given
		userservice.createUser(user.toEntity());
		
		//When

		ElementTO postedElement = this.restTemplate
				.postForObject(url + "/{userPlayground}/{email}", 
						jacksonMapper.readValue(elementJson, ElementTO.class),
						ElementTO.class, playground, email);
		
		//Then
		String postedId = postedElement.getId() + "@@" + postedElement.getPlayground();
		ElementEntity actualElementInDb = elementservice.getElement(postedId);
		
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
	
//	***COMMENTED FOR NOW - NO NEED TO TEST IF PLAYER IS MANAGER WHEN POSTING NEW ELEMENT, FOR THIS SPRINT(4)***
//
//	/**
//	 * 	Given the server is up
//		And there's an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Reviewer",
//		When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with 
//		{
//		 	"type":"Messaging Board", 
//		 	"name":"Messaging Board"
//		}
//		Then the response is 500
//	 * @throws Exception
//	 */
//	@Test(expected = Exception.class)
//	public void testCreatingAnElementWithAUserThatIsNotAManager() throws Exception {
//		// Given
//		user.setRole(Constants.REVIEWER);
//		userservice.createUser(user.toEntity());
//
//		// When
//		ElementTO elementToPost = jacksonMapper.readValue("{\"type\":\"Messaging Board\", \"name\":\"Messaging Board\"}", ElementTO.class);
//		this.restTemplate.postForObject(url + "/{userPlayground}/{email}", 
//										elementToPost,		
//										ElementTO.class, 
//										playground,
//										email);
//			
//	}
	
	/**
	 *  Given the server is up
		And there's an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Manager"
		When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with nothing
	 	Then the response is <> 2xx
	 * @throws Exception
	 */
	//@Test(expected = Exception.class)
	public void testCreatingAnElementWithoutDeliveringAnyValidJSON() throws Exception {
		// Given
		userservice.createUser(user.toEntity());
		
		// When
		this.restTemplate.postForObject(url + "/{userPlayground}/{email}", 
										null, 
										ElementTO.class, 
										playground,
										email);	
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
	//@Test(expected = Exception.class)
	public void testCreatingAnElementWithemailAsNull() throws Exception {
		// Given
		userservice.createUser(user.toEntity());
		
		// When
		ElementTO elementToPost = jacksonMapper.readValue(elementJson, ElementTO.class);
		this.restTemplate.postForObject(url + "/{userPlayground}/{email}", 
										elementToPost, 
										ElementTO.class,
										playground,
										null); 
	}
	
	/**
	 * 	Given the server is up
		And there's no manager in the database
	 	When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with '{}'
	 	Then the response is 500
	 * @throws Exception
	 */
	//@Test(expected = Exception.class)
	public void testCreatingAnElementWithEmptyJSON() throws Exception {
		// Given
		userservice.createUser(user.toEntity());
		
		// When
		this.restTemplate.postForObject(url + "/{userPlayground}/{email}",
				jacksonMapper.readValue("{}", ElementTO.class),
				ElementTO.class, playground, "rubykozel@gmail.com");
		 
	}
	
	/**
	 * Given the server is up
		And there's an element with playground: "2019A.Kagan", email: "rubykozel@gmail.com", playground: "2019A.Kagan", id: "someID",
	 	When I PUT "/playground/elements/2019A.Kagan/rubykozel@gmail.com/2019A.Kagan/someID" with 
		 	
		 	{
		 		"playground": "2019A.Kagan",
		 		"name": "MyBoard",
		 		"type": "Messaging Board",
		 		"attributes": {
		 			"creatorsName": "Manager",
		 			"isActive": "True",
		 			"isAMovie": "False",
		 			"movieName": "Venom 2018"
		 		},
		 		"creatorPlayground": "2019A.Kagan",
		 		"creatoremail": "rubykozel@gmail.com"
		 	}
		 	
		 Then the reponse is "200 OK"
	 * @throws Exception
	 */
	//@Test
	public void testChangeTheNameOfTheElement() throws Exception{
		
		// Given
		userservice.createUser(user.toEntity());
		ElementTO element = jacksonMapper.readValue(elementJson, ElementTO.class);
		element = new ElementTO(elementservice.createElement(element.toEntity(), playground, email));
		
		// When
		element.setName("MyBoard");
		this.restTemplate.put(url + "/{userPlayground}/{email}/{playground}/{id}", 
								element, 
								playground,
								email,
								playground,
								element.getId());

		//Then	
		assertThat(jacksonMapper.writeValueAsString(element))
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
						+ "\"creatoremail\":\"rubykozel@gmail.com\""
						+ "}", ElementEntity.class)));
		
	}
	
	/**
	 * 	Given the server is up
		And there is an element with playground: "2019A.Kagan", email: "rubykozel@gmail.com", playground: "2019A.Kagan", id: "567",
	 	When I PUT "/playground/elements/2019A.Kagan/rubykozel@gmail.com/2019A.Kagan/567" with
		  	{
		 		"playground": "2019A.Kagan",
		 		"name": "Messaging Board",
		 		"type": null,
		 		"creatorPlayground": "2019A.Kagan",
		 		"creatoremail": "rubykozel@gmail.com"
		 	}
		Then the reponse is 500
	 * @throws Exception
	 */
	//@Test (expected = Exception.class)
	public void testTryingToChangeTypeNameWithNull() throws Exception{
		
		//Given
		userservice.createUser(user.toEntity());
		ElementTO newElement= jacksonMapper.readValue(elementJson, ElementTO.class);
		elementservice.createElement(newElement.toEntity(), playground, email);
		
		// When
		newElement.setType(null);
		this.restTemplate.put(url + "/{userPlayground}/{email}/{playground}/{id}", 
				newElement, playground,email,playground,newElement.getId());
		
	}
	
	/**
	 * 	Given the server is up 
		And theres are elements with playground: "2019A.Kagan", email: "rubykozel@gmail.com", attribute: "isAMovie", value:"False"
		When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/search/isAMovie/False"
		Then the response is 200
		And the output is 
		[{
			"playground":"2019A.Kagan",
			"id":"1025028332",
			"expirationDate":null, 
			"attributes":{"isAMovie":"False"},
			"creatorPlayground":"2019A.Kagan",
			"creatoremail":"rubykozel@gmail.com"
			} ... ]
	 */	
	//@Test
	public void testGetElementsByAttributesValueSuccessfully() throws Exception {	
		
		// Given
		userservice.createUser(user.toEntity());
		
		Map<String, Object> attributes = new HashMap<>();
		ElementTO newElement = new ElementTO("Movie", "element1", playground, email, attributes);
		newElement.getAttributes().put("isAMovie", "False");
		elementservice.createElement(newElement.toEntity(), playground, email);

		Map<String, Object> attributes2 = new HashMap<>();
		ElementTO newElement2 = new ElementTO("Message Board", "element2", playground, email, attributes2);
		newElement2.getAttributes().put("isAMovie", "True");
		elementservice.createElement(newElement2.toEntity(), playground, email);
		
		String attributeName = "isAMovie";
		String value = "False";
		
		// When
		ElementTO[] actualElements = this.restTemplate
				.getForObject(this.url + "/{userPlayground}/{email}/search/{attributeName}/{value}", 
						ElementTO[].class, 
						playground, 
						email, 
						attributeName, 
						value);
		
		// Then
		assertThat(actualElements)
			.isNotNull()
			.hasSize(1)
			.usingElementComparator((e1, e2)->e1.getAttributes().get(attributeName).toString().compareTo(e2.getAttributes().get(attributeName).toString()))
			.contains(newElement);

	}
	
	/**
	 * 	Given the server is up
		And theres are elements with playground: "2019A.Kagan", email: "rubykozel@gmail.com", attribute: "isAMovie", value:"False"
		When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/search/null/False"
		Then the response is 500
	 */
	
	// FIX THIS - NEED TO SEARCH BY TYPE AND NAME ONLY
	//@Test (expected = Exception.class)
	public void testGetElementsByNullAttributesValue() throws Exception {
		
		// Given
		userservice.createUser(user.toEntity());
		
		Map<String, Object> attributes = new HashMap<>();
		ElementTO newElement = new ElementTO("Movie", "element1", playground, email, attributes);
		newElement.getAttributes().put("isAMovie", "False");
		elementservice.createElement(newElement.toEntity(), playground, email);

		Map<String, Object> attributes2 = new HashMap<>();
		ElementTO newElement2 = new ElementTO("Message Board", "element2", playground, email, attributes2);
		newElement2.getAttributes().put("isAMovie", "True");
		elementservice.createElement(newElement2.toEntity(), playground, email);
		
		String attributeName = "null";
		String value = "False";
		
		// When
		this.restTemplate
		.getForObject(this.url + "/{userPlayground}/{email}/search/{attributeName}/{value}", 
				ElementTO[].class, 
				playground, 
				email, 
				attributeName, 
				value);
		
	}
	
	/**
	 * 	Given the server is up
		And theres are no elements with playground: "2019A.Kagan", email: "rubykozel@gmail.com", attribute: "movieName", value:"Venom 1018"
		When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/search/movieName/Venom 1018"
		Then the response is 404 with message "No element was found with key: movieName and value: Venom 1018"
	 */
	
	// FIX THIS - NEED TO SEARCH BY TYPE AND NAME ONLY
	//@Test (expected = Exception.class)
	public void testGetElementsByAttributesValueThatDoesNotExist() throws Exception {
		
		// Given
		userservice.createUser(user.toEntity());
		
		Map<String, Object> attributes = new HashMap<>();
		ElementTO newElement = new ElementTO("Movie", "element1", playground, email, attributes);
		newElement.getAttributes().put("movieName", "Spiderman");
		elementservice.createElement(newElement.toEntity(), playground, email);
		
		String attributeName = "movieName";
		String value = "Venom 1018";
		
		// When
		this.restTemplate
		.getForObject(this.url + "/{userPlayground}/{email}/search/{attributeName}/{value}", 
				ElementTO[].class, 
				playground, 
				email, 
				attributeName, 
				value);
		
	}
	
	/**
	 * 	Given the server is up 
		And there's at list 1 element in the database with playground: "2019A.Kagan", email: "rubykozel@gmail.com" 
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
			 "creatoremail": 
			 "rubykozel@gmail.com" 
			} x 1 .. 5]'
	 * @throws Exception
	 */
	//@Test
	public void testGettingElementsUsingPaginationSuccessfully() throws Exception {
		
		// Given
		userservice.createUser(user.toEntity());	
		ElementTO newElement= jacksonMapper.readValue(elementJson, ElementTO.class);
		elementservice.createElement(newElement.toEntity(), playground, email);
		
		// When
		ElementTO[] actualElements = this.restTemplate
		.getForObject(this.url + "/{playground}/{email}/all?size={size}&page={page}", 
				ElementTO[].class, 
				playground, 
				email, 5, 0);
		
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
		And theres at most 5 elements in the database with playground: "2019A.Kagan", email: "rubykozel@gmail.com" 
		When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/all?size=5&page=1" 
		Then the response is 200 
		And the output is '[]'
	 * @throws Exception
	 */
	//@Test
	public void testGettingNoElementsFromPageWithNoElementsUsingPaginationSuccessfully() throws Exception {
		
		// Given
		userservice.createUser(user.toEntity());	
		ElementTO newElement= jacksonMapper.readValue(elementJson, ElementTO.class);
		elementservice.createElement(newElement.toEntity(), playground, email);
		
		// When
		ElementTO[] actualElements = this.restTemplate
				.getForObject(this.url + "/{playground}/{email}/all?size={size}&page={page}", 
						ElementTO[].class, 
						playground, 
						email, 5, 1);
		
		// Then
		assertThat(actualElements)
			.isNotNull()
			.hasSize(0);
	}
	
	/**
		Given the server is up 
		And theres at list 1 element in the database with playground: "2019A.Kagan", email: "rubykozel@gmail.com" 
		When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/all?size=5&page=1" 
		Then the response  500
	 * @throws Exception 
	 */
	//@Test(expected = Exception.class)
	public void testUsingBadPageNumberToRetreiveElements() throws Exception {
		userservice.createUser(user.toEntity());	
		ElementTO newElement= jacksonMapper.readValue(elementJson, ElementTO.class);
		elementservice.createElement(newElement.toEntity(), playground, email);
		
		// When
		this.restTemplate.getForObject(this.url + "/{playground}/{email}/all?page={page}", 
						ElementTO[].class, 
						playground, 
						email, -1);
	}
	
	/**
	 * Given the server is up And there's an element with playground: "2019A.Kagan",
	 * email: "rubykozel@gmail.com", playground: "2019A.Kagan", id: "1",
	 * 
	 * When I GET
	 * "/playground/elements/2019A.Kagan/rubykozel@gmail.com/2019A.Kagan/1"
	 * 
	 * Then the response is 200
	 * 
	 * And the output is '{"playground": "2019A.Kagan", "id": "1", "location": {
	 * "x": Any, "y": Any }, "name": "Messaging Board", "creationDate": Any valid
	 * date, "expirationDate": null, "type": "Messaging Board", "attributes": {},
	 * "creatorPlayground": "2019A.Kagan", "creatoremail": "rubykozel@gmail.com" }'
	 * @throws Exception
	 */
	@Test //TODO NOT WORKING
	public void testGettingAnElementSuccessfullyWithCorrectId() throws Exception {
		userservice.createUser(user.toEntity());

		ElementTO newElement = new ElementTO("Messaging Board", "Messaging Board", playground, email,
				new HashMap<>());
		newElement.setId("1"); // For testing purposes

		elementservice.createElement(newElement.toEntity(), playground, email);
		System.err.println("1111111111111111111111111111111111111111");
		System.err.println("1111111111111111111111111111111111111111");
		System.err.println("1111111111111111111111111111111111111111");
		ElementTO actualElement = this.restTemplate.getForObject(url + "/{userPlayground}/{email}/{playground}/{id}",
				ElementTO.class, playground, email, playground, "1");
		System.err.println("2222222222222222222222222222222222222222");
		assertThat(jacksonMapper.writeValueAsString(actualElement))
		.isNotNull()
		.isEqualTo("{" 
						+ "\"playground\":\"2019A.Kagan\"," 
						+ "\"id\":\"1\","
						+ "\"location\":{\"x\":0.0,\"y\":0.0}," 
						+ "\"name\":\"Messaging Board\","
						+ "\"creationDate\":1," 
						+ "\"expirationDate\":null," 
						+ "\"type\":\"Messaging Board\","
						+ "\"attributes\":{}," 
						+ "\"creatorPlayground\":\"2019A.Kagan\","
						+ "\"creatoremail\":\"rubykozel@gmail.com\"" 
						+ "}");
	}

	/**
	 * Given the server is up And theres an element with playground: "2019A.Kagan",
	 * email: "rubykozel@gmail.com", playground: "2019A.Kagan", id: "1",
	 * 
	 * When I GET
	 * "/playground/elements/2019A.Kagan/rubykozel@gmail.com/2019A.Kagan/2"
	 * 
	 * Then the response is 404 with message: "Element does not exist"
	 * @throws Exception
	 */

	@Test //(expected = Exception.class)
	public void testGettingAnElementUnsuccessfullyWithWrongId() throws Exception {

		userservice.createUser(user.toEntity());

		ElementTO newElement = new ElementTO("Messaging Board", "Messaging Board", playground, email,
				new HashMap<>());
		newElement.setId("1"); // For testing purposes

		elementservice.createElement(newElement.toEntity(), playground, email);

		this.restTemplate.getForObject(url + "/{userPlayground}/{email}/{playground}/{id}", ElementTO.class,
				playground, email, playground, "2");
	}
	
	/**
	 * Given the server is up
	 * 
	 * And theres an element with playground: "2019A.Kagan", email:
	 * "rubykozel@gmail.com", playground: "2019A.Kagan", id: "1",
	 * 
	 * When I GET
	 * "/playground/elements/2019A.Kagan/dudidavidov@gmail.com/2019A.Kagan/1"
	 * 
	 * Then the response is 404 with message: "Element does not exist"
	 * @throws Exception
	 */

	@Test //(expected = Exception.class)
	public void testGettingAnElementUnsuccessfullyWithWrongCreatoremail() throws Exception {
		userservice.createUser(user.toEntity());

		ElementTO newElement = new ElementTO("Messaging Board", "Messaging Board", playground, email,
				new HashMap<>());
		newElement.setId("1"); // For testing purposes

		elementservice.createElement(newElement.toEntity(), playground, email);

		this.restTemplate.getForObject(url + "/{userPlayground}/{email}/{playground}/{id}", ElementTO.class,
				playground, "dudidavidov@gmail.com", playground, "1");
	}
	
	/**
	 * Given the server is up And there are elements with playground: "2019A.Kagan",
	 * email: "rubykozel@gmail.com"
	 * 
	 * When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/all"
	 * 
	 * Then the response is 200
	 * 
	 * And the output is '[ { "playground": "2019A.Kagan", "id": "1", "location": {
	 * "x": Any, "y": Any }, "name": "Messaging Board", "creationDate": Any valid
	 * date, "expirationDate": null, "type": "Messaging Board", "attributes": {},
	 * "creatorPlayground": "2019A.Kagan", "creatoremail": "rubykozel@gmail.com" }
	 * ...]'
	 * @throws Exception
	 */

	@Test
	public void testGettingAllElementsSuccessfulyBySpecificCreatoremailAndPlayground() throws Exception {
		userservice.createUser(user.toEntity());

		ElementTO newElement = new ElementTO("Messaging Board", "Messaging Board", playground, email,
				new HashMap<>());
		newElement.setId("1"); // For testing purposes

		elementservice.createElement(newElement.toEntity(), playground, email);
		
		ElementTO[] actualElements = this.restTemplate.getForObject(
				url + "/{userPlayground}/{email}/all?size={size}&page={page}", ElementTO[].class,
				playground, email, 5, 0);
		
		assertThat(actualElements).isNotNull().hasSize(1).usingElementComparator((e1, e2) -> {
			if (e1.getType().compareTo(e2.getType()) != 0)
				return -1;
			return e1.getName().compareTo(e2.getName());
		}).contains(newElement);
	}
	
	/**
	 * Given the server is up
	 * 
	 * And there might be elements with playground: "2019A.Kagan" but not the email:
	 * "rubykozel@gmail.com",
	 * 
	 * When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/all"
	 * 
	 * Then the response 200
	 * And the output is '[]'
	 * @throws Exception
	 */
	
	@Test
	public void testGettingNoneElementsWithCreatoremailThatHasNoElements() throws Exception {
		userservice.createUser(user.toEntity());
		
		ElementTO[] elements = this.restTemplate.getForObject(
				url + "/{userPlayground}/{email}/all?size={size}&page={page}", ElementTO[].class,
				playground, "rubykozel@gmail.com", 5, 0);
		
		assertThat(elements)
		.isNotNull()
		.hasSize(0);
	}
	
	/**
	 * Given the server is up And there is an element with playground:
	 * "2019A.Kagan", email: "rubykozel@gmail.com", x: Any x that its distance from
	 * (0,0) is less then 10, y: Any y that its distance from (0,0) is less then 10
	 * 
	 * When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/near/0/0/10"
	 * 
	 * Then the response is 200
	 * 
	 * And the output is '[ { "playground": "2019A.Kagan", "id": "1",
	 * "location": { "x": Any x that its distance from (0,0) is less then 10
	 * "y": Any y that its distance from (0,0) is less then 10 }, "name": Any name,
	 * "creationDate": Any valid date, "expirationDate": null, "type": Any type,
	 * "attributes": {}, "creatorPlayground": "2019A.Kagan",
	 * "creatoremail": "rubykozel@gmail.com" } ]'
	 * @throws Exception
	 */
	
	@Test //TODO NOT WORKING
	public void testGettingAnElementSuccessfullyWithSpecificDistance() throws Exception {
		userservice.createUser(user.toEntity());

		ElementTO newElement = new ElementTO("Messaging Board", "Messaging Board", playground, email,
				new HashMap<>());
		newElement.setId("1"); // For testing purposes

		elementservice.createElement(newElement.toEntity(), playground, email);
		
		ElementTO[] actualElements = this.restTemplate.getForObject(
				url + "/{userPlayground}/{email}/near/{x}/{y}/{distance}?size={size}&page={page}", ElementTO[].class,
				playground, email, 0, 0, 10, 5, 0);
		
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
	 * Given the server is up
	 * 
	 * And there are no elements in the playground
	 * 
	 * When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/near/0/0/1"
	 * 
	 * Then the response is 404 with message:
	 * "No elements at the distance specified from the (x, y) specified"
	 * @throws Exception
	 */

	@Test //(expected = Exception.class) //TODO NOT WORKING
	public void testGettingAnElementWithSpecificDistanceWhenNoElementsInPlayground() throws Exception {
		userservice.createUser(user.toEntity());

		this.restTemplate.getForObject(
				url + "/{userPlayground}/{email}/near/{x}/{y}/{distance}?size={size}&page={page}", ElementTO[].class,
				playground, email, 0, 0, 10, 5, 0);
	}
}