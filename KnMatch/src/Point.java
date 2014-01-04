
public class Point {
	static int counter = 1;

	int id;
	double[] values;

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
		return "#" + id + "#(" + str.substring(0, str.length() - 2) + ")";
	}
	
	class Attribute{
		double value;
		int p_id;
		public Attribute(double value, int p_id){
			this.p_id = p_id;
			this.value = value;
		}
	}
}