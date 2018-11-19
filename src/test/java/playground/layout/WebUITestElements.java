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

import playground.logic.Constants;
import playground.logic.ElementService;
import playground.logic.NewUserForm;
import playground.logic.UserService;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class WebUITestElements {
	@Autowired
	private ElementService elementservice;
	
	@Autowired
	private UserService userservice;
	private RestTemplate restTemplate;
	private String oldId;
	private String url;
	private NewUserForm form;
	public static final String EMAIL = "rubykozel@gmail.com";
	
	@LocalServerPort
	private int port;
	
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/elements";
		form = new NewUserForm(EMAIL, "ruby", ":-)", "Manager");
	}
	
	@Before
	public void setup () {
		
	}

	@After
	public void teardown() {
		this.userservice.cleanup();
		this.elementservice.cleanup();
	}

	
	@Test
	public void testServerInitializesProperly() throws Exception {
		
	}
	
	@Test
	public void testCreatingAnElementSuccessfully() throws Exception {
		/*
		  	Given the server is up 
			And there's an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Manager" 
		*/
		userservice.createUser(form);
		
		
		/*When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with 
			{
				"type":"Messaging Board", 
				"name":"Messaging Board"
			} 
		*/
		
		ElementTO actualElement = this.restTemplate
				.postForObject(url + "/{userPlayground}/{email}", 
						new ElementTO("Messaging Board","Messaging Board",Constants.PLAYGROUND,EMAIL), 
						ElementTO.class, Constants.PLAYGROUND,EMAIL);
		
		//Then the response is 200 
	
		/* And the output is 
		 	{
		 		"playground": "2019A.Kagan",
		 		"id": Any ID ,
		 		"location": {"x": Any X,"y": Any Y},
		 		"name": "Messaging Board",
		 		"creationDate": Any valid date,
		 		"expirationDate": null,
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
		assertThat(actualElement)
			.isNotNull()
			.extracting(
					"playground",
					"id",
					"location",
					"name",
					"creationDate",
					"expirationDate",
					"type",
					"attributes",
					"creatorPlayground",
					"creatorEmail")
			.containsExactly(
					Constants.PLAYGROUND,
					actualElement.getId(),
					actualElement.getLocation(),
					"Messaging Board",
					actualElement.getCreationDate(),
					actualElement.getExpirationDate(),
					"Messaging Board",
					actualElement.getAttributes(),
					Constants.PLAYGROUND,
					EMAIL);
	}
	
	@Test(expected = Exception.class)
	public void testCreatingAnElementWithAUserThatIsNotAManager() throws Exception {
		/*
		 	Given the server is up
			And theres an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Reviewer",
		 */
		form.setRole(Constants.REVIEWER);
		userservice.createUser(form);
		
		/* When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with 
		 	{
		 		"type":"Messaging Board", 
		 		"name":"Messaging Board"
		 	}
		 */
		
		this.restTemplate
				.postForObject(url + "/{userPlayground}/{email}", 
						new ElementTO("Messaging Board","Messaging Board",Constants.PLAYGROUND,EMAIL), 
						ElementTO.class, Constants.PLAYGROUND,EMAIL);
		
		//Then the response is 500
	}
	
	@Test(expected = Exception.class)
	public void testCreatingAnElementWithoutDeliveringAnyValidJSON() throws Exception {
		/*
		  	Given the server is up
			And there's an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Manager"
		 */
		userservice.createUser(form);
		
		//When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with nothing
		
		this.restTemplate.postForObject(url + "/{userPlayground}/{email}", null, ElementTO.class, Constants.PLAYGROUND,EMAIL);
		
		//Then the response is <> 2xx
	}
	
	
	@Test(expected = Exception.class)
	public void testCreatingAnElementWithEmailAsNull() throws Exception {
		/*
		  	Given the server is up
			And theres an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Manager"
		 */
		userservice.createUser(form);
		
		//When I POST "/playground/elements/2019A.Kagan/null" with 
		/*
		  {
		  	"type":"Messaging Board", 
		  	"name":"Messaging Board"
		  }
		 */
		
		this.restTemplate
		.postForObject(url + "/{userPlayground}/{email}", 
				new ElementTO("Messaging Board","Messaging Board",Constants.PLAYGROUND,EMAIL), 
				ElementTO.class, Constants.PLAYGROUND, null);
		
		//Then the response is 500 with 
	}
	
	@Test
	public void testCreatingAnElementWithEmptyJSON() throws Exception {
		
		/*
		 * Given the server is up
		   And theres an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Manager"
	
	
		 */
		
		userservice.createUser(form);
		
		//When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with '{}'
		ElementTO actualElement = this.restTemplate.postForObject(url + "/{userPlayground}/{email}", 
				new ElementTO(null, null, Constants.PLAYGROUND,EMAIL),
				ElementTO.class, Constants.PLAYGROUND, "rubykozel@gmail.com");
		
		/*
		  	Then the response is 200
			And the output is 
			{
			    "playground": "2019A.Kagan",
			    "id": Any valid id,
			    "location": {
			        "x": Any x,
			        "y": Any y
			    },
			    "name": null,
			    "creationDate": Any valid date,
			    "expirationDate": null,
			    "type": null,
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
		
		assertThat(actualElement)
		.isNotNull()
		.extracting(
				"playground",
				"id",
				"location",
				"name",
				"creationDate",
				"expirationDate",
				"type",
				"attributes",
				"creatorPlayground",
				"creatorEmail")
		.containsExactly(
				Constants.PLAYGROUND,
				actualElement.getId(),
				actualElement.getLocation(),
				null,
				actualElement.getCreationDate(),
				actualElement.getExpirationDate(),
				null,
				actualElement.getAttributes(),
				Constants.PLAYGROUND,
				EMAIL);
	}
	
	@Test
	public void testChangeTheIdOfTheElement() throws Exception{
	/*
	 	Given the server is up
		And theres an element with playground: "2019A.Kagan", email: "rubykozel@gmail.com", playground: "2019A.Kagan", id: "2061451755",
	*/
		userservice.createUser(form);
		ElementTO newElement= new ElementTO();
		elementservice.createElement(newElement, Constants.PLAYGROUND, EMAIL);
		
		/* When I PUT "/playground/elements/2019A.Kagan/rubykozel@gmail.com/2019A.Kagan/2061451755" with 
		 	{
		 		"playground": "2019A.Kagan",
		 		"id": "765",
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
		oldId = newElement.getId();
		newElement.setId("765");
		
		this.restTemplate.put(url + "/{userPlayground}/{email}/{playground}/{id}", newElement, Constants.PLAYGROUND,EMAIL,Constants.PLAYGROUND,oldId);
		//Then the reponse is "200 OK"
	}
	
	@Test (expected = Exception.class)
	public void testTryingToChangeSomeAttributeWithNull() throws Exception{
	/*
		Given the server is up
		And there is an element with playground: "2019A.Kagan", email: "rubykozel@gmail.com", playground: "2019A.Kagan", id: "567",
	*/
		userservice.createUser(form);
		ElementTO newElement= new ElementTO("Messaging Board","Messaging Board",Constants.PLAYGROUND,EMAIL);
		elementservice.createElement(newElement, Constants.PLAYGROUND, form.getEmail());
		
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
		
		oldId = newElement.getId();
		newElement.setId(null);
		
		this.restTemplate.put(url + "/{userPlayground}/{email}/{playground}/{id}", newElement, Constants.PLAYGROUND,EMAIL,Constants.PLAYGROUND,oldId);
		
		//Then the reponse is 500 with message
	}
	
	@Test
	public void getElementsByAttributesValueSuccessfully() throws Exception {
		// Given the server is up - do nothing
		// And there are elements with playground: "2019A.Kagan", email: "rubykozel@gmail.com", attribute: "isAMovie", value:"False"
		userservice.createUser(form);
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("isAMovie", "False");
		ElementTO newElement = new ElementTO();
		newElement.setAttributes(attributes);
		elementservice.createElement(newElement, Constants.PLAYGROUND, EMAIL);

//		When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/search/isAMovie/False"
		String userPlayground = "2019A.Kagan";
		String attributeName = "isAMovie";
		String value = "False";
		ActivityTO actualActivity = this.restTemplate.getForObject(this.url + "/{userPlayground}/{email}/search/{attributeName}/{value}", ActivityTO.class, userPlayground, EMAIL, attributeName, value);
//		Then the response is 200
		
//		And the output is '[{"playground":"2019A.Kagan","id":"1025028332","location":{"x": Any x ,"y": Any y },"name": Any name,"creationDate": Any valid date ,"expirationDate":null, "type": any type ,"attributes":{"creatorsName":"Manager","isActive":"True","isAMovie":"False","movieName":"Venom 2018"},"creatorPlayground":"2019A.Kagan","creatorEmail":"rubykozel@gmail.com"} ... ]'
		assertThat(actualActivity)
		.isNotNull(); //TODO

	}
}