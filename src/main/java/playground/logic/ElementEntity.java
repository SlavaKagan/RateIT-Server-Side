package playground.logic;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="elements")
public class ElementEntity {
	private String uniqueKey;
	private Double x;
	private Double y;
	private String name;
	private Date creationDate;
	private Date expirationDate;
	private String type;
	private Map<String, Object> attributes;
	private String creatorPlayground;
	private String creatorEmail;
	private String number;

	public ElementEntity() {
		this.expirationDate = null;
		this.attributes = new HashMap<>();
		this.attributes.put("like", 0);
		this.attributes.put("dislike", 0);
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

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
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

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
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
		return "ElementEntity [uniqueKey=" + uniqueKey + ", x=" + x + ", y=" + y + ", name=" + name + ", creationDate="
				+ creationDate + ", expirationDate=" + expirationDate + ", type=" + type + ", attributes=" + attributes
				+ ", creatorPlayground=" + creatorPlayground + ", creatorEmail=" + creatorEmail + ", number=" + number
				+ "]";
	}
}