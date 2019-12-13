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
	 * True if center needs to be drawn
	 */
	boolean drawCenter = false;
	
	/**
	 * If the distance between the four dots is above the limit, do not draw.
	 */
	static final double DIST_LIMIT = 1500;
	
	/**
	 * Multiplies object by this size
	 */
	float ratio = 0.2f;
	
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
		applet.stroke(0f, 0f, 0f, 0f);
		PVector[] front = storedValues.getVectorArray();
		boolean[] foreground = null;
		float[] center = null;
		// Calculate which part is in the foreground
		if (backgroundSet) {
			foreground = background.calcForeground(storedValues.getArray());
			if (drawCenter) {
				center = findCenter(foreground);
			}
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
		    		 // If this is set, color the center more cyan
		    		 if (drawCenter) {
		    			 float distance = Math.abs(x - center[0]) + Math.abs(y - center[1]);
		    			 applet.fill(255f - distance, 255f, 255f);
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
	}
	
	/**
	 * Finds the center of all foreground pixels
	 */
	float[] findCenter(boolean[] foregroundArray) {
		float[] result = {0f, 0f};
		int counter = 0;
		// Iterate through array to get every pixel in foreground
		for (int x = 0; x < width; x += 1) {
		     for (int y = 0; y < height; y += 1) {
		    	 int offset = x + y * width;
		    	 if (foregroundArray[offset]) {
		    		 result[0] += x;
		    		 result[1] += y;
		    		 counter++;
		    	 }
		     }
		}
		if (counter > 0) {
			result[0] = result[0]/counter;
			result[1] = result[1]/counter;
		}
		return result;
	}
	
	/**
	 * Tests if all four points specified are part of the foreground
	 */
	boolean testForeground(boolean[] foregroundArray, int offset, int spacing) {
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
		PVector invalid = new PVector(-100, -100, -100);
		if (point1.equals(invalid) || point2.equals(invalid) || point3.equals(invalid) || point4.equals(invalid)) {
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
	
	/**
	 * Toggles center between true and false
	 */
	public void drawCenter() {
		drawCenter = !drawCenter;
	}
}
