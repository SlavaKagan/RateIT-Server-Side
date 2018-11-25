package playground.logic;

import java.util.List;

public interface ElementService {
	public void addElement(ElementEntity element);

	public ElementEntity createElement(ElementEntity elementEntity, String userPlayground, String email) throws Exception;

	public ElementEntity getElement(String userPlayground, String email, String playground, String id)
			throws ElementNotFoundException;

	public List<ElementEntity> getAllElements(String userPlayground, String email, int size, int page);

	public List<ElementEntity> getAllElementsByDistance(String userPlayground, String email, int size, int page,
			double x, double y, double distance);

	public List<ElementEntity> getAllElementsByAttributeAndItsValue(String userPlayground, String email, int size,
			int page, String attributeName, Object value);

	public void updateElement(String userPlayground, String email, String playground, String id,
			ElementEntity newElement) throws ElementNotFoundException, Exception;

	public void cleanup();

}
