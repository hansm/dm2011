import java.awt.Point;


public class P {
	public int x;
	public int y;
	public int cluster;
	
	public P(Point coords) {
    	this.x = coords.x;
    	this.y = coords.y;
    	this.cluster = -1;
    }
}
