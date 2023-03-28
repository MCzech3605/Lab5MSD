import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

public class Board extends JComponent implements MouseInputListener, ComponentListener {
	private static final long serialVersionUID = 1L;
	public static final int lineNumber = 2;
	private Point[][] points;
	private int size = 10;
	public int editType=0;


	public Point[][] copyPoints(){
		Point[][] copyPair = new Point[points.length][lineNumber];
		for(int i = 0; i < lineNumber; i++){
			for(int j = 0; j < points.length; j++){
				copyPair[j][i] = points[j][i];
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
		return true;
	}
	public void SetLineChange(){
		for(int i = 0; i < lineNumber; i++){
			for(int j = 0; j < points.length; j++){
				boolean isLeft = false, isRight = false;
				if(points[j][i].hasCar == true && i != 0)
					isLeft = isLeftLineBetter(points[j][i]);
				if(points[j][i].hasCar == true && i != lineNumber-1)
					isRight = isRightLineBetter(points[j][i]);

				if(isRight == true){
					points[j][i].isChangingLineToRight = true;
				}
				else if(isLeft == true){
					points[j][i].isChangingLineToLeft = true;
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
					points[i][j+1].copyFromOther(points[i][j]);
					points[i][j].clear();
				}
				if(points[i][j].isChangingLineToLeft){
					points[i][j-1].copyFromOther(points[i][j]);
					points[i][j].clear();
				}
			}
		}

//		for(int x = 0; x < points.length; x++){
//			points[x][0].updateVelocity();
//		}
//
//		for(int x = 0; x < points.length; x++){
//			points[x][0].clear();
//		}
//		for(int x = 0; x < points.length; x++){
//			Point oldPoint = points[x][1];
//			if(oldPoint.hasCar){
//				Point newPoint = points[(x+oldPoint.velocity)%points.length][0];
//				newPoint.copyFromOther(oldPoint);
//				if((x+oldPoint.velocity)%points.length!=x+oldPoint.velocity)
//					newPoint.maybeDisappear();
//			}
//		}
//		points[0][0].maybeAppear();
		//todo new iteration
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
		int random_points_min = 15;
		int[] rand_loc = new int[random_points_min];
		for(int i = 0; i < random_points_min; i++) {
			rand_loc[i] = ThreadLocalRandom.current().nextInt(0, length);
		}

		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y)
				points[x][y] = new Point();

		for(int i=0; i<random_points_min; i++){
			if(i%2==0)
				points[rand_loc[i]][0].clicked();
			else
				points[rand_loc[i]][1].clicked();
		}

		for (int x = 0; x < points.length; ++x) {
			for (int y = 0; y < lineNumber; ++y) {
				for(int i = 0; i <= 5; i++){
					points[x][y].frontNeighbors[i] = points[(x+i+1)%points.length][y];
				}
				if(y > 0){
					for(int i = -5; i <= 5; i++){
						points[x][y].leftLaneNeighbors[i+5] = points[(points.length+x+i)%points.length][y-1];
					}
				}
				else {
					for(int i = -5; i <= 5; i++){
						points[x][y].leftLaneNeighbors[i+5] = null;
					}
				}
				if(y < lineNumber-1){
					for(int i = -5; i <= 5; i++){
						points[x][y].rightLaneNeighbors[i+5] = points[(points.length+x+i)%points.length][y+1];
					}
				}
				else {
					for(int i = -5; i <= 5; i++){
						points[x][y].rightLaneNeighbors[i+5] = null;
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
