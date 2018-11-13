package playground.layout;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import playground.logic.Constants;
import playground.logic.ElementEntity;
import playground.logic.Location;

public class ElementTO implements Constants {
	private String playground;
	private String id;
	private Location location;
	private String name;
	private Date creationDate;
	private Date expirationDate;
	private String type;
	private Map<String, Object> attributes;
	private String creatorPlayground;
	private String creatorEmail;

	public ElementTO() {
		this.playground = PLAYGROUND;
		this.location = new Location(Math.random() * 20, Math.random() * 20);
		this.creationDate = new Date();
		this.expirationDate = null;
		this.attributes = new HashMap<>();
		this.attributes.put("isActive", "True");
		this.attributes.put("creatorsName", MANAGER_NAME);
		this.attributes.put("isAMovie", "False");
		this.attributes.put("movieName", "Venom 2018");
		setId(hashId()+"");
	}

	public ElementTO(ElementEntity element) {
		this();
		if (element != null) {
			this.playground = element.getPlayground();
			this.creationDate = element.getCreationDate();
			this.location = element.getLocation();
			this.expirationDate = element.getExpirationDate();
			this.attributes = element.getAttributes();
			this.type = element.getType();
			this.name = element.getName();
			this.creatorPlayground = element.getCreatorPlayground();
			this.creatorEmail = element.getCreatorEmail();
			this.id = element.getId();
		}
	}

	public ElementTO(String type, String name, String creatorPlayground, String creatorEmail) throws Exception {
		this();
		this.type = type;
		this.name = name;
		this.creatorPlayground = creatorPlayground;
		this.creatorEmail = creatorEmail;
		setId(hashId() + "");
	}

	public String getPlayground() {
		return playground;
	}

	public void setPlayground(String playground) {
		this.playground = playground;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int hashId() {
		return Math.abs((this.creatorEmail + this.name + this.type).hashCode());
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

	public void setCreationDate(Date creationDate){
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
		return "[playground=" + playground + ", id=" + id + ", location=" + location + ", name=" + name
				+ ", creationDate=" + creationDate + ", expirationDate=" + expirationDate + ", type=" + type
				+ ", attributes=" + attributes + ", creatorPlayground=" + creatorPlayground + ", creatorEmail="
				+ creatorEmail + "]";
	}
	
	public ElementEntity toEntity() {
		ElementEntity rv = new ElementEntity();
		rv.setAttributes(this.attributes);
		rv.setCreationDate(this.creationDate);
		rv.setId(this.id);
		rv.setExpirationDate(this.expirationDate);
		rv.setCreatorEmail(this.creatorEmail);
		rv.setCreatorPlayground(this.creatorPlayground);
		rv.setType(this.type);
		rv.setName(this.name);
		rv.setLocation(this.location);
		rv.setPlayground(this.playground);
		
		return rv;
		
	}

	public void setParams(ElementTO newElement) {
		this.playground = newElement.playground;
		this.location = newElement.location;
		this.creationDate = newElement.creationDate;
		this.expirationDate = newElement.expirationDate;
		this.creatorPlayground = newElement.creatorPlayground;
		this.creatorEmail = newElement.creatorEmail;
		this.attributes = newElement.attributes;
		this.id = newElement.id;
		this.name = newElement.name;
		this.type = newElement.type;
	}
}