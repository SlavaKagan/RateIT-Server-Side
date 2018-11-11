package playground.layout;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
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
	public void setPools(UserPool userpool, ElementsPool elementpool) {
		this.userpool = userpool;
		this.elementpool = elementpool;

	}

	@RequestMapping(
			method = RequestMethod.POST, 
			path = "/playground/users", 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public UserTO createUser(@RequestBody NewUserForm userForm) throws IllegalArgumentException, IllegalAccessException, Exception {
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
		if (theUser.getRole().equals(REVIEWER) || theUser.getRole().equals(MANAGER))
			throw new ConfirmationException("User is already confirmed");
		if (!code.equals(TEMPORARY_CODE))
			throw new ConfirmationException("You have entered the wrong confirmation code");
		return null;
	}

	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/playground/users/login/{playground}/{email}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserTO getUser(
			@PathVariable("playground") String playground,
			@PathVariable("email") String email) throws Exception {
		validateParamsNotNull(playground,email);
		UserTO user;
		try {
			user = userpool.getUser(playground, email);
		} catch(Exception e) {
			throw new ConfirmationException("This is an unregistered account");
		}
		if(!user.getRole().equals(GUEST))
			return user;
		else
			throw new ConfirmationException("This is an unconfirmed account");
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
		return elementpool.createElement(element.getType(), element.getName(), userPlayground, email);
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
		try {
			return elementpool.getElement(userPlayground, email, playground, id);
		} catch (Exception e) {
			throw new ElementNotFoundException("Element does not exist");
		}
	}

	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/playground/elements/{userPlayground}/{email}/all", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getAllElements(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email) throws Exception {
		validateParamsNotNull(userPlayground,email);
		ElementTO[] element = elementpool.getAllElements(userPlayground, email).toArray(new ElementTO[0]);
		if (element.length <= 0) {
			throw new ElementNotFoundException("Creator " + email + " has no elements it created");
		}
		return element;
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
		ElementTO[] element = elementpool
				.getAllElementsByDistance(userPlayground, email, Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(distance)).toArray(new ElementTO[0]);
		if (element.length <= 0)			
			throw new ElementNotFoundException("No elements at the distance specified from the (x, y) specified");
		return element;
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
		ElementTO[] elements = elementpool.getAllElementsByAttributeAndItsValue(userPlayground, email, attributeName, value).toArray(new ElementTO[0]);
		if (elements.length <= 0)			
			throw new ElementNotFoundException("No elements at the distance specified from the (x, y) specified");
		return elements;
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
	
	@ExceptionHandler({
		ElementNotFoundException.class, 
		ConfirmationException.class
		})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage handleException(NotFoundExceptions e) {
		String msg = e.getMessage();
		return new ErrorMessage(msg == null ? "There's no specified message for this exception" : msg);
	}
	
	private void validateParamsNotNull(String... strings) throws Exception {
		for(String string : strings) 
			if ("null".equals(string) || string == null)
				throw new Exception("One of the paramters provided was null");					
	}
}
