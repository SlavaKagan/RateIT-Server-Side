package playground.plugins;
import java.util.List;
public class MessagesList {
	
	private List<String> messages;
	private long elementCount;
	
	public MessagesList() {
	}
	
	public MessagesList(List<String> messages, long elementCount) {
		this.messages = messages;
		this.elementCount = elementCount;
	}

	public long getElementCount() {
		return elementCount;
	}

	public void setElementCount(long elementCount) {
		this.elementCount = elementCount;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	@Override
	public String toString() {
		return "MessagesList [messages=" + messages + "]";
	}
}