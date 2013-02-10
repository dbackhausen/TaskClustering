package de.fernuni.browserhistoryclustering.pageclusterer.clustering;

import org.apache.hadoop.conf.Configuration;

import de.fernuni.browserhistoryclustering.graph.ClusterDescriptor;
import de.fernuni.browserhistoryclustering.graph.SetTree;

public interface ClustererIF {

   SetTree<ClusterDescriptor> run(Configuration p_HadoopConfig, Boolean p_Sequential) throws Exception;
   
}
