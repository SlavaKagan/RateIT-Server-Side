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
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "ELEMENTS")
public class ElementEntity {
	private String uniqueKey;
	private Double x;
	private Double y;
	private String name;
	
	@Value("${default.date:Anonymous}")
	private Date creationDate;

	private Date expirationDate;
	private String type;
	private Map<String, Object> attributes;
	private String creatorPlayground;
	private String creatorEmail;
	private String number;

	public ElementEntity() {
		this.x = Math.random() * 20;
		this.y = Math.random() * 20;
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

	@Temporal(TemporalType.TIMESTAMP)
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
		return "ElementEntity [uniqueKey=" + uniqueKey + ", x=" + x + ", y=" + y + ", name=" + name + ", creationDate="
				+ creationDate + ", expirationDate=" + expirationDate + ", type=" + type + ", attributes=" + attributes
				+ ", creatorPlayground=" + creatorPlayground + ", creatorEmail=" + creatorEmail + ", number=" + number
				+ "]";
	}
}