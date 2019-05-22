import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class NearestNeighbor {
	LinkedList<Order> orders=new LinkedList<Order>(); //list of orders
	LinkedList<LinkedList<Order>> depotsRoutes=new LinkedList<LinkedList<Order>>(); //routes each vehicle from each depot follows
	double[][]ordersDistances; //matrix of distances between orders
	
	HashMap<Integer,String>depotColors=new HashMap<Integer,String>();
	HashMap<Integer,int[]>depotsQuotas=new HashMap<Integer,int[]>();
	//quotas of each depot
	int[] quota1= {20,30};
	int[] quota2= {35,50};
	int[] quota3= {20,80};
	String ordersFile="siparisler.csv";
	String depotsFile="bayiler.csv";
	int delivered=0;	//number of orders delivered
	
	public NearestNeighbor() throws IOException{
		assignQuotas();
		readData(ordersFile);
		ordersDistances=new double[orders.size()][orders.size()];
		fillDistancesMatrix();
		readData(depotsFile);
		calculateRoutes();
	}
	//accepts or rejects candidate neighbor depending on its relative cost to the current state
	public boolean acceptNeighbor(double cost1,double cost2,double T) {
		return Math.exp(-(cost2-cost1)/T)>Math.random();
	}
	public static void main(String[] args) throws IOException {
		//After nearest neighbor greedy approach is used to create an initial solution, "Simulated Annealing" heuristic is used to optimize the
		//solution further. 
		NearestNeighbor problem=new NearestNeighbor();
		State initialState=new State(problem.depotsRoutes);
		State temp=initialState;
		State temp1;
		double T=0.002; //Empirically adjusted to the problem
		double cost1;
		double cost2;
		State best=initialState;
		double min=best.costOfState(problem.ordersDistances);
		long noOfSteps = 10000000;
		for(int i=1;i<noOfSteps;i++){
			temp1=temp.generateNeighbor(problem.ordersDistances,problem.depotsQuotas);
			T-=0.002/noOfSteps;
			cost1=temp.costOfState(problem.ordersDistances);
			cost2=temp1.costOfState(problem.ordersDistances);
	//if cost of the neighbor state is smaller than current or distance of between them is not large given T value, move to the neighbor state
			if(cost2<cost1 || problem.acceptNeighbor(cost1,cost2,T)) {
				if(cost2<min) {
					min=temp1.costOfState(problem.ordersDistances);
					best=temp1;
				}
				temp=temp1;
			}			
		}
		//print the cost of optimum solution found
		System.out.println(best.costOfState(problem.ordersDistances));
		BufferedWriter out=new BufferedWriter(new FileWriter("output.json")); //output file containing optimal routes found
		out.write(best.printState());
		out.close();
		
	}
	//reads the coordinates of orders and depots from given files
	public void readData(String fileName) throws FileNotFoundException{
		Scanner sc = new Scanner(new File(fileName));
		sc.nextLine();
		String coordinates[];
        String line;
        int i=0;
		while(sc.hasNextLine()) {
        	line=sc.nextLine();
        	if(line.equals(""))
        		break;
            coordinates = line.split(";");
            if(fileName.equals(ordersFile)){
            	Order s=new Order(Double.valueOf(coordinates[1]),Double.valueOf(coordinates[2]),Integer.valueOf(coordinates[0]));
            	orders.add(s);
            }else if(fileName.equals(depotsFile)){
            	i++;
            	Order depot=new Order(Double.valueOf(coordinates[1]),Double.valueOf(coordinates[2]),i);
            	LinkedList<Order>route=new LinkedList<Order>();
            	route.add(depot);
            	depotsRoutes.add(route);
            	depotColors.put(i,coordinates[0]);
            }
            
        }
		sc.close();
	}
	//assigns the quotas of depots
	public void assignQuotas(){
		depotsQuotas.put(1,quota1);
		depotsQuotas.put(2,quota2);
		depotsQuotas.put(3,quota3);
	}
	//fills the matrix that contains the distances between orders
	public void fillDistancesMatrix(){
		double dist;
		for(int i=0;i<orders.size();i++){
			for(int j=i;j<orders.size();j++){
				dist=orders.get(i).distance(orders.get(j));
				ordersDistances[i][j]=dist;
				ordersDistances[j][i]=dist;
			}
		}
	}
	//finds routes followed by each vehicle from each depot using a nearest neighbor greedy approach. 
	//At each iteration the order that is not yet delivered and is closest to the order that was last delivered is delivered and added to 
	//the route of the vehicle. Vehicles from each depot take turns delivering orders.
	public void calculateRoutes(){
		double min;
		Order nextNeighbor=null;
		while(delivered!=orders.size()){

			for(int i=0;i<depotsRoutes.size();i++){
				//check if the quota of the vehicle is reached
				if(depotsRoutes.get(i).size()-1==depotsQuotas.get(i+1)[1]) 
					continue;
				
				Order temp=depotsRoutes.get(i).peekLast();
				min=Double.MAX_VALUE;
				//find the order that is not yet delivered and is closest to the last order delivered which was on the current route.
				for(int j=0;j<orders.size();j++) {
					if(!orders.get(j).getVisited() && temp.distance(orders.get(j))<min && temp.getID()!=orders.get(j).getID()) {
						nextNeighbor=orders.get(j);
						min=temp.distance(orders.get(j));
					}
				}
					//add the order to the route
					depotsRoutes.get(i).add(nextNeighbor);
					nextNeighbor.setVisited(); //order is assumed delivered
					delivered++;
					if(delivered==orders.size())
						break;
				
				
			}
		}
	}
}
