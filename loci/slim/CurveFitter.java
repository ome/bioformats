package loci.slim;

public interface CurveFitter {

  /**
   * iterate() runs through one iteration of whatever curve fitting
   * technique this curve fitter uses. This will generally update the
   * information returned by getCurve and getChiSquaredError
   **/
  public void iterate();

  // Returns the Chi Squared Error of the current curve estimate
  public double getChiSquaredError();

  // Returns the Reduced Chi Squared Error of the current curve estimate
  // This is based on the number of datapoints in data and the number
  // of exponentials in setDegrees
  public double getReducedChiSquaredError();
  
  /**
   * Sets the data to be used to generate curve estimates.
   * The array is expected to be of size [datapoints][2].
   * [][0] contains a time value, [][1] contains the data value.
   * TODO: Do I actually need the time values, or can I hardcode?
   **/
  public void setData(double[][] data);

  // TODO: Set time domain?
  
  /**
   * Sets how many exponentials are expected to be fitted.
   * Currently, more than 2 is not supported.
   * TODO: Change Degrees to exponentials or whatever
   **/
  public void setDegrees(int degrees);

  // Returns the number of exponentials to be fitted.
  public int getDegrees();

  // Initializes the curve fitter with a starting curve estimate.
  public void estimate();

  /**
   * Returns the current curve estimate.
   * Return size is expected to be [numExponentials][3]
   * For each exponential of the form ae^-bt+c,
   * [][0] is a, [1] is b, [2] is c.
   * TODO: Make multiple exponentials a class, to remove multi-c stupidity
   **/
  public double[][] getCurve();

  /**
   * Sets the current curve estimate, useful if information about the
   * curve is already known.
   * See getCurve for information about the array to pass.
   **/
  public void setCurve(double[][] curve);

}
