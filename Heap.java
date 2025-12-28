import java.util.LinkedList;
import java.util.Objects;

/**
 * Heap
 *
 * An implementation of Fibonacci heap over positive integers 
 * with the possibility of not performing lazy melds and 
 * the possibility of not performing lazy decrease keys.
 *
 */
public class Heap
{
    public final boolean lazyMelds;
    public final boolean lazyDecreaseKeys;
    public HeapNode min; // used as LinkedList of roots.
    public int HeapSize=0;
    public int numOfTrees=0;
    public int numOfLinks=0;
    public int numOfCuts=0;
    public int numOfHeapify=0;
    public int numOfMarked=0;
    
    /**
     *
     * Constructor to initialize an empty heap.
     *
     */
    public Heap(boolean lazyMelds, boolean lazyDecreaseKeys)
    {
        this.lazyMelds = lazyMelds;
        this.lazyDecreaseKeys = lazyDecreaseKeys;
        // student code can be added here
    }

    /**
     * 
     * pre: key > 0
     *
     * Insert (key,info) into the heap and return the newly generated HeapNode.
     *
     */
    public HeapNode insert(int key, String info) //TODO edge case of inserting first node
    {    // insert using meld, returns pointer to the node after insertion
    	 // complexity depends on meld complexity
    	HeapNode node = new HeapNode(key,info);

    	if(this.min== null) {
    		this.min=node;
    		this.HeapSize=1;this.numOfTrees=1;
    	}
    	else {
    	Heap H = new Heap(this.lazyMelds,this.lazyDecreaseKeys);
    	H.setMin(node);
    	H.numOfTrees=1;H.HeapSize=1;
    	this.meld(H);

    	}
    	return node;
    }
    
    

    /**
     * 
     * Return the minimal HeapNode, null if empty.
     *
     */
    public HeapNode findMin()
    {
        return this.min; // should be replaced by student code
    }

    /**
     * 
     * Delete the minimal item.
     *
     */
    public void deleteMin()
    {
        return; // should be replaced by student code
    }

    /**
     * 
     * pre: 0<=diff<=x.key
     * 
     * Decrease the key of x by diff and fix the heap.
     * 
     */
    public void decreaseKey(HeapNode x, int diff) 
    {    
        return; // should be replaced by student code
    }

    /**
     * 
     * Delete the x from the heap.
     *
     */
    public void delete(HeapNode x) 
    {    
        return; // should be replaced by student code
    }


    /**
     * 
     * Meld the heap with heap2
     * pre: heap2.lazyMelds = this.lazyMelds AND heap2.lazyDecreaseKeys = this.lazyDecreaseKeys
     *
     */
    public void meld(Heap heap2)
    {
    	// complexity depends on lazy melds value
    	// if True, O(1) complexity, otherwise O(numTrees) = O(logn) using consolidate
    	this.HeapSize+=heap2.size();                     // updating fields
    	this.numOfLinks+= heap2.totalLinks();           // by heap 2 values
    	this.numOfCuts+=heap2.totalCuts();
    	this.numOfHeapify+= heap2.totalHeapifyCosts();
    	this.numOfMarked = heap2.numMarkedNodes();
  
    	HeapNode heap2Head = heap2.min;
    	HeapNode heap2Tail=heap2Head.prev;
    	
    	heap2Head.prev = this.min;    /// adapting heap 2 head and tail pointers
    	heap2Tail.next = this.min.next; /// to this linkedList
    	
    	this.min.next.prev = heap2Tail;  // adding heap 2 root linkedList
    	this.min.next = heap2Head;       // into this root linkedList
    	
    	if(heap2.min.key<this.min.key) {
    		this.min=heap2.min;
    	}
    	

    	if(!this.lazyMelds) {   // consolidate depends on lazy melds
    		this.consolidate();
    	}

        return; // should be replaced by student code           
    }
    
    
    /**
     * 
     * Return the number of elements in the heap
     *   
     */
    public int size()
    {
        return 46; // should be replaced by student code
    }


