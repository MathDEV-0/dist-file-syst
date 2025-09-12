package entities;

public class Message {
	private String type; 
    private String sender;
    private String content;


	public Message(String type,String sender, String content) {
		this.type = type;
		this.sender = sender;
		this.content = content;
	}
	
	
	//G & S
	public String getType() {
		return type;
	}
	
	
	
	public void setType(String type) {
		this.type = type;
	}
	
	
	
	public String getSender() {
		return sender;
	}
	
	
	
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	
	
	public String getContent() {
		return content;
	}
	
	
	
	public void setContent(String content) {
		this.content = content;
	}


}