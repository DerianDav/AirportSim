package Client;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Creates the GUI for the sim and start the sim
 * @author Derian Davila Acuna
 *
 */
public class GUIMain extends Application{

	private Canvas canvas;
	private GraphicsContext gc;
	
	private static final int windowSize = 700;
	private static final int drawScale = windowSize/100;
	private static final int topBar = 0;
	private static final int milisecPerTick = 5;
	
	private static Airport airport;
	private static Random random;
	
	private static int globalTime;//seconds
	private static Label timeLabel;
	private static Label nextPlaneTime;
	private StackPane pane;
	private StackPane newPlanePane;
	private Scene scene;
	private MenuBar menu;
	private List<planeQ> planeList = new ArrayList<planeQ>();
	/**
	 * launches the GUI
	 */
	public static void main(String[] args) {
		random = new Random();
		airport = new Airport();
	//	for(int i = 0; i < 20; i++)
	//		airport.newPlane(random.nextInt(2));
		launch();
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		pane = new StackPane();
		canvas = new Canvas(windowSize, windowSize);
		gc = canvas.getGraphicsContext2D();
		pane.setAlignment(canvas, Pos.BOTTOM_CENTER);
		pane.getChildren().add(canvas);
		timeLabel = new Label("TIME: 00:00:00");
		timeLabel.setMinSize(100, 10);
		globalTime = 0;
		pane.setAlignment(timeLabel, Pos.TOP_RIGHT);
		

		nextPlaneTime = new Label("Next Plane At: 00:00:00");
		nextPlaneTime.setMinSize(100, 10);
		pane.setAlignment(nextPlaneTime, Pos.TOP_RIGHT);
		pane.setMargin(nextPlaneTime, new Insets(15,0,0,0));
		//pane.setMargin(time, value);
		setupMenuBar();
		pane.getChildren().add(timeLabel);
		pane.getChildren().add(nextPlaneTime);
		
/*
		window.setAlignment(shop, Pos.BOTTOM_RIGHT);
		window.setMargin(shop, new Insets(300,0,0,scaleSize*11));
*/		
		startAnimation();
		scene = new Scene(pane, windowSize, windowSize+topBar);
		stage.setScene(scene);
		stage.show();
		stage.setOnCloseRequest(event -> {
		    System.exit(0);//exits out of all the other threads
		});
	}
	
	
	/**
	 * Creates the menubar 
	 */
	private void setupMenuBar() {
		menu = new MenuBar();
		pane.setAlignment(menu, Pos.TOP_CENTER);
		pane.getChildren().add(menu);
		MenuItem newPlane = new MenuItem("New Plane");
		MenuItem randPlane = new MenuItem("New Random Planes");
		MenuItem randMulPlane = new MenuItem("Multiple Random Planes");
		MenuItem planesNow = new MenuItem("6 New Planes Now");
		Menu options = new Menu("Options");
		options.getItems().addAll(newPlane, randPlane, randMulPlane, planesNow);
		menu.getMenus().addAll(options);
		
		newPlane.setOnAction(event ->{
			newPlanePane = new newPlaneScreen();
			pane.getChildren().add(newPlanePane);});
		
		randPlane.setOnAction(event->{createPlane(-1,-1,-1, 1);});
		randMulPlane.setOnAction(event->{
			int randPlanes = random.nextInt(20);
			for(int i = 0; i < randPlanes; i++)
				createPlane(-1,-1,-1, 1);
		});
		
		planesNow.setOnAction(event->{
			for(int i = 0; i < 6; i++)
				airport.newPlane(random.nextInt(2));
		});
	}
	

