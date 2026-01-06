

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
    public HeapItem min; // used as LinkedList of roots.
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
    {// constructor time complexity O(1)
        this.lazyMelds = lazyMelds;
        this.lazyDecreaseKeys = lazyDecreaseKeys;
        // student code can be added here
    }
    
    public Heap(boolean lazyMelds, boolean lazyDecreaseKeys, HeapNode x, int HeapSize, int numOfTrees) {
    	// constructor time complexity O(1)
    	this.numOfTrees=numOfTrees;
    	this.HeapSize=HeapSize;
        this.lazyMelds = lazyMelds;
        this.lazyDecreaseKeys = lazyDecreaseKeys;
        if(x==null) {
        	this.min=null;
        }
        else {
        this.min=x.item;
        }
    }

    /**
     * 
     * pre: key > 0
     *
     * Insert (key,info) into the heap and return the newly generated HeapNode.
     *
     */
    public HeapItem insert(int key, String info) 
    {    // insert using meld, returns pointer to the node after insertion
    	 // complexity depends on meld complexity (which depends on lazy meld value)
    	HeapNode node = new HeapNode(key,info);

    	if(this.min== null) {  // edge case, tree were empty
    		this.min=node.item;
    		this.HeapSize=1;this.numOfTrees=1;
    	}
    	else {
    	Heap H = new Heap(this.lazyMelds,this.lazyDecreaseKeys);
    	H.setMin(node.item);
    	H.numOfTrees=1;H.HeapSize=1;
    	this.meld(H);

    	}
    	return node.item;
    }
    
    
    
    

    /**
     * 
     * Return the minimal HeapNode, null if empty.
     *
     */
    public HeapItem findMin()
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
    	//function deletesMinimal Node from the Heap
    	//complexity Analysis, all Helper function (unMarkRoots, SiblingsToFather, findMinimalInLevel)
    	// goes over roots and minNode Childs, which happens in any case through meld/consolidate
    	// therefore time complexity if LazyMeld O(logn) amrotize and O(n) Worst Case
    	// else (lazyMelds = false) time complexity is o(logn)

    	
    	if(this.min ==null) { // special case, trying to delete from empty tree
    		return;
    	}

    	this.HeapSize--;this.numOfTrees--;
    	HeapNode prevMin = this.min.node;
    	if(this.min.node.next== this.min.node) { // means minimum has no siblings
    		this.min=null;

    	}
    	
    	else { // means minimum has siblings
    		this.min.node.next.prev=this.min.node.prev; // connecting between minimum prev and next
    		this.min.node.prev.next=this.min.node.next; // for removal of minimum from the tree
    		this.min= findMinimalInLevel(prevMin.next).item;

    	}
    	
    	HeapNode newHeapMin=null; /// TODO
    	if(prevMin.child!= null) { 
    		newHeapMin = findMinimalInLevel(prevMin.child);// updating minimal Node for the Tree formed by min childrens
    		newHeapMin.parent=null;// updating field here cause siblings to father dont updates him
    		siblingsToFather(newHeapMin,null); // removing connection of min Childrens from min
    	}
    	Heap heap2 = new Heap(this.lazyMelds, this.lazyDecreaseKeys, newHeapMin, 0, prevMin.rank);
    	heap2.unMarkRoots();

    	this.meld(heap2);
    	prevMin.child=null;prevMin.next=null;prevMin.prev=null;// removing connection of old min from the tree
    	
    	if(this.lazyMelds) {//making sure that consolidate is called in delete min
    		if((this.min!= null)) {
    		this.consolidate();
    		}
    	}
    	
        return; 
    }

    
    public HeapNode findMinimalInLevel(HeapNode x) {
    	//helper function to find minimal Node between siblings
    	//used to update new MinimalNodes for deleteMin
    	//time complexity O(numOfSiblings)-o(n) if called on the initiail roots otherwise o(logn)
    	HeapNode min = x;
    	HeapNode current = x.next;
    	while(current!=x) {
    		if(current.item.key<min.item.key) {
    			min=current;
    		}
    		current=current.next;
    	}
    	return min;
    }
    /**
     * 
     * pre: 0<=diff<=x.item.key
     * 
     * Decrease the key of x by diff and fix the heap.
     * 
     */
    public void decreaseKey(HeapItem x, int diff) 
    {   // function decrease key of x by diff
    	// time complexity analysis
    	//if LazyDecreasekey= false time complexity O(log(n)) by calling heapifyUp
    	//if LazyDecreaseKey = true time complexity decided by Lazymeld
    	// if Lazy Meld= True then O(1) amrotize (and O(n) Worst Case)
    	//if LazyMeld = false then O(logn) amortize (and O(nlogn) Worst Case)
    	
    	x.setKey(x.key-diff);
    	if((x.node.parent == null) ||(x.key>=x.node.parent.item.key)) { // x already a root, no heapify or cascade is necessary
    		if(this.min.key>x.key) {
    			this.min=x;
    		}
    		return;
    	}
    	
    	if(this.lazyDecreaseKeys) {
    		cascadingCuts(x.node);
    		
    	}
    	
    	
    	else {
    		heapifyUp(x.node);

    	}
		if(this.min.key>x.key) {
			this.min=x;
		}
        return; // should be replaced by student code
    }
    
  	public void unMarkRoots() {
		// function remove marks from roots of new Heap and updates its numOfMark field
		if(this.min==null) {
			return;
		}
		if(this.min.node.flag) {
			this.min.node.flag=false;
			this.numOfMarked--;
		}
		HeapNode curr = this.min.node.next;
		while(curr!= this.min.node) {
			if(curr.flag) {
				curr.flag=false;
				this.numOfMarked--;
			}
			curr = curr.next;
		}
		return;
		
	}
  	
    public void heapifyUp(HeapNode x) {
    	//Helper function for DecreaseKey, LazyDecrease = false
    	// function preforms routine of HeapifyUp calls
    	// time complexity O(Log(n)) logn loop calls for single Heapify
    	while ((x.parent!=null) && (x.item.key<x.parent.item.key)){//not a root and need to go up
    		singleHeapify(x); // loop runs O(logn), every tree depth is blocked
    		x=x.parent;
    	}
    	return;
    }
    
    
    public void singleHeapify(HeapNode x) {
       	// Helper function for Heapify, LazyDecrease = false
    	// performs single heapify up
    	//o(1) memory complex
    	//time complexity Based on helper functions, therefore 
    	//time complexity is O(1)
    	
    	// first keeping pointers
    	// updating heapify up attribute
    	this.numOfHeapify++;
    	HeapItem parentItem=x.parent.item;
    	x.parent.item=x.item;           /// connecting x item
    	x.parent.item.node = x.parent;  ///  in parent
    	x.item=parentItem; /// connecting parent item
    	x.item.node = x;   /// in x
    	
    	
    	
    	
    }
    public void PreviousSingleHeapify(HeapNode x) {
    	// Helper function for Heapify, LazyDecrease = false
    	// performs single heapify up
    	//o(1) memory complex
    	//time complexity Based on helper functions, therefore 
    	//time complexity is O(logn)
    	
    	
    	// first keeping pointers
    	// updating heapify up attribute
    	this.numOfHeapify++;
    	HeapNode parent=x.parent;
    	int tempRank = parent.rank;
    	parent.rank=x.rank;
    	x.rank=tempRank;
    	HeapNode xNext=x.next;
    	HeapNode xPrev=x.prev;
    	HeapNode parentNext=parent.next;
    	HeapNode parentPrev=parent.prev;
    	siblingsToFather(x,x);// updating the parent of x siblings pointer to x
    	connectingNewSibling(x,parent,xNext,xPrev);// connecting parent to x siblings siblings 
    	connectingNewSibling(parent,x,parentNext,parentPrev);// connecting x to parents siblings
    	x.parent=parent.parent; // adjusting x parent
    	if (x.parent!=null) {
    		x.parent.child=x;
    	}
    	parent.child=x.child; // adjusting parent child to be x child
    	if (parent.child!=null) { // it means he has a child than connect all his child to point him as his father
    		siblingsToFather(parent.child,parent);
    	}
    	//after all now all we need is to change parent and x
    	x.child=parent;
    	parent.parent=x;
    }
    public void connectingNewSibling(HeapNode oldSibling,HeapNode newSibling,HeapNode next ,HeapNode prev) {
    	//Helper function for Heapify, LazyDecrease = false
    	// connecting new siblings in place of old sibling
    	//time complexity O(1) only pointers adjustments
    	if (next==oldSibling) { // edge case: old is an only child 
    		newSibling.next=newSibling;
    		newSibling.prev=newSibling;
    		
    	}
    	else {// x has siblings
    		// first adjusting parent siblings suce as x
    		newSibling.next=next;
    		newSibling.prev=prev;
    		next.prev=newSibling;
    		prev.next=newSibling;
    		
    	}
    	
    }
    public void siblingsToFather(HeapNode c, HeapNode  parent) {
    	//Helper function for Heapify, LazyDecrease = false
    	// the function recieve a node and connect all his brothers (excluding him) to his father
    	// time complexity O(rank(c.parent) = O(logn)
    	HeapNode curr=c.next;
    	while( curr!=c) {
    		curr.parent=parent;
    		curr=curr.next;
    	}
    }
    public void cascadingCuts(HeapNode x) { // we can assume x isnt a root
    	// Helper fcuntion for Decrease key, LazyDecrease = true
    	// while loop runs O(n) Worst Case but O(1) in amoritze.
    	//therefore time complexity if lazy meld O(1) amortize, O(n) Worst Case
    	// else time complexity is O(logn) amrotize O(nLogn) Worst Case
    	while((x.parent.flag) && (x.parent.parent!=null)) {
    		this.numOfMarked--; // case our parent isnt root 
    		x.parent.flag=false;// unmark him, cut then move upwards
    		HeapNode parent=x.parent;

    		singleCut(x);

    		x=parent;
    	}
    	if(x.parent.parent!=null) { // case we stopped cause our parent wasnt flagged
    		x.parent.flag=true; 
    		this.numOfMarked++;
    	}
    	
		singleCut(x); // cut once more and terminate
		return;
    	
    }
    
    public void singleCut(HeapNode x) {
    	// Helper fcuntion for cascadingCuts, LazyDecrease = true
    	//time complexity by meld complexity
    	// if lazyMeld time complexity O(1), else time complexity O(logn)
    	x.parent.rank--;
    	this.numOfCuts++;
		if(x.parent.child==x) { // connecting its parent to other child
			
			if(x.prev==x) { // x is only son of its parent
			x.parent.child=null; // need to find for him a new child
			}
			else {
				x.parent.child=x.next;
			}
		}
		x.parent=null;
		x.next.prev=x.prev; // connecting between x
		x.prev.next=x.next;// next and prev
		
		x.next=x;x.prev=x;
		Heap H = new Heap(this.lazyMelds,this.lazyDecreaseKeys,x, 0,1);
		H.unMarkRoots();// removing mark from H roots
		this.meld(H);
    }
    /**
     * 
     * Delete the x from the heap.
     *
     */
    public void delete(HeapItem x) 
    {    
    	this.decreaseKey(x, x.key+1);//making x the min in the heap 
    	this.deleteMin();
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
    	this.numOfMarked += heap2.numMarkedNodes();
    	this.numOfTrees+= heap2.numTrees();
    	
   
    	
    	if(this.min==null) { /// edge case this is an empty heap
    		this.min=heap2.min;
    		return;
    	}
    	if(heap2.min==null) { /// edge case heap2 is an empty heap
    		return;
    	}
    	
    	HeapNode heap2Head = heap2.min.node;
    	HeapNode heap2Tail=heap2Head.prev;

    	
    	heap2Head.prev = this.min.node;    /// adapting heap 2 head and tail pointers
    	heap2Tail.next = this.min.node.next; /// to this linkedList
    	
    	this.min.node.next.prev = heap2Tail;  // adding heap 2 root linkedList
    	this.min.node.next = heap2Head;       // into this root linkedList
    	
    	
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
        return this.HeapSize; // should be replaced by student code
    }


    /**
     * 
     * Return the number of trees in the heap.
     * 
     */
    public int numTrees()
    {
        return this.numOfTrees; // should be replaced by student code
    }
    
    
    /**
     * 
     * Return the number of marked nodes in the heap.
     * 
     */
    public int numMarkedNodes()
    {
        return this.numOfMarked; // should be replaced by student code
    }
    
    
    /**
     * 
     * Return the total number of links.
     * 
     */
    public int totalLinks()
    {
        return this.numOfLinks; // should be replaced by student code
    }
    

    
    public void consolidate() {
    	// helper function unites all same degree trees
    	// adjust heads so does every tree has unique deg
    	// time complexity O(numTrees), outer while runs numTree iterations
    	// inner while iteration by number of links (blocked by O(numTree) either).
    	// numOfTrees if lazyMelds is O(logn) amortize and O(n) WorstCase
    	// else numOfTrees is O(logn)
    	
    	
    	//double len = (Math.log(HeapSize)/Math.log(1.5))+1;
    	//HeapNode [] rankArr = new HeapNode[(int) len]; // initialize array length, blocked by log 
    	HeapNode[] rankArr = new HeapNode[100];
    	HeapNode min = this.min.node;
    	HeapNode curr = min.next;
    	rankArr[this.min.node.rank]=min;
    	while(curr != min) { // running over every treeRoot
			HeapNode next = curr.next;
			
    		while(rankArr[curr.rank] !=null) {   // linking until open spot in the array
    			int prevRank = curr.rank;
    			if(curr==this.min.node) { // edge case, avoiding linking min as this with equal value
    				curr = rankArr[curr.rank].link(curr);
    			}
    			else {
    			curr = curr.link(rankArr[curr.rank]);
    			}
    			rankArr[prevRank]=null;
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
        return this.numOfCuts; // should be replaced by student code
    }
    

    /**
     * 
     * Return the total heapify costs.
     * 
     */
    public int totalHeapifyCosts()
    {
        return this.numOfHeapify; // should be replaced by student code
    }
    
	/**
	 * @param min the min to set
	 */
	public void setMin(HeapItem min) {
		this.min = min;
	}
	@Override
    public String toString() {
        if (this.min == null) {
            return "Heap is empty";
        }
        if (this.min.node == null) {
            return "Heap corrupted: min.node is null";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Heap Structure (min: ").append(min.key).append("):\n");
        
        HeapNode startRoot = this.min.node;
        HeapNode currentRoot = startRoot;
        int safety = 0;
        
        do {
            printSubTree(currentRoot, sb, "", true);
            sb.append("\n"); 
            currentRoot = currentRoot.next;
            safety++;
        } while (currentRoot != startRoot && currentRoot != null && safety < this.HeapSize + 10);

        return sb.toString();
    }

    private void printSubTree(HeapNode node, StringBuilder sb, String indent, boolean isRoot) {
        sb.append(indent);
        if (!isRoot) {
            sb.append("|__");
        }
        
        // NOW USES HeapNode.toString()
        sb.append(node.toString()); 
        sb.append("\n");

        if (node.child != null) {
            HeapNode child = node.child;
            HeapNode startChild = child;
            String newIndent = indent + (isRoot ? "   " : "   |"); 
            do {
                printSubTree(child, sb, newIndent, false);
                child = child.next;
            } while (child != startChild && child != null);
        }
    }
    
    /**
     * Triggers a comprehensive structural integrity check of the Heap.
     * Throws a RuntimeException immediately if any pointer or logic is broken.
     */
    /**
     * Triggers a comprehensive structural integrity check of the Heap.
     * Throws a RuntimeException immediately if any pointer or logic is broken.
     */
    /**
     * Triggers a comprehensive structural integrity check of the Heap.
     * SILENT SUCCESS: Only prints or throws if an error is found.
     */
    public void validate() {
        // 1. Empty Heap Check
        if (min == null) {
            if (HeapSize != 0) throw new RuntimeException("ERROR: min is null but HeapSize is " + HeapSize);
            return; 
        }

        if (min.node == null) {
            throw new RuntimeException("CRITICAL: min.node is null");
        }

        // 2. Validate Structure & Count Size
        int calculatedSize = validateNodeAndChildren(min.node, null);

        // 3. Global Heap Properties
        if (calculatedSize != this.HeapSize) {
            throw new RuntimeException("SIZE ERROR: HeapSize field = " + HeapSize + ", but actual count = " + calculatedSize);
        }
        
        // 4. Validate Min Pointer Accuracy and Root Properties
        HeapNode startRoot = min.node;
        HeapNode curr = startRoot;
        int observedMinKey = Integer.MAX_VALUE;
        
        java.util.HashSet<HeapNode> visitedRoots = new java.util.HashSet<>();

        do {
            if (visitedRoots.contains(curr)) {
                throw new RuntimeException("INFINITE LOOP detected in Root List");
            }
            visitedRoots.add(curr);

             if (curr.item.key < observedMinKey) {
                observedMinKey = curr.item.key;
            }
             
            if (curr.parent != null) {
                throw new RuntimeException("ROOT ERROR: Root node [" + curr.item.key + "] has a parent.");
            }
            
            curr = curr.next;
        } while (curr != startRoot);

        if (this.min.key != observedMinKey) {
            throw new RuntimeException("MIN POINTER ERROR: min.key = " + this.min.key + ", but actual min key in roots = " + observedMinKey);
        }
        
        // Success! (No print)
    }

    /**
     * Recursive helper that validates a node and its entire subtree.
     * @return The total number of nodes in this subtree (siblings + their children)
     */
    private int validateNodeAndChildren(HeapNode node, HeapNode expectedParent) {
        if (node == null) return 0;

        int sizeCount = 0;
        HeapNode currentSibling = node;
        HeapNode startSibling = node;

        // Iterate over the circular sibling list
        do {
            sizeCount++;

            // --- CHECK 1: Item <-> Node Integrity ---
            if (currentSibling.item == null) {
                throw new RuntimeException("DATA ERROR: Node exists but has NULL item.");
            }
            if (currentSibling.item.node != currentSibling) {
                 throw new RuntimeException("LINK ERROR: Item [" + currentSibling.item.key + "] points to wrong Node! (item.node != this)");
            }

            // --- CHECK 2: Pointers (Next/Prev) ---
            if (currentSibling.next == null || currentSibling.prev == null) {
                 throw new RuntimeException("POINTER ERROR: Node [" + currentSibling.item.key + "] has null next/prev.");
            }
            if (currentSibling.next.prev != currentSibling) {
                throw new RuntimeException("BROKEN LIST: Node [" + currentSibling.item.key + "] next->prev is wrong.");
            }

            // --- CHECK 3: Parent Consistency ---
            if (currentSibling.parent != expectedParent) {
                throw new RuntimeException("PARENT ERROR: Node [" + currentSibling.item.key + "] points to wrong parent.");
            }

            // --- CHECK 4: Heap Property (Parent <= Child) ---
            if (expectedParent != null) {
                if (currentSibling.item.key < expectedParent.item.key) {
                     throw new RuntimeException("HEAP VIOLATION: Child [" + currentSibling.item.key + "] < Parent [" + expectedParent.item.key + "]");
                }
            }

            // --- CHECK 5: Rank Accuracy ---
            int actualRank = 0;
            if (currentSibling.child != null) {
                // Count children manually
                HeapNode childRunner = currentSibling.child;
                do {
                    actualRank++;
                    childRunner = childRunner.next;
                } while (childRunner != currentSibling.child);
            }

            if (currentSibling.rank != actualRank) {
                throw new RuntimeException("RANK ERROR: Node [" + currentSibling.item.key + "] has rank " + currentSibling.rank + " but actually has " + actualRank + " children.");
            }

            // --- CHECK 6: Recursion ---
            if (currentSibling.child != null) {
                // Validate children, passing current node as the expected parent
                sizeCount += validateNodeAndChildren(currentSibling.child, currentSibling);
            }

            currentSibling = currentSibling.next;
            
            // Safety check for loops within sibling lists
            if (sizeCount > this.HeapSize + 100) {
                 throw new RuntimeException("INFINITE LOOP detected inside sibling list of [" + startSibling.item.key + "]");
            }

        } while (currentSibling != startSibling);

        return sizeCount;
    }
    
    
    /**
     * Class implementing a node in a ExtendedFibonacci Heap.
     *  
     */
    public static class HeapNode{
    	public HeapItem item;
        public HeapNode child;
        public HeapNode next;
        public HeapNode prev;
        public HeapNode parent;
        public int rank;
        public boolean flag;
        
        public HeapNode(int key, String info) {
        	this.item= new HeapItem(key,info);
        	this.item.setNode(this);
    		this.next=this;
    		this.prev=this;
    		this.flag=false;

        }
        
        @Override
        public String toString() {
            return this.item.toString(); 
        }
        
        public HeapNode link(HeapNode other) {
        	// function linking between this and other HeapNodes as of Heap Linking Algorithm
        	// time complexity O(1), only pointers adjusments


        	
        	if(this.item.key<other.item.key) {
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




    }
    
    public static class HeapItem{
    	public HeapNode node;
    	public int key;
    	public String info;
    	
    	public HeapItem(int key, String info) {
    		this.key = key;
    		this.info = info;
    	}

		/**
		 * @param node the node to set
		 */
		public void setNode(HeapNode node) {
			this.node = node;
		}
		
		/**
		 * @param key the key to set
		 */
		public void setKey(int key) {
			this.key = key;
		}

		@Override
		public String toString() {
			return "[" +key + "," + info + "]";
		}
    	
    }

    

}

