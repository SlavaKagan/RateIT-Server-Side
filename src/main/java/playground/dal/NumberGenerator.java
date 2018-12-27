package playground.dal;

import javax.persistence.GeneratedValue;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="number_generator")
public class NumberGenerator {
	private String nextNumber;
	
	public NumberGenerator() {
		
	}
	
	@Id
	@GeneratedValue
	public String getNextNumber() {
		return nextNumber;
	}

	public void setNextNumber(String nextNumber) {
		this.nextNumber = nextNumber;
	}
}
