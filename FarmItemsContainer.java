package application;

import java.util.ArrayList;

public class FarmItemsContainer {
	private String subject;
	private double value;
	private int x_pole;
	private int y_pole;
	private int length;
	private int width;
	private int height;
	private boolean isParent;
	private String parentName;
	
	private ArrayList<FarmItem> itemsCollection = new ArrayList<FarmItem>();
	public FarmItemsContainer(String subject,int x_pole,int y_pole,int length, int width,int height, String parentName) {
		this.subject = subject;
		this.x_pole = x_pole;
		this.y_pole= y_pole;
		this.length = length;
		this.width = width;
		this.height = height;
		this.isParent = true;
		this.parentName = parentName;
	}
	
	public String getName() {
		return subject;
	}
	public void setName(String subject) {
		this.subject = subject;
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
	public void setLength(int length) {
		this.length = length;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public ArrayList<FarmItem> getItemsCollection() {
		return itemsCollection;
	}
	
	public void addItem(FarmItem item) {
		itemsCollection.add(item);
	}
	
	public void setItemsCollection(ArrayList<FarmItem> itemsCollection) {
		this.itemsCollection = itemsCollection;
	}
	public boolean isParent() {
		return isParent;
	}
	public void setParent(boolean isParent) {
		this.isParent = isParent;
	}
	
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	
	@Override
	public String toString() {
		return this.subject+","
				+this.value+","
				+this.x_pole+","
				+this.y_pole+","
				+this.length+","
				+this.width+","
				+this.height+","
				+this.parentName+","
				+"itemContainer";
		
	}

}
