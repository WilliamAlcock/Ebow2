package VMQ;

public class DLinkedListNode<T> {
	private T data;
	private DLinkedListNode<T> previous;
	private DLinkedListNode<T> next;
	
	public DLinkedListNode (T data, DLinkedListNode<T> previous, DLinkedListNode<T> next) {
		this.data = data;
		this.previous = previous;
		this.next = next;
	}
	
	public DLinkedListNode<T> getNext() {
		return next;
	}
	
	public DLinkedListNode<T> getPrevious() {
		return previous;
	}
	
	public T getData() {
		return data;
	}
	
	public void setNext(DLinkedListNode<T> next) {
		this.next = next;
	}
	
	public void setPrevious(DLinkedListNode<T> previous) {
		this.previous = previous;
	}
	
	public void setMove(T data) {
		this.data = data;
	}
}
