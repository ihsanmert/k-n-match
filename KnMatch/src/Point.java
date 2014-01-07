
public class Point {
	static int counter = 1;

	int id;
	double[] values;
	String label = "";
	int appearNumber=0;
	public Point(double... values){
		this.values = values;
		id = counter;
		counter++;
	}
	@Override
	public String toString() {
		String str = "";
		for(double d : values){
			str += String.format("%.2f, ", d);
		}
		return "#" + id + "#(" + str.substring(0, str.length() - 2) + ") label:" + label +" numberOfApperances:" + appearNumber;
	}
	
	public void setLabel(String label){
		this.label = label;
	}
}