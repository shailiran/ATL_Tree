
/**
 *
 * AVLTree
 *
 * Yossi Saydof - yossisaydof
 * Shai Liran - shailiran
 *
 * An implementation of a AVL Tree with
 * distinct integer keys and info
 *
 */

public class AVLTree {
	
	private IAVLNode root;
	private IAVLNode min = null, max = null;
	
	//Constructor for empty tree
	public AVLTree() {
		
		this.root = new AVLNode(-1, null);
		this.min = this.root;
		this.max = this.root;
	}
	
	//Constructor for tree with given key and info 
	public AVLTree(int key, String val) {
		
		IAVLNode root = new AVLNode(key, val);
		this.root = root;
		root.setRight(new AVLNode(-1, null));
		root.setLeft(new AVLNode(-1, null));
		this.min = this.root;
		this.max = this.root;
		root.setSum(key);
		root.setHeight(0);
	}

	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 *
	 */
	public boolean empty() {
		
		if (this.root.getHeight() == -1) {
			return true;
		}
		return false;
	}

	/**
	 * public String search(int k)
	 *
	 * returns the info of an item with key k if it exists in the tree
	 * otherwise, returns null
	 */
	public String search(int k)
	{
		
		IAVLNode node = searchRec(k, this.root);
		if (node == null) {
			return null;
		}
		return node.getValue();
	}
	
	private IAVLNode searchRec(int k, IAVLNode node) {
		
		if (k == node.getKey()) {
			return node;
		}
		else if (node.getKey() == -1) {
			return null;
		}
		else if (k < node.getKey()) {
			return searchRec(k, node.getLeft());
		}
		else {
			return searchRec(k, node.getRight());
		}
	}

  /**
   * public int insert(int k, String i)
   *
   * inserts an item with key k and info i to the AVL tree.
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
   * returns -1 if an item with key k already exists in the tree.
   */
	public int insert(int k, String i) {
		
		//build new node with the given key and info
		IAVLNode newNode = new AVLNode(k, i);
		newNode.setRight(new AVLNode(-1, null));
		newNode.setLeft(new AVLNode(-1, null));
		newNode.setHeight(0);
		
		
		//check if the tree is empty
		if (empty()) {
			this.root = newNode;
			this.root.setParent(null);
			this.max = newNode;
			this.min = newNode;
			return 0;
		}
		
		//update min and max of the tree
		if (k < this.min.getKey()){
			this.min = newNode;
		}
		if (k > this.max.getKey()) {
			this.max = newNode;
		}
		
		IAVLNode insertPlace = searchForInsert(this.root, k, i);
		if (insertPlace == null) {
			return -1;
		}
		newNode.setParent(insertPlace);
		if (k > insertPlace.getKey()) {
			insertPlace.setRight(newNode);
		}
		else {
			insertPlace.setLeft(newNode);
		}
		
		updateSum(newNode);
		updateHeight(newNode);
		updateSize(newNode);
		
		int cnt = checkBalance(insertPlace);
		
		return cnt;
   }

	private IAVLNode searchForInsert(IAVLNode node, int k, String info) {
		
		IAVLNode result;
		if (k == node.getKey()) {
			return null;
		}
		else if (!node.isRealNode()) {
			return node.getParent();
		}
		else if (k < node.getKey()) {
			if (!node.getLeft().isRealNode()) {
				return node;
			}
			result = searchForInsert(node.getLeft(), k, info);
		}
		else {
			if (!node.getRight().isRealNode()) {
				return node;
			}
			result = searchForInsert(node.getRight(), k, info);
		}
		return result;
	}
	
	private void updateSize(IAVLNode node) {
		
		while (node.getParent() != null) {
			node = node.getParent();
			node.setSubtreeSize(node.getLeft().getSubtreeSize() + node.getRight().getSubtreeSize() + 1);
		}
	}
	
	private void updateHeight(IAVLNode node) {
		node = node.getParent();
		while (node != null) {
			node.setHeight(Math.max(node.getLeft().getHeight(), node.getRight().getHeight()) + 1);
			node = node.getParent();
		}
	}
	
