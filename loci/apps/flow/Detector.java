//
// Detector.java
//

/*
Utility class for doing particle detection in flow cytometry.

Copyright (c) 2008 Hidayath Ansari and Curtis Rueden. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the UW-Madison LOCI nor the names of its
    contributors may be used to endorse or promote products derived from
    this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE UW-MADISON LOCI ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package loci.apps.flow;

import ij.*;
import ij.process.*;

import java.awt.image.IndexColorModel;
import java.util.Vector;

public class Detector {
	

	private static IndexColorModel theCM = makeCM();
    private static IndexColorModel makeCM() {
		byte[] r = new byte[256];
		byte[] g = new byte[256];
		byte[] b = new byte[256];

		for(int ii=0 ; ii<256 ; ii++)
		r[ii] = g[ii] = b[ii] = (byte)ii;		
		
		return new IndexColorModel(8, 256, r,g,b);
    }

    public static ImagePlus impParticles, imp3;
	private int size = 512;
	private int intensityThreshold = 30;
	private int areaThreshold = 100;
	
	private int numRegions;
	private int[][] floodArray;
	
	private byte[] imageData;
	
	public void setIntensityThreshold(int t) { intensityThreshold = t; }
	public void setAreaThreshold(int t) { areaThreshold = t; }

	public static void createImageHolder(int x, int y) {
		impParticles = new ImagePlus("Particles", new ByteProcessor(x, y));
		impParticles.hide();
	}
	
	public static void main(String[] args) {
		Detector d = new Detector(512, 30, 100);
		Detector.impParticles = new ImagePlus("Particles", new ByteProcessor(d.size,d.size));
		

		
		Detector.imp3 = IJ.openImage("particles.tiff");
		Detector.imp3.show();
		IJ.run("Despeckle");
		//IJ.run("Haar wavelet filter", "k1=3 k2=3 k3=3 std=1.6");
		
		
		
		d.findParticles((ByteProcessor) Detector.imp3.getProcessor());
		d.crunchArray();
		displayImage(d.floodArray);
	}
	
	public Detector(int s, int intensT, int areaT) {
		size = s;
		intensityThreshold = intensT;
		areaThreshold = areaT;
	}
	
	public Vector<Particle> crunchArray() {
		
		Vector<Particle> retval = new Vector<Particle>();
		int[] area = new int[numRegions], totalIntensity = new int[numRegions];
		for (int i=0; i<size; i++) {
			for (int j=0; j<size; j++) {
				if (floodArray[i][j] < 2) continue;
				area[floodArray[i][j]-2]++;
				totalIntensity[floodArray[i][j]-2] += (imageData[i*size+j] & 0xff);
			}
		}
		for (int i=0; i<numRegions; i++) {
			if (area[i] < areaThreshold) continue;
			retval.add(new Particle(area[i], totalIntensity[i]));
			//System.out.println("Region "+i+": Area "+area[i]+"  Intensity "+totalIntensity[i]);
		}
		return retval;
	}

	public void findParticles(ImageProcessor ip) {

		byte[] oldByteArray = (byte[]) ip.getPixels();
		byte[] newByteArray = new byte[oldByteArray.length];
		for (int i=0; i<newByteArray.length; i++) newByteArray[i] = oldByteArray[i];

		IJ.run(new ImagePlus("temp", new ByteProcessor(size, size, newByteArray, theCM)), "Despeckle", "");
		imageData = newByteArray;
		floodArray = new int[size][size];
		markPixelsInitial();

		//Heuristic 1
		for (int i=0; i<50; i++) markPixels(floodArray);
		
		// Find the particle regions.
		numRegions = fillParticles(floodArray);
	}
	
	public static void displayImage(int[][] arr) {
		byte[] particleArray = new byte[arr.length*arr[0].length];
		for (int i=0; i<arr.length; i++) {
			for (int j=0; j<arr[0].length; j++) {
				if (arr[i][j] > 0) particleArray[i*arr[0].length+j] = new Integer(140).byteValue();
			}
		}
		impParticles.setProcessor("particles", new ByteProcessor(arr.length, arr[0].length, particleArray, theCM));
		impParticles.show();
	}
	
	private int fillParticles(int[][] floodArray) {
		int num = 2;
		for (int i=0; i<size; i++) {
			for (int j=0; j<size; j++) {
				if (floodArray[i][j]==0) continue;
				if (floodArray[i][j]==1) {
					//System.out.println("Starting flood fill "+num+" at ("+i+", "+j+")");
					heuristicFloodFill(floodArray, i, j, num++);
				}
			}
		}
		System.out.println("Particles found: "+(num-2));
		return (num-2);
	}

	private void markPixelsInitial() {
		for (int i=0; i<imageData.length; i++) {
			if ((imageData[i] & 0xff) > intensityThreshold) floodArray[i/size][i%size] = 1;
		}
	}
	
	private void markPixels(int[][] arr) {
		int m=size-1;
		for (int i=0; i<size; i++) {
			for (int j=0; j<size; j++) {
				if (arr[i][j]==0) {
					int top=0, bottom=0, left=0, right=0; //Keeping counts in case we want to do something more sophisticated later.
					if (i>1 && j>1 && arr[i-1][j-1]==1) {top++; left++;}
					if (i>1 &&        arr[i-1][j]  ==1) {top++;}
					if (i>1 && j<m && arr[i-1][j+1]==1) {top++; right++;}
					if (       j>1 && arr[i][j-1]  ==1) {left++;}
					if (       j<m && arr[i][j+1]  ==1) {right++;}
					if (i<m && j>1 && arr[i+1][j-1]==1) {bottom++; left++;}
					if (i<m &&        arr[i+1][j]  ==1) {bottom++;}
					if (i<m && j<m && arr[i+1][j+1]==1) {bottom++; right++;}
					
					int binTop = top>0 ? 1 : 0;
					int binBottom = bottom>0 ? 1 : 0;
					int binLeft = left>0 ? 1 : 0;
					int binRight = right>0 ? 1 : 0;
					if (binTop+binBottom+binLeft+binRight > 3) arr[i][j] = 1;
					if (bottom+left+right>6) arr[i][j]=1;
				}
			}
		}
	}

	private void heuristicFloodFill(int[][] array, int i, int j, int num) {
		array[i][j] = num;
		int m=size-1;
		
		int iter=j-1, left=j, right=j;
		while (iter>0 && array[i][iter]==1) {
			array[i][iter--] = num;
		}
		left=iter;
		iter=j+1;
		while (iter<m && array[i][iter]==1) {
			array[i][iter++] = num;
		}
		right=iter;
		
		if (left<0) left=0;
		if (right>m) right=m;
		
		for (int k=left; k<=right; k++) {
			if (i>1 && array[i-1][k] == 1) heuristicFloodFill(array, i-1, k, num);
			if (i<m && array[i+1][k] == 1) heuristicFloodFill(array, i+1, k, num);
		}
	}

	public int getNumRegions() {
		return numRegions;
	}

	public int[][] getFloodArray() {
		return floodArray;
	}
}
