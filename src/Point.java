import java.util.concurrent.ThreadLocalRandom;

public class Point {

	public static Integer []types ={0,1,2};
	public static final int maxVel = 5;
	public static int prob_speed = 2;
	public int velocity;
	public Point[] frontNeighbors;
	public Point[] leftLaneNeighbors;
	public Point[] rightLaneNeighbors;
	public boolean hasCar;

	public Point() {
		clear();
		frontNeighbors = new Point[maxVel+1];
		hasCar = false;
		leftLaneNeighbors = new Point[2*maxVel+1];
		rightLaneNeighbors = new Point[2*maxVel+1];
	}

	public void clicked() {
		if(!hasCar){
			hasCar = true;
			velocity = 1;
		}
	}

	public void clear() {
		hasCar = false;
		velocity = 0;
	}

	public void updateVelocity() {
		boolean carAhead = false;
		for(int i = 0; i <= velocity; i++){
			if(frontNeighbors[i].hasCar){
				carAhead = true;
				break;
			}
		}
		if(carAhead){
			reduceVelocity();
		}
		else {
			accelerate();
		}
		randomChangeInVelocity();
	}
	public void reduceVelocity(){
		for(int i = 0; i <= velocity; i++){
			if(frontNeighbors[i].hasCar){
				velocity = i;
				return;
			}
		}
		if(velocity > 0){
			velocity--;
		}
	}
	public void accelerate(){
		if(velocity < maxVel){
			velocity++;
		}
	}
	public void randomChangeInVelocity(){
		int rand = ThreadLocalRandom.current().nextInt(0, prob_speed);
		if(rand == 0){
			reduceVelocity();
		}
	}
	public void copyFromOther(Point other){
		this.hasCar = other.hasCar;
		this.velocity = other.velocity;
	}



	//todo changing lanes methods
}