package playground.logic.data;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.aop.annotations.ValidateManager;
import playground.aop.annotations.ValidateNull;
import playground.aop.annotations.ValidateUser;
import playground.aop.logger.Logger;
import playground.aop.logger.PlaygroundPerformance;
import playground.dal.ElementDao;
import playground.dal.NumberGenerator;
import playground.dal.NumberGeneratorDao;
import playground.logic.ElementEntity;
import playground.logic.ElementNotFoundException;
import playground.logic.ElementService;

@Service
public class JpaElementService implements ElementService {

	private ElementDao elements;
	private NumberGeneratorDao numberGenerator;
	
	@Value("${playground:Default}")
	private String playground;
	
	@Value("${delim:@@}")
	private String delim;

	@Autowired
	public void setElementDao(ElementDao elements, NumberGeneratorDao numberGenerator) {
		this.elements = elements;
		this.numberGenerator = numberGenerator;
	}

	@Override
	@Transactional
	@ValidateManager
	@ValidateNull
	@Logger
	@PlaygroundPerformance
	public ElementEntity createElement(String userPlayground, String email, ElementEntity elementEntity)
			throws Exception {
		if (!this.elements.existsById(elementEntity.getUniqueKey())) {
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
	@ValidateNull
	@ValidateUser
	@Logger
	@PlaygroundPerformance
	public ElementEntity getElement(String userPlayground, String email, String id, String playground)
			throws ElementNotFoundException {
		System.err.println(id + delim + playground);
		Optional<ElementEntity> op = this.elements.findById(id + delim + playground);
		if (op.isPresent())
			return op.get();
		throw new ElementNotFoundException("Element not found");
	}

	@Override
	@Transactional(readOnly = true)
	@ValidateNull
	@ValidateUser
	@Logger
	@PlaygroundPerformance
	public List<ElementEntity> getAllElements(String userPlayground, String email, int size, int page) {
		return this
				.elements
				.findAll(PageRequest.of(page, size, Direction.DESC, "creationDate"))
				.getContent();
	}

	@Override
	@Transactional(readOnly = true)
	@ValidateNull
	@ValidateUser
	@Logger
	@PlaygroundPerformance
	public List<ElementEntity> getAllElementsByDistance(String userPlayground, String email, int size, int page,
			double x, double y, double distance) {
		return this
				.elements
				.findAllByXBetweenAndYBetween(
						x-distance, 
						x+distance, 
						y-distance, 
						y+distance, 
						PageRequest.of(page, size, Direction.DESC, "creationDate"));
	}
	
	@Override
	@Transactional(readOnly = true)
	@ValidateNull
	@ValidateUser
	@Logger
	@PlaygroundPerformance
	public List<ElementEntity> getAllElementsByAttributeAndItsValue(String userPlayground, String email, int size,
			int page, String attributeName, String value) throws Exception {
		if(attributeName.toLowerCase().equals("name"))
			return this
				.elements
				.findAllByNameLike(
						value, 
						PageRequest.of(page, size, Direction.DESC, "creationDate"));
		else if(attributeName.toLowerCase().equals("type"))
			return this
					.elements
					.findAllByTypeEquals(
							value, 
							PageRequest.of(page, size, Direction.DESC, "creationDate"));
		else
			throw new Exception("No such attribute name");
			
	}

	@Override
	@Transactional
	@ValidateManager
	@ValidateNull
	@Logger
	@PlaygroundPerformance
	public void updateElement(String userPlayground, String email, String id, String playground, ElementEntity newElement) 
			throws Exception {
		if(this.elements.existsById(id + delim + playground)) {
			ElementEntity existing = getElement(userPlayground, email, id, playground);
			this.elements.delete(existing);
			newElement.setUniqueKey(existing.getUniqueKey());
			this.createElement(existing.getCreatorPlayground(), existing.getCreatorEmail(), newElement);
		} else {
			throw new ElementNotFoundException("There's no such element");
		}
	}

	@Override
	@Transactional
	@Logger
	@PlaygroundPerformance
	public void cleanup() {
		this.elements.deleteAll();
	}

}
