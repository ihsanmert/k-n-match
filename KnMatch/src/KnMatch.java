import java.util.Arrays;
import java.util.Comparator;



public class KnMatch {

	static final int NUMBER_OF_DIMENSIONS = 3; 
	static final int n = 2;
	static final int k = 2;


	/**
	 * @param args
	 */
	public static void main(String[] args) {


		Point p1 = new Point(0.4, 1.0, 1.0);
		Point p2 = new Point(2.8, 5.5, 2.0);
		Point p3 = new Point(6.5, 7.8, 5.0);
		Point p4 = new Point(9, 9, 9);
		Point p5 = new Point(3.5, 1.5, 8);

		Point[] ps = new Point[] { p1, p2, p3, p4, p5};

		Point query = new Point(3, 7, 4);

		int appear[] = new int[ps.length];
		int h = 0; 	// h: number of point ID’s that have appeared n times
		int S[] = new int[k];

		/* 	Pre-process 
			Attributes are sorted in each dimension (that is, d sorted list)
		 */
		Point sortedLists [][] = new Point[NUMBER_OF_DIMENSIONS][ps.length];
		for(int i = 0; i<NUMBER_OF_DIMENSIONS; i++){
			for(int j=0; j<ps.length; j++){
				sortedLists[i][j] = ps[j];
			}
			final int dim = i;
			Arrays.sort(sortedLists[i], new Comparator<Point>(){
				@Override
				public int compare(Point o1, Point o2) {
					// TODO Auto-generated method stub
					return (o1.values[dim] > o2.values[dim]) ? 1 : 0;
				}});

		}
		/****************************************************************************/

		/*Locate the query’s attributes in every dimension*/
		int query_location[] = new int[NUMBER_OF_DIMENSIONS];
		for(int i = 0; i<NUMBER_OF_DIMENSIONS; i++){
			Point new_list_i[] = new Point[sortedLists[i].length+1];
			boolean inserted = false;
			int k = 0;
			for(int j=0; j<ps.length; j++){
				if(sortedLists[i][j].values[i] >= query.values[i] && !inserted){
					new_list_i[k] = query;
					query_location[i] = k;
					k++;
					inserted = true;

				}
				new_list_i[k] = sortedLists[i][j];
				k++;
			}
			if(!inserted) 
			{
				new_list_i[new_list_i.length-1] = query;
				query_location[i] = new_list_i.length-1;
			}
			sortedLists[i] = new_list_i;
		}

		/*construct triples*/
		double triples [][] = new double [2*NUMBER_OF_DIMENSIONS][3];
		for(int i = 0; i<2*NUMBER_OF_DIMENSIONS; i++){
			int ind = query_location[i/2] - (int)Math.pow(-1,i%2); // index of the closest point in dimension i
			if(ind <= sortedLists[i/2].length-1 && ind >= 0){ 
				triples[i][0] =	sortedLists[i/2][ind].id;	// pid
				triples[i][1] =	i;	// pd
				triples[i][2] =	Math.abs(query.values[i/2]-sortedLists[i/2][ind].values[i/2]);	// diff
			}
		}
		do{
			/*find the point with smallest difference among triples*/
			double smallest_diff = triples[0][2];
			int smallest_diff_ind = 0;
			for(int i = 1; i<2*NUMBER_OF_DIMENSIONS; i++){
				if(smallest_diff > triples[i][2] ){
					smallest_diff = triples[i][2];
					smallest_diff_ind = i;
				}
			}
			int pid = (int)triples[smallest_diff_ind][0]-1;
			appear[pid] ++;
			if(appear[pid] == n){
				S[h++] = pid;

			}

			int updatedDimension= (int) triples[smallest_diff_ind][1]/2;

			int ind = query_location[updatedDimension] - (int)Math.pow(-1,triples[smallest_diff_ind][1]%2);
			ind = ind - (int)Math.pow(-1,triples[smallest_diff_ind][1]%2);
			if(ind>=0 && ind < sortedLists[0].length-1){
				triples[smallest_diff_ind][0] = sortedLists[updatedDimension][ind].id ;
				triples[smallest_diff_ind][2] = Math.abs(query.values[updatedDimension]-sortedLists[updatedDimension][ind].values[updatedDimension]);	// diff
			}else{
				triples[smallest_diff_ind][2] = Integer.MAX_VALUE;
			}
			/*print triples - for test
			System.out.println("Triples: ");
			printTriples(triples); */

		}while(h<k);
		/****************************************************************************/
		
		//Results as point ids
		for(int a: S)
			System.out.println(a);
	}

	public static void printTriples(double triples[][]){
		for(int i=0; i< triples.length; i++)
			System.out.println(triples[i][0] + " " + triples[i][1] + " " + triples[i][2]);
	}
	public static void printSortedListInDimesionD(Point sortedLists[][], int d){
		for(int j=0; j<sortedLists[d].length; j++){
			System.out.println(sortedLists[d][j]);
		}


	}

}