	private void updateSum(IAVLNode node) {
		
		node = node.getParent();
		while (node != null) {
			node.setSum(node.getRight().getSum() + node.getLeft().getSum() + node.getKey());
			node = node.getParent();
		}
	}
	
	/**
	 * 
	 * @param node
	 * check balance for a given node
	 * @return number of rotation after insert
	 */
	private int checkBalance(IAVLNode node) {
		
		int cnt = 0;
		int BF = (node.getLeft().getHeight() - node.getRight().getHeight());
		while ((BF == 0 || BF == 1 || BF == -1) && node.getParent() != null) {
			node = node.getParent();
			BF = (node.getLeft().getHeight() - node.getRight().getHeight());
		}
		if ((BF > 1) || (BF < -1)){
				cnt = balanceAfterInsert(node, BF);
		}
		
		return cnt;
	}
	/**
	 * 
	 * @param node, balance factor
	 * balance after insert
	 * check which rotation is needed for a given node
	 */
	private int balanceAfterInsert(IAVLNode node, int BF) {
		
		int childLeftBF = 0;
		int childRightBF = 0;
		
		if (node.getLeft().getKey() != -1){
			childLeftBF = (node.getLeft().getLeft().getHeight() - node.getLeft().getRight().getHeight());	
		}
		
		if (node.getRight().getKey() != -1){
			childRightBF = (node.getRight().getLeft().getHeight() - node.getRight().getRight().getHeight());	
		}
		
		// LL Rotation
		if (BF == 2 && (childLeftBF == 1 || childLeftBF == 0)) {
			LLrotation(node);
			return 1;
		}
		
		// RR Rotation
		if (BF == -2 && (childRightBF == -1 || childRightBF == 0)) {
			RRrotation(node);
			return 1;
		}
		
		// LR Rotation
		if (BF == 2 && childLeftBF == -1) {
			LRrotation(node);
			return 2;
		}
		
		// RL Rotation
		if (BF == -2 && childRightBF == 1) {
			RLrotation(node);
			return 2;
		}
		return 0;
		
	}
	
	private void updateHeight2(IAVLNode node) { // updating height from node.parent to root
		while(node.getParent() != null) {
			node = node.getParent();
			node.setHeight(Math.max(node.getLeft().getHeight(),node.getRight().getHeight()) + 1);
		}
	}
	/**
	 * 
	 * @param node
	 * rotate the tree 
	 */
	private void LLrotation(IAVLNode node) {
		
		IAVLNode tmpAR = node.getLeft().getRight();
		IAVLNode tmpA = node.getLeft();
		IAVLNode tmpB = node;
		tmpA.setParent(tmpB.getParent());
		if (tmpB.getParent() != null) {
			if(tmpB.getParent().getKey() > tmpB.getKey()) {
				tmpB.getParent().setLeft(tmpA);
			}
			else {
				tmpB.getParent().setRight(tmpA);
			}
		}
		tmpA.setRight(tmpB);
		tmpB.setParent(tmpA);
		tmpAR.setParent(tmpB);
		tmpB.setLeft(tmpAR);
	
		// update height
		tmpB.setHeight(Math.max(tmpAR.getHeight(), tmpB.getRight().getHeight()) + 1);
		tmpA.setHeight(Math.max(tmpA.getLeft().getHeight(), node.getHeight() + 1));
		updateHeight2(tmpA);
		
		// update size
		tmpB.setSubtreeSize(tmpAR.getSubtreeSize() + tmpB.getRight().getSubtreeSize() + 1);
		tmpA.setSubtreeSize(tmpA.getLeft().getSubtreeSize() + tmpB.getSubtreeSize() + 1);
		updateSize(tmpA);
		
		// update sum
		tmpA.setSum(tmpB.getSum());
		tmpB.setSum(tmpB.getLeft().getSum() + tmpB.getRight().getSum() + tmpB.getKey());
		updateSum(tmpA);
		
		if (tmpA.getParent() == null){
			this.root = tmpA;
		}
	}
	
