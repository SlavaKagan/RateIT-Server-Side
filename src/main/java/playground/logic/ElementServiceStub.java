package playground.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

@Service
public class ElementServiceStub implements Constants, ElementService {

	private Map<String, ElementEntity> elements;

	@PostConstruct
	public void init() {
		elements = new HashMap<>();
	}

	public ElementServiceStub() {
		elements = new HashMap<>();
	}

	public Map<String, ElementEntity> getAllElements() {
		return elements;
	}

	public void addElement(ElementEntity element) {
		elements.put(element.getId(), element);
	}

	public void setElements(Map<String, ElementEntity> elements) {
		this.elements = elements;
	}

	public void createElement(String type, String name, String userPlayground, String email) throws Exception {
		ElementEntity element = new ElementEntity(type, name, userPlayground, email);
		addElement(element);
	}

	public ElementEntity getElement(String userPlayground, String email, String playground, String id)
			throws ElementNotFoundException {
		ElementEntity element = elements.get(id);
		if (element == null || !(element.getPlayground().equals(playground)
				&& element.getCreatorPlayground().equals(userPlayground) && element.getCreatorEmail().equals(email)))
			throw new ElementNotFoundException("Element not found");
		return element;
	}

	public List<ElementEntity> getAllElements(String userPlayground, String email) {
		return elements
				.values()
				.stream()
				.filter(element -> 
				element.getCreatorPlayground().equals(userPlayground)
				&& element.getCreatorEmail().equals(email))
				.collect(Collectors.toList());
	}

	public List<ElementEntity> getAllElementsByDistance(String userPlayground, String email, double x, double y,
			double distance) {
		return elements.values().stream()
				.filter(element -> element.getCreatorPlayground().equals(userPlayground)
						&& element.getCreatorEmail().equals(email)
						&& Math.sqrt(Math.pow(x - element.getLocation().getX(), 2)
								+ Math.pow(y - element.getLocation().getY(), 2)) <= distance)
				.collect(Collectors.toList());
	}

	public List<ElementEntity> getAllElementsByAttributeAndItsValue(String userPlayground, String email,
			String attributeName, Object value) {
		return elements.values().stream()
				.filter(element -> userPlayground.equals(element.getCreatorPlayground())
						&& email.equals(element.getCreatorEmail())
						&& element.getAttributes().get(attributeName).equals(value))
				.collect(Collectors.toList());
	}

	public void updateElement(String userPlayground, String email, String playground, String id,
			ElementEntity newElement) throws ElementNotFoundException {
		ElementEntity element = getElement(userPlayground, email, playground, id);
		if (element == null)
			throw new ElementNotFoundException("Element does not exist");
		element.setParams(newElement);

	}

	@Override
	public void cleanup() {
		this.elements.clear();
	}
}
