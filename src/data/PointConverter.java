package data;

import java.util.Random;

import processing.core.PVector;

public class PointConverter {
	
	Random rand = new Random();
	
	public PointConverter() {
		
	}
	
	public PVector depthToPointCloudPos(int x, int y, float depthValue) {
		  PVector point = new PVector();
		  point.z = (depthValue);
		  point.x = (x - CameraParams.cx) * point.z / CameraParams.fx;
		  point.y = (y - CameraParams.cy) * point.z / CameraParams.fy;
		  return point;
	}
	
	// This should work for Kinect 1
	// Credit http://graphics.stanford.edu/~mdfisher/Kinect.html
	/**PVector depthToWorld(int x, int y, int depthValue) {

		  final double fx_d = 1.0 / 5.9421434211923247e+02;
		  final double fy_d = 1.0 / 5.9104053696870778e+02;
		  final double cx_d = 3.3930780975300314e+02;
		  final double cy_d = 2.4273913761751615e+02;

		// Drawing the result vector to give each point its three-dimensional space
		  PVector result = new PVector();
		  double depth =  depthLookUp[depthValue];//rawDepthToMeters(depthValue);
		  result.x = (float)((x - cx_d) * depth * fx_d);
		  result.y = (float)((y - cy_d) * depth * fy_d);
		  result.z = (float)(depth);
		  return result;
		}
		
	float rawDepthToMeters(int depthValue) {
  		if (depthValue < 2047) {
    		return (float)(1.0 / ((double)(depthValue) * -0.0030711016 + 3.3309495161));
  		}
  		return 0.0f;
	}
	 */
}