	private void RRrotation(IAVLNode node) {
		
		IAVLNode tmpB = node;
		IAVLNode tmpAL = tmpB.getRight().getLeft();
		IAVLNode tmpA = tmpB.getRight();
		tmpA.setParent(tmpB.getParent());
		if (tmpB.getParent() != null) {
			if (tmpB.getParent().getKey() < tmpB.getKey()) {
				tmpB.getParent().setRight(tmpA);
			}
			else {
				tmpB.getParent().setLeft(tmpA);
			}
		}
		tmpB.setParent(tmpA);
		tmpAL.setParent(tmpB);
		tmpA.setLeft(tmpB);
		tmpB.setRight(tmpAL);
		
		// update height
		tmpB.setHeight(Math.max(tmpAL.getHeight(), tmpB.getRight().getHeight()) + 1);
		tmpA.setHeight(Math.max(tmpA.getLeft().getHeight(), tmpB.getHeight() + 1));
		updateHeight2(tmpA);
		
		// update size
		tmpB.setSubtreeSize(tmpB.getLeft().getSubtreeSize() + tmpAL.getSubtreeSize() + 1);
		tmpA.setSubtreeSize(tmpB.getSubtreeSize() + tmpA.getRight().getSubtreeSize() + 1);
		updateSize(tmpA);
		
		// update sum
		tmpA.setSum(tmpB.getSum());
		tmpB.setSum(tmpB.getLeft().getSum() + tmpB.getRight().getSum() + tmpB.getKey());
		updateSum(tmpA);
		
		if (tmpA.getParent() == null) {
			this.root = tmpA;
		}
	}
	
	private void RLrotation(IAVLNode node) {
		
		IAVLNode tmpC = node;
		IAVLNode tmpBL = tmpC.getRight().getLeft().getLeft();
		IAVLNode tmpBR = tmpC.getRight().getLeft().getRight();
		IAVLNode tmpB = tmpC.getRight().getLeft();
		IAVLNode tmpA = tmpC.getRight();
		tmpB.setParent(tmpC.getParent());
		if (tmpC.getParent() != null) {
			if (tmpC.getParent().getKey() > tmpC.getKey()) {
				tmpC.getParent().setLeft(tmpB);
			}
			else {
				tmpC.getParent().setRight(tmpB);
			}
		}
		tmpC.setParent(tmpB);
		tmpA.setParent(tmpB);
		tmpBL.setParent(tmpC);
		tmpBR.setParent(tmpA);
		tmpC.setRight(tmpBL);
		tmpA.setLeft(tmpBR);
		tmpB.setLeft(tmpC);
		tmpB.setRight(tmpA);
		
		// update height
		tmpA.setHeight(Math.max(tmpBR.getHeight(), tmpA.getRight().getHeight()) + 1);
		tmpC.setHeight(Math.max(tmpC.getLeft().getHeight(), tmpBL.getHeight()) + 1);
		tmpB.setHeight(Math.max(tmpA.getHeight(), tmpC.getHeight()) + 1);
		updateHeight2(tmpB);
		
		// update size
		node.setSubtreeSize(node.getLeft().getSubtreeSize() + tmpBL.getSubtreeSize() + 1);
		tmpA.setSubtreeSize(tmpA.getRight().getSubtreeSize() + tmpBR.getSubtreeSize() + 1);
		tmpB.setSubtreeSize(node.getSubtreeSize() + tmpA.getSubtreeSize() + 1);
		updateSize(tmpB);
		
		// update sum
		tmpB.setSum(node.getSum());
		tmpA.setSum(tmpA.getRight().getSum() + tmpBR.getSum() + tmpA.getKey());
		node.setSum(node.getLeft().getSum() + tmpBL.getSum() + node.getKey());
		updateSum(tmpB);
		
		if (tmpB.getParent() == null) {
			this.root = tmpB;
		}
	}
	
