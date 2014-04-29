package orz.yanagin.stragengine;


public class Data {

	private String uri;

	private String name;

	private String contentType;

	private byte[] data;

	public Data(String uri, String name, String contentType, byte[] data) {
		this.uri = uri;
		this.name = name;
		this.contentType = contentType;
		this.data = data;
	}

	public String getUri() {
		return uri;
	}

	public String getName() {
		return name;
	}

	public String getContentType() {
		return contentType;
	}

	public byte[] getData() {
		return data;
	}

}
