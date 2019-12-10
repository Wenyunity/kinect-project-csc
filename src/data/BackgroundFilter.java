package data;

/**
 * Filters out an array from another array
 * @author Geron Sena
 *
 */
public class BackgroundFilter {
	/**
	 * If the difference in depth is less than threshold, considered part of the background (in mm)
	 */
	int threshold;
	/**
	 * Holds array of background
	 */
	int[] backgroundArray;
	
	public BackgroundFilter() {
		this(10);
	}
	
	/**
	 * Creates a background filter
	 * @param bgThreshold 
	 */
	public BackgroundFilter(int bgThreshold) {
		threshold = bgThreshold;
	}
	
	/**
	 * Sets the background to the following set of int[]
	 * @param background
	 */
	public void setBackground(int[] background) {
		backgroundArray = background.clone();
	}
	
	/**
	 * Creates a boolean list showing which ints are in the foreground
	 * @param picture List to check against background
	 * @return list of foreground
	 */
	public boolean[] calcForeground(int[] picture) {
		boolean[] returnValue = new boolean[Math.min(picture.length, backgroundArray.length)];
		for (int i = 0; i < returnValue.length; i++) {
			if (Math.abs(picture[i] - backgroundArray[i]) > threshold) {
				returnValue[i] = true;
			}
			else {
				returnValue[i] = false;
			}
		}
		return returnValue;
	}
	
	/**
	 * Changes threshold by amount given
	 * @param change amount to change foreground threshold by
	 */
	public void changeThreshold (int change) {
		threshold += change;
	}
	
	/**
	 * Sets threshold to the given number
	 * @param set Number to set the threshold to
	 */
	public void setThreshold (int set) {
		threshold = set;
	}

}