
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GUIMain extends Application{

	private Canvas canvas;
	private GraphicsContext gc;

	private static final int windowSize = 700;
	private static final int drawScale = windowSize/100;
	
	private static Airport airport;
	private static Random random;
	
	/**
	 * launches the GUI
	 */
	public static void main(String[] args) {
		random = new Random();
		airport = new Airport();
		for(int i = 0; i < 3; i++)
			airport.newPlane(random.nextInt(2));
		launch();
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		BorderPane pane = new BorderPane();
		canvas = new Canvas(windowSize, windowSize);
		gc = canvas.getGraphicsContext2D();
		pane.setLeft(canvas);
		startAnimation();
		Scene scene = new Scene(pane, windowSize, windowSize);
		stage.setScene(scene);;
		stage.show();
		stage.setOnCloseRequest(event -> {
		    System.exit(0);//exits out of all the other threads
		});
	}
	
	

	/**
	 * starts the timeline 
	 * runs mainloop every 20 miliseconds
	 */
	private void startAnimation() {
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(3), new mainLoop()));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}
	
	/** 
	 * mainLoop redraws the bodies on a white background
	 */
	private class mainLoop implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent arg0) {
			gc.setFill(Color.WHITE);
			gc.fillRect(0, 0, windowSize, windowSize);
			airport.nextTick();
			airport.drawSimulation(gc, drawScale);
		}
	}

	
}
