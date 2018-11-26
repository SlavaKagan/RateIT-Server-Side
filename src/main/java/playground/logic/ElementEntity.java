package playground.logic;

import java.io.IOException;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import playground.logic.Constants;

@Entity
@Table(name="ELEMENTS")
public class ElementEntity implements Constants {
	private String uniqueKey;
	private Location location;
	private String name;
	private Date creationDate;
	private Date expirationDate;
	private String type;
	private Map<String, Object> attributes;
	private String creatorPlayground;
	private String creatorEmail;
	private String number;
	
	@Id
	public String getUniqueKey() {
		return uniqueKey;
	}

	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public ElementEntity() {
		this.location = new Location(Math.random() * 20, Math.random() * 20);
		this.creationDate = DEFAULT_DATE;
		this.expirationDate = null;
		this.attributes = new HashMap<>();
	}

	public ElementEntity(String type, String name, String creatorPlayground, String creatorEmail,
			Map<String, Object> attributes) {
		this();
		this.type = type;
		this.name = name;
		this.creatorPlayground = creatorPlayground;
		this.creatorEmail = creatorEmail;
		this.attributes = attributes;
	}

	public String getPlayground() {
		return this.uniqueKey.split("@@")[1];
	}

	public String getId() {
		return this.uniqueKey.split("@@")[0];
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
	
	public String getCreatorPlayground() {
		return creatorPlayground;
	}

	public void setCreatorPlayground(String creatorPlayground) {
		this.creatorPlayground = creatorPlayground;
	}

	public String getCreatorEmail() {
		return creatorEmail;
	}

	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
	}

	@Override
	public String toString() {
		return "ElementEntity [uniqueKey=" + uniqueKey + ", location=" + location + ", name=" + name + ", creationDate="
				+ creationDate + ", expirationDate=" + expirationDate + ", type=" + type + ", attributes=" + attributes
				+ ", creatorPlayground=" + creatorPlayground + ", creatorEmail=" + creatorEmail + ", number=" + number
				+ "]";
	}

}