	private void LRrotation(IAVLNode node) {
		
		IAVLNode tmpC = node;
		IAVLNode tmpBL = tmpC.getLeft().getRight().getLeft();
		IAVLNode tmpBR = tmpC.getLeft().getRight().getRight();
		IAVLNode tmpB = tmpC.getLeft().getRight();
		IAVLNode tmpA = tmpC.getLeft();
		tmpB.setParent(tmpC.getParent());
		tmpC.setParent(tmpB);
		if (tmpB.getParent() != null) {
			if (tmpB.getParent().getKey() > tmpB.getKey()) {
				tmpB.getParent().setLeft(tmpB);
			}
			else {
				tmpB.getParent().setRight(tmpB);
			}
		}
		tmpA.setParent(tmpB);
		tmpBL.setParent(tmpA);
		tmpBR.setParent(tmpC);
		tmpC.setLeft(tmpBR);
		tmpA.setRight(tmpBL);
		tmpB.setLeft(tmpA);
		tmpB.setRight(tmpC); 
		
		// update height
		tmpA.setHeight(Math.max(tmpA.getLeft().getHeight(), tmpBL.getHeight()) + 1);
		tmpC.setHeight(Math.max(tmpC.getRight().getHeight(), tmpBR.getHeight()) + 1);
		tmpB.setHeight(Math.max(tmpA.getHeight(), tmpC.getHeight()) + 1);
		updateHeight2(tmpB);
		
		// update size
		tmpC.setSubtreeSize(tmpBR.getSubtreeSize() + tmpC.getRight().getSubtreeSize() + 1);
		tmpA.setSubtreeSize(tmpBL.getSubtreeSize() + tmpA.getLeft().getSubtreeSize() + 1);
		tmpB.setSubtreeSize(tmpC.getSubtreeSize() + tmpA.getSubtreeSize() + 1);
		updateSize(tmpB);
		
		// update sum
		tmpB.setSum(node.getSum());
		tmpA.setSum(tmpA.getLeft().getSum() + tmpBL.getSum() + tmpA.getKey());
		node.setSum(node.getRight().getSum() + tmpBR.getSum() + node.getKey());
		updateSum(tmpB);
		
		if (tmpB.getParent() == null) {
			this.root = tmpB;
		}
	}
	
	
	/**
	* public int delete(int k)
	*
	* deletes an item with key k from the binary tree, if it is there;
	* the tree must remain valid (keep its invariants).
	* returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
	* returns -1 if an item with key k was not found in the tree.
	*/
	public int delete(int k) {
		
		// if the tree is empty
		if (this.empty()) {
			return -1;
		}

		// search for node k in the tree (if exist)
		IAVLNode deleteNode = searchForDelete(this.root, k);

		// if k is not in the tree return -1
		if (deleteNode.getKey() != k) {
			return -1;
		}
		
		// if there is only one or two nodes in the tree
		if (this.getRoot().getSubtreeSize() == 1) {
			IAVLNode vn = new AVLNode(-1, null);
			this.root = vn;
			this.max = null;
			this.min = null;
			this.root.setParent(null);
			return 0;
		}
		else if (this.getRoot().getSubtreeSize() == 2) {
			// deleted node is the root
			if (this.getRoot().getKey() == k) {
				if (this.getRoot().getLeft().getKey() == -1) {
					this.root = this.getRoot().getRight();
				}
				else {
					this.root = this.getRoot().getLeft();
				}
			}
			else {
				IAVLNode vn = new AVLNode(-1,null);
				this.getRoot().setLeft(vn);
				this.getRoot().setRight(vn);
			}
			this.root.setHeight(0);
			this.root.setSubtreeSize(1);
			this.root.setSum(root.getKey());
			this.root.setParent(null);
			this.min = root;
			this.max = root;
			return 0;
		}
		
		/** DELTET NODE FROM THE TREE */
		int cntSuccessor = 0;
		boolean flag = false;
		boolean successorFlag = false;
		// if delete node is a leaf
		if (deleteNode.getHeight() == 0) {
			if (deleteNode.getParent().getKey() < k) {
				deleteNode.getParent().setRight(new AVLNode(-1, null));
			}
			else {
				deleteNode.getParent().setLeft(new AVLNode(-1, null));
			}
		}
		// if delete node has one child
		else if (deleteNode.getLeft().isRealNode() && !deleteNode.getRight().isRealNode()) {
			if (deleteNode.getParent().getKey() > k) {
				deleteNode.getParent().setLeft(deleteNode.getLeft());
			}
			else {
				deleteNode.getParent().setRight(deleteNode.getLeft());
			}
			deleteNode.getLeft().setParent(deleteNode.getParent());
		}
		else if (!deleteNode.getLeft().isRealNode() && deleteNode.getRight().isRealNode()) {
			if (deleteNode.getParent() != null) {
				if (deleteNode.getParent().getKey() > k) {
					deleteNode.getParent().setLeft(deleteNode.getRight());
				}
				else {
					deleteNode.getParent().setRight(deleteNode.getRight());
				}
			}
			deleteNode.getRight().setParent(deleteNode.getParent());

		}
		// if deleted node has 2 children
		else {
			IAVLNode successor = getSuccessor(deleteNode);
			IAVLNode tmp = successor.getParent(); //to update height if deleted node is a root
			if (!successor.getLeft().isRealNode()) {
				successorFlag = true;
			}
			// the successor is the right child of the deleted node
			if (successor.getParent() == deleteNode) {
				// deleted node is a root
				if (deleteNode.getParent() == null) {
					flag = true;
					if (successor.getKey() > k) {
						successor.setLeft(deleteNode.getLeft());
					}
					else {
						successor.setRight(deleteNode.getRight());
					}
					successor.setParent(null);
					successor.setLeft(deleteNode.getLeft());
					updateHeightAfterDeleteForSuccessor(tmp);
					updateSumAfterDeleteForSuccessor(tmp, successor.getKey());
					updateSizeAfterDeleteForSuccessor(tmp);
					this.root = successor;
					
				}
				// deleted node is not a root
				else if (deleteNode.getParent().getKey() > k) {
					deleteNode.getParent().setLeft(successor);
					successor.setParent(deleteNode.getParent());
				}
				else {
					deleteNode.getParent().setRight(successor);
					successor.setParent(deleteNode.getParent());
				}
				successor.setLeft(deleteNode.getLeft());
				deleteNode.getLeft().setParent(successor);
				}
			else {
				if (successor.getRight().isRealNode()) {
					successor.getParent().setLeft(successor.getRight());
					successor.getRight().setParent(successor.getParent());
				}
				else {
					successor.getParent().setLeft(successor.getRight());
				}
				// deleted node is a root
				if (deleteNode.getParent() == null) {
					flag = true;
					successor.setParent(null);
					successor.setRight(deleteNode.getRight());
					successor.setLeft(deleteNode.getLeft());
					successor.setLeft(deleteNode.getLeft());
					successor.setRight(deleteNode.getRight());
					deleteNode.getRight().setParent(successor);
					deleteNode.getLeft().setParent(successor);
					this.root = successor;
				}
				else if (deleteNode.getParent().getKey() > k) {
					deleteNode.getParent().setLeft(successor);
					successor.setLeft(deleteNode.getLeft());
					successor.setRight(deleteNode.getRight());
					successor.setParent(deleteNode.getParent());
					deleteNode.getRight().setParent(successor);
					deleteNode.getLeft().setParent(successor);
				}
				else {
					deleteNode.getParent().setRight(successor);
					successor.setLeft(deleteNode.getLeft());
					successor.setRight(deleteNode.getRight());
					successor.setParent(deleteNode.getParent());
					deleteNode.getRight().setParent(successor);
					deleteNode.getLeft().setParent(successor);
				}
			}
			
			updateHeightAfterDeleteForSuccessor(tmp);
			updateSumAfterDeleteForSuccessor(tmp, successor.getKey());
			updateSizeAfterDeleteForSuccessor(tmp);
			cntSuccessor += checkBalance(tmp);
			
			successor.setHeight(Math.max(successor.getLeft().getHeight(), successor.getRight().getHeight()) + 1);
			successor.setSubtreeSize(successor.getLeft().getSubtreeSize() + successor.getRight().getSubtreeSize() + 1);
			successor.setSum(successor.getLeft().getSum() + successor.getRight().getSum() + successor.getKey());
			cntSuccessor += checkBalance(successor);
			if (successorFlag) {
				deleteNode = tmp;
			}	
		}
		
		//update height
		updateHeight(deleteNode);
		// update size
		updateSize(deleteNode);
		// update sum
		updateSum(deleteNode);
		
		int count = cntSuccessor;
		if (flag == true) {
			count = checkBalance(this.root);
		}
		IAVLNode y = deleteNode.getParent();
		while (y != null) {
			count += checkBalance(y);
			y = y.getParent();
		}
		
		// update max and min
		if (min.getKey() == k) {
			min = findMin();
		}
		else if (max.getKey() == k) {
			max = findMax();
		}
		
		return count;
	}
	
