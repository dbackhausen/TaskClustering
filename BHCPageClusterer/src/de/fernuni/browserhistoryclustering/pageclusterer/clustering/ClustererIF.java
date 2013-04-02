package de.fernuni.browserhistoryclustering.pageclusterer.clustering;

import org.apache.hadoop.conf.Configuration;

import de.fernuni.browserhistoryclustering.graph.ClusterDescriptor;
import de.fernuni.browserhistoryclustering.graph.SetTree;

/**
 * @author ah
 *
 */
public interface ClustererIF {

   /**
    * @param p_HadoopConfig
    * @param p_Sequential
    * @return Structure of cluster descriptors
    * @throws Exception
    */
   SetTree<ClusterDescriptor> run(Configuration p_HadoopConfig, Boolean p_Sequential) throws Exception;
   
}
