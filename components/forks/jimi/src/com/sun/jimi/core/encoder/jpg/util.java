package com.sun.jimi.core.encoder.jpg;

public final class util{
	public static void errexit(String s){
		throw new RuntimeException("Error during encoding: " +s);
	}
	
	public static int roundUp(int a, int b){
		a += b-1;
		return a - (a % b);
	}
}
