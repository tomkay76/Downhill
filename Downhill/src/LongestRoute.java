import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class LongestRoute {

	// 1. read map and store elevation nodes in 2D array
	// 2. process elevation nodes
	// 3. check for downhill inclines in all directions 
	// 4. begin routing from initial node in all directions until no further downhill inclines are available
	// 5. compare current result with recorded results and replace if higher
	
	private String mapfile = "map.txt";
	private int[][] map = null; // map board
	private int mapx = 0; // amount of map cols
	private int mapy = 0; // amount of map rows
	private String longestRoute = null;
	private int longestRouteLen = 0; // length
	private int longestRouteInc = 0; // incline
	
	public static void main(String[] args) throws IOException {
		var n = new LongestRoute();
		n.readMapFile(); // 1. get map data
		n.procMapNodes(); // 2. - 5. 
		n.getResult();		
	}
	
	private void readMapFile() throws IOException {
		
		try {
			int currentval = 0;
			int cnt = 0;
			int icnt = 0;
			Scanner input = new Scanner(new File(this.mapfile)); // init file scanner
			
			while(input.hasNextInt()) {
				cnt++;
				currentval = input.nextInt();
				if(cnt == 1) {
					// amount of rows
					this.mapy = currentval;

				} else if (cnt == 2) {	
					// amount of cols
					this.mapx = currentval;
					this.mapSet(); // set bounds
				} else if (cnt > 2) {
					// elevations
					icnt++;
					this.mapPopulate(icnt,currentval);
				}
				
			}

			input.close(); // close scanner
		} catch(IOException e) {
			System.out.println("ERROR - reading file");
		}
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
	}
	
	private int getRowIndex(int icnt) {
		return (int) Math.ceil((double) icnt / this.mapy) - 1;
	}
	
	private int getColIndex(int icnt) {
		return icnt - ((this.getRowIndex(icnt) * this.mapx) + 1);
	}
	
	private int getId(int row, int col) {
		// create incremental id to address nodes
		return (row * this.mapy) + col + 1;
	}
	
	private int getNodeElevation(int icnt) {
		// return node elevation value
		return this.map[this.getRowIndex(icnt)][this.getColIndex(icnt)];
	}
	
	private void procMapNodes() {
		// process all map nodes 
		for(int rows = 0; rows < this.map.length; rows++) {
		    for(int cols = 0; cols < this.map[rows].length; cols++) {
		    	if(hasIncline(this.getId(rows, cols))) {
					this.getInclines(rows, cols);
				}
		    }
		}
	}	
	
	private void procRoutes(ArrayList<String> routes) {
		int length = routes.size();
		int index = length - 1;
		int routeLen = 0;
		int routeInc = 0;
		String routeTot = null;
		for(int i = index; i > 0; i--) {
			//process the longest entries first
			String route = routes.get(index);
			String[] nodes = route.split("-");
			int len = nodes.length;
			int inc = this.getNodeElevation(Integer.parseInt(nodes[0])) - this.getNodeElevation(Integer.parseInt(nodes[len-1]));
			
			if(len > routeLen && inc > routeInc) {
				routeLen = len;
				routeInc = inc;
				routeTot = this.nodeIndex2Elevation(nodes);
			}
			
		}
		
		if(routeLen > this.longestRouteLen && routeInc > this.longestRouteInc) { 
			this.longestRoute = routeTot;
			this.longestRouteInc = routeInc;
			this.longestRouteLen = routeLen;
		}
	}
	
	private String nodeIndex2Elevation(String[] nodes) {

		StringBuffer sb = new StringBuffer();
		
		for(int i = 0; i < nodes.length; i++) {
			int nodeId = Integer.parseInt(nodes[i]);
			if(i > 0)
				sb.append("-");
			sb.append(this.getNodeElevation(nodeId));
			//sb.append("[");
			//sb.append(this.getRowIndex(nodeId));
			//sb.append("x");
			//sb.append(this.getColIndex(nodeId));
			//sb.append("]");
		}
		
		return sb.toString();
	}
	
	private void getInclines(int row, int col) {
		int level = 0; // 0 => first (main) node of route
		int id = this.getId(row, col); // id of first node
		
		// Two ArrayLists: first to iterate through, second to store updated routes
		ArrayList<String> nodeRoutes = new ArrayList<String>();
		ArrayList<String> newRoutes = new ArrayList<String>();

		while(true) {
			// loop until the last route, which originated at current main node (id), reached its final node
			if(level == 0) {
				// push main node 
				nodeRoutes.add(Integer.toString(id));
			} else {
				// clear & update list for next iteration
				nodeRoutes.clear();
				nodeRoutes.addAll(newRoutes);
			}
			
			boolean isNewNodes = false;
			
			// start iterating through all current routes to find all new branches
			for(String node : nodeRoutes) {
				
				int index = level + 1;
				String[] temp = node.split("-");
				if(temp.length < index)
					continue; // current route already reached its final node, skip to next route
				
				// get last node of route (id, row-index, col-index)
				int nodeId = Integer.parseInt(temp[temp.length-1]);
				int nodeRow = this.getRowIndex(nodeId);
				int nodeCol = this.getColIndex(nodeId);
				
				if(nodeRow > 0) {
					// if top row exists
					int newid = this.getId(nodeRow-1, nodeCol);
					if(this.getNodeElevation(nodeId) > this.getNodeElevation(newid)) {
						// top adjacent has lower elevation -> routing
						isNewNodes = true;
						newRoutes.add(node + "-" + newid);
					}
				} 
				
				if(nodeCol < (this.mapx - 1)) {
					// if right col exists
					int newid = this.getId(nodeRow, nodeCol+1);
					if(this.getNodeElevation(nodeId) > this.getNodeElevation(newid)) {
						// top adjacent has lower elevation -> routing
						isNewNodes = true;
						newRoutes.add(node + "-" + newid);
					}
				}
				
				if(nodeRow < (this.mapy-1)) {
					// if bottom row exists
					int newid = this.getId(nodeRow+1, nodeCol);
					if(this.getNodeElevation(nodeId) > this.getNodeElevation(newid)) {
						// top adjacent has lower elevation -> routing
						isNewNodes = true;
						newRoutes.add(node + "-" + newid);
					}
				}
				
				if(nodeCol > 0) {
					// if left col exists
					int newid = this.getId(nodeRow, nodeCol-1);
					if(this.getNodeElevation(nodeId) > this.getNodeElevation(newid)) {
						// top adjacent has lower elevation -> routing
						isNewNodes = true;
						newRoutes.add(node + "-" + newid);
					}
				}
				
				
			}	// end FOR
			
			if(isNewNodes == false) {
				// all routes of main node have reached their final node, process results and abort loop
				this.procRoutes(nodeRoutes);
				break;
			} else {
				level++;
			}	
			
			
		} // end WHILE	
		
	}
	
	private boolean hasIncline(int id) {
		// check whether current main node has any downhill inclines
		boolean result = false;
		
		int row = this.getRowIndex(id);
		int col = this.getColIndex(id);
		
		if(row > 0) {
			// if top row exists
			int newid = this.getId(row-1, col);
			if(this.getNodeElevation(id) > this.getNodeElevation(newid)) 
				result = true;
		}
		
		if(col < (this.mapx - 1)) {
			// if top row exists
			int newid = this.getId(row, col+1);
			if(this.getNodeElevation(id) > this.getNodeElevation(newid)) 
				result = true;
			
		}
		
		if(row < (this.mapy-1)) {
			// if top row exists
			int newid = this.getId(row+1, col);
			if(this.getNodeElevation(id) > this.getNodeElevation(newid))
				result = true;
		}
		
		if(col > 0) {
			// if top row exists
			int newid = this.getId(row, col-1);
			if(this.getNodeElevation(id) > this.getNodeElevation(newid))
				result = true;
		}
		
		return result;
	}
	
	private void getResult() {
		// output result:
		System.out.println("Longest Route: " + this.longestRoute);
		System.out.println("Longest Route Length: " + this.longestRouteLen);
		System.out.println("Longest Route Incline: " + this.longestRouteInc);
	}
}
