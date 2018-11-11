package playground.logic;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

	public void setPlayground(String playground) throws Exception {
		validateNull(playground);
		this.playground = playground;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) throws Exception {
		validateNull(id);
		this.id = id;
	}
	
	public int hashId() {
		return Math.abs((this.creatorEmail + this.name + this.type).hashCode());
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) throws Exception {
		validateNull(location.getX() + "");
		validateNull(location.getY() + "");
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) throws Exception {
		validateNull(name);
		this.name = name;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) throws Exception {
		validateNull(creationDate.toString());
		this.creationDate = creationDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) throws Exception {
		validateNull(expirationDate.toString());
		this.expirationDate = expirationDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) throws Exception {
		validateNull(type);
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

	public void setCreatorPlayground(String creatorPlayground) throws Exception {
		validateNull(creatorPlayground);
		this.creatorPlayground = creatorPlayground;
	}

	public String getCreatorEmail() {
		return creatorEmail;
	}

	public void setCreatorEmail(String creatorEmail) throws Exception {
		validateNull(creatorEmail);
		this.creatorEmail = creatorEmail;
	}

	@Override
	public String toString() {
		return "[playground=" + playground + ", id=" + id + ", location=" + location + ", name=" + name
				+ ", creationDate=" + creationDate + ", expirationDate=" + expirationDate + ", type=" + type
				+ ", attributes=" + attributes + ", creatorPlayground=" + creatorPlayground + ", creatorEmail="
				+ creatorEmail + "]";
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
	
	private void validateNull(String string) throws Exception {
		if ("null".equals(string) || string == null)
			throw new Exception("One of the paramters provided was null");
	}
}