package be.baur.sds.validation;

import java.util.Iterator;
import java.util.List;

import be.baur.sda.Node;

/**
 * This non-public class provides a Node iterator with a revert() method to deal
 * with a specific validation issue.
 */
class NodeIterator implements Iterator<Node>{

    private Node saved = null;  // reference to the most recently returned element
    private boolean reverted = false; // whether we are in reverted state
	private Iterator<Node> internal; // the internal backing iterator

	/** Creates an iterator over the elements contained in a node <code>list</code>. */
	public NodeIterator(List<Node> list) {
		this.internal = list.iterator();
	}
    
    @Override
    public boolean hasNext() {
    	// return true if we have a reverted element, otherwise rely on the backing method
    	return (reverted && saved != null) ? true : internal.hasNext(); 
    }

    
    @Override
    public Node next() {
    	// return a reverted element if we have one, otherwise rely on the backing method
    	if (! reverted || saved == null)
            saved = internal.next(); 
        reverted = false;
        return saved;
    }

	/**
	 * Reverts the effect of the most recent call to <code>next()</code>, and will
	 * cause a future invocation of <code>next()</code> to return the same element
	 * again. A practical example would be to inspect the next element in the
	 * iteration, and <code>revert()</code> it if processing should be resumed at a
	 * later time. Calling this method prior to <code>next()</code> or calling it
	 * more than once has no effect.
	 */
    public void revert() {
    	reverted = true;
    }

//    public static void main(String[] args) {
//    	List<Node> set = new ArrayList<Node> ();
//    	set.add(new Node("node","1")); 
//    	set.add(new Node("node","2"));
//    	set.add(new Node("node","3"));
//    	NodeIterator it = new NodeIterator(set);
//    	it.revert(); it.revert();
//    	System.out.println("next: " + it.next());
//    	it.revert(); it.revert();
//    	System.out.println("next: " + it.next());
//    	System.out.println("next: " + it.next());
//    	it.revert(); it.revert();
//    	System.out.println("next: " + it.next());
//    	System.out.println("next: " + it.next());
//    }
}