    /**
     * 
     * Return the number of trees in the heap.
     * 
     */
    public int numTrees()
    {
        return 46; // should be replaced by student code
    }
    
    
    /**
     * 
     * Return the number of marked nodes in the heap.
     * 
     */
    public int numMarkedNodes()
    {
        return 46; // should be replaced by student code
    }
    
    
    /**
     * 
     * Return the total number of links.
     * 
     */
    public int totalLinks()
    {
        return 46; // should be replaced by student code
    }
    

    
    public void consolidate() {
    	// helper function unites all same degree trees
    	// adjust heads so does every tree has unique deg 
    	// time complexity O(numTrees), outer while runs numTree iterations
    	// inner while iteration by number of links (blocked by O(numTree) either).
    	double len = (Math.log(HeapSize)/Math.log(1.5))+1;
    	HeapNode [] rankArr = new HeapNode[(int) len]; // initialize array length, blocked by log 
    	HeapNode min = this.min;
    	HeapNode curr = min.next;
    	rankArr[this.min.rank]=min;

    	while(curr != min) { // running over every treeRoot
			HeapNode next = curr.next;
    		while(rankArr[curr.rank] !=null) {   // linking until open spot in the array
 
    			curr = curr.link(rankArr[curr.rank]);

    			this.numOfLinks++;this.numOfTrees--;
    					
    		}
    		rankArr[curr.rank] =curr;
    		curr=next;
    	}
    	
    }
    
    
    /**
     * 
     * Return the total number of cuts.
     * 
     */
    public int totalCuts()
    {
        return 46; // should be replaced by student code
    }
    

    /**
     * 
     * Return the total heapify costs.
     * 
     */
    public int totalHeapifyCosts()
    {
        return 46; // should be replaced by student code
    }
    
	/**
	 * @param min the min to set
	 */
	public void setMin(HeapNode min) {
		this.min = min;
	}
	
	@Override
    public String toString() {
        if (this.min == null) {
            return "Heap is empty";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Heap Structure:\n");
        
        // We start from min, but since it's circular, we iterate until we hit min again
        HeapNode currentRoot = this.min;
        do {
            printSubTree(currentRoot, sb, "", true);
            sb.append("\n"); // Separate different trees in the root list
            currentRoot = currentRoot.next;
        } while (currentRoot != this.min);

        return sb.toString();
    }

    /**
     * Recursive helper to print the tree structure
     * @param node The node to print
     * @param sb The StringBuilder to append to
     * @param indent The current indentation string
     * @param isRoot Whether this node is a root of the heap (level 0)
     */
    private void printSubTree(HeapNode node, StringBuilder sb, String indent, boolean isRoot) {
        sb.append(indent);
        
        if (!isRoot) {
            sb.append("|__"); // Visual connection for children
        }
        
        // Print the current node using the existing HeapNode toString
        sb.append(node.toString());
        // Optional: Add rank or other debug info
        // sb.append(" (Rank:").append(node.rank).append(")"); 
        sb.append("\n");

        // If the node has children, we need to iterate over the children's circular list
        if (node.child != null) {
            HeapNode currentChild = node.child;
            
            // Calculate new indent: Roots get simple spacing, children get pipe alignment
            String newIndent = indent + (isRoot ? "   " : "   |"); 
            
            do {
                printSubTree(currentChild, sb, newIndent, false);
                currentChild = currentChild.next;
            } while (currentChild != node.child);
        }
    }
    
    /**
     * Class implementing a node in a ExtendedFibonacci Heap.
     *  
     */
    public static class HeapNode{
        public int key;
        public String info;
        public HeapNode child;
        public HeapNode next;
        public HeapNode prev;
        public HeapNode parent;
        public int rank;
        public boolean flag;
        
        public HeapNode(int key, String info) {
        	this.key=key;
        	this.info=info;
    		this.next=this;
    		this.prev=this;
    		this.flag=false;

        }
        
        public HeapNode link(HeapNode other) {

        	if(this.key<other.key) {
        		other.parent=this;
        		
        		other.prev.next=other.next;  /// taking other out of the 
        		other.next.prev=other.prev;  /// roots linkedList
        		
        		if(this.rank==0) { // special case, no childs
        		this.child=other;
        		other.next=other;other.prev=other;
        		}
        		else {

        		other.prev=this.child;      /// adapting other next and prev 
        		other.next=this.child.next; /// to be childs of this

        		this.child.next.prev=other; /// adapting this childs linkedList
        		this.child.next=other;      /// to include other
        		}
        		this.rank++;
        		return this;
        	}
        	
        	else {
        		this.parent=other;
        		this.prev.next=this.next;  /// taking this out of the 
        		this.next.prev=this.prev;  /// roots linkedList
           		if(this.rank==0) { // special case, no childs
            		other.child=this;
            		this.next=this;this.prev=this;
            		}
           		else {
        		this.prev=other.child;      /// adapting this next and prev  
        		this.next=other.child.next; /// to be childs of other
        		
        		other.child.next.prev=this; /// adapting other childs linkedList
        		other.child.next=this;      /// to include this
        	}
           		other.rank++;
           		return other;
        	}
        	
        	
        }

		@Override
		public int hashCode() {
			return Objects.hash(info, key);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			HeapNode other = (HeapNode) obj;
			return Objects.equals(info, other.info) && key == other.key;
		}

		@Override
		public String toString() {
			return "[" + key + info + "]";
		}
    }



}

