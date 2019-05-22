import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public class State {
	private LinkedList<LinkedList<Order>>routes;
	
	public State(LinkedList<LinkedList<Order>>routess){
		routes=new LinkedList<LinkedList<Order>>();
		for(int i=0;i<routess.size();i++){
			LinkedList<Order>list=new LinkedList<Order>(routess.get(i));
			this.routes.add(list);
		}
	}
	//calculates lengths of each route and adds them, which is the cost of a state 
	public double costOfState(double[][]distances){
		double sum=0;
		for(int i=0;i<routes.size();i++) {
			sum+=routes.get(i).get(0).distance(routes.get(i).get(1));
			int j=1;
			for(;j<routes.get(i).size()-1;j++){
				sum+=distances[routes.get(i).get(j).getID()-distances.length][routes.get(i).get(j+1).getID()-distances.length];
			}
			sum+=routes.get(i).get(j).distance(routes.get(i).get(0));
		}
		return sum;
	}
	//The neighbors of a state is defined as follows; two consecutive orders on one route can be swapped to reach a neighbor or
	//distance of each order on a route to route's mean is calculated and weighted probabilities are assigned to orders in such a way that further
	//the order is from the mean the higher the probability of the order being swapped out from the route and given to another route.
	//The swapped out order will come right after the order that it is closest to in the route it is given.
	public State generateNeighbor(double[][]distances,HashMap<Integer,int[]>bayiKotalar){
		State neighbor=new State(routes);
		int selectMode=ThreadLocalRandom.current().nextInt(0,2);
		int randomRoute=ThreadLocalRandom.current().nextInt(0,routes.size());
		
		if(selectMode==0){
			//swap to consecutive orders on randomly selected route
			int randomOrder=ThreadLocalRandom.current().nextInt(1,routes.get(randomRoute).size()-1);
			Collections.swap(neighbor.routes.get(randomRoute), randomOrder, randomOrder+1);
		}else{
			
			while((bayiKotalar.get(randomRoute+1))[0]==routes.get(randomRoute).size()-1)
				randomRoute=(randomRoute+1)%routes.size();

			double x=0;
			double y=0;
			double sum=0;
			LinkedList<Double>swapProb=new LinkedList<Double>();
			//find the mean of the randomly selected route
			for(int i=0;i<routes.get(randomRoute).size();i++) {
				x+=routes.get(randomRoute).get(i).getLatitude();
				y+=routes.get(randomRoute).get(i).getLongtitude();
			}
			x/=routes.get(randomRoute).size();
			y/=routes.get(randomRoute).size();
			Order mean=new Order(x,y,0);
			for(int i=1;i<routes.get(randomRoute).size();i++) {
				double dist=routes.get(randomRoute).get(i).distance(mean);
				sum+=dist;
				swapProb.add(dist);
			}
			//assign weighted probabilities of being swapped out of the route
			swapProb.set(0, swapProb.get(0)/sum);
			for(int i=1;i<swapProb.size();i++) {
				swapProb.set(i, swapProb.get(i)/sum+swapProb.get(i-1));
			}
			double rand=Math.random();
			int orderIndex=1;
			//select randomly the order to be swapped out 
			for(;orderIndex<swapProb.size();orderIndex++) {
				if(swapProb.get(orderIndex)>=rand)
					break;
			}
			int i=ThreadLocalRandom.current().nextInt(0,routes.size());
			//select randomly to which route the swapped out order will be assigned
				while(i==randomRoute || (bayiKotalar.get(i+1))[1]==routes.get(i).size()-1)
					i=ThreadLocalRandom.current().nextInt(0,routes.size());
				
				int index=1;
				double min=Double.MAX_VALUE;
				for(int j=1;j<routes.get(i).size();j++) {
					if(routes.get(i).get(j).distance(routes.get(randomRoute).get(orderIndex+1))<min) {
						min=routes.get(i).get(j).distance(routes.get(randomRoute).get(orderIndex+1));
						index=j;
					}
				}
				//swap out an order from a randomly chosen route and put that order to another randomly chosen route
				neighbor.routes.get(i).add(index+1, routes.get(randomRoute).get(orderIndex+1));
				neighbor.routes.get(randomRoute).remove(orderIndex+1);
		}
		return neighbor;
	}
	//prints the current state
	public String printState(){
		String paths="";
		paths += "[";
		for(int i=0;i<routes.size();i++) {
			paths += "[";
			for(int j=0;j<routes.get(i).size();j++) {
				paths+=routes.get(i).get(j).print() +",";
			}
			paths = paths.substring(0, paths.length() - 1);
			paths+="\n";
			paths += "],";
		}
		paths = paths.substring(0, paths.length() - 1);
		paths += "]";
		return paths;
	}
}
