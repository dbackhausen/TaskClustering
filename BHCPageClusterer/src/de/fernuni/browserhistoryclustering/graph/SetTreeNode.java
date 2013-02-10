package de.fernuni.browserhistoryclustering.graph;

import java.util.Collection;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.discoversites.util.collections.tree.TreeNode;


@XmlType(name="SetTreeNodeFUH")
public abstract class SetTreeNode<T> extends com.discoversites.util.collections.tree.set.SetTreeNode<T> {
	
	@XmlElement
	public Collection<SetTreeNode<T>> getChildNodes() {
		Collection<SetTreeNode<T>> v_Children = new HashSet<SetTreeNode<T>>();
		Collection<TreeNode<T>> v_TreeNodes = super.getChildren();
		for (TreeNode<T> v_TreeNode : v_TreeNodes) {
			v_Children.add((SetTreeNode<T>)v_TreeNode);
		}
		return v_Children;
	}
	
}
