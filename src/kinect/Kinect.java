package kinect;

import processing.core.*;
import data.*;

import org.openkinect.processing.*;

/**
 * PApplet class
 * 
 * Main class. Runs PApplet.
 * @author Geron Sena
 */
public class Kinect extends PApplet {

	Kinect kinect;
	Kinect2 kinect2;
	Mesh mesh;
	float temp = 0;
	float temp2 = 200;
	boolean getCloudImage;
	
	PImage depthImg;
	
	/**
	 * Main command
	 * @param args
	 */
	public static void main(String[] args) {
		PApplet.main("kinect.Kinect");
	}
	
	/**
	 * Settings of the PApplet
	 */
	public void settings(){
		size(900,900,P3D);
    }

	/**
	 * Initiate Kinect
	 */
	public void setup() {		
		kinect2 = new Kinect2(this);
		kinect2.initVideo();
		kinect2.initDepth();
		kinect2.initIR();
		kinect2.initRegistered();
		// Start all data
		kinect2.initDevice();
		
		mesh = new Mesh(kinect2.depthWidth, kinect2.depthHeight);
		
		// Blank image
		depthImg = new PImage(kinect2.depthWidth, kinect2.depthHeight);
	}

	/**
	 * Occurs every time the kinect sends a video event
	 * @param k Kinect that sends a video event
	 */
	void videoEvent(Kinect2 k) {
		mesh.updateArray(kinect2.getRawDepth());
	}
	
	/**
	 * Draws the current mesh
	 */
	public void draw() {
		// Camera positions
		lights();
		camera(0f, 0f, temp2, 0f, 0f, temp, 0f, 1f, 0f);
		// Threshold the depth image
		background(0, 0, 0);
		//getCloud(kinect2.getRawDepth(), kinect2.depthWidth, kinect2.depthHeight, 4);
		if (getCloudImage) {
		  mesh.getMesh(4, this);
		} 
		videoEvent(kinect2);
		//System.out.println(zCamera);
	}
	
	/**
	 * Press p to activate Kinect Camera
	 * Press b to activate Background
	 * Press z to make the picture smaller, press x to make the picture bigger
	 * Press n to decrease background sensitivity, press b to increase background sensitivity
	 * Press c to draw the center white and edges cyan
	 */
	public void keyPressed() {
		switch (key) {
		// Sets background
		case 'b':
			mesh.setBackground();
			break;
		case 'c':
			mesh.drawCenter();
			break;
		case 'p':
			getCloudImage = !getCloudImage;
			System.out.println(getCloudImage);
			break;
		// Changes ratio
		case 'z':
			mesh.changeRatio(-0.05f);
			break;
		case 'x':
			mesh.changeRatio(0.05f);
			break;
		// Changes background
		case 'n':
			mesh.changeBackground(-5);
			break;
		case 'm':
			mesh.changeBackground(5);
			break;
		}		
	}
}
