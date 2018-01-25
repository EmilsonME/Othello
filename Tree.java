import java.util.*;

public class Tree {

    int index;
    int data;
    Tree parent;
    List< Tree > children;

    public Tree(int index, int data) {
    	this.index = index;
        this.data = data;
        this.children = new ArrayList<>();
    }

    public Tree addChild(int index, int childData) {

        Tree childNode = new Tree(index, childData);
        childNode.parent = this;
        this.children.add(childNode);

        return childNode;
    }

    public static Tree dfs(Tree tree, Tree max) {

        //System.out.println(tree.data + " " + max);

        if(max.data < tree.data) {
            max = tree;
        }
        
        //System.out.println(tree.index + " " + tree.data + " " + max + "\n");

        for( Tree child : tree.children ) {
            max = dfs(child, max);
        }
        
        return max;
    }
}
