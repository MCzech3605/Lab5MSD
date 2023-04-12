import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

public class Board extends JComponent implements MouseInputListener, ComponentListener {
	private static final long serialVersionUID = 1L;
	private String savePath="data.txt";
	private String savePathV="vdata.txt";
	public static final int lineNumber = 3;
	public static ArrayList<Integer> iterationCounter = new ArrayList<>();
	private Point[][] points;
	private int size = 10;
	public int editType=0;


	public Point[][] copyPoints(){
		Point[][] copyPair = new Point[points.length][2*lineNumber];
		for(int i = 0; i < 2*lineNumber; i++){
			for(int j = 0; j < points.length; j++){
				copyPair[j][i] = new Point();
				copyPair[j][i].copyFromOther(points[j][i]);
			}
		}
		return copyPair;
	}

	public boolean isLeftLineBetter(Point x){
		if(x.leftLaneNeighbors[5].hasCar)
			return false;
		for(int i = 0; i < Point.maxVel; i++)
			if(x.leftLaneNeighbors[i].hasCar)
				return false;
		int cnt = Point.maxVel;
		for(int i = 1 ; i <= Point.maxVel; i++){
			if(x.frontNeighbors[i].hasCar == true) {
				cnt = i;
				break;
			}
		}
		if(cnt != Point.maxVel){
			for(int i = 1 ; i <= cnt; i++){
				if(x.leftLaneNeighbors[i].hasCar == true) {
					return false;
				}
			}
		}
		else
			return false;
		return true;
	}
	public boolean isRightLineBetter(Point x){
		if(x.rightLaneNeighbors[5].hasCar)
			return false;
		for(int i = 0; i < Point.maxVel; i++)
			if(x.rightLaneNeighbors[i].hasCar)
				return false;
		int cnt = Point.maxVel;
		for(int i = 1 ; i <= Point.maxVel; i++){
			if(x.frontNeighbors[i].hasCar == true) {
				cnt = i;
				break;
			}
		}
		if(cnt != Point.maxVel){
			for(int i = 1 ; i <= cnt; i++){
				if(x.rightLaneNeighbors[i].hasCar == true) {
					return false;
				}
			}
		}
		else
			return false;
		return true;
	}
	public void SetLineChange(){
		for(int i = 0; i < lineNumber; i++){
			for(int j = 0; j < points.length; j++){
				boolean isLeft = false, isRight = false, oposLeft = false, oposRight = false;
				if(points[j][i].hasCar == true && i != 0) {
					isLeft = isLeftLineBetter(points[j][i]);
					oposRight = isRightLineBetter(points[j][i + lineNumber]);
				}
				if(points[j][i].hasCar == true && i != lineNumber-1) {
					isRight = isRightLineBetter(points[j][i]);
					oposLeft = isLeftLineBetter(points[j][i + lineNumber]);
				}
				if(isRight == true){
					points[j][i].isChangingLineToRight = true;
				}
				else if(isLeft == true){
					points[j][i].isChangingLineToLeft = true;
				}
				if(oposRight == true){
					points[j][i + lineNumber].isChangingLineToRight = true;
				}
				else if(oposLeft == true){
					points[j][i + lineNumber].isChangingLineToLeft = true;
				}
			}
		}
	}

	public Board(int length, int height) {
		initialize(length, height);
		addMouseListener(this);
		addComponentListener(this);
		addMouseMotionListener(this);
		setBackground(Color.WHITE);
		setOpaque(true);
	}

	public void iteration() {

		
		SetLineChange();

		for(int i = 0; i < points.length; i++){
			for(int j = 0; j < lineNumber; j++){
				if(points[i][j].isChangingLineToRight){
					points[i][j].isChangingLineToRight = false;
					points[i][j].rightLaneNeighbors[Point.maxVel].copyFromOther(points[i][j]);
					points[i][j].clear();
				}
				if(points[i][j].isChangingLineToLeft){
					points[i][j].isChangingLineToLeft = false;
					points[i][j].leftLaneNeighbors[Point.maxVel].copyFromOther(points[i][j]);
					points[i][j].clear();
				}
			}
		}

		for(int x = 0; x < points.length; x++){
			for(int y = 0; y < 2 * lineNumber; y++){
				points[x][y].updateVelocity();
			}
		}

		Point[][] pointsBuffer = copyPoints();

		for(int x = 0; x < points.length; x++){
			for(int y = 0; y <2 * lineNumber; y++){
				points[x][y].clear();
			}
		}
		int carsPassed = 0;
		for(int x = 0; x < points.length; x++){
			for(int y = 0; y < lineNumber; y++){
				Point oldPoint = pointsBuffer[x][y];
				if(oldPoint.hasCar){
					Point newPoint = points[(x+oldPoint.velocity)%points.length][y];
					newPoint.copyFromOther(oldPoint);
					if(x+oldPoint.velocity!=(x+oldPoint.velocity)%points.length)
						carsPassed++;
				}
			}
		}
		int velSum = 0;
		int carSum = 0;
		for(int x = 0; x < points.length; x++){
			for(int y = lineNumber; y < 2 * lineNumber; y++){
				Point oldPoint = pointsBuffer[x][y];
				if(oldPoint.hasCar){
					carSum++;
					velSum += oldPoint.velocity;
					Point newPoint = points[(points.length + x - oldPoint.velocity)%points.length][y];
					newPoint.copyFromOther(oldPoint);
					if(x-oldPoint.velocity!=(x + points.length -oldPoint.velocity)%points.length)
						carsPassed++;
				}
			}
		}
		iterationCounter.add(carsPassed);
		OutputStream os = null;
		try {
			os = new FileOutputStream(new File(savePath), true);
			String toSave = Integer.toString(carsPassed) + "\n";
			os.write(toSave.getBytes(), 0, toSave.length());
			os.close();
			os = new FileOutputStream(new File(savePathV), true);
			toSave = Double.toString((double) velSum/(double) carSum) + "\n";
			os.write(toSave.getBytes(), 0, toSave.length());
			os.close();
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't save file");
		} catch (IOException e) {
			System.out.println("Couldn't save file IO exception");
		}

		this.repaint();
	}

