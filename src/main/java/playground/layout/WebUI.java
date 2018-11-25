package playground.layout;

import java.util.stream.Collectors;

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

import playground.logic.ConfirmationException;
import playground.logic.Constants;
import playground.logic.ElementNotFoundException;
import playground.logic.ThreadSafeElementServiceStub;
import playground.logic.NotFoundExceptions;
import playground.logic.ThreadSafeUserServiceStub;

@RestController
public class WebUI implements Constants {

	private ThreadSafeUserServiceStub userservice;
	private ThreadSafeElementServiceStub elementservice;

	@Autowired
	public void setPools(ThreadSafeUserServiceStub userservice, ThreadSafeElementServiceStub elementervice) {
		this.userservice = userservice;
		this.elementservice = elementervice;

	}

	@RequestMapping(
			method = RequestMethod.POST, 
			path = "/playground/users", 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public UserTO createUser(@RequestBody NewUserForm userForm) throws IllegalArgumentException, IllegalAccessException, Exception {
		UserTO user = new UserTO(userForm);
		userservice.createUser(user.toEntity());
		return new UserTO(userservice.getAllUsers().get(userForm.getEmail()));
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
		return new UserTO(userservice.confirmUser(playground, email, code));
	}

	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/playground/users/login/{playground}/{email}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserTO getUser(
			@PathVariable("playground") String playground,
			@PathVariable("email") String email) throws Exception {
		validateParamsNotNull(playground,email);
		return new UserTO(userservice.getRegisteredUser(playground, email));	
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
		if(!userservice.getAllUsers().get(email).getRole().equals(Constants.MANAGER))
			throw new ConfirmationException("User is not a manager!");
		elementservice.createElement(element.toEntity(), userPlayground, email);
		return new ElementTO(elementservice.getAllElements().get(element.getId()));
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
		return new ElementTO(elementservice.getElement(userPlayground, email, playground, id));

	}

	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/playground/elements/{userPlayground}/{email}/all", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getAllElements(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email) throws Exception {
		validateParamsNotNull(userPlayground,email);
		ElementTO[] element = elementservice.getAllElements(userPlayground, email).toArray(new ElementTO[0]);
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
		elementservice.updateElement(userPlayground, email, playground, id,newElement.toEntity());
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
		ElementTO[] element = elementservice
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
		ElementTO[] elements = elementservice.getAllElementsByAttributeAndItsValue(userPlayground, email, attributeName, value)
				.stream() //MessageEntity stream
				.map(ElementTO::new) //ElementTO stream
				.collect(Collectors.toList()) //ElementTO List
				.toArray(new ElementTO[0]); //ElementTO[];
		if (elements.length <= 0)			
			throw new ElementNotFoundException("No element was found with key: " + attributeName + " and value: " + value);
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
		userservice.editUser(playground, email, newUser.toEntity());
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