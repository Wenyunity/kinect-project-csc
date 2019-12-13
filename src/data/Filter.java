package data;

import processing.core.PVector;

public class Filter {
	
	/**
	 * Point cloud array
	 */
	PVector[] vectorArray;
	/**
	 * Filtered array
	 */
	int[] filterArray;
	/**
	 * Stability of pixel in current array
	 * Affects how much new readings are factored into filterArray
	 */
	double[] stability;
	/**
	 * Goes up when readings have a value different from the average
	 * Affects stability
	 */
	int[] change;
	static int BOUNDS = 2;
	/**
	 * The minimum value of the change array
	 */
	static int MIN_CHANGE = -50;
	/**
	 * The maximum value that the change value can move
	 */
	static int MAX_CHANGE_UPDATE = 10;
	/**
	 * The difference needed to increase the change by 1
	 */
	static int TO_CHANGE = 50;
	/**
	 * If change is > 0, this value is multiplied by stability to lower it.
	 */
	static double STABILITY_CHANGE = 0.7;
	/**
	 * If stability is less than this number, updates PVector
	 */
	static double UPDATE_PVECTOR_BELOW = 1.3;
	/**
	 * If stability is less than this number, removes vector
	 */
	static double REMOVE_PVECTOR_BELOW = 0.3;
	/**
	 * The maximum amount that the stored value of the PVector will factor in when changing.
	 */
	double maxOldVectorCoefficient = 0.9;
	
	
	// Width of array
	int width;
	// Height of array
	int height;
	// Which version of the Kinect is being used
	int kinectVersion;
	
	PointConverter vectorCreate;
	
	/**
	 * Creates a Filter
	 * @param cacheLength Length of list.
	 * @param widthValue Width of list.
	 * @param heightValue Height of list.
	 * @param kinectVer Which version of kinect is being used.
	 */
	public Filter(int cacheLength, int widthValue, int heightValue, int kinectVer) {
		vectorCreate = new PointConverter();
		filterArray = new int[cacheLength];
		stability = new double[cacheLength];
		change = new int[cacheLength];
		vectorArray = new PVector[cacheLength];
		width = widthValue;
		height = heightValue;
		kinectVersion = kinectVer;
		for (int i = 0; i < cacheLength; i++) {
			filterArray[i] = -1;
			stability[i] = 0;
			change[i] = 0;
			vectorArray[i] = new PVector(-100, -100, -100);
		}
	}
	
	/**
	 * Returns the integer array.
	 * @return Integer array.
	 */
	public int[] getArray() {
		return filterArray;
	}
	
	/**
	 * Returns the PVector array.
	 * @return PVector array
	 */
	public PVector[] getVectorArray() {
		return vectorArray;
	}
	
	/**
	 * Updates the array with a new one.
	 * @param array
	 */
	public void updateArray(int[] array) {
		// Get length of array
		int length = Math.min(array.length, filterArray.length);
		// For the items in the array
		for (int i = 0; i < length; i++) {
			averagePixel(i, array[i]);
		}
	}
	
	/**
	 * Updates the average value of an index.
	 * @param index
	 * @param newValue
	 */
	void averagePixel(int index, int newValue) {
		if (newValue == 0) {
			updateStability(index, -1000);
			if (stability[index] < REMOVE_PVECTOR_BELOW) {
				removePVector(index);
			}
			return;
		}
		else {
			updateChange(index, newValue);
			updateValue(index, newValue);
			updateStability(index, newValue);
			if (stability[index] < UPDATE_PVECTOR_BELOW) {
				updatePVector(index);
			}
		}
	}
	
	/**
	 * Updates the change of an index.
	 * @param index
	 * @param newValue
	 */
	void updateChange(int index, int newValue) {
		// If first value, return
		if (filterArray[index] == -1) {
			return;
		}
		
		int difference = Math.abs(filterArray[index] - newValue);
		int changeValue = Math.min(MAX_CHANGE_UPDATE, (difference / TO_CHANGE) - 1);
		change[index] += changeValue;
		if (change[index] < MIN_CHANGE) {
			change[index] = MIN_CHANGE;
		}
	}
	
	/**
	 * Updates the value of an index.
	 * @param index
	 * @param newValue
	 */
	void updateValue(int index, int newValue) {
		double valueChange = Math.min(stability[index], maxOldVectorCoefficient);
		filterArray[index] = (int)Math.round(newValue * (1 - valueChange) + filterArray[index] * valueChange);
	}
	
	/**
	 * Updates the stability of an index.
	 * @param index
	 * @param newValue
	 */
	void updateStability(int index, int newValue) {
		// Stability 0, update
		if (stability[index] == 0) {
			stability[index] = 0.5;
			return;
		}
		if (change[index] < 0) {
			stability[index] += (1 - stability[index]) / 2;
		}
		else {
			stability[index] *= STABILITY_CHANGE;
		}
	}
	
	/**
	 * Updates the PVector of an index
	 */
	void updatePVector(int index) {
		int x = index % width;
		int y = Math.floorDiv(index, width);
		vectorArray[index] = vectorCreate.depthToPointCloudPos(x, y, filterArray[index]);
	}
	
	/**
	 * Removes a PVector
	 */
	void removePVector(int index) {
		vectorArray[index] = new PVector(-100, -100, -100);
	}
}
