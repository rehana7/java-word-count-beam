/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.examples;

import java.util.ArrayList;
import java.io.Serializable;
// beam-playground:
//   name: MinimalWordCount
//   description: An example that counts words in Shakespeare's works.
//   multifile: false
//   pipeline_options:
//   categories:
//     - Combiners
//     - Filtering
//     - IO
//     - Core Transforms

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

//import org.apache.beam.model.pipeline.v1.RunnerApi.PCollection;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Combine;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.Filter;
import org.apache.beam.sdk.transforms.FlatMapElements;
import org.apache.beam.sdk.transforms.Flatten;
import org.apache.beam.sdk.transforms.GroupByKey;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.Max;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionList;
import org.apache.beam.sdk.values.TypeDescriptors;

/**
 * An example that counts words in Shakespeare.
 *
 * <p>This class, {@link MinimalWordCount}, is the first in a series of four successively more
 * detailed 'word count' examples. Here, for simplicity, we don't show any error-checking or
 * argument processing, and focus on construction of the pipeline, which chains together the
 * application of core transforms.
 *
 * <p>Next, see the {@link WordCount} pipeline, then the {@link DebuggingWordCount}, and finally the
 * {@link WindowedWordCount} pipeline, for more detailed examples that introduce additional
 * concepts.
 *
 * <p>Concepts:
 *
 * <pre>
 *   1. Reading data from text files
 *   2. Specifying 'inline' transforms
 *   3. Counting items in a PCollection
 *   4. Writing data to text files
 * </pre>
 *
 * <p>No arguments are required to run this pipeline. It will be executed with the DirectRunner. You
 * can see the results in the output files in your current working directory, with names like
 * "wordcounts-00001-of-00005. When running on a distributed service, you would use an appropriate
 * file service.
 */
public class MinimalPageRankRehana {

    // DEFINE DOFNS
  // ==================================================================
  // You can make your pipeline assembly code less verbose by defining
  // your DoFns statically out-of-line.
  // Each DoFn<InputT, OutputT> takes previous output
  // as input of type InputT
  // and transforms it to OutputT.
  // We pass this DoFn to a ParDo in our pipeline.

  /**
   * DoFn Job1Finalizer takes KV(String, String List of outlinks) and transforms
   * the value into our custom RankedPageRehana Value holding the page's rank and list
   * of voters.
   * 
   * The output of the Job1 Finalizer creates the initial input into our
   * iterative Job 2.
   */
   /**
    *
    */
  static class Job1Finalizer extends DoFn<KV<String, Iterable<String>>, KV<String, RankedPageRehana>> {
    @ProcessElement
    public void processElement(@Element KV<String, Iterable<String>> element,
        OutputReceiver<KV<String, RankedPageRehana>> receiver) {
      Integer contributorVotes = 0;
      if (element.getValue() instanceof Collection) {
        contributorVotes = ((Collection<String>) element.getValue()).size();
      }
      ArrayList<VotingPageRehana> voters = new ArrayList<VotingPageRehana>();
      for (String voterName : element.getValue()) {
        if (!voterName.isEmpty()) {
          voters.add(new VotingPageRehana(voterName, contributorVotes));
        }
      }
      receiver.output(KV.of(element.getKey(), new RankedPageRehana(element.getKey(), voters)));
    }
  }

  /**
   * Output Job1
   * KV{go.md, go.md,1.00000,[ README.md, 1.00000, 1]}
   * KV{python.md, python.md,1.00000,[ README.md, 1.00000, 1]}
   * KV{README.md, README.md,1.00000,[ go.md, 1.00000, 3,  java.md, 1.00000, 3,  python.md, 1.00000, 3]}
   * KV{java.md, java.md,1.00000,[ README.md, 1.00000, 1]}
  */


  /**
   * DoFn Job2Mapper
   * Flat map voter pages to keys each with key page with rank as value.
   * 
   */

   static class Job2Mapper extends DoFn<KV<String,RankedPageRehana>,KV<String, RankedPageRehana>> {

