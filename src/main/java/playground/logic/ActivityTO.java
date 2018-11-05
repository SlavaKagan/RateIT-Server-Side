package playground.logic;

import java.util.HashMap;
import java.util.Map;

public class ActivityTO implements Constants {
	private String playground;
	private String id;
	private String elementPlayground;
	private String elementId;
	private String type;
	private String playerPlayground;
	private String playerEmail;
	private Map<String, Object> attributes;

	public ActivityTO() {
		this.playground = PLAYGROUND;
		this.playerPlayground = REVIEWER;
		this.playerEmail = PLAYER_MAIL;
		this.id = this.playerEmail.hashCode() + "";
		this.attributes = new HashMap<>();
		this.attributes.put("isActive", "True");
		this.attributes.put("creatorsName", REVIEWER);
		this.attributes.put("activity", new Object());
		this.attributes.put("activityName", "Post a Review");
	}

	public ActivityTO(ElementTO newElement, String type) {	
		this.elementId = newElement.getId();
		this.elementPlayground = newElement.getPlayground();	
		this.type = type;
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

	public String getElementPlayground() {
		return elementPlayground;
	}

	public void setElementPlayground(String elementPlayground) {
		this.elementPlayground = elementPlayground;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
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

	public void setPlayerPlayground(String playerPlayground) {
		this.playerPlayground = playerPlayground;
	}

	public String getPlayerEmail() {
		return playerEmail;
	}

	public void setPlayerEmail(String playerEmail) {
		this.playerEmail = playerEmail;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		return "ActivityTO [playground=" + playground + ", id=" + id + ", elementPlayground=" + elementPlayground
				+ ", elementId=" + elementId + ", type=" + type + ", playerPlayground=" + playerPlayground
				+ ", playerEmail=" + playerEmail + ", attributes=" + attributes + "]";

	}

	public void setParams(ActivityTO newActivity) {
		this.playground = newActivity.playground;
		this.id = newActivity.id;
		this.elementPlayground = newActivity.elementPlayground;
		this.elementId = newActivity.elementId;
		this.type = newActivity.type;
		this.playerPlayground = newActivity.playerPlayground;
		this.playerEmail = newActivity.playerEmail;
		this.attributes = newActivity.attributes;
	}
}