	private IAVLNode searchForDelete(IAVLNode node, int k) {
		
		IAVLNode result;
		if (node.getKey() == k || !node.isRealNode()) {
			return node;
		}
		else if (k < node.getKey()) {
			result = searchForDelete(node.getLeft(), k);
		}
		else {
			result = searchForDelete(node.getRight(), k);
		}
		return result;
	}
	
	private void updateSumAfterDeleteForSuccessor(IAVLNode node, int k) {
		
		while (node.getParent() != null) {
			node.setSum(node.getRight().getSum() + node.getLeft().getSum() + node.getKey());
			node = node.getParent();
		}
	}
	
	private void updateHeightAfterDeleteForSuccessor(IAVLNode node) {
		
		while (node != null) {
			node.setHeight(Math.max(node.getLeft().getHeight(), node.getRight().getHeight()) + 1);
			node = node.getParent();
		}
	}
	
	private void updateSizeAfterDeleteForSuccessor(IAVLNode node) {
		
		while (node != null) {
			node.setSubtreeSize((node.getLeft().getSubtreeSize() + node.getRight().getSubtreeSize()) + 1);
			node = node.getParent();
		}
	}
	
	private IAVLNode findMax() {
		
		IAVLNode node = this.root;
		while (node.getRight().isRealNode()) {
			node = node.getRight();
		}
		return node;
	}
	
