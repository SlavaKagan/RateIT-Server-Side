package playground.plugins;
import java.util.List;
public class MessagesList {
private List<String> messages;
	
	public MessagesList() {
	}
	
	public MessagesList(List<String> messages) {
		this.messages = messages;
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