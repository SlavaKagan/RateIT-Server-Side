package playground.dal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class NumberGenerator {
	private Long nextNumber;
	
	public NumberGenerator() {
		
	}
	
	@Id
	@GeneratedValue
	public Long getNextNumber() {
		return nextNumber;
	}

	public void setNextNumber(Long nextNumber) {
		this.nextNumber = nextNumber;
	}
}
