import java.util.concurrent.LinkedBlockingQueue;

public class QueueTest<T> {

    private Node<T> front;
    private Node<T> rear;

    private int length;

    private class Node<T>{
        private final T data;
        private Node<T> next;

        public Node(T data){
            super();
            this.data = data;
            this.next = null;
        }
    }

    public void enQueue(T data){
        if(front == null){
            rear = new Node<T>(data);
            front = rear;
        }
        else{
            rear.next = new Node<T>(data);
            rear = rear.next;
        }
        length++;
    }

    public T deQueue(){
        if(front != null){
            T result = front.data;
            front = front.next;
            length--;
            return result;  
        }
        return null;
    }

    public int getSize(){
        return length;
    }

    public void displayQueue(){
        Node<T> currenNode = front;
        while(currenNode!=null){
            System.out.println(currenNode.data+" ");
            currenNode = currenNode.next;
        }
    }


    public static void main(String[] args) {
        QueueTest<Integer> queue = new QueueTest<>();
        
        queue.enQueue(10);
        queue.enQueue(20);
        queue.enQueue(30);
        queue.enQueue(40);
        queue.enQueue(50);
        System.out.println("size: "+ queue.getSize());
        queue.displayQueue();

        System.out.println("dequeue: "+ queue.deQueue());
        System.out.println("size: "+ queue.getSize());

        String[] test = new String[]{"1","2","3","4"};
        String[] a = test.clone();
        String[] b = test;

        b[0] = "bbb";
        a[0] = "aaa";

        System.out.println(test[0]);
        
    
    }
}

