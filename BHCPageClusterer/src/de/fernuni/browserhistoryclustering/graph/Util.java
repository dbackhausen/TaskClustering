package de.fernuni.browserhistoryclustering.graph;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.discoversites.util.collections.tree.Tree;
import com.discoversites.util.collections.tree.TreeNode;
import com.discoversites.util.collections.tree.TreeRoot;

import de.fernuni.browserhistoryclustering.common.utils.Stringutils;

public class Util{

	
	/**
	 * Print the tree to STDOUT
	 * 
	 * @param tree The tree to print
	 */
	public static void prettyPrintFiltered(Tree<ClusterDescriptor> tree)
	{		
		prettyPrintFiltered(tree, System.out);
	}
	
	/**
	 * Print the tree to STDOUT
	 * 
	 * @param tree The tree to print
	 */
	public static void prettyPrintLabel(Tree<ClusterDescriptor> tree)
	{		
		prettyPrintLabel(tree, System.out);
	}
	
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
		printChildren(root, 0, ps);	
	}

	private static void printChildren(TreeNode<ClusterDescriptor> node, int level, PrintStream ps)
	{	
		int childCount = 0;
		for (TreeNode<ClusterDescriptor> child : node.getChildren()) {
			childCount++;
			
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
			
			if(v_Element.getLabel().equals("7-0"))
			{
				int a=0;
				v_Element.filterNormtopics_Internal();
			}			
			
			Map<String, Double> v_Normtopic2Value = v_Element.getRankedNormtopics();
			Set<String> v_Normtopics = v_Element.getRankedNormtopics().keySet();
			List<String> v_FilteredNormtopics = v_Element.getFilteredNormtopics();
			v_Normtopics.retainAll(v_FilteredNormtopics);
			
			
			int j = 0;
			StringBuilder v_TopicConcat = new StringBuilder();
			for (String v_Topic : v_Normtopic2Value.keySet())
			{
				if (j > 0 )
				{
					v_TopicConcat.append(" | ");
				}
				
				v_TopicConcat.append(v_Topic);
				
				if (j > 2) break;
				j++;
				
			}
									
			List<String> v_Files = v_Element.getDocuments();
			
			ps.println("- " + v_Element.getLabel() + " *** #docs: " + v_Files.size() + " *** Topics: " + v_TopicConcat);
			
			printChildren(child, level + 1, ps);
		}		
	}
	
	/**
	 * Print the tree to the specified {@link PrintStream}
	 * 
	 * @param tree The tree to print
	 * @param ps The {@link PrintStream} to print to
	 */
	public static void prettyPrintFiltered(Tree<ClusterDescriptor> tree, PrintStream ps)
	{
		TreeRoot<ClusterDescriptor> root = tree.getRoot();
		ps.println("root");
		printChildrenFiltered(root, 0, ps);	
	}

	private static void printChildrenFiltered(TreeNode<ClusterDescriptor> node, int level, PrintStream ps)
	{	
		int childCount = 0;
		for (TreeNode<ClusterDescriptor> child : node.getChildren()) {
			childCount++;
			
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
			
			List<String> v_Files = v_Element.getDocuments();
			
			ps.println("#Docs: " + v_Element.getDocuments().size() + " *** Label (Topics): " + v_Element.getLabel()  +  " *** Queries: " + Stringutils.concatStrings(v_Element.getQueries(), " / ") + " *** Taks: " + Stringutils.concatStrings(v_Element.getTasks(), " / "));
			
			printChildrenFiltered(child, level + 1, ps);
		}		
	}
	
	/**
	 * Print the tree to the specified {@link PrintStream}
	 * 
	 * @param tree The tree to print
	 * @param ps The {@link PrintStream} to print to
	 */
	public static void prettyPrintLabel(Tree<ClusterDescriptor> tree, PrintStream ps)
	{
		TreeRoot<ClusterDescriptor> root = tree.getRoot();
		ps.println("root");
		printChildrenLabel(root, 0, ps);	
	}

	private static void printChildrenLabel(TreeNode<ClusterDescriptor> node, int level, PrintStream ps)
	{	
		int childCount = 0;
		for (TreeNode<ClusterDescriptor> child : node.getChildren()) {
			childCount++;
			
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
			List<String> v_Topics = v_Element.getFilteredNormtopics();
			int j = 0;
			StringBuilder v_TopicConcat = new StringBuilder();
			for (String v_Topic : v_Topics)
			{
				if (j > 0 )
				{
					v_TopicConcat.append(" | ");
				}
				
				v_TopicConcat.append(v_Topic);
				
				if (j > 2) break;
				j++;
				
			}
			
			List<String> v_Files = v_Element.getDocuments();
			
			ps.println("- " + v_Element.getLabel() + " *** #docs: " + v_Files.size() + " *** Topics: " + v_TopicConcat + " *** Queries: " + Stringutils.concatStrings(v_Element.getQueries(), "/"));
			
			printChildren(child, level + 1, ps);
		}		
	}
	
}
