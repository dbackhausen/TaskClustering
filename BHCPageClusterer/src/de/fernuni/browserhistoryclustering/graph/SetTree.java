package de.fernuni.browserhistoryclustering.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.discoversites.util.collections.tree.TreeNode;


@XmlRootElement
@XmlType(name="")
@XmlSeeAlso(ClusterDescriptor.class)
public class SetTree<T> extends com.discoversites.util.collections.tree.set.SetTree<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9027290384794191339L;
	
	public SetTree(final T element)
	{
		super(element);		
	}
	
	public SetTree()
	{
		super();
	}
	
	public Set<TreeNode<T>> getNodes(int v_Depth)
	{
	   return super.getNodes(v_Depth);
	}
	
	
	@XmlElement
	public Collection<ClusterDescriptor> getTreeElement() {
		Collection<ClusterDescriptor> v_TreeElements = new HashSet<ClusterDescriptor>();
		Collection<TreeNode<T>> v_TreeNodes = getRoot().getChildren();
		for (TreeNode<T> v_TreeNode : v_TreeNodes) {
			v_TreeElements.add((ClusterDescriptor) v_TreeNode.getElement());			
		}
		return v_TreeElements;
	}	

}
