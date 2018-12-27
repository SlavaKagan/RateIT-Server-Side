package playground.logic;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="activities")
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
	
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
		
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public String toString() {
		return "ActivityEntity [uniqueKey=" + uniqueKey + ", elementPlayground=" + elementPlayground + ", elementId="
				+ elementId + ", type=" + type + ", playerPlayground=" + playerPlayground + ", playerEmail="
				+ playerEmail + ", number=" + number + ", creationDate=" + creationDate + ", attributes=" + attributes
				+ "]";
	}
	
}