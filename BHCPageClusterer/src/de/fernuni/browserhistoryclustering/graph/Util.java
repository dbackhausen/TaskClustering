package de.fernuni.browserhistoryclustering.graph;

import java.io.PrintStream;

import com.discoversites.util.collections.tree.Tree;
import com.discoversites.util.collections.tree.TreeNode;
import com.discoversites.util.collections.tree.TreeRoot;

import de.fernuni.browserhistoryclustering.common.utils.Stringutils;

/**
 * @author ah
 *
 * Utility class for printing a ClusterDescriptor tree.
 * Based on original work of Mark Baird
 * (https://github.com/markbaird/JavaTree).
 *
 */
public class Util{

	
	/**
	 * Print the tree to STDOUT
	 * 
	 * @param tree The tree to print
	 */
	public static void prettyPrint(Tree<ClusterDescriptor> tree)
	{		
		prettyPrint(tree, System.out);
	}

	/**
	 * Print the tree to the specified {@link PrintStream}
	 * 
	 * @param tree The tree to print
	 * @param ps The {@link PrintStream} to print to
	 */
	public static void prettyPrint(Tree<ClusterDescriptor> tree, PrintStream ps)
	{
		TreeRoot<ClusterDescriptor> root = tree.getRoot();
		ps.println("root");
		printChildrend(root, 0, ps);	
	}

	private static void printChildrend(TreeNode<ClusterDescriptor> node, int level, PrintStream ps)
	{	
		for (TreeNode<ClusterDescriptor> child : node.getChildren()) {
			
			for (int i = 0; i < level + 1; i++) {
					ps.print("|   ");
			}
			ps.println("");
			
			for (int i = 0; i < level + 1; i++) {
				if (i == level)
					ps.print("+ ");
				else
					ps.print("| ");
				if (i < level) {
					ps.print("  ");
				}
			}
			
			ClusterDescriptor v_Element = child.getElement();
			
			ps.println("#Docs: " + v_Element.getDocuments().size() + " *** Label (Topics): " + v_Element.getLabel()  +  " *** Queries: " + Stringutils.concatStrings(v_Element.getQueries(), " / ") + " *** Tasks: " + Stringutils.concatStrings(v_Element.getTasks(), " / "));
			
			printChildrend(child, level + 1, ps);
		}		
	}

	
}
