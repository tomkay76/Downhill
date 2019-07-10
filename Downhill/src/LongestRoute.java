import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class LongestRoute {

	// 1. read map and stored elevation nodes in 2D array
	
	private int[][] map = null; // map board
	private int mapx = 0; // amount of map cols
	private int mapy = 0; // amount of map rows
	
	public static void main(String[] args) throws IOException {
		var n = new LongestRoute();
		n.readMapFile();
	}
	
	private void readMapFile() throws IOException {
		
		//System.out.println("Trying to read map file");
		int currentval = 0;
		int cnt = 0;
		int icnt = 0;
		Scanner input = new Scanner(new File("4x4.txt"));
		//System.out.println("Scanner init");
		
		// iterate all integers in map file
		while(input.hasNextInt()) {
			cnt++;
			currentval = input.nextInt();
			if(cnt == 1) {
				// amount of rows
				this.mapy = currentval;
				System.out.println("Setting max y");
			} else if (cnt == 2) {	
				// amount of cols
				this.mapx = currentval;
				System.out.println("Setting max x");
				this.mapSet();
			} else if (cnt > 2) {
				// elevations
				icnt++;
				this.mapPopulate(icnt,currentval);
			}
			
		}
		System.out.println("Map populated");
		input.close();
	}
	
	private void mapSet() {
		// set array bounds for map
		//System.out.println("Setting up Map");
		this.map = new int[this.mapy][this.mapx];
	}
	
	private void mapPopulate(int icnt, int currentval) {
		// create map indices from elevation count
		//int row = (int) Math.ceil((double) icnt / this.mapy) - 1;
		//int col = icnt - ((row * this.mapx) + 1);
		int row = this.getRowIndex(icnt);
		int col = this.getColIndex(icnt);
		
		this.map[row][col] = currentval;
		System.out.println("Val:" + currentval +" Coord: " + row + "-" + col);
	}
	
	private int getRowIndex(int icnt) {
		return (int) Math.ceil((double) icnt / this.mapy) - 1;
	}
	
	private int getColIndex(int icnt) {
		return icnt - ((this.getRowIndex(icnt) * this.mapx) + 1);
	}
}
