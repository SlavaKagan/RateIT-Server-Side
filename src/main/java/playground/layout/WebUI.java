package playground.layout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import playground.logic.ActivityTO;
import playground.logic.Constants;
import playground.logic.ElementTO;
import playground.logic.ElementsPool;
import playground.logic.NewUserForm;
import playground.logic.UserPool;
import playground.logic.UserTO;

@RestController
public class WebUI implements Constants {

	private UserPool userpool;
	private ElementsPool elementpool;

	@Autowired
	public void setUserPool(UserPool userpool, ElementsPool elementpool) {
		this.userpool = userpool;
		this.elementpool = elementpool;

	}

	@RequestMapping(
			method = RequestMethod.POST, 
			path = "/playground/users", 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public UserTO createUser(@RequestBody NewUserForm userForm) {
		return userpool.createUser(userForm);
	}
	
	@RequestMapping(
			method = RequestMethod.GET,
			path = "/playground/users/confirm/{playground}/{email}/{code}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserTO confirmUser(
			@PathVariable("playground") String playground,
			@PathVariable("email") String email,
			@PathVariable("code") String code) throws Exception {
		validateParamsNotNull(playground,email);
		UserTO theUser = userpool.getUser(playground, email);
		if (code.equals(TEMPORARY_CODE) && theUser.getRole().equals(GUEST))
			return userpool.confirmUser(playground, email);
		else if (!code.equals(TEMPORARY_CODE))
			throw new Exception("You have entered the wrong confirmation code");
		else
			throw new Exception("User is already confirmed");
	}

	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/playground/users/login/{playground}/{email}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserTO getUser(
			@PathVariable("playground") String playground,
			@PathVariable("email") String email) throws Exception {
		validateParamsNotNull(playground,email);
		return userpool.getUser(playground, email);
	}
	
	@RequestMapping(
			method = RequestMethod.POST, 
			path = "/playground/elements/{userPlayground}/{email}", 
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO createElement(
			@RequestBody ElementTO element,
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email) throws Exception {
		validateParamsNotNull(userPlayground,email);
		if (!userpool.getUser(userPlayground, email).getRole().equals(MANAGER))
			throw new Exception("Given user is not Manager");
		return elementpool.createElement(element.getType(), element.getName());
	}

	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/playground/elements/{userPlayground}/{email}/{playground}/{id}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO getElement(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, 
			@PathVariable("playground") String playground,
			@PathVariable("id") String id) throws Exception {
		validateParamsNotNull(userPlayground,email,playground,id);
		return elementpool.getElement(userPlayground, email, playground, id);
	}

	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/playground/elements/{userPlayground}/{email}/all", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getAllElements(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email) throws Exception {
		validateParamsNotNull(userPlayground,email);
		return elementpool.getAllElements(userPlayground, email).toArray(new ElementTO[0]);
	}

	@RequestMapping(
			method = RequestMethod.PUT, 
			path = "/playground/elements/{userPlayground}/{email}/{playground}/{id}", 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateElement(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, 
			@PathVariable("playground") String playground,
			@PathVariable("id") String id, 
			@RequestBody ElementTO newElement) throws Exception {
		validateParamsNotNull(playground,email,id);
		elementpool.updateElement(userPlayground, email, playground, id, newElement);
	}

	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/playground/elements/{userPlayground}/{email}/near/{x}/{y}/{distance}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getElementsByDistnace(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, 
			@PathVariable("x") String x, 
			@PathVariable("y") String y,
			@PathVariable("distance") String distance) throws Exception {
		validateParamsNotNull(userPlayground,email,x,y,distance);
		return elementpool.getAllElementsByDistance(userPlayground, email, Double.parseDouble(x), Double.parseDouble(y),
				Double.parseDouble(distance)).toArray(new ElementTO[0]);
	}
	
	@RequestMapping(
			method = RequestMethod.GET,
			path = "/playground/elements/{userPlayground}/{email}/search/{attributeName}/{value}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getAllElementsByAttributeAndItsValue( 
			@PathVariable("userPlayground") String userPlayground, 
			@PathVariable("email") String email,
			@PathVariable("attributeName") String attributeName, 
			@PathVariable("value") Object value) throws Exception {
		validateParamsNotNull(userPlayground,email, attributeName);
		return elementpool.getAllElementsByAttributeAndItsValue(userPlayground, email, attributeName, value).toArray(new ElementTO[0]);
	}
	
	@RequestMapping(
			method = RequestMethod.PUT, 
			path = "/playground/users/{playground}/{email}", 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateUser(
			@PathVariable("playground") String playground,
			@PathVariable("email") String email, 
			@RequestBody UserTO newUser) throws Exception {
		validateParamsNotNull(playground,email);
		userpool.editUser(playground, email, newUser);
	}
	
	@RequestMapping(
			method= RequestMethod.POST,
			path = "/playground/activities/{userPlayground}/{email}",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public Object getActivity(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@RequestBody ActivityTO theActivity) throws Exception {
		validateParamsNotNull(userPlayground,email);
		switch(theActivity.getType()) {
		case "x":
			return new UserTO(new NewUserForm("rubykozel@gmail.com","ruby",":-)","Guest"));
		default:
			return new UserTO();
		}
	}
	
	private void validateParamsNotNull(String... strings) throws Exception {
		for(String string : strings) {
			if ("null".equals(string) || string == null) {
				throw new Exception("message not found");
			}
		}
	}
}
