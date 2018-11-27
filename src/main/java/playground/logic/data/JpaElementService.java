package playground.logic.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.dal.ElementDao;
import playground.dal.NumberGenerator;
import playground.dal.NumberGeneratorDao;
import playground.logic.ElementEntity;
import playground.logic.ElementNotFoundException;
import playground.logic.ElementService;
import playground.logic.Location;

@Service
public class JpaElementService implements ElementService {

	private ElementDao elements;
	private NumberGeneratorDao numberGenerator;

	@Autowired
	public void setElementDao(ElementDao elements, NumberGeneratorDao numberGenerator) {
		this.elements = elements;
		this.numberGenerator = numberGenerator;
	}

	@Override
	@Transactional
	public ElementEntity createElement(ElementEntity elementEntity, String userPlayground, String email)
			throws Exception {
		if (!this.elements.existsById(elementEntity.getUniqueKey())) {
			checkForNulls(elementEntity);
			NumberGenerator temp = this.numberGenerator.save(new NumberGenerator());
			
			elementEntity.setNumber("" + temp.getNextNumber());
			elementEntity.setCreatorPlayground(userPlayground);
			elementEntity.setCreatorEmail(email);
			
			this.numberGenerator.delete(temp);

			return this.elements.save(elementEntity);
		}
		throw new Exception("Element already exists!");
	}

	@Override
	@Transactional(readOnly = true)
	public ElementEntity getElement(String id)
			throws ElementNotFoundException {
		System.err.println("id= " + id);
		Optional<ElementEntity> op = this.elements.findById(id);
		if (op.isPresent())
			return op.get();
		throw new ElementNotFoundException("Element not found");
	}

	private List<ElementEntity> getAllValuesFromDao() {
		List<ElementEntity> allList = new ArrayList<>();
		this.elements.findAll().forEach(allList::add);

		return allList;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementEntity> getAllElements(int size, int page) {
		return getAllValuesFromDao()
				.stream()
				.skip(page * size)
				.limit(size)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementEntity> getAllElementsByDistance(int size, int page,
			double x, double y, double distance) {
		return getAllValuesFromDao()
				.stream()
				.filter(element -> 
				Location.getDistance(
						new Location(x, y), new Location(element.getX(),element.getY())) <= distance)
				.skip(page * size)
				.limit(size)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementEntity> getAllElementsByAttributeAndItsValue(int size,
			int page, String attributeName, Object value) {
		return getAllValuesFromDao()
				.stream()
				.filter(element -> element.getAttributes().get(attributeName).equals(value))
				.skip(page * size)
				.limit(size)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void updateElement(String id,ElementEntity newElement) throws ElementNotFoundException, Exception {
		checkForNulls(newElement);
		ElementEntity existing = this.getElement(id);
		this.elements.delete(existing);
		this.createElement(newElement, existing.getCreatorEmail(), existing.getCreatorEmail());
	}

	@Override
	@Transactional
	public void cleanup() {
		this.elements.deleteAll();
	}
	
	private void checkForNulls(ElementEntity e) throws Exception {
		if(e.getName() == null || e.getType() == null || e.getUniqueKey() == null)
			throw new Exception("Null was given to name or type");
	}

}
