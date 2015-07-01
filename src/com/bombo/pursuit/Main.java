package com.bombo.pursuit;
 
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
 
public class Main extends Application implements Observer {
	
	private GraphicsContext gc;
    
	public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage primaryStage) {
    	Parameters parameters = getParameters();
    	List<String> args = parameters.getRaw();
    	int size = 5; 
    	if(args.size()>0) {
    		try {
    			size = Integer.parseInt(args.get(0));
    		} catch (NumberFormatException e) {
    			System.err.println("The first argument must be an integer.");
    		}
    	}
    	
        primaryStage.setTitle("Drawing Operations Test");
        Group root = new Group();
        int canvaWidth = 700;
        Canvas canvas = new Canvas(canvaWidth, canvaWidth);
        
        gc = canvas.getGraphicsContext2D();
        double[][] points = strokePolygon(gc, 350, 350, size, 300);
        
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        
        PuirsuitThread pt = new PuirsuitThread(points, size, 15);
        pt.addObserver(this);
        new Thread(pt).start();
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
            	pt.stop();
            }
        });
    }
        
    private double[][] strokePolygon(GraphicsContext gc, int x, int y, int point, double width) {
   		gc.setFill(Color.BLACK);
   		gc.setStroke(Color.BLACK);
    	gc.setLineWidth(2);
    	double[] xPoints = new double[point];
    	double[] yPoints = new double[point];
    	for(int i = 0; i < point; i++) {
    		xPoints[i] = width * Math.cos(2*Math.PI*i/point) + x;
    		yPoints[i] = width * Math.sin(2*Math.PI*i/point) + y;
    	}
	   	gc.strokePolygon(xPoints, yPoints, point);
	   	return new double[][]{xPoints, yPoints};
    }
    
	@Override
	public void update(Observable o, Object arg) {
		Platform.runLater(new Runnable() {			
			@Override
			public void run() {
				if(arg instanceof List) {
					Point lastPoint = null;
					for(Object p : (List<?>)arg) {
						if(p instanceof Point) {
							gc.setFill(Color.GREEN);
					    	gc.fillOval(((Point) p).getX()-2.5, ((Point) p).getY()-2.5, 5, 5);
							if(lastPoint != null) {
								gc.strokeLine(lastPoint.getX(), lastPoint.getY(), ((Point) p).getX(), ((Point) p).getY());
							}
							lastPoint = (Point) p;
						}
					}
					gc.strokeLine(lastPoint.getX(), lastPoint.getY(), ((Point)((List<?>) arg).get(0)).getX(), ((Point)((List<?>) arg).get(0)).getY());
				}
			}
		});	
	}
    
}