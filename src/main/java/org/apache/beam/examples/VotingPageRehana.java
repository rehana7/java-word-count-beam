package org.apache.beam.examples;

import java.io.Serializable;

public class VotingPageRehana implements Serializable{

    
    String name = "unknown.md";
    Double rank = 1.0;
    Integer votes = 0;

    public VotingPageRehana(String nameIn, Double rank, Integer votesIn) {
        this.name = nameIn;
        this.rank = rank;
        this.votes = votesIn;
    }

    public VotingPageRehana(String voterName, Integer contributorVotes) {
        this.name = voterName;
        this.votes = contributorVotes;
    }

    public String getName() {
        return this.name;
    }

   
    public Double getRank() {
        return this.rank;
    }

    
    public Integer getVotes() {
        return this.votes;
    }

   

    @Override
    public String toString() {
       // return "VotingPageRehana [name=" + this.name + ", rank=" + this.rank + ", votes=" + this.votes + "]"; 
        return String.format( " %s, %.5f, %d", this.name, this.rank, this.votes  );

        
    
    }
    
    

}
