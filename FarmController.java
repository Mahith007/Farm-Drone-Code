package application;

import java.io.IOException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class FarmController {
	
	@FXML private Pane bottomPane;
	@FXML private TreeView<String> treeView;
	@FXML private ListView<String> listCtrl;
	private TreeItem<String> rootItem = new TreeItem<String>("Root");
	@FXML private ImageView myDroneImage;
	private ArrayList<FarmItemsContainer> itemsContainerCollection = new ArrayList<FarmItemsContainer>();
	private static FarmController INSTANCE;
	
	@FXML
	private void initialize() {
		treeView.setRoot(rootItem);
	}
	
	@FXML
	public TreeItem<String> selectItem() {
		return (treeView.getSelectionModel().getSelectedItem());
	}
	
	public static FarmController getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new FarmController();
		}
		return INSTANCE;
	}
	
	public void setValuesInitialize() {
		MultipleSelectionModel<TreeItem<String>> msm = treeView.getSelectionModel();
		addDefaultItemsToRootTree();
		rootItem.setExpanded(true);
		int row = treeView.getRow( rootItem );
		treeView.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent arg0) {
				showSelectedItemCommands();
			}
		});
		listCtrl.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				String selectedCommand = listCtrl.getSelectionModel().getSelectedItem();
				if(selectedCommand != null) {
					if(!selectedCommand.contains("Commands")) {
						checkCommandsAndDoFn(selectedCommand);
					}
				}	
			}
		});
		msm.select(row);
		showSelectedItemCommands();
		listCtrl.setVisible(true);
	}
	
	public void addToTree(FarmItem item,FarmItemsContainer container,String parentName) {
		String name;
		if(container != null) {
			name = container.getName();
		}else {
			name = item.getName();
		}
		if((rootItem.getChildren().size() == 0 && container != null)|| parentName.equals("Root")) {
			TreeItem<String> containerName = new TreeItem<>(name);
			rootItem.getChildren().add(containerName);
		}else {
			boolean isFound = false;
			for (TreeItem<String> depNode : rootItem.getChildren()) {
				if (depNode.getValue().contentEquals(parentName)){
					TreeItem<String> containerName = new TreeItem<>(name);
					depNode.getChildren().add(containerName);
					isFound = true;
	                break;
				}
			}
			if(!isFound) {
				for (TreeItem<String> depNode : rootItem.getChildren()) {
					iterateAndAdd(depNode,name, parentName);
				}
			}
		}
		
	}
	
	private void iterateAndAdd(TreeItem<String> node, String name, String parentName) {
		boolean isFound = false;
		for (TreeItem<String> depNodeChild :node.getChildren()) {
			if (depNodeChild.getValue().contentEquals(parentName)){
				TreeItem<String> containerName = new TreeItem<>(name);
				depNodeChild.getChildren().add(containerName);
				isFound = true;
                break;
			}
		}
		if(!isFound) {
			for (TreeItem<String> depNodeChild :node.getChildren()) {
				iterateAndAdd(depNodeChild,name, parentName);
			}
		}
	}
	
	public void showSelectedItemCommands() {
		listCtrl.setVisible(false);
		listCtrl.getItems().clear();
		boolean isParent =false;
		for (FarmItemsContainer itemsCont : getItemsContainerCollection()) {
			if(selectItem().getValue().equals(itemsCont.getName())) {
				isParent = true;
			}
		}
		if(selectItem() != null && (selectItem().getValue().equals("Root") || isParent)) {
			if(!selectItem().getValue().equals("Root")) {
				listCtrl.getItems().add("Item Container Commands");
				if(!selectItem().getValue().equals("Command Center")) {
					listCtrl.getItems().add("Rename");
				}
				listCtrl.getItems().add("Change Location X");
				listCtrl.getItems().add("Change Location Y");
				listCtrl.getItems().add("Change Price");
				listCtrl.getItems().add("Change Width");
				listCtrl.getItems().add("Change Height");
				listCtrl.getItems().add("Add Item");
			}else {;
				listCtrl.getItems().add("Item Root Commands");
			}
			listCtrl.getItems().add("Add Item Container");
			if(!selectItem().getValue().equals("Root") && !selectItem().getValue().equals("Command Center")) {
				listCtrl.getItems().add("Delete");
			}
		} else {
			listCtrl.getItems().add("Item Commands");
			if(!selectItem().getValue().equals("Drone")) {
				listCtrl.getItems().add("Rename");
			}
			
			listCtrl.getItems().add("Change Location X");
			listCtrl.getItems().add("Change Location Y");
			listCtrl.getItems().add("Change Price");
			listCtrl.getItems().add("Change Market Value");
			listCtrl.getItems().add("Change Width");
			listCtrl.getItems().add("Change Height");
			if(!selectItem().getValue().equals("Drone")) {
				listCtrl.getItems().add("Delete");
			}
		}
		listCtrl.setVisible(true);
	}
	
	public FarmItemsContainer createItemContainerObject(String name,double price,int x_pole,int y_pole,int length, int width,int height,String parentName) {
		FarmItemsContainer eachItemsContainer = new FarmItemsContainer(name,x_pole,y_pole,y_pole,width,height,parentName);
		getItemsContainerCollection().add(eachItemsContainer);
		return eachItemsContainer;
	}

	public FarmItem createItemObject(String name,int x_pole,int y_pole,int length, int width,int height,FarmItemsContainer container, double marketVal) {
		FarmItem eachItem = new FarmItem(name,x_pole,y_pole,y_pole,width,height,marketVal,container.getName());
		container.addItem(eachItem);
		return eachItem;
	}
	
	public void addDefaultItemsToRootTree() {
		FarmItemsContainer container = createItemContainerObject("Command Center",1000.00, 350,20,10,200,150,"Root");
		addSubNode(container,false);
		FarmItem eachItem = createItemObject("Drone",400,60,10,100,100, container, 500);
		addChildNode(container, eachItem);
		renderTheCharts();
	}
	
	
	private void renderTheCharts() {
		bottomPane.getChildren().removeAll(bottomPane.getChildren());
		ArrayList<Rectangle> rect = new ArrayList<Rectangle>();
		ArrayList<Text> Tex = new ArrayList<Text>();
		for (FarmItemsContainer itemsCont : itemsContainerCollection) {
			Rectangle rect2 = new Rectangle(itemsCont.getx_pole(), itemsCont.gety_pole(), itemsCont.getWidth(), itemsCont.getHeight());
	        rect2.setFill(Color.TRANSPARENT);
	        rect2.setStroke(Color.RED);
	        rect2.setStrokeWidth(1);
	        rect.add(rect2);
	        Text reqText = new Text(itemsCont.getx_pole()+10,itemsCont.gety_pole()+20,itemsCont.getName() );
	        Tex.add(reqText);
	        if(itemsCont.getItemsCollection().size() ==0 ) {
//	        	rightPane.getChildren().addAll(rect2,reqText);
	        } else {
	        	for (FarmItem itemEach : itemsCont.getItemsCollection()) {
	        		Rectangle rect22 = new Rectangle(itemEach.getx_pole(), itemEach.gety_pole(), itemEach.getWidth(), itemEach.getHeight());
			        rect22.setFill(Color.TRANSPARENT);
			        rect22.setStroke(Color.RED);
			        rect22.setStrokeWidth(1);
			        rect.add(rect22);
			        Text reqText2 = new Text(itemEach.getx_pole()+10,itemEach.gety_pole()+20,itemEach.getName() );
			        Tex.add(reqText2);
					if(itemEach.getName().equals("Drone")) {
			        	Image imageObject = new Image(getClass().getResourceAsStream("drone.png"));
			        	myDroneImage = new ImageView(imageObject);
			        	myDroneImage.setX(itemEach.getx_pole()+20);
			        	myDroneImage.setY(itemEach.gety_pole()+30);
			        	myDroneImage.setFitWidth(50);
			        	myDroneImage.setPreserveRatio(true);
			        	bottomPane.getChildren().add(myDroneImage);
			        }

		        }
	        }

			
		}
		for(Rectangle r: rect) {
			bottomPane.getChildren().add(r);
		}
		for(Text rTex: Tex) {
			bottomPane.getChildren().add(rTex);
		}
		
	}
	
	
	public TreeItem<String> addSubNode(FarmItemsContainer container,boolean notInital) {
		TreeItem<String> containerName = null;
		boolean isFound = false;
		if(notInital && !selectItem().getValue().equals("Root") ) {
			for (TreeItem<String> depNode : rootItem.getChildren()) {
				if (depNode.getValue().contentEquals(selectItem().getValue())){
					isFound = true;
					containerName = new TreeItem<>(container.getName());
					depNode.getChildren().add(containerName);
	                break;
				}
			}
			if(!isFound && selectItem().getParent() != null) {
				for (TreeItem<String> depNode : selectItem().getParent().getChildren()) {
					if (depNode.getValue().contentEquals(selectItem().getValue())){
						isFound = true;
						containerName = new TreeItem<>(container.getName());
						depNode.getChildren().add(containerName);
		                break;
					}
				}	
			}
		}else {
			containerName = new TreeItem<>(container.getName());
			rootItem.getChildren().add(containerName);
		}
		
//		
		return containerName;
	}
	
	
	public TreeItem<String> addChildNode(FarmItemsContainer container, FarmItem ReqItemNode) {
		TreeItem<String> childNodeName =  null;
		if(selectItem() !=null && selectItem().getParent() != null) {
			for (TreeItem<String> depNode : selectItem().getParent().getChildren()) {
				if (depNode.getValue().contentEquals(container.getName())){
					childNodeName = new TreeItem<>(ReqItemNode.getName());
	                depNode.getChildren().add(childNodeName);
	                break;
	            }
			}
		}else {
			for (TreeItem<String> depNode : rootItem.getChildren()) {
				if (depNode.getValue().contentEquals(container.getName())){
					childNodeName = new TreeItem<>(ReqItemNode.getName());
	                depNode.getChildren().add(childNodeName);
	                break;
	            }
			}
		}
		
		return childNodeName;
	}
	
	public void checkCommandsAndDoFn(String command) {
		if(command.contains("Rename")) {
			renameTheNode();
		}else if (command.contains("Location X")) {
			changeCoordinatesX();
		}else if (command.contains("Location Y")) {
			changeCoordinatesY();
		}else if (command.contains("Price")) {
		}else if(command.contains("Width")) {
			changeWidth();
		}else if(command.contains("Height")) {
			changeHeight();
		} else if(command.equals("Add Item")) {
			addChildItem();
		} else if(command.equals("Add Item Container")) {
			addContainerItem();
		} else if(command.contains("Delete")) {
			deleteItem();
		}else if(command.contains("Market Value")) {
		}
	}
	
	private void renameTheNode() {
		boolean isParent =false;
		FarmItemsContainer itemContainer = null;
		FarmItem selectedItem = null;
		for (FarmItemsContainer itemsCont : getItemsContainerCollection()) {
			if(selectItem().getValue().equals(itemsCont.getName())) {
				isParent = true;
				itemContainer = itemsCont;
			}
		}
		if(!isParent) {
			String parentVal = selectItem().getParent().getValue();
			for (FarmItemsContainer itemsConter : getItemsContainerCollection()) {
				if(parentVal.equals(itemsConter.getName())) {
					itemContainer = itemsConter;
				}
			}
			if(itemContainer != null) {
				for (FarmItem itemEach : itemContainer.getItemsCollection()) {
					if(selectItem().getValue().equals(itemEach.getName())) {
						selectedItem = itemEach;
					}
				}
			}	
			
		}
		TextInputDialog popup = new TextInputDialog(selectItem().getValue());
		popup.setHeaderText("Enter the updated name");
		popup.showAndWait();
		String updatedName = popup.getEditor().getText();
		if(updatedName != null) {
			if(selectedItem !=null || itemContainer !=null) {
				if(isParent) {
					itemContainer.setName(updatedName);
					selectItem().setValue(itemContainer.getName());
				} else {
					selectedItem.setName(updatedName);
					selectItem().setValue(selectedItem.getName());
				}
			}
		}
		renderTheCharts();
	}

	private void deleteItem() {
		boolean isParent =false;
		int counter =0;
		for (FarmItemsContainer itemsCont : getItemsContainerCollection()) {
			if(selectItem().getValue().equals(itemsCont.getName())) {
				isParent = true;
				this.itemsContainerCollection.remove(counter);
			}
			counter++;
		}
		if(!isParent) {
			int increment = 0;
			for (FarmItemsContainer itemsConter : getItemsContainerCollection()) {
				for (FarmItem itemEach : itemsConter.getItemsCollection()) {
					if(selectItem().getValue().equals(itemEach.getName())) {
						itemsConter.getItemsCollection().remove(increment);
					}
					increment++;
				}
				
			}
			
		}
		
		for (FarmItemsContainer itemsCont : getItemsContainerCollection()) {
			System.out.println("Delete parents size: "+getItemsContainerCollection().size() );
			System.out.println("Delete childern size: "+itemsCont.getItemsCollection().size() );
		}
		
		selectItem().getParent().getChildren().remove(selectItem());
		listCtrl.setVisible(false);
		renderTheCharts();
	}

	private void addContainerItem() {
		MultipleSelectionModel<TreeItem<String>> msm = treeView.getSelectionModel();
		TextInputDialog popup1 = new TextInputDialog("");
		TextInputDialog popup2 = new TextInputDialog("0");
		TextInputDialog popup3 = new TextInputDialog("0");
		TextInputDialog popup4 = new TextInputDialog("0");
		TextInputDialog popup5 = new TextInputDialog("0");
		popup1.setHeaderText("Enter container name");
		popup2.setHeaderText("Enter X Co-oridnate");
		popup3.setHeaderText("Enter Y Co-oridnate");
		popup4.setHeaderText("Enter Width");
		popup5.setHeaderText("Enter Height");
		popup1.showAndWait();
		popup2.showAndWait();
		popup3.showAndWait();
		popup4.showAndWait();
		popup5.showAndWait();
		String name = popup1.getEditor().getText();
		int xCord = Integer.valueOf(popup2.getEditor().getText());
		int yCord = Integer.valueOf(popup3.getEditor().getText());
		int width = Integer.valueOf(popup4.getEditor().getText());
		int height = Integer.valueOf(popup5.getEditor().getText());
		FarmItemsContainer container = createItemContainerObject(name,0, xCord,yCord,0,width,height,selectItem().getValue());
		TreeItem<String> addedItem = addSubNode(container,true);
		selectItem().setExpanded(true);
		int row = treeView.getRow(addedItem);
		msm.select(row);
		showSelectedItemCommands();
		renderTheCharts();
	}

	private void addChildItem() {
		MultipleSelectionModel<TreeItem<String>> msm = treeView.getSelectionModel();
		TextInputDialog popup1 = new TextInputDialog("");
		TextInputDialog popup2 = new TextInputDialog("0");
		TextInputDialog popup3 = new TextInputDialog("0");
		TextInputDialog popup4 = new TextInputDialog("0");
		TextInputDialog popup5 = new TextInputDialog("0");
		

		popup1.setHeaderText("Enter container name");
		popup2.setHeaderText("Enter X Co-oridnate");
		popup3.setHeaderText("Enter Y Co-oridnate");
		popup4.setHeaderText("Enter Width");
		popup5.setHeaderText("Enter Height");		

		popup1.showAndWait();
		popup2.showAndWait();
		popup3.showAndWait();
		popup4.showAndWait();
		popup5.showAndWait();
		
		String name = popup1.getEditor().getText();
		int xCord = Integer.valueOf(popup2.getEditor().getText());
		int yCord = Integer.valueOf(popup3.getEditor().getText());
		int width = Integer.valueOf(popup4.getEditor().getText());
		int height = Integer.valueOf(popup5.getEditor().getText());
		FarmItemsContainer container = null;
		for (FarmItemsContainer itemsCont : getItemsContainerCollection()) {
			if(selectItem().getValue().equals(itemsCont.getName())) {
				container = itemsCont;
			}
		}
		if(container != null) {
			FarmItem eachItem = createItemObject(name, xCord,yCord,0,width,height, container,0);
			TreeItem<String>addedItem =addChildNode(container, eachItem);
			selectItem().setExpanded(true);
			
			int row = treeView.getRow(addedItem);
			
			
			msm.select(row);
			showSelectedItemCommands();
		}
		renderTheCharts();
	}

	private void changeWidth() {
		boolean isParent =false;
		FarmItemsContainer itemContainer = null;
		FarmItem selectedItem = null;
		for (FarmItemsContainer itemsCont : getItemsContainerCollection()) {
			if(selectItem().getValue().equals(itemsCont.getName())) {
				isParent = true;
				itemContainer = itemsCont;
			}
		}
		
		if(!isParent) {
			String parentVal = selectItem().getParent().getValue();
			for (FarmItemsContainer itemsConter : getItemsContainerCollection()) {
				if(parentVal.equals(itemsConter.getName())) {
					itemContainer = itemsConter;
				}
			}
			if(itemContainer != null) {
				for (FarmItem itemEach : itemContainer.getItemsCollection()) {
					if(selectItem().getValue().equals(itemEach.getName())) {
						selectedItem = itemEach;
					}
				}
			}	
			
		}
		if(selectedItem !=null || itemContainer !=null) {
			if(isParent) {
				TextInputDialog popup2 = new TextInputDialog(String.valueOf(itemContainer.getWidth()));
				popup2.setHeaderText("Enter Width");
				popup2.showAndWait();
				int width = Integer.valueOf(popup2.getEditor().getText());
				itemContainer.setWidth(width);
				selectItem().setValue(itemContainer.getName());
			} else {
				TextInputDialog popup2 = new TextInputDialog(String.valueOf(selectedItem.getWidth()));
				popup2.setHeaderText("Enter Width");
				popup2.showAndWait();
				int width = Integer.valueOf(popup2.getEditor().getText());
				selectedItem.setWidth(width);
				selectItem().setValue(selectedItem.getName());
			}
		}
		renderTheCharts();
	}
	private void changeHeight() {
		boolean isParent =false;
		FarmItemsContainer itemContainer = null;
		FarmItem selectedItem = null;
		for (FarmItemsContainer itemsCont : getItemsContainerCollection()) {
			if(selectItem().getValue().equals(itemsCont.getName())) {
				isParent = true;
				itemContainer = itemsCont;
			}
		}
		
		if(!isParent) {
			String parentVal = selectItem().getParent().getValue();
			for (FarmItemsContainer itemsConter : getItemsContainerCollection()) {
				if(parentVal.equals(itemsConter.getName())) {
					itemContainer = itemsConter;
				}
			}
			if(itemContainer != null) {
				for (FarmItem itemEach : itemContainer.getItemsCollection()) {
					if(selectItem().getValue().equals(itemEach.getName())) {
						selectedItem = itemEach;
					}
				}
			}	
			
		}
		if(selectedItem !=null || itemContainer !=null) {
			if(isParent) {
				TextInputDialog popup2 = new TextInputDialog(String.valueOf(itemContainer.getHeight()));
				popup2.setHeaderText("Enter Height");
				popup2.showAndWait();
				int height = Integer.valueOf(popup2.getEditor().getText());
				itemContainer.setHeight(height);
				selectItem().setValue(itemContainer.getName());
			} else {
				TextInputDialog popup2 = new TextInputDialog(String.valueOf(selectedItem.getHeight()));
				popup2.setHeaderText("Enter Height");
				popup2.showAndWait();
				int height = Integer.valueOf(popup2.getEditor().getText());
				selectedItem.setHeight(height);
				selectItem().setValue(selectedItem.getName());
			}
		}
		renderTheCharts();
		
	}
	
	
	private void changeCoordinatesX() {
		boolean isParent =false;
		FarmItemsContainer itemContainer = null;
		FarmItem selectedItem = null;
		for (FarmItemsContainer itemsCont : getItemsContainerCollection()) {
			if(selectItem().getValue().equals(itemsCont.getName())) {
				isParent = true;
				itemContainer = itemsCont;
			}
		}
		
		if(!isParent) {
			String parentVal = selectItem().getParent().getValue();
			for (FarmItemsContainer itemsConter : getItemsContainerCollection()) {
				if(parentVal.equals(itemsConter.getName())) {
					itemContainer = itemsConter;
				}
			}
			if(itemContainer != null) {
				for (FarmItem itemEach : itemContainer.getItemsCollection()) {
					if(selectItem().getValue().equals(itemEach.getName())) {
						selectedItem = itemEach;
					}
				}
			}	
			
		}
		if(selectedItem !=null || itemContainer !=null) {
			if(isParent) {
				TextInputDialog popup2 = new TextInputDialog(String.valueOf(itemContainer.getx_pole()));
				popup2.setHeaderText("Enter X Coordinate");
				popup2.showAndWait();
				int xCordiante = Integer.valueOf(popup2.getEditor().getText());
				itemContainer.setx_pole(xCordiante);
				selectItem().setValue(itemContainer.getName());
			} else {
				TextInputDialog popup2 = new TextInputDialog(String.valueOf(selectedItem.getx_pole()));
				popup2.setHeaderText("Enter X Coordinate");
				popup2.showAndWait();
				int xCordiante = Integer.valueOf(popup2.getEditor().getText());
				selectedItem.setx_pole(xCordiante);
				selectItem().setValue(selectedItem.getName());
			}
		}
		renderTheCharts();
		
	}
	
	private void changeCoordinatesY() {
		boolean isParent =false;
		FarmItemsContainer itemContainer = null;
		FarmItem selectedItem = null;
		for (FarmItemsContainer itemsCont : getItemsContainerCollection()) {
			if(selectItem().getValue().equals(itemsCont.getName())) {
				isParent = true;
				itemContainer = itemsCont;
			}
		}
		
		if(!isParent) {
			String parentVal = selectItem().getParent().getValue();
			for (FarmItemsContainer itemsConter : getItemsContainerCollection()) {
				if(parentVal.equals(itemsConter.getName())) {
					itemContainer = itemsConter;
				}
			}
			if(itemContainer != null) {
				for (FarmItem itemEach : itemContainer.getItemsCollection()) {
					if(selectItem().getValue().equals(itemEach.getName())) {
						selectedItem = itemEach;
					}
				}
			}	
			
		}
		if(selectedItem !=null || itemContainer !=null) {
			if(isParent) {
				TextInputDialog popup2 = new TextInputDialog(String.valueOf(itemContainer.gety_pole()));
				popup2.setHeaderText("Enter Y Coordinate");
				popup2.showAndWait();
				int yCordiante = Integer.valueOf(popup2.getEditor().getText());
				itemContainer.sety_pole(yCordiante);
				selectItem().setValue(itemContainer.getName());
			} else {
				TextInputDialog popup2 = new TextInputDialog(String.valueOf(selectedItem.gety_pole()));
				popup2.setHeaderText("Enter Y Coordinate");
				popup2.showAndWait();
				int yCordiante = Integer.valueOf(popup2.getEditor().getText());
				selectedItem.sety_pole(yCordiante);
				selectItem().setValue(selectedItem.getName());
			}
		}
		renderTheCharts();
		
	}
	
	public void visitItemDrone(ActionEvent e) throws IOException {
		boolean isParent =false;
		FarmItemsContainer itemContainer = null;
		FarmItem selectedItem = null;
		int droneX =0;
		int droneY =0;
		for (FarmItemsContainer itemsCont : getItemsContainerCollection()) {
			if(selectItem().getValue().equals(itemsCont.getName())) {
				isParent = true;
				itemContainer = itemsCont;
			}
			for (FarmItem itemEach : itemsCont.getItemsCollection()) {
				if(itemEach.getName().equals("Drone")) {
					droneX = itemEach.getx_pole();
					droneY = itemEach.gety_pole();
				}
			}
		}
		
		if(!isParent) {
			String parentVal = selectItem().getParent().getValue();
			for (FarmItemsContainer itemsConter : getItemsContainerCollection()) {
				if(parentVal.equals(itemsConter.getName())) {
					itemContainer = itemsConter;
				}
			}
			if(itemContainer != null) {
				for (FarmItem itemEach : itemContainer.getItemsCollection()) {
					if(selectItem().getValue().equals(itemEach.getName())) {
						selectedItem = itemEach;
					}
				}
			}	
			
		}
		if(selectedItem !=null || itemContainer !=null) {
			if(isParent) {
				int xCord = itemContainer.getx_pole();
				int yCord = itemContainer.gety_pole();
				DroneAnimation visitDroneObj = new DroneAnimation(myDroneImage); 
				visitDroneObj.visitDroneAnimation(xCord, yCord, droneX, droneY);
				
			} else {
				int xCord = selectedItem.getx_pole();
				int yCord = selectedItem.gety_pole();
				DroneAnimation visitDroneObj = new DroneAnimation(myDroneImage); 
				visitDroneObj.visitDroneAnimation(xCord, yCord, droneX, droneY);
			}
		}
	}
	
	public void goHomeDrone(ActionEvent e) {
		FarmItemsContainer itemsCont = getItemsContainerCollection().get(0);
		FarmItem itemEach = itemsCont.getItemsCollection().get(0);
		if(itemEach != null && itemEach.getName().equals("Drone")) {
			int xCord = itemEach.getx_pole();
			int yCord = itemEach.gety_pole();
			myDroneImage.setX(xCord+10);
			myDroneImage.setY(yCord+10);
		}    
	}
	
	public void scanFarmDrone(ActionEvent e) throws IOException {
		goHomeDrone(e);
		DroneAnimation droneObj = new DroneAnimation(myDroneImage);
		droneObj.scanWholeFarm();
	}
	
	public ArrayList<FarmItemsContainer> getItemsContainerCollection() {
		return itemsContainerCollection;
	}

	public void setItemsContainerCollection(ArrayList<FarmItemsContainer> itemsContainerCollection) {
		this.itemsContainerCollection = itemsContainerCollection;
	}
	

}