	/**
	 * starts the timeline 
	 * runs mainloop every 20 miliseconds
	 */
	private void startAnimation() {
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(milisecPerTick), new mainLoop()));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}
	
	/** 
	 * mainLoop redraws the map and does a tick of the sim
	 */
	private class mainLoop implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent arg0) {
			gc.setFill(Color.WHITE);
			gc.fillRect(0, 0, windowSize, windowSize);
			airport.nextTick();
			airport.drawSimulation(gc, drawScale, topBar);
			globalTime++;
			int second = globalTime%60;
			int minute = (globalTime/60)%60;
			int hour = (globalTime/60/60)%60;
			if(hour >= 24) {
				globalTime -= 60*60*24;
				Collections.sort(planeList);
			}
			
			timeLabel.setText("TIME: " + timeToString(hour,minute,second));
			
			if(planeList.size() > 0) {
				while(planeList.size() > 0 && planeList.get(0).time == globalTime) {
					airport.newPlane(planeList.get(0).runway);
					planeList.remove(0);
				}
			//	if(planeList.get(0).time < globalTime)
			//		planeList.remove(0);
				second = planeList.get(0).time%60;
				minute = planeList.get(0).time/60%60;
				hour = planeList.get(0).time/60/60%60;
				nextPlaneTime.setText("Next Plane At: " + timeToString(hour,minute,second));
			}
			else {
				nextPlaneTime.setText("Next Plane At: " + timeToString(0,0,0));
			}
		}
	}

	/**
	 * stores info on the next plane to be created
	 */
	private class planeQ implements Comparable<planeQ>{
		int time;
		int runway;
		public planeQ(int time, int runway) {
			this.time = time; 
			this.runway = runway;
		}
		@Override
		public int compareTo(planeQ p) {
			int timeTemp, pTimeTemp;
			timeTemp = time;
			pTimeTemp = p.time;
			if(globalTime > time)
				timeTemp = time + 60*60*24;
			if(globalTime > p.time)
				pTimeTemp = p.time + 60*60*24;
			return timeTemp - pTimeTemp;
		}
	}
	
	/**
	 * creates a plane
	 * if hour == -1 then all the values are randomized
	 * @param hour
	 * @param minute
	 * @param runway - 0 = horizontal runway, 1 = vertical runway
	 * @param numbPlanes
	 */
	private void createPlane(int hour, int minute, int runway, int numbPlanes) {
		if(hour == -1) {
			int time = 0;
			time += random.nextInt(24)*60*60;
			time += random.nextInt(60)*60;
			planeList.add(new planeQ(time, random.nextInt(1)));
		}
		else {
			for(int i = 0; i < numbPlanes; i++)
				planeList.add(new planeQ(hour*60*60 + minute*60, runway));
		}
		Collections.sort(planeList);
		
	}
	
	/**
	 * converts the 3 ints to a string of hour:minute:second 
	 * if the value is < 10 then it become "01" if it was 1
	 * @param hour
	 * @param minute
	 * @param second
	 * @return a string showing the time
	 */
	private String timeToString(int hour, int minute, int second) {
		String secondS = "" + second;
		String minuteS = "" + minute;
		String hourS = "" + hour;
		if(second < 10)
			secondS = "0" + second;
		if(minute < 10)
			minuteS = "0" + minute;
		if(hour < 10)
			hourS = "0" + hour;
		return hourS + ":" + minuteS + ":" + secondS;
	}
	
	/**
	 * creates the screen when the user pressed new plane from the menu bar
	 * @author derian
	 */
	private class newPlaneScreen extends StackPane {
		public newPlaneScreen() {
			setMaxSize(400,400);
			Canvas canvas = new Canvas(400, 400);
			GraphicsContext gc = canvas.getGraphicsContext2D();
			gc.setFill(new Color(0.89,0.89,0.89,1));
			gc.fillRect(0, 0, 400, 400);
			getChildren().add(canvas);
			
			GridPane grid = new GridPane();
			Label hour = new Label("Hour");
			Label minute = new Label("Minute");
			Label runway = new Label("Runway To Takeoff");
			Label numbPlanes = new Label("Number of Planes");
			
			Font font = new Font("Ariel", 15);
			
			hour.setFont(font);
			minute.setFont(font);
			runway.setFont(font);
			
			TextField hourField = new TextField();
			TextField minuteField = new TextField();
			TextField runwayField = new TextField();
			TextField numbPlanesField = new TextField();
			
			grid.add(hour, 0, 0);
			grid.add(minute, 0, 1);
			grid.add(runway, 0, 2);
			grid.add(numbPlanes, 0, 3);
			grid.add(hourField, 1, 0);
			grid.add(minuteField, 1, 1);
			grid.add(runwayField, 1, 2);
			grid.add(numbPlanesField, 1, 3);
			
			setAlignment(grid, Pos.TOP_CENTER);
			setMargin(grid, new Insets(20,0,0,20));
			grid.setVgap(30);
			grid.setHgap(50);
			
			Button exit = new Button("Exit");
			setAlignment(exit, Pos.BOTTOM_LEFT);
			setMargin(exit, new Insets(0,0,20,40));
			
			Button createPlane = new Button("Create Plane");
			setAlignment(createPlane, Pos.BOTTOM_RIGHT);
			setMargin(createPlane, new Insets(0,40,20,0));
			
			exit.setOnAction(event -> {pane.getChildren().remove(newPlanePane);});
			createPlane.setOnAction(event ->{
				int hourText, minuteText, runwayText, numbPlanesText;
				try {
					hourText = Integer.parseInt(hourField.getText());
				}catch(NumberFormatException e) {hourText = -1;}
				try {
					minuteText = Integer.parseInt(minuteField.getText());
				}catch(NumberFormatException e) {minuteText = -1;}
				try {
					runwayText = Integer.parseInt(runwayField.getText());
				}catch(NumberFormatException e) {runwayText = -1;}
				try {
					numbPlanesText = Integer.parseInt(numbPlanesField.getText());
				}catch(NumberFormatException e) {numbPlanesText = -1;}
			
				boolean invalid = false;
				if(hourText < 0 || hourText > 23) {
					hourField.setText("Invalid Number");
					invalid = true;
				}
				if(minuteText < 0 || minuteText > 59) {
					minuteField.setText("Invalid Number");
					invalid = true;
				}
				if(runwayText < 0 || runwayText > 1) {
					runwayField.setText("Invalid Number");
					invalid = true;
				}
				if(numbPlanesText < 0 || runwayText > 1) {
					numbPlanesField.setText("Invalid Number");
					invalid = true;
				}
				if(invalid)
					return;
				createPlane(hourText,minuteText,runwayText, numbPlanesText);
				pane.getChildren().remove(this);
			});
			
			getChildren().addAll(grid, exit, createPlane);
		}
	}
	
}
