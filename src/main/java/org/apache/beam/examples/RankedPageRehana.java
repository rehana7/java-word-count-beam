package org.apache.beam.examples;

import java.io.Serializable;
import java.util.ArrayList;

public class RankedPageRehana implements Serializable{
  

    String name = "unknown.md";
    Double rank = 1.000;
    ArrayList<VotingPageRehana>  voters= new ArrayList<VotingPageRehana>(); 

    public RankedPageRehana(String nameIn, Double rankIn, ArrayList<VotingPageRehana> votersIn) {
    this.name = nameIn;
    this.rank = rankIn;
    this.voters = votersIn; 
    }
public RankedPageRehana(String nameIn, ArrayList<VotingPageRehana> votersIn) { 
    this.name = nameIn;
    this.voters = votersIn; 

    }
/**
 * 
 * @return
 */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getRank() {
        return this.rank;
    }

   
    public ArrayList<VotingPageRehana> getVoters() {
        return this.voters;
    }

    public void setVoters(ArrayList<VotingPageRehana> voters) {
        this.voters = voters;
    }
    @Override
    public String toString() {
      //  return "RankedPageRehana [name=" + this.name + ", rank=" + this.rank + ", voters=" + this.voters.toString() + "]";
         return String.format( "%s,%.5f,%s", this.name, this.rank,this.voters.toString() ); 
        
        }
   
    
    
}
