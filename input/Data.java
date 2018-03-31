package input;

public class Data {
	 private Double time;
	 private String id;
	 
	 public Data(){
		 time = 0.0;
		 id = null;
	 }
	 
	 public void putTime(double time){
		 this.time = time;
	 }
	 
	 public void putId(String id){
		 this.id = id;
	 }
	 
	 public Double getTime(){
		 return this.time;
	 }
	 
	 public String getId(){
		 return this.id;
	 }
	 
}