    @ProcessElement   
    public void processElement(@Element KV<String,RankedPageRehana>element,OutputReceiver<KV<String, RankedPageRehana>>receiver) {
      // Set zero for the integer votes.
      Integer votes =0;
      // creating the arrayList<VotingPage> named voters
      // set the element getValue() , for RankedPage and call getVoters()
      ArrayList<VotingPageRehana> voters = element.getValue().getVoters();
      //  
      if(voters instanceof Collection) { 
        votes = ((Collection<VotingPageRehana>) voters).size();

      }
      for(VotingPageRehana vp : voters){
        String pageName = vp.getName();
        Double pageRank = vp.getRank();
        String contributingPageName = element.getKey();
        Double contributingPageRank = element.getValue().getRank();
        VotingPageRehana contributor = new VotingPageRehana(contributingPageName, contributingPageRank, votes );
        ArrayList<VotingPageRehana> arr = new ArrayList<VotingPageRehana>();
        arr.add(contributor);
        receiver.output(KV.of(vp.getName(), new RankedPageRehana(pageName, pageRank,arr)));
      
      }




    }
   } 
static class Job2Updater extends DoFn<KV<String , Iterable<RankedPageRehana>>,KV<String, RankedPageRehana>>{
  @ProcessElement   
  public void processElement(@Element KV<String,Iterable<RankedPageRehana>> element, OutputReceiver<KV<String, RankedPageRehana>>receiver) {
    // Set zero for the integer votes.
    Integer votes =0;
    // creating the arrayList<VotingPage> named voters
    // set the element getValue() , for RankedPage and call getVoters()
   Double dampingFactor = 0.85;
   Double updateRank = 1-dampingFactor;
    ArrayList<VotingPageRehana> newvotes = new ArrayList<>();
    for(RankedPageRehana rankpage:element.getValue()){
        if(rankpage != null){
          for(VotingPageRehana votePage : rankpage.getVoters()){
            newvotes.add(votePage);
            updateRank += dampingFactor * votePage.getRank()/ (double) votePage.getVotes();

          }
        }
    }
   receiver.output(KV.of(element.getKey(),new RankedPageRehana(element.getKey(),updateRank, newvotes)));

  }
}
static class Job3 extends DoFn<KV<String, RankedPageRehana>, KV<String, Double>> {
  @ProcessElement
  public void processElement(@Element KV<String, RankedPageRehana> element,
      OutputReceiver<KV<String, Double>> receiver) {
    String currentPage = element.getKey();
    Double currentPageRank = element.getValue().getRank();

    receiver.output(KV.of(currentPage, currentPageRank));
  }
}

public static class Job3Final implements Comparator<KV<String, Double>>, Serializable {
  @Override
  public int compare(KV<String, Double> value1, KV<String, Double> value2) {
    return value1.getValue().compareTo(value2.getValue());
  }
}

/**
 * 
 * @param args
 */
  public static void main(String[] args) { 



    // Create a PipelineOptions object. This object lets us set various execution
    // options for our pipeline, such as the runner you wish to use. This example
    // will run with the DirectRunner by default, based on the class path configured
    // in its dependencies.
    PipelineOptions options = PipelineOptionsFactory.create();

    // In order to run your pipeline, you need to make following runner specific changes:
    //
    // CHANGE 1/3: Select a Beam runner, such as BlockingDataflowRunner
    // or FlinkRunner.
    // CHANGE 2/3: Specify runner-required options.
    // For BlockingDataflowRunner, set project and temp location as follows:
    //   DataflowPipelineOptions dataflowOptions = options.as(DataflowPipelineOptions.class);
    //   dataflowOptions.setRunner(BlockingDataflowRunner.class);
    //   dataflowOptions.setProject("SET_YOUR_PROJECT_ID_HERE");
    //   dataflowOptions.setTempLocation("gs://SET_YOUR_BUCKET_NAME_HERE/AND_TEMP_DIRECTORY");
    // For FlinkRunner, set the runner as follows. See {@code FlinkPipelineOptions}
    // for more details.
    //   options.as(FlinkPipelineOptions.class)
    //      .setRunner(FlinkRunner.class);

    // Create the Pipeline object with the options we defined above
    Pipeline p = Pipeline.create(options);

    // Concept #1: Apply a root transform to the pipeline; in this case, TextIO.Read to read a set
    // of input text files. TextIO.Read returns a PCollection where each element is one line from
    // the input text (a set of Shakespeare's texts).

    // This example reads from a public dataset containing the text of King Lear.
    //
    // DC: We don't need king lear....
    // We want to read from a folder - assign to a variable since it may change.
    // We want to read from a file - just one - we need the file name - assign to a variable. 

    String dataFolder = "web04";
    String dataFile = "go.md";
    // 
    PCollection<KV<String, String>> pcol1 = InputmapperFile(p, dataFolder, "go.md");
    PCollection<KV<String, String>> pcol2 = InputmapperFile(p, dataFolder, "java.md");
    PCollection<KV<String, String>> pcol3 = InputmapperFile(p, dataFolder, "python.md");
    PCollection<KV<String, String>> pcol4 = InputmapperFile(p, dataFolder, "README.md");
// will make a list of all
    PCollectionList<KV<String, String>> pcolList = PCollectionList.of(pcol1).and(pcol2).and(pcol3).and(pcol4); 
   // apply flatten to a single PCollection.
    PCollection<KV<String, String>> mergedkv = pcolList.apply(Flatten.<KV<String, String>>pCollections());
 
    // Group by Key to get a single record for each page
    PCollection<KV<String, Iterable<String>>> kvStringReducedPairs = mergedkv
        .apply(GroupByKey.<String, String>create());

    // Convert to a custom Value object (RankedPage) in preparation for Job 2
    PCollection<KV<String, RankedPageRehana>> job2in = kvStringReducedPairs.apply(ParDo.of(new Job1Finalizer()));

// End Job1 

PCollection<KV<String, RankedPageRehana>> job2out = null;
int iterationscount = 40; 
// using job2in for the calculations of job2out
for(int i= 1;i<= iterationscount; i++){
 PCollection<KV<String,RankedPageRehana>> job2mapper = job2in.apply(ParDo.of(new Job2Mapper()));
 PCollection<KV<String,Iterable<RankedPageRehana>>> job2mapperGroupByKey = job2mapper.apply(GroupByKey.create());
 job2out = job2mapperGroupByKey.apply(ParDo.of(new Job2Updater()));
 job2in =job2out; // job2out is reading into job2in



}

PCollection<KV<String, Double>> maximumRank = job2out.apply(ParDo.of(new Job3()));

PCollection<KV<String, Double>> finalMaximum = maximumRank.apply(Combine.globally(Max.of(new Job3Final())));


    // into the format of go.md and README.md  
 //  PCollection<KV<String,Iterable<String>>> ReducedkvPairs = mergedkv.apply(
  //   GroupByKey.<String,String>create());

// using tostring() : changing the kv pairs to string, 
    PCollection<String> Rehanaoutput = finalMaximum.apply(
      MapElements.into(TypeDescriptors.strings())
      .via(kvinput -> kvinput.toString())
    ); 
    Rehanaoutput.apply(TextIO.write().to("RehanaOut")); 
    p.run().waitUntilFinish();
  }

