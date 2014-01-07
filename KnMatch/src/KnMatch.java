import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;

import com.sun.corba.se.impl.orb.ParserTable.TestContactInfoListFactory;



public class KnMatch {

	static final int GLASS_ROW = 214;
	static final int GLASS_DIMENSION = 9;

	static final int SPAM_ROW = 4601;
	static final int SPAM_DIMENSION = 57;

	static final int ION_ROW = 351;
	static final int ION_DIMENSION = 34;	

	static final int IRISH_ROW = 150;
	static final int IRISH_DIMENSION = 4;	

	static final int SEG_ROW = 210;
	static final int SEG_DIMENSION = 19;	

	static final int NUMBER_OF_DIMENSIONS = GLASS_DIMENSION; 
	static int d = GLASS_DIMENSION ;
	static final int k = 20; /*number of points in answer set*/
	
	static final int TEST_NUMBER = 100;

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {


		/*	
		 * Sample data set and query in the paper to follow the algorithm
		Point p1 = new Point(0.4, 1.0, 1.0);
		Point p2 = new Point(2.8, 5.5, 2.0);
		Point p3 = new Point(6.5, 7.8, 5.0);
		Point p4 = new Point(9, 9, 9);
		Point p5 = new Point(3.5, 1.5, 8);
		Point query = new Point(3, 7, 4);

		Point[] ps = new Point[] { p1, p2, p3, p4, p5, query}; 
		
		*/
		int SS[][] = new int[NUMBER_OF_DIMENSIONS+1][k];


		double total = 0;
		for(int testCount = 0; testCount<TEST_NUMBER; testCount++){ // we run TEST_NUMBER query randomly selected from data set
			Point.counter = 1;
			Point[] ps = fillDbFromFile("glass.data",GLASS_ROW);
			Point query = ps[(int)(Math.random()*(ps.length-1))];
			//System.out.println("Query:" + query);
			double success_counter = 0;
			for(int n=1; n<=d; n++){ //for frequent k-n match, a range of n values from 1 to d is used 

				int resultSet[] = new int[k];
				int appear[] = new int[ps.length];
				int h = 0; 	// h: number of point ID’s that have appeared n times
				int S[] = new int[k];
				for(Point p:ps){
					p.appearNumber = 0;
				}

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
							return Double.compare(o1.values[dim] ,o2.values[dim]);
						}});

				}
				/****************************************************************************/

				/*Locate the query’s attributes in every dimension*/
				int query_location[] = new int[NUMBER_OF_DIMENSIONS];

				for(int i = 0; i<NUMBER_OF_DIMENSIONS; i++){
					query_location[i] = find_index(sortedLists[i],query);
				}

				/*construct triples*/
				double triples [][] = new double [2*NUMBER_OF_DIMENSIONS][3];
				for(int i = 0; i<2*NUMBER_OF_DIMENSIONS; i++){
					int ind = query_location[i/2] - (int)Math.pow(-1,i%2); // index of the closest point in dimension i
					if(ind <= sortedLists[i/2].length-1 && ind >= 0){ 
						triples[i][0] =	sortedLists[i/2][ind].id;	// pid
						triples[i][1] =	i;	// pd
						triples[i][2] =	Math.abs(query.values[i/2]-sortedLists[i/2][ind].values[i/2]);	// diff
					}else{
						triples[i][2] =	Integer.MAX_VALUE;	// diff

					}
				}
				do{
					/*find the point with smallest difference among triples*/
					double smallest_diff = Integer.MAX_VALUE;
					int smallest_diff_ind = 0;
					boolean determined = false;
					for(int i = 0; i<2*NUMBER_OF_DIMENSIONS; i++){
						if(smallest_diff > triples[i][2] && triples[i][0] != 0 ){
							smallest_diff = triples[i][2];
							smallest_diff_ind = i;
							determined = true;
						}
					}
					if(!determined) break;
					int pid = (int)triples[smallest_diff_ind][0]-1;
					if(pid == -1) {
						System.out.println("pid=-1 ERROR!");
						printTriples(triples);
						printSortedListInDimesionD(sortedLists, 0);
						System.exit(0);
					}
					appear[pid] ++;
					if(appear[pid] == n){
						S[h++] = pid;
					}

					int updatedDimension= (int) triples[smallest_diff_ind][1]/2;

					int ind = query_location[updatedDimension] - (int)Math.pow(-1,triples[smallest_diff_ind][1]%2);
					ind = ind - (int)Math.pow(-1,triples[smallest_diff_ind][1]%2);
					query_location[updatedDimension] = ind;			
					if(ind>=0 && ind < sortedLists[0].length-1){
						triples[smallest_diff_ind][0] = sortedLists[updatedDimension][ind].id ;
						triples[smallest_diff_ind][2] = Math.abs(query.values[updatedDimension]-sortedLists[updatedDimension][ind].values[updatedDimension]);	// diff
					}else{
						triples[smallest_diff_ind][2] = Integer.MAX_VALUE;
					}
					//print triples - for test
					//System.out.println("Triples: ");
					//printTriples(triples); 

				}while(h<k);
				/****************************************************************************/

				int index = 0;
				for(int a: S){
					//System.out.print(a+1 + " ");
					resultSet[index++] = ps[a].id;
				}
				SS[n] = resultSet;
			}
			for(int i=1; i<=NUMBER_OF_DIMENSIONS; i++){
				for(int j=0; j<k; j++){
					if(SS[i][j]!=0)ps[SS[i][j]-1].appearNumber ++;		
				}
			}
			Arrays.sort(ps, new Comparator<Point>(){
				@Override
				public int compare(Point o1, Point o2) {
					// TODO Auto-generated method stub
					return (- o1.appearNumber + o2.appearNumber) ;
				}});


			for(int i=0; i<k;i++){
				if(ps[i].label.equalsIgnoreCase(query.label)) success_counter++;
			}	
			total += success_counter/k;
		}
		System.out.println("Success Rate:" + total/TEST_NUMBER);

	}
	private static int find_index(Point[] points, Point query) {
		// TODO Auto-generated method stub
		for(int i =0; i<points.length; i++ )
			if(points[i].id == query.id) return i;
		return -1;
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
	public static Point[] fillDbFromFile(String filename, int size){
		Point[] dataSet = new Point[size];
		try{
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream(filename);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line
			int counter = 0;
			while ((strLine = br.readLine()) != null) 	{
				//System.out.println(strLine);
				StringTokenizer sT = new StringTokenizer(strLine,",");
				sT.nextToken();
				double[] attrs = new double[NUMBER_OF_DIMENSIONS];
				for(int i = 0; i<NUMBER_OF_DIMENSIONS; i++){
					attrs[i] = Double.parseDouble(sT.nextToken());
				}
				Point p = new Point(attrs);
				//System.out.println(sT.nextToken());
				p.setLabel(sT.nextToken());
				dataSet[counter++] = p; 

			}
			//Close the input stream
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
			System.exit(0);
		}
		return dataSet;

	}

}
