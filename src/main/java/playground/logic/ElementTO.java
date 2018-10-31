package playground.logic;

import java.util.Date;
import java.util.Map;

public class ElementTO implements Constants {
	private String playground;
	private String id;
	private Location location;
	private String name;
	private Date creationDate;
	private Date experationDate;
	private String type; // What's the difference between type and name?
	private Map<String, Object> attributes;
	private String creatorPlayground;
	private String creatorEmail;

	public ElementTO() {
		this.playground = PLAYGROUND;
		this.location = new Location(Math.random() * 20, Math.random() * 20);
		this.creationDate = new Date();
		this.experationDate = null;
		this.creatorPlayground = PLAYGROUND;
		this.creatorEmail = MANAGER_MAIL;
		this.attributes.put("isActive", "True");
		this.attributes.put("creatorsName", MANAGER_NAME);
		this.attributes.put("isAMovie", "False");
		this.attributes.put("movieName", "Venom 2018");
		// Maybe you can think about more attributes to add...
		this.id = creatorEmail + creatorPlayground;

	}

	public ElementTO(String type, String name) {
		this();
		this.type = type;
		this.name = name;
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

	public Date getExperationDate() {
		return experationDate;
	}

	public void setExperationDate(Date experationDate) {
		this.experationDate = experationDate;
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
				+ ", creationDate=" + creationDate + ", experationDate=" + experationDate + ", type=" + type
				+ ", attributes=" + attributes + ", creatorPlayground=" + creatorPlayground + ", creatorEmail="
				+ creatorEmail + "]";
	}

}
