import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

public class Board extends JComponent implements MouseInputListener, ComponentListener {
	private static final long serialVersionUID = 1L;
	private Point[][] points;
	private int size = 10;
	public int editType=0;

	public static final int lineNumber = 2;

	public Point[][] copyPoints(){
		Point[][] copyPair = new Point[points.length][lineNumber];
		for(int i = 0; i < lineNumber; i++){
			for(int j = 0; j < points.length; j++){
				copyPair[j][i] = points[j][i];
			}
		}
		return copyPair;
	}

	public void SetLineChange(){
		for(int i = 0; i < lineNumber; i++){
			for(int j = 0; j < points.length; j++){
				if(points[j][i].hasCar == true){
					//boolean NeighbourRule = points[j][i]
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
		Point[][] pointsBuffer = copyPoints();

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
			for (int y = 0; y < 2; ++y) {
				for(int i = 0; i <= 5; i++){
					points[x][y].frontNeighbors[i] = points[(x+i+1)%points.length][y];
				}
				for(int i = -5; i <= 5; i++){
					points[x][y].otherLaneNeighbors[i+5] = points[(points.length+x+i)%points.length][(y+1)%2];
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
