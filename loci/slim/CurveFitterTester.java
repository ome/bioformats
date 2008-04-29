package loci.slim;

import java.io.*;
import java.util.*;

public class CurveFitterTester {

  // Read a test file
  public static double[][] curveData(BufferedReader in) throws IOException {
    in.readLine();
    in.readLine();
    StringTokenizer st = new StringTokenizer(in.readLine());
    st.nextToken();
    int factors = Integer.parseInt(st.nextToken());
    //System.out.println("Read " + factors + " factors");
    in.readLine();
    in.readLine();
    st = new StringTokenizer(in.readLine());
    st.nextToken();
    int datapoints = Integer.parseInt(st.nextToken());
    //System.out.println("Read " + datapoints + " datapoints");
    st = new StringTokenizer(in.readLine());
    st.nextToken();
    double timeinc = Double.parseDouble(st.nextToken());
    //System.out.println("Read " + timeinc + " timeinc");
    for(int i = 0; i < factors; i++) {
      in.readLine();
      in.readLine();
    }
    in.readLine();
    double[][] data = new double[datapoints][2];
    for(int i = 0; i < datapoints; i++) {
      st = new StringTokenizer(in.readLine());
      st.nextToken();
      data[i][1] = Integer.parseInt(st.nextToken());
      data[i][0] = timeinc * (i + 1);
      //System.out.println(i + ": " + data[i][1]);
    }
    return data;
  }

  public static void main(String[] args) throws Exception {
    BufferedReader in = new BufferedReader(new FileReader(args[0]));
    int degrees = Integer.parseInt(args[1]);
    
    
    double[][] data = curveData(in);
    long start = System.currentTimeMillis();
    CurveFitter cf = new GACurveFitter();
    // TODO: Change this to test all of the test data.
    cf.setDegrees(degrees);
    cf.setData(data);
    cf.estimate();
    long duration = System.currentTimeMillis() - start;
    System.out.println("Time: " + duration);
    System.out.println("Chi Squared Error: " + cf.getChiSquaredError()); 
    System.out.println("Reduced Chi Squared Error: " + 
        cf.getReducedChiSquaredError());
    double[][] curveEst = cf.getCurve();
    for(int i = 0; i < curveEst.length; i++) {
      System.out.print("Exponential " + i + ": ");
      System.out.println(curveEst[i][0] + "e ^ -" + curveEst[i][1] +
          "t + " + curveEst[i][2]);
    }
    int iter = 0; 
    while(true) {
      cf.iterate();
      duration = System.currentTimeMillis() - start;
      double cse = cf.getReducedChiSquaredError();
      iter++;
      if(iter % 50 == 0) {
        System.out.println("Reduced Chi Squared Error: " + 
            cse + "  Elapsed: " + duration);
        for(int i = 0; i < curveEst.length; i++) {
          System.out.print("Exponential " + i + ": ");
          System.out.println(curveEst[i][0] + "e ^ -" + curveEst[i][1] +
              "t + " + curveEst[i][2]);
        }
      }
      //System.exit(0);
      
      //System.out.println("Reduced Chi Squared Error: " + 
      //    cse + "  Elapsed: " + duration);
      //if(cse < 1.0) System.exit(0);
      /* 
      for(int i = 0; i < curveEst.length; i++) {
        System.out.print("Exponential " + i + ": ");
        System.out.println(curveEst[i][0] + "e ^ -" + curveEst[i][1] +
            "t + " + curveEst[i][2]);
      }
      System.out.println(); 
      */
    }
  }

}
