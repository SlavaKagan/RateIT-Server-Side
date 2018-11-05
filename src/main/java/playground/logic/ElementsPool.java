package playground.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class ElementsPool {

	private List<ElementTO> elements = new ArrayList<>(
			Arrays.asList(new ElementTO("Messaging Board", "Messaging Board"),
					new ElementTO("Movie Panel", "Venom 2018"), new ElementTO("Movie Panel", "Halloween")));

	public List<ElementTO> getAllElements() {
		return elements;
	}

	public void addElement(ElementTO element) {
		elements.add(element);
	}

	public void setElements(List<ElementTO> elements) {
		this.elements = elements;
	}

	public ElementTO createElement(String type, String name) {
		ElementTO element = new ElementTO(type, name);
		elements.add(element);
		return element;
	}

	public ElementTO getElement(String userPlayground, String email, String playground, String id) {
		return elements.stream()
				.filter(element -> element.getPlayground().equals(playground)
						&& element.getCreatorPlayground().equals(userPlayground)
						&& element.getCreatorEmail().equals(email) && element.getId().equals(id))
				.findFirst().get();
	}

	public List<ElementTO> getAllElements(String userPlayground, String email) {
		return elements.stream().filter(element -> element.getCreatorPlayground().equals(userPlayground)
				&& element.getCreatorEmail().equals(email)).collect(Collectors.toList());
	}

	public List<ElementTO> getAllElementsByDistance(String userPlayground, String email, double x, double y,
			double distance) {
		return elements.stream()
				.filter(element -> element.getCreatorPlayground().equals(userPlayground)
						&& element.getCreatorEmail().equals(email)
						&& Math.sqrt(Math.pow(x - element.getLocation().getX(), 2)
								+ Math.pow(y - element.getLocation().getY(), 2)) <= distance)
				.collect(Collectors.toList());
	}

	public List<ElementTO> getAllElementsByAttributeAndItsValue(String userPlayground, String email,
			String attributeName, Object value) {
		return elements.stream()
				.filter(element -> userPlayground.equals(element.getCreatorPlayground())
						&& email.equals(element.getCreatorEmail())
						&& element.getAttributes().get(attributeName).equals(value))
				.collect(Collectors.toList());
	}

	public void updateElement(String userPlayground, String email, String playground, String id, ElementTO newElement) {
		getElement(userPlayground, email, playground, id).setParams(newElement);

	}
}
