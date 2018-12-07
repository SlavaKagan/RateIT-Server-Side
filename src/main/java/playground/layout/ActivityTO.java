package playground.layout;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Value;
import playground.logic.ActivityEntity;

public class ActivityTO {
	
	private String playground;
	private String playerPlayground;
	private String playerEmail;
	private String id;
	private String elementPlayground;
	private String elementId;
	private String type;
	private static AtomicLong generator = new AtomicLong();
	private Map<String, Object> attributes;

	public ActivityTO() {
		this.attributes = new HashMap<>();
		this.attributes.put("isActive", "True");
		this.attributes.put("creatorsName", playerPlayground);
		this.attributes.put("activityName", "Post a Review");
		this.id = "" + generator.getAndIncrement();
	}

	public ActivityTO(ElementTO newElement, String type) {
		this();
		this.elementId = newElement.getId();
		this.elementPlayground = newElement.getPlayground();	
		this.type = type;
	}

	public String getPlayground() {
		return playground;
	}

	public void setPlayground(String playground) {
		validateNull(playground);
		this.playground = playground;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		validateNull(id);
		this.id = id;
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

	public void setElementId(String elementId) {
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

	public void setPlayerPlayground(String playerPlayground) {
		validateNull(playerPlayground);
		this.playerPlayground = playerPlayground;
	}

	public String getPlayerEmail() {
		return playerEmail;
	}

	public void setPlayerEmail(String playerEmail) {
		validateNull(playerEmail);
		this.playerEmail = playerEmail;
	}
	
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	public ActivityEntity toEntity() {
		ActivityEntity rv = new ActivityEntity();
		rv.setElementId(this.getElementId());
		rv.setElementPlayground(this.getElementPlayground());
		rv.setType(this.getType());
		rv.setPlayerEmail(this.getPlayerEmail());
		rv.setPlayerPlayground(this.playerPlayground);
		rv.setAttributes(this.attributes);
		rv.setUniqueKey(this.id + "@@" + this.playground);
		
		return rv;
	}

	@Override
	public String toString() {
		return "ActivityTO [playground=" + playground + ", id=" + id + ", elementPlayground=" + elementPlayground
				+ ", elementId=" + elementId + ", type=" + type + ", playerPlayground=" + playerPlayground
				+ ", playerEmail=" + playerEmail + ", attributes=" + attributes + "]";

	}
	
	private void validateNull(String string) throws RuntimeException {
		if ("null".equals(string) || string == null)
			throw new RuntimeException("One of the paramters provided was null");
	}
}