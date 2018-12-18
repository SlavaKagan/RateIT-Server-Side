package playground.logic;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name="ACTIVITIES")
public class ActivityEntity {
	
	private String uniqueKey;
	private String elementPlayground;
	private String elementId;
	private String type;
	private String playerPlayground;
	private String playerEmail;	
	private String number;
	private Date creationDate;
	private Map<String, Object> attributes;

	public ActivityEntity() {
		this.attributes = new HashMap<>();
		this.creationDate = new Date();
	}

	public ActivityEntity(ElementEntity newElement, String type) throws Exception {
		this();
		String[] idAndPlayground = newElement.getUniqueKey().split("@@");
		this.elementId = idAndPlayground[0];
		this.elementPlayground = idAndPlayground[1];	
		this.type = type;
	}
	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	@Id
	public String getUniqueKey() {
		return uniqueKey;
	}

	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}
	
	public String getElementPlayground() {
		return elementPlayground;
	}

	
	public void setElementPlayground(String elementPlayground) {
		this.elementPlayground = elementPlayground;
	}

	public String getElementId() {
		return elementId;
	}

	
	public void setElementId(String elementId)  {
		this.elementId = elementId;
	}

	public String getType() {
		return type;
	}

	
	public void setType(String type) {
		this.type = type;
	}

	public String getPlayerPlayground() {
		return playerPlayground;
	}

	
	public void setPlayerPlayground(String playerPlayground)  {
		this.playerPlayground = playerPlayground;
	}

	public String getPlayerEmail() {
		return playerEmail;
	}

	
	public void setPlayerEmail(String playerEmail)  {
		this.playerEmail = playerEmail;
	}
	
	@Transient
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
		
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Lob
	public String getAttributesJson() {
		try {
			return new ObjectMapper().writeValueAsString(this.attributes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setAttributesJson(String attributes) {
		try {
			this.attributes = new ObjectMapper().readValue(attributes, Map.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}