package VMQ;

import java.awt.List;

public class DLinkedList<T> extends List{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DLinkedListNode<T> head;
	private DLinkedListNode<T> last;
	private int size;
	private int pointerIndex;
	private DLinkedListNode<T> pointer;
	
	public DLinkedList() {
		clear();
	}
	
	public DLinkedListNode<T> addFirst(T data) {
		if (data==null) throw new IllegalArgumentException("data cannot be null");
		DLinkedListNode<T> newNode = new DLinkedListNode<T> (data,null,null);
		addNodeFirst(newNode);
		return newNode;
	}
	
	public DLinkedListNode<T> addLast(T data) {
		if (data==null) throw new IllegalArgumentException("data cannot be null");
		DLinkedListNode<T> newNode = new DLinkedListNode<T> (data,null,null);
		addNodeLast(newNode);
		return newNode;
	}
	
	public boolean removeFirst() {
		if (size>0) {
			DLinkedListNode<T> deleteNode = head.getNext();
			DLinkedListNode<T> newFirstNode = deleteNode.getNext();
			newFirstNode.setPrevious(head);
			head.setNext(newFirstNode);
			deleteNode.setNext(null);
			deleteNode.setPrevious(null);
			decSize();
			return true;
		}
		throw new NullPointerException("Size is 0");
	}
	
	public boolean removeLast() {
		if (size>0) {
			DLinkedListNode<T> deleteNode = last.getPrevious();
			DLinkedListNode<T> newLastNode = deleteNode.getPrevious();
			newLastNode.setNext(last);
			last.setPrevious(newLastNode);
			deleteNode.setNext(null);
			deleteNode.setPrevious(null);
			decSize();
			return true;
		}
		throw new NullPointerException("Size is 0");
	}
	
	public boolean remove(T data) {
		if (data==null) throw new IllegalArgumentException("data cannot be null");
		DLinkedListNode<T> curNode = head.getNext();
		boolean deleted = false;
		while ((curNode!=last) && (deleted==false)) {
			if (curNode.getData().equals(data)) {
				deleteNode(curNode);
				deleted = true;
				decSize();
			}
			curNode = curNode.getNext();
		}
		return deleted;
	}
	
	private void deleteNode(DLinkedListNode<T> curNode) {
		curNode.getPrevious().setNext(curNode.getNext());
		curNode.getNext().setPrevious(curNode.getPrevious());
	}			
	
	public boolean removeNode(DLinkedListNode<T> delNode) {
		DLinkedListNode<T> curNode = head.getNext();
		boolean deleted = false;
		while ((curNode!=last) && (deleted==false)) {
			if (curNode==delNode) {
				deleteNode(delNode);
				deleted = true;
				decSize();
			}
			curNode = curNode.getNext();
		}
		return deleted;
	}
	
	public void addNodeFirst(DLinkedListNode<T> curNode) {
		if (curNode.getData()==null) throw new IllegalArgumentException("data cannot be null");
		head.getNext().setPrevious(curNode);
		curNode.setPrevious(head);
		curNode.setNext(head.getNext());
		head.setNext(curNode);
		incSize();
	}
	
	public void addNodeLast(DLinkedListNode<T> curNode) {
		if (curNode.getData()==null) throw new IllegalArgumentException("data cannot be null");
		last.getPrevious().setNext(curNode);
		curNode.setNext(last);
		curNode.setPrevious(last.getPrevious());
		last.setPrevious(curNode);
		incSize();
	}
		
	public T getFirst() {
		if (size>0) {
			pointerIndex = 0;
			pointer = head.getNext();
			return pointer.getData();
		} else {
			pointerIndex=-1;
			return null;
		}
	}
	
	public T getNext() {
		if (size>0) {
			pointerIndex++;
			if (pointerIndex<size) {
				pointer = pointer.getNext();
				return pointer.getData();
			}
		}
		pointerIndex=-1;
		return null;
	}
	
	public DLinkedListNode<T> getCurrentNode() {
		return pointer;
	}
	
	public int getCurrentIndex() {
		return pointerIndex;
	}
	
	public void clear() {
		head = new DLinkedListNode<T> (null,null,null);
		last = new DLinkedListNode<T> (null,null,null);
		size = 0;
		pointerIndex = -1;
		pointer = null;
		head.setNext(last);
		last.setPrevious(head);
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	private void incSize() {
		pointerIndex=-1;
		size++;
	}
	
	private void decSize() {
		pointerIndex=-1;
		size--;
	}
	
	public int length() {
		return size;
	}

}
