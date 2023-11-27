package application;

public class FarmItem {
	private String subject;
	private int x_pole;
	private int y_pole;
	private int length;
	private int width;
	private int height;
	
	public FarmItem(String subject,int x_pole,int y_pole,int length, int width,int height,double marketVal,String parentName) {
		this.subject = subject;
		this.x_pole = x_pole;
		this.y_pole= y_pole;
		this.length = length;
		this.width = width;
		this.height = height;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	public int getWidth() {
		return width;
	}
	public String getName() {
		return subject;
	}
	public void setName(String subject) {
		this.subject = subject;
	}
	
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getx_pole() {
		return x_pole;
	}
	public void setx_pole(int x_pole) {
		this.x_pole = x_pole;
	}
	public int gety_pole() {
		return y_pole;
	}
	public void sety_pole(int y_pole) {
		this.y_pole = y_pole;
	}
	public int getLength() {
		return length;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	

}
