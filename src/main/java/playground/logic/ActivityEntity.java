package playground.logic;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.ObjectMapper;

import playground.logic.Constants;

@Entity
@Table(name="ACTIVITIES")
public class ActivityEntity implements Constants {
	private String uniqueKey;
	private String elementPlayground;
	private String elementId;
	private String type;
	private String playerPlayground;
	private String playerEmail;
	private String number;
	private Map<String, Object> attributes;

	public ActivityEntity() {
		this.playerPlayground = REVIEWER;
		this.playerEmail = PLAYER_MAIL;
		this.attributes = new HashMap<>();
		this.attributes.put("isActive", "True");
		this.attributes.put("creatorsName", REVIEWER);
		this.attributes.put("activity", new Object());
		this.attributes.put("activityName", "Post a Review");
	}

	public ActivityEntity(ElementEntity newElement, String type) throws Exception {
		this();
		this.elementId = newElement.getId();
		this.elementPlayground = newElement.getPlayground();	
		this.type = type;
	}

	public String getPlayground() {
		return this.uniqueKey.split("@@")[0];
	}

	public String getId() {
		return this.uniqueKey.split("@@")[1];
	}
	
	public String getElementPlayground() {
		return elementPlayground;
	}

	public void setElementPlayground(String elementPlayground) {
		validateNull(elementPlayground);
		this.elementPlayground = elementPlayground;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId)  {
		validateNull(elementId);
		this.elementId = elementId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		validateNull(type);
		this.type = type;
	}

	public String getPlayerPlayground() {
		return playerPlayground;
	}

	public void setPlayerPlayground(String playerPlayground)  {
		validateNull(playerPlayground);
		this.playerPlayground = playerPlayground;
	}

	public String getPlayerEmail() {
		return playerEmail;
	}

	public void setPlayerEmail(String playerEmail)  {
		validateNull(playerEmail);
		this.playerEmail = playerEmail;
	}
	
	@Transient
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
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
	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getUniqueKey() {
		return uniqueKey;
	}

	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	private void validateNull(String string) {
		if ("null".equals(string) || string == null)
			throw new RuntimeException("One of the paramters provided was null");
	}
}