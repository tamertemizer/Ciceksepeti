public class Order{
	private double latitude;
	private double longtitude;
	private boolean visited;
	private int id;
	
	public Order(double la,double lo,int id){
		this.latitude=la;
		this.longtitude=lo;
		this.id=id;
		this.visited=false;
	}
	
	public void setVisited(){
		this.visited=true;	
	}
	public boolean getVisited(){
		return this.visited;	
	}
	public void setLongtitude(double l){
		this.longtitude=l;	
	}
	public void setLatitude(double l){
		this.latitude=l;	
	}
	public double getLongtitude(){
		return this.longtitude;	
	}
	public double getLatitude(){
		return this.latitude;	
	}
	public int getID(){
		return this.id;	
	}
	public String print(){
		return "{\"lat\":"+this.getLatitude()+","+"\"lng\":"+this.getLongtitude()+",\"title\":"+this.getID()+"}";
	}
	public double distance(Order s2){
		return Math.sqrt(Math.pow(this.getLatitude()-s2.getLatitude(), 2)+Math.pow(this.getLongtitude()-s2.getLongtitude(), 2));
	}
}