	public void clear() {
		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y) {
				points[x][y].clear();
			}
		this.repaint();
	}

	private void initialize(int length, int height) {
		points = new Point[length][height];
		int random_points_min = 49;
		try {
			Files.writeString(Path.of("data_" + random_points_min + ".txt"), "", StandardCharsets.UTF_8);
			savePath = "data_" + random_points_min + ".txt";
			Files.writeString(Path.of("vdata_" + random_points_min + ".txt"), "", StandardCharsets.UTF_8);
			savePathV = "vdata_" + random_points_min + ".txt";
		}

		catch (IOException ex) {
			System.out.print("Invalid Path");
		}
		ArrayList<Integer> randNums = new ArrayList<>();
		for(int i=0; i<points.length; i++){
			randNums.add(i);
		}

		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y)
				points[x][y] = new Point();
		for(int j = 0; j < 6; j++){
			Collections.shuffle(randNums);
			for(int i = 0; 6*i + j < random_points_min; i++){
				points[randNums.get(i)][j].clicked();
			}
		}

		for (int x = 0; x < points.length; ++x) {
			for (int y = 0; y < lineNumber; ++y) {
				for(int i = 0; i <= Point.maxVel; i++){
					points[x][y].frontNeighbors[i] = points[(x+i+1)%points.length][y];
				}
				if(y > 0){
					for(int i = -Point.maxVel; i <= Point.maxVel; i++){
						points[x][y].leftLaneNeighbors[i+Point.maxVel] = points[(points.length+x+i)%points.length][y-1];
					}
				}
				else {
					for(int i = -Point.maxVel; i <= Point.maxVel; i++){
						points[x][y].leftLaneNeighbors[i+Point.maxVel] = null;
					}
				}
				if(y < lineNumber-1){
					for(int i = -Point.maxVel; i <= Point.maxVel; i++){
						points[x][y].rightLaneNeighbors[i+Point.maxVel] = points[(points.length+x+i)%points.length][y+1];
					}
				}
				else {
					for(int i = -Point.maxVel; i <= Point.maxVel; i++){
						points[x][y].rightLaneNeighbors[i+Point.maxVel] = null;
					}
				}
			}
		}

		for (int x = 0; x < points.length; ++x) {
			for (int y = lineNumber; y < 2*lineNumber; ++y) {
				for(int i = 0; i <= Point.maxVel; i++){
					points[x][y].frontNeighbors[i] = points[(x+points.length-i-1)%points.length][y];
				}
				if(y > lineNumber){
					for(int i = -Point.maxVel; i <= Point.maxVel; i++){
						points[x][y].rightLaneNeighbors[Point.maxVel - i] = points[(points.length+x+i)%points.length][y-1];
					}

				}
				else {
					for(int i = -Point.maxVel; i <= Point.maxVel; i++){
						points[x][y].rightLaneNeighbors[i+Point.maxVel] = null;
					}

				}
				if(y < 2*lineNumber-1){
					for(int i = -Point.maxVel; i <= Point.maxVel; i++){
						points[x][y].leftLaneNeighbors[Point.maxVel - i] = points[(points.length+x+i)%points.length][y+1];
					}
				}
				else {
					for(int i = -Point.maxVel; i <= Point.maxVel; i++){
						points[x][y].leftLaneNeighbors[i+Point.maxVel] = null;
					}
				}
			}
		}
	}

	protected void paintComponent(Graphics g) {
		if (isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
		g.setColor(Color.GRAY);
		drawNetting(g, size);
	}

	private void drawNetting(Graphics g, int gridSpace) {
		Insets insets = getInsets();
		int firstX = insets.left;
		int firstY = insets.top;
		int lastX = this.getWidth() - insets.right;
		int lastY = this.getHeight() - insets.bottom;

		int x = firstX;
		while (x < lastX) {
			g.drawLine(x, firstY, x, lastY);
			x += gridSpace;
		}

		int y = firstY;
		while (y < lastY) {
			g.drawLine(firstX, y, lastX, y);
			y += gridSpace;
		}

		for (x = 0; x < points.length; ++x) {
			for (y = 0; y < points[x].length; ++y) {
				if(points[x][y].hasCar){
					g.setColor(new Color(0,0,0));
				}
				else {
					g.setColor(new Color(255,255,255));
				}

				g.fillRect((x * size) + 1, (y * size) + 1, (size - 1), (size - 1));
			}
		}

	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX() / size;
		int y = e.getY() / size;
		if ((x < points.length) && (x >= 0) && (y < points[x].length) && (y >= 0)) {
			points[x][y].clicked();
			this.repaint();
		}
	}

	public void componentResized(ComponentEvent e) {
		int dlugosc = (this.getWidth() / size) + 1;
		int wysokosc = (this.getHeight() / size) + 1;
		initialize(dlugosc, wysokosc);
	}

	public void mouseDragged(MouseEvent e) {
		int x = e.getX() / size;
		int y = e.getY() / size;
		if ((x < points.length) && (x >= 0) && (y < points[x].length) && (y >= 0)) {
			points[x][y].clicked();
			this.repaint();
		}
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

}