  //  String dataPath = dataFolder + "/" + dataFile;
    //p.apply(TextIO.read().from("gs://apache-beam-samples/shakespeare/kinglear.txt"))

   // PCollection<String> pcolInputLines = p.apply(TextIO.read().from(dataPath)); 
     // PCollection<String> pcolLinkLines= pcolInputLines.apply(Filter.by((String line) -> !line.isEmpty())) 
     //.apply(Filter.by((String line) -> !line.equals("")))
     
   // PCollection<String> pcolLinkLines = pcolInputLines.apply(Filter.by((String line) -> line.startsWith("[")));
   // PCollection<String> pcolLinks =  pcolLinkLines.apply(MapElements
        //    .into(TypeDescriptors.strings())
        //        .via(
        //          (String linkline) ->
        //           linkline.substring(linkline.indexOf("(")+1, linkline.length()-1)
        //           )
        //           );
 
        // // Concept #2: Apply a FlatMapElements transform the PCollection of text lines.
        // This transform splits the lines in PCollection<String>, where each element is an
        // individual word in Shakespeare's collected texts.
        //   .apply(
        //       FlatMapElements.into(TypeDescriptors.strings())
        //           .via((String line) -> Arrays.asList(line.split("[^\\p{L}]+"))))
        // // // // We use a Filter transform to avoid empty word
        //.apply(Filter.by((String word) -> !word.isEmpty()))
        // Concept #3: Apply the Count transform to our PCollection of individual words. The Count
        // transform returns a new PCollection of key/value pairs, where each key represents a
        // unique word in the text. The associated value is the occurrence count for that word.
        //.apply(Count.perElement())
        // Apply a MapElements transform that formats our PCollection of word counts into a
        // printable string, suitable for writing to an output file.
        //  .apply(
        //      MapElements.into(TypeDescriptors.strings())
        //          .via(
        //              (KV<String, String> OutputT) ->
        //              OutputT.getKey() + ": " + OutputT.getValue()))
        // // // Concept #4: Apply a write transform, TextIO.Write, at the end of the pipeline.
        // TextIO.Write writes the contents of a PCollection (in this case, our PCollection of
        // formatted strings) to a series of text files.
        //
        // By default, it will write to a set of files with names like wordcounts-00001-of-00005
       // pcolLinks.apply(TextIO.write().to("RehPR"));
   //    Rehanaoutput.apply(TextIO.write().to("RehanaPageRank"));
   // p.run().waitUntilFinish();
//} 
/**
 *
 * @param p
 * @param dataFolder
 * @param dataFile
 * @return
 */
  private static PCollection<KV<String, String>> InputmapperFile(Pipeline p, String dataFolder, String dataFile){
    String datapath = dataFolder + "/" + dataFile;
    PCollection<String> inputline = p.apply(TextIO.read().from(datapath));
    PCollection<String> longlinklines = inputline
    .apply(Filter.by((String line) -> line.startsWith("[")));
    // PCollection<String> linkline = longlinklines
    // .apply(MapElements.into(
    //   TypeDescriptors.strings())
    //   .via((String linkline -> linkline.strip()));

    // formating the out links
    PCollection<String> linkpage = longlinklines
    .apply(MapElements
    .into(TypeDescriptors.strings())
    .via(linkline -> linkline
    .substring(linkline.indexOf("(") + 1, linkline.length()-1)));
    // mapping the links with the respective file names passed to it. 
    PCollection<KV<String, String>> kvpairs = linkpage.apply(MapElements
    .into( TypeDescriptors.kvs(
     TypeDescriptors.strings(),
     TypeDescriptors.strings()))
    .via(
      outlink -> KV.of(dataFile, outlink  )
    )
    );
    
    
    // Returning kv pairs
  
    return kvpairs;

  }


}
