package playground.logic;

import java.util.List;

public interface ElementService{

	public ElementEntity createElement(ElementEntity elementEntity, String userPlayground, String email) throws Exception;

	public ElementEntity getElement(String id) throws ElementNotFoundException;

	public List<ElementEntity> getAllElements(int size, int page);

	public List<ElementEntity> getAllElementsByDistance(int size, int page, double x, double y, double distance);

	public List<ElementEntity> getAllElementsByAttributeAndItsValue(int size, int page, String attributeName,
			Object value);

	public void updateElement(String id, ElementEntity newElement) throws ElementNotFoundException, Exception;

	public void cleanup();
}