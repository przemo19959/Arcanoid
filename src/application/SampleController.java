package application;

import java.util.List;

import application.drawer.MapGenerator;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;
import javafx.util.Duration;
import javafx.scene.layout.BorderPane;
import javafx.scene.Group;
import javafx.scene.control.Label;

public class SampleController {
	@FXML private Rectangle floor;
	@FXML private Rectangle ball;
	@FXML private BorderPane mainPane;
	
	private Timeline timer;
	private int ballXDir=-1;
	private int ballYDir=-2;
	
	private List<Rectangle> brickArray;
	private MapGenerator gen;
	@FXML private Group group;
	@FXML private Label textInfo;
	
	private final int floorLevel=600;
	private final int floorStep=20;
	@FXML Rectangle leftWall;
	@FXML Rectangle topWall;
	@FXML Rectangle rightWall;
	private boolean won=false;
	@FXML Label scoreInfo;
	private int wonScore=0;
	private int lostScore=0;
	private Window window;
	
	public void init() {
		gen=new MapGenerator(3, 7);
		drawBrickArray();
		
		window=mainPane.getScene().getWindow();
		
		//ruch pod³ogi
		window.addEventHandler(KeyEvent.KEY_PRESSED, (keyEvent)->{
			if(won) {
				drawBrickArray();
				newGameDefaultSetting();
			}
			if(keyEvent.getCode().toString().equals("RIGHT"))
				moveRightInBounds();
			if(keyEvent.getCode().toString().equals("LEFT"))
				moveLeftInBounds();
		});
		
		//ruch pod³ogi przez mysz
		window.addEventFilter(MouseEvent.MOUSE_MOVED, mouseEvent->{
			keepFloorInBounds(mouseEvent.getX());
		});
		
		//ruch kulki
		timer=new Timeline(new KeyFrame(Duration.millis(10), (event)->{
				checkForCollisionWithFloor();
				checkForBrickCollision();
				checkForVictory();
		
				ball.setX(ball.getX()+ballXDir);
				ball.setY(ball.getY()+ballYDir);
				
				checkForBallOutOfBox();
				checkForGameOver();
		}));
		timer.setCycleCount(Timeline.INDEFINITE);
	}
	
	//sprawdzenie czy zasz³a kolizja pi³ki z jak¹œ ceg³¹
	private void checkForBrickCollision() {
		for(int i=0;i<brickArray.size();i++) {
			if(ball.intersects(brickArray.get(i).getBoundsInParent())) {
				int indexOfBrickToDelete=group.getChildren().indexOf(brickArray.get(i));
				group.getChildren().remove(indexOfBrickToDelete);
				
				double brickWidth=brickArray.get(i).getWidth();
				double brickX=brickArray.get(i).getX();
				
				if(ball.getX()>brickX && ball.getX()<brickX+brickWidth)
					ballYDir=-ballYDir;
				else if(ball.getX()+ball.getWidth()==brickX || ball.getX()==brickX+brickWidth)
					ballXDir=-ballXDir;
				
				brickArray.remove(brickArray.get(i));
				break;
			}
		}
	}
	
	//przywróc domyœlne wartoœci
	private void newGameDefaultSetting() {
		ball.setX(180);
		ball.setY(350);
		ballXDir=-1;
		ballYDir=-2;
		won=false;
	}
	
	//dodaj punkt wygranej
	private void addWonScore() {
		scoreInfo.setText("Won: "+(++wonScore)+" Lost: "+lostScore);
	}
	
	//dodaj punkt przegranej i posprz¹taj planszê
	private void addLostScore() {
		scoreInfo.setText("Won: "+wonScore+" Lost: "+(++lostScore));
		int indexOfBrickToDelete=0;
		for(int i=0;i<brickArray.size();i++) {
			indexOfBrickToDelete=group.getChildren().indexOf(brickArray.get(i));
			group.getChildren().remove(indexOfBrickToDelete);
		}
		brickArray.clear();
	}
	
	//narysuj macierz cegie³
	private void drawBrickArray() {
		brickArray=gen.generate();
		group.getChildren().addAll(brickArray);
	}
	
	//sprawdzenie czy gracz zbi³ wszystkie cegie³ki
	private void checkForVictory() {
		if(brickArray.size()==0) {	//jeœli iloœæ cegie³ek == 0
			timer.stop();	//zatrzymaj timer
			won=true;
			addWonScore();
			textInfo.setText("You Won\nClick to start new game");	//wyœwietl wiadomoœæ
			textInfo.setVisible(true);
		}
	}
	
	//zmiana kierunku pi³ki, gdy wejdziew kontakt ze œcianami
	private void checkForBallOutOfBox() {
		if(ball.getX()<leftWall.getWidth())
			ballXDir=-ballXDir;
		if(ball.getY()<topWall.getHeight())
			ballYDir=-ballYDir;
		if(ball.getX()+ball.getWidth()>rightWall.getX())
			ballXDir=-ballXDir;
	}
	
	//jeœli kulka dotknie pod³ogi, zmieñ kierunek na przeciwny (symulacja odbicia)
	private void checkForCollisionWithFloor() {
		if(ball.intersects(floor.getBoundsInParent()))
			ballYDir=-ballYDir;
	}
	
	//jeœli kulka spadnie poni¿ej poziomu pod³ogi
	private void checkForGameOver() {
		if(ball.getY()+ball.getHeight()>floorLevel) {
			timer.stop();
			addLostScore();
			won=true; //nie wygra³, ale chodzi o sam¹ flagê
			textInfo.setText("Game Over\nClick to start new game");
			textInfo.setVisible(true);
		}
	}
	
	//kontrola 
	private void keepFloorInBounds(double newValue) {
		if(newValue>rightWall.getX()-floor.getWidth())
			floor.setX(rightWall.getX()-floor.getWidth());
		else if(newValue<leftWall.getX()+leftWall.getWidth())
			floor.setX(leftWall.getWidth());
		else
			floor.setX(newValue);
	}
	
	//ruch pod³ogi w prawo w granicach planszy
	private void moveRightInBounds() {
		if(floor.getX()+floor.getWidth()+floorStep>=rightWall.getX())
			floor.setX(rightWall.getX()-floor.getWidth());
		else
			moveRight();		
	}
	
	//ruch pod³ogi w lewo w granicach planszy
	private void moveLeftInBounds() {
		if(floor.getX()-floorStep<=leftWall.getX()+leftWall.getWidth())
			floor.setX(leftWall.getWidth());
		else
			moveLeft();
	}
	
	//ruch pod³ogi w prawo
	private void moveRight() {
		if(timer.getStatus().equals(Animation.Status.STOPPED)) {
			timer.playFromStart();
			textInfo.setVisible(false);
		}
		floor.setX(floor.getX()+floorStep);
	}
	
	//ruch pod³ogi w lewo
	private void moveLeft() {
		if(timer.getStatus().equals(Animation.Status.STOPPED)) {
			timer.playFromStart();
			textInfo.setVisible(false);
		}
		floor.setX(floor.getX()-floorStep);
	}
}
