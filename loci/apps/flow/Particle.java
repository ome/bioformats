
public class Particle {
		int area;
		int num;
		int totalIntensity;
		int sliceNum;
		
	public Particle(int a, int i) {
		area = a;
		totalIntensity = i;
	}
	
	public void print() {
		System.out.println("Particle "+num+" on slice "+sliceNum+" with area "+area+" and total intensity "+totalIntensity);
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getSliceNum() {
		return sliceNum;
	}

	public void setSliceNum(int sliceNum) {
		this.sliceNum = sliceNum;
	}

	public int getArea() {
		return area;
	}

	public int getIntensity() {
		return totalIntensity;
	}

	public int getMeanIntensity() {
		if (area==0) return 0; 
		else return totalIntensity/area;
	}
}
