package application.drawer;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MapGenerator {
	private int row;
	private int column;
	private int brickWidth;
	private int brickHeight;
	private Rectangle brick;
	
	public MapGenerator(int row, int column){
		this.row=row;
		this.column=column;
		this.brickWidth=540/column;
		this.brickHeight=150/row;
	}
	
	public List<Rectangle> generate() {
		List<Rectangle> brickArray=new ArrayList<>();
		for(int i=0;i<row;i++){
			for(int j=0;j<column;j++) {
				brick=new Rectangle(j*brickWidth+80, i*brickHeight+50,
						brickWidth, brickHeight);
				brick.setFill(Color.WHITE);
				brick.setStroke(Color.BLACK);
				brickArray.add(brick);
			}
		}
		return brickArray;
	}
}
