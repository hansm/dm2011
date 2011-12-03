import java.awt.Point;


public class P {
	public int coordX;
	public int coordY;
	public int cluster;
	
	public P(Point coords) {
    	this.coordX = coords.x;
    	this.coordY = coords.y;
    	this.cluster = -1;
    }
}