	private IAVLNode findMin() {
		
		IAVLNode node = this.root;
		while (node.getLeft().isRealNode()) {
			node = node.getLeft();
		}
		return node;
	}

	/**
	* public String min()
    *
    * Returns the info of the item with the smallest key in the tree,
    * or null if the tree is empty
    */
	public String min() {
		
		if(empty()) {
			return null;
		}
		return min.getValue();
	}
	
	public int minKey() {
		
		if(empty()) {
			return -1;
		}
		return min.getKey();
	}
	
	public IAVLNode minNode(){
		
		if(empty()) {
			return null;
		}
		return min;
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree,
	 * or null if the tree is empty
	 */
	public String max(){
		
		if(empty()) {
			return null;
		}
		return max.getValue();
	}

	public int maxKey(){
		
		if(empty()) {
			return -1;
		}
		return max.getKey();
	}
	
	public IAVLNode maxNode(){
		
		if(empty()) {
			return null;
		}
		return max;
	}
  /**
   * public int[] keysToArray()
   *
   * Returns a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   */
	public int[] keysToArray(){
		
		if (this.empty()) {
			int[] result = new int[0];
			return result;
		}
		int[] arr = new int[this.size()];
		inOrderKeys(this.root, arr, 0);
		
		return arr;              
	}
	/**
	 * in order function
	 * @return sorted array with all the keys in the tree
	 */
	private int inOrderKeys(IAVLNode node, int[] arr, int i) {
		
		if (node.getKey() != -1) {
			i = inOrderKeys(node.getLeft(), arr, i);
			arr[i++] = node.getKey();
			i = inOrderKeys(node.getRight(), arr, i);
		}
		return i;
	}

	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree,
	 * sorted by their respective keys,
	 * or an empty array if the tree is empty.
	 */
	public String[] infoToArray(){
		
		if (this.empty()) {
			String[] result = new String[0];
			return result;
		}
		
		String[] arr = new String[this.size()];
		inOrderInfo(this.root, arr, 0);
		return arr;
	}
	/**
	 * in order function
	 * @param node
	 * @return sorted array with all the node's info of the tree
	 */
	private int inOrderInfo(IAVLNode node, String[] arr, int i) {
		
		if (node.getKey() != -1) {
			i = inOrderInfo(node.getLeft(), arr, i);
			arr[i++] = node.getValue();
			i = inOrderInfo(node.getRight(), arr, i);
		}
		return i;
	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 * precondition: none
	 * postcondition: none
    */
	public int size()
	{
		return this.root.getSubtreeSize();
	}
   
	/**
	 * public int getRoot()
	 *
	 * Returns the root AVL node, or null if the tree is empty
	 *
	 * precondition: none
	 * postcondition: none
	 */
	public IAVLNode getRoot()
	{
		if (empty()) {
			return null;
		}
		return this.root;
	}
	/**
	 * public string select(int i)
	 *
	 * Returns the value of the i'th smallest key (return null if tree is empty)
	 * Example 1: select(1) returns the value of the node with minimal key 
	 * Example 2: select(size()) returns the value of the node with maximal key 
	 * Example 3: select(2) returns the value 2nd smallest minimal node, i.e the value of the node minimal node's successor 	
	 *
	 * precondition: size() >= i > 0
	 * postcondition: none
	 */   
	public String select(int i)
	{
		if (empty() || i > this.size() || i < 1) {
			return null;
		}
		IAVLNode result = selectRec(this.root, i);
		return result.getValue();
	}	
	
	private IAVLNode selectRec(IAVLNode node, int i) {
		
		int nodeRank = node.getLeft().getSubtreeSize() + 1;
		if (i == nodeRank) {
			return node;
		}
		else if (i < nodeRank) {
			return selectRec(node.getLeft(), i);
		}
		else {
			return selectRec(node.getRight(), i - nodeRank);
		}
	}
	/**
	 * public int less(int i)
	 *
	 * Returns the sum of all keys which are less or equal to i
	 * i is not neccessarily a key in the tree 	
	 *
	 * precondition: none
	 * postcondition: none
	 */   
	
	public int less(int i) {
		
		int sum = 0;
		if (i < min.getKey() || empty()) {
			return sum;
		}
		
		IAVLNode node = searchForLess(this.root, i);
		if (node.getKey() <= i) {
			sum = node.getLeft().getSum() + node.getKey(); 
		}
		else {
			node = getPredeccesssor(node);
			sum = node.getLeft().getSum() + node.getKey();
		}
		while (node != root) {
			if (node == node.getParent().getRight()) {
				sum += (node.getParent().getLeft().getSum() + node.getParent().getKey());
			}
			node = node.getParent();
		}	
		return sum;
	}
	
	private IAVLNode searchForLess(IAVLNode node, int k) {
		
		IAVLNode result;
		if (k == node.getKey()) {
			return node;
		}
		else if (!node.isRealNode()) {
			return node.getParent();
		}
		else if (k < node.getKey()) {
			if (!node.getLeft().isRealNode()) {
				return node;
			}
			result = searchForLess(node.getLeft(), k);
		}
		else {
			if (!node.getRight().isRealNode()) {
				return node;
			}
			result = searchForLess(node.getRight(), k);
		}
		return result;
	}
	
	private IAVLNode getPredeccesssor(IAVLNode node) {
		
		IAVLNode predeccesssor = node;
		if (node.getParent() != null && node.getParent().isRealNode()) {
			if(node.getParent().getKey() < node.getKey()) {
				return node.getParent();
			}
			else if (node.getParent().isRealNode()) {
				while (node.getParent().getLeft() == node) {
					node = node.getParent();
				}
				predeccesssor = node.getParent();
			}
		}
		else {
			node = node.getLeft();
			while (node.getRight().isRealNode()) {
				node = node.getRight();	
			}
			predeccesssor = node;
		}
		
		return predeccesssor;
	}
	
	private IAVLNode getSuccessor(IAVLNode node) {
		
		IAVLNode successorNode;
		if (node.getRight().isRealNode()) {
			successorNode = node.getRight();
			while(successorNode.getLeft().isRealNode()) {
				successorNode = successorNode.getLeft(); 
			}
		}
		else {
			successorNode = node;
			while (successorNode.getParent() != null && successorNode.getParent().getKey() < successorNode.getKey()) { //going up-left
				successorNode = successorNode.getParent();
			}
			if (successorNode.getParent().getKey() > node.getKey()) { //going right one step, if possible
				successorNode = successorNode.getParent();
			}
			else {
				successorNode = node; 
			}
		}
		return successorNode;
	}


	/**
	* public interface IAVLNode
	* ! Do not delete or modify this - otherwise all tests will fail !
	*/
	public interface IAVLNode
	{	
		public int getKey(); //returns node's key (for virtuval node return -1)
		public String getValue(); //returns node's value [info] (for virtuval node return null)
		public void setLeft(IAVLNode node); //sets left child
		public IAVLNode getLeft(); //returns left child (if there is no left child return null)
		public void setRight(IAVLNode node); //sets right child
		public IAVLNode getRight(); //returns right child (if there is no right child return null)
		public void setParent(IAVLNode node); //sets parent
		public IAVLNode getParent(); //returns the parent (if there is no parent return null)
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node
		public void setSubtreeSize(int size); // sets the number of real nodes in this node's subtree
		public int getSubtreeSize(); // Returns the number of real nodes in this node's subtree (Should be implemented in O(1))
		public void setHeight(int height); // sets the height of the node
		public int getHeight(); // Returns the height of the node (-1 for virtual nodes)
		
		public void setSum(int k);
		public int getSum();
	}

	/**
	 * public class AVLNode
	 *
	 * If you wish to implement classes other than AVLTree
	 * (for example AVLNode), do it in this file, not in 
	 * another file.
	 * This class can and must be modified.
	 * (It must implement IAVLNode)
	 */
	public class AVLNode implements IAVLNode{
		
		private IAVLNode parent;
		private IAVLNode right;
		private IAVLNode left;
		private int key;
		private String info;
		private int height;
		private int size;
		private int sum;
		
		
		/**
		 * AVLNode constructor - create a node with key and info
		 * @param key
		 * @param info
		 */
		public AVLNode(int key, String info) {
			
			if (key != -1) {
				this.key = key;
				this.info = info;
				this.right = new AVLNode(-1, null);
				this.left = new AVLNode(-1, null);
				this.height = 0;
				this.size = 1;
				this.sum = key;
			}
			else {
				this.key = -1;
				this.info = null;
				this.height = -1;
				this.size = 0;
				this.sum = 0;
			}
		}
		
		public int getKey()
		{
			if (this.isRealNode()) {
				return key;
			}
			return -1;
		}
		
		public String getValue()
		{
			return info;
		}
		
		public void setLeft(IAVLNode node)
		{
			left = node;
		}
		
		public IAVLNode getLeft()
		{
			if (!this.isRealNode()) {
				return null;
			}
			return left;
		}
		
		public void setRight(IAVLNode node)
		{
			right = node;
		}
		
		public IAVLNode getRight()
		{
			if (!this.isRealNode()) {
				return null;
			}
			return right;
		}
		
		public void setParent(IAVLNode node)
		{
			parent = node;
		}
		
		public IAVLNode getParent()
		{
			if (parent == null) {
				return null;
			}
			return parent; 
		}
		
		// Returns True if this is a non-virtual AVL node
		public boolean isRealNode()
		{
			if(this == null || key == -1) {
				return false;
			}
			return true;
		}
		
		public void setSubtreeSize(int size)
		{
			this.size = size;
		}
		
		public int getSubtreeSize()
		{
			return size;
		}
		
		public void setHeight(int height)
		{
			this.height = height;
		}
		
		public int getHeight()
		{
			return height; 
		}
		
		public void setSum(int k) 
		{
			this.sum = k;
		}
		
		public int getSum() 
		{
			return this.sum;
		}
		
	}

}
  

