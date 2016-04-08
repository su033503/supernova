package com.example.administrator.audiorecorder;

/**
 * Created by inter on 2016-04-01.
 */
public class Queue {
    private Qnode head; // 큐에 가장 최근에 들어온 값
    private Qnode tail; // 남은 데이터중 제일 처음 들어온 값

    // 생성자, queue 비어있으므로 head은 null이다.
    public Queue() {
        this.head = null;
        this.tail = null;
    }

    // 큐가 비어있는지 확인,
    public boolean empty() {

        return (tail == null); // tail 비어있다면 head은 null, true 반환 , 비어있지 않으면
        // false
        // 반환 -> 가장 늦게 들어온값이 존재 하지 않는 경우이므로 큐는 비어있다
    }

    // item 을 큐의 head에 넣는다.
    public void push(String item) {

        Qnode newNode = new Qnode(item); // 새 노드 객체 생성
        newNode.setNextNode(null);
        if (empty()) // 큐가 비어있는 경우
        {
            head = newNode;
            tail = newNode;
        } else {
            head.setNextNode(newNode);
            head = newNode;
        }
    }

    public String peek() {
        if (empty()) {// 큐이 비어있는경우
            return "EMPTY";
        }
        return tail.getData(); //처음 들어온 값들이 tail 값이 된다
    }

    public String pop() {

        String item = peek();
        tail = tail.getNextNode();  //tail은 다음 가리키는 값으로 이동

        return item;
    }
}

class Qnode{
    private String data; // 큐에 들어오는 값, Object 값으로 선언
    private Qnode nextNode; // 큐 타입으로 다음 노드를 가리킴

    // 생성자 선언 초기화
    Qnode() {
    }

    Qnode(String data) {
        this.data = data; //
        this.setNextNode(null);
    }

    // 값 리턴
    public String getData() {
        return this.data;
    }

    // 값 세팅
    public void setData(String data) {
        this.data = data;
    }

    // 노드 반환
    public Qnode getNextNode() {
        return this.nextNode;
    }

    // 노드 세팅
    public void setNextNode(Qnode nextNode) {
        this.nextNode = nextNode;
    }
}