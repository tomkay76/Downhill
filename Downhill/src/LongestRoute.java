import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class LongestRoute {

	// 1. read map and stored elevation nodes in 2D array
	// 
	
	private int[][] map = null; // map board
	private int mapx = 0; // amount of map cols
	private int mapy = 0; // amount of map rows
	private String longestRoute = null;
	private int longestRouteLen = 0; // length
	private int longestRouteInc = 0; // incline
	private ArrayList<String> routes = new ArrayList<String>();
	
	public static void main(String[] args) throws IOException {
		var n = new LongestRoute();
		n.readMapFile();
		System.out.println("\n------ Process Map Nodes -----");
		n.procMapNodes();
	}
	
	private void readMapFile() throws IOException {
		
		System.out.println("\n------ Reading Map File -----");
		int currentval = 0;
		int cnt = 0;
		int icnt = 0;
		Scanner input = new Scanner(new File("4x4.txt")); // init file scanner
		
		while(input.hasNextInt()) {
			cnt++;
			currentval = input.nextInt();
			if(cnt == 1) {
				System.out.println("\n------ Set Map Dimensions -----");
				// amount of rows
				this.mapy = currentval;
				System.out.println("Setting max y");
			} else if (cnt == 2) {	
				// amount of cols
				this.mapx = currentval;
				System.out.println("Setting max x");
				System.out.println("\n------ Populate Map -----");
				this.mapSet();
			} else if (cnt > 2) {
				// elevations
				icnt++;
				this.mapPopulate(icnt,currentval);
			}
			
		}

		input.close(); // close scanner
	}
	
	private void mapSet() {
		// set array bounds for map
		this.map = new int[this.mapy][this.mapx];
	}
	
	private void mapPopulate(int icnt, int currentval) {
		// create map indices from elevation count
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
	
	private int getId(int row, int col) {
		return (row * this.mapy) + col + 1;
	}
	
	private int getElevationVal(int icnt) {
		return this.map[this.getRowIndex(icnt)][this.getColIndex(icnt)];
	}
	
	private void procMapNodes() {
		
		for(int rows = 0; rows < this.map.length; rows++) {
		    for(int columns = 0; columns < this.map[rows].length; columns++) {
		    	//System.out.print(this.map[rows][columns] + "\t" );
		    	this.nodeRouting(rows, columns);
		    }
		    //System.out.println();
		}
		
	}
	
	private void nodeRouting(int row, int col) { 
		
		int level = 0;
		
		this.routes.clear(); // clear all previous entries from arrayList
		
		System.out.println("\n------ Node Routes -----");
		
			
		if(hasIncline(this.getId(row, col))) {
			this.getInclines(row, col);
		}
		
		
	}
	
	private void getInclines(int row, int col) {
		int level = 0;
		int id = this.getId(row, col);
		ArrayList<String> nodeRoutes = new ArrayList<String>();
		ArrayList<String> newRoutes = new ArrayList<String>();
		/*
		if(level == 0) {
			nodeRoutes.add(Integer.toString(id));
		} 
		*/
		while(true) {
			
			if(level == 0) {
				nodeRoutes.add(Integer.toString(id));
			} else {
				nodeRoutes.clear();
				nodeRoutes = (ArrayList<String>) newRoutes.clone();
			}
			
			boolean isNewNodes = false;
			
			//level++;
			System.out.println("----- FOR START -----");
			for(String node : nodeRoutes) {
				
				int index = level + 1;
				System.out.println("{"+node+"}");
				String[] temp = node.split("-");
				if(temp.length < index)
					continue;
				
				//int nodeId = Integer.parseInt(temp[index]);
				int nodeId = Integer.parseInt(temp[temp.length-1]);
				int nodeRow = this.getRowIndex(nodeId);
				int nodeCol = this.getColIndex(nodeId);
				//System.out.println("L"+level);
				//System.out.println("#"+nodeId);
				
				if(nodeRow > 0) {
					// if top row exists
					int newid = this.getId(nodeRow-1, nodeCol);
					if(this.getElevationVal(nodeId) > this.getElevationVal(newid)) {
						// top adjacent has lower elevation -> routing
						isNewNodes = true;
						newRoutes.add(node + "-" + newid);
					}
				} 
				
				if(nodeCol < (this.mapx - 1)) {
					// if top row exists
					int newid = this.getId(nodeRow, nodeCol+1);
					if(this.getElevationVal(nodeId) > this.getElevationVal(newid)) {
						// top adjacent has lower elevation -> routing
						isNewNodes = true;
						newRoutes.add(node + "-" + newid);
					}
				}
				
				if(nodeRow < (this.mapy-1)) {
					// if top row exists
					int newid = this.getId(nodeRow+1, nodeCol);
					if(this.getElevationVal(nodeId) > this.getElevationVal(newid)) {
						// top adjacent has lower elevation -> routing
						isNewNodes = true;
						newRoutes.add(node + "-" + newid);
					}
				}
				
				if(nodeCol > 0) {
					// if top row exists
					int newid = this.getId(nodeRow, nodeCol-1);
					if(this.getElevationVal(nodeId) > this.getElevationVal(newid)) {
						// top adjacent has lower elevation -> routing
						isNewNodes = true;
						newRoutes.add(node + "-" + newid);
					}
				}
				
				
			}	// end FOR
			System.out.println("----- FOR END -----");
			System.out.println("isNewNodes: " + isNewNodes);
			
			if(isNewNodes == false) {			
				break;
			} else {
				level++;
			}	
			
			
		} // end WHILE	
		
		for(String op : nodeRoutes) {
			System.out.print(op + "\t");
		}
		System.out.println();
	}
	
	private boolean hasIncline(int id) {
		boolean result = false;
		
		int row = this.getRowIndex(id);
		int col = this.getColIndex(id);
		
		if(row > 0) {
			// if top row exists
			int newid = this.getId(row-1, col);
			if(this.getElevationVal(id) > this.getElevationVal(newid)) 
				result = true;
		}
		
		if(col < (this.mapx - 1)) {
			// if top row exists
			int newid = this.getId(row, col+1);
			if(this.getElevationVal(id) > this.getElevationVal(newid)) 
				result = true;
			
		}
		
		if(row < (this.mapy-1)) {
			// if top row exists
			int newid = this.getId(row+1, col);
			if(this.getElevationVal(id) > this.getElevationVal(newid))
				result = true;
		}
		
		if(col > 0) {
			// if top row exists
			int newid = this.getId(row, col-1);
			if(this.getElevationVal(id) > this.getElevationVal(newid))
				result = true;
		}
		
		return result;
	}
}
