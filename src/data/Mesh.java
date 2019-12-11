package data;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**
 * Main class for handling the mesh.
 * 
 * @author Bakar
 *
 */
public class Mesh {

	/**
	 * Stores the array
	 */
	Filter storedValues;
	/**
	 * Width of the picture
	 */
	int width;
	/**
	 * Height of the picture
	 */
	int height;
	/**
	 * True if the background is set
	 */
	boolean backgroundSet = false;
	/**
	 * Handles background
	 */
	BackgroundFilter background;
	
	/**
	 * If the distance between the four dots is above the limit, do not draw.
	 */
	static final double DIST_LIMIT = 1000;
	
	/**
	 * Multiplies object by this size
	 */
	float ratio = 0.2f;
	float a = 0;
	
	/**
	 * Create a new mesh
	 * @param widthArray width of the array
	 * @param heightArray height of the array
	 */
	public Mesh(int widthArray, int heightArray) {
		width = widthArray;
		height = heightArray;
		storedValues = new Filter(width * height, width, height, 2);
		background = new BackgroundFilter(200);
	}
	
	/**
	 * Set current frame of the mesh to the background
	 */
	public void setBackground() {
		backgroundSet = true;
		background.setBackground(storedValues.getArray());
	}
	
	/**
	 * Draws mesh on an applet
	 * @param spacing The distance between points to sample (If 1, samples all points)
	 * @param applet Applet to draw the mesh on.
	 */
	public void getMesh(int spacing, PApplet applet) {
		applet.pushMatrix();
		applet.rotateY(PConstants.PI);
		applet.fill(255f, 255f, 255f);
		PVector[] front = storedValues.getVectorArray();
		boolean[] foreground = null;
		// Calculate which part is in the foreground
		if (backgroundSet) {
			foreground = background.calcForeground(storedValues.getArray());
		}
		// Loop through
		for (int x = spacing/2; x < width - spacing; x += spacing) {
		     for (int y = spacing/2; y < height - spacing; y += spacing) {
		    	 int offset = x + y * width;
		    	 // If the background is set, and the square is part of the background, skip
		    	 if (backgroundSet) {
		    		 if (!testForeground(foreground, offset, spacing)) {
		    			 continue;
		    		 }
		    	 }
		    	 PVector point = front[offset];
		    	 PVector point2 = front[offset+spacing];
		    	 PVector point3 = front[offset+spacing+width*spacing];
		    	 PVector point4 = front[offset+width*spacing];
		    	 if (testSquare(point, point2, point3, point4)) {
		    		 applet.beginShape();
			    	 applet.vertex(point.x*ratio, point.y*ratio, point.z*ratio);
			    	 applet.vertex(point2.x*ratio, point2.y*ratio, point2.z*ratio);
			    	 applet.vertex(point3.x*ratio, point3.y*ratio, point3.z*ratio);
			    	 applet.vertex(point4.x*ratio, point4.y*ratio, point4.z*ratio);
			    	 applet.endShape(PConstants.CLOSE);
		    	 }
		    }     
		}
		applet.popMatrix();
		a += 0.01f;
	}
	
	/**
	 * Tests if all four points specified are part of the foreground
	 */
	boolean testForeground (boolean[] foregroundArray, int offset, int spacing) {
		return foregroundArray[offset] && foregroundArray[offset+spacing] && foregroundArray[offset+spacing+width*spacing] && foregroundArray[offset+width*spacing];
	}
	
	/**
	 * Tests if four points would make a square
	 * @param point1
	 * @param point2
	 * @param point3
	 * @param point4
	 * @return True if the distance between values is not too big, false otherwise
	 */
	boolean testSquare(PVector point1, PVector point2, PVector point3, PVector point4) {
		PVector zero = new PVector(0, 0, 0);
		if (point1.equals(zero) || point2.equals(zero) || point3.equals(zero) || point4.equals(zero)) {
			return false;
		}
		return (PVector.dist(point1, point2) + PVector.dist(point2, point3) + PVector.dist(point3, point4) + PVector.dist(point4, point1)) < DIST_LIMIT;
	}
	
	/**
	 * Changes the size of the drawing
	 * @param change float showing how much to increase or decrease the value
	 */
	public void changeRatio(float change) {
		ratio += change;
	}
	/**public void getMesh(int spacing, PApplet applet) {
		int[] data = storedValues.getArray();
		for (int x = spacing/2; x < width - spacing; x += spacing) {
		     for (int y = spacing/2; y < height - spacing; y += spacing) {
		    	  applet.beginShape();
		    	  int offset = x + y * width;
		    	  PVector point = pointConv.depthToPointCloudPos(x, y, data[offset]);
		    	  applet.vertex(point.x/10, point.y/10, point.z/10);
		    	  PVector point2 = pointConv.depthToPointCloudPos(x+spacing, y, data[offset+spacing]);
		    	  applet.vertex(point2.x/10, point2.y/10, point2.z/10);
		    	  PVector point3 = pointConv.depthToPointCloudPos(x+spacing, y+spacing, data[offset+spacing+width*spacing]);
		    	  applet.vertex(point3.x/10, point3.y/10, point3.z/10);
		    	  PVector point4 = pointConv.depthToPointCloudPos(x, y+spacing, data[offset+width*spacing]);
		    	  applet.vertex(point4.x/10, point4.y/10, point4.z/10);
		    	  applet.endShape(PConstants.CLOSE);
		     }     
		}
	}*/
	
	/**
	 * Updates filter array
	 * @param array New picture
	 */
	public void updateArray(int[] array) {
		storedValues.updateArray(array);
	}
	
	/**
	 * Changes how far away from the background an object has to be to be considered foreground
	 * @param change Amount to change by
	 */
	public void changeBackground(int change) {
		background.changeThreshold(change);
	}
}
