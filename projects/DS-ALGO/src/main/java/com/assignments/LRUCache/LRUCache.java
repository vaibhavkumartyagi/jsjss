package com.assignments.LRUCache;

import org.w3c.dom.Node;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class LRUCache {



    public static class Node{

        Node next;
        Node prev;
        String value;
        String key;

        public Node(String key , String value){
            this.key = key;
            this.value= value;
        }
    }

    Map<String,Node> map = new ConcurrentHashMap<>();
    Node head = null;
    Node tail = null;
    public static int CAPACITY = 10;

    public void add(String key , String value){

        System.out.println("Capacity fulll key "+key + " value "+value+ " size "+map.size());



        Node node = null;
        if(map.containsKey(key)){
            node = map.get(key);
            node.value=value;
            removeNode(node);
            addNode(node);
            return;
        }
        if(map.size() >= CAPACITY){
            System.out.println("Capacity fulll key "+key + " value "+value);
            removeFromEnd();
        }
        node = new Node(key, value);
        addNode(node);
        System.out.println("After put key "+key + " value "+value+ " size "+map.size());

    }
    public Node get(String key){

        Node node = null;
        if(map.containsKey(key)){
            node = map.get(key);
            removeNode(node);
            addNode(node);
            return node;
        }
          return null;
    }
    public Node remove(String key){

        Node node = null;
        if(map.containsKey(key)){
            node = map.get(key);
            removeNode(node);
            return node;
        }
        return null;
    }


    public void removeNode(Node node){

        map.remove(node.key);
        Node prev = node.prev;
        Node next = node.next;

        if(prev != null ) prev.next = next;
        else head = next;

        if(next != null ) next.prev = prev;
        else tail = prev;

        node.next = null;
        node.prev = null;

    }
    public void addNode(Node node){

        if(head == null ){
            head = node;
            node.prev=null;
            node.next=null;
            tail=head;

        }else {
            Node head1 = head;
            node.next = head1;
            head1.prev = node;
            head = node;
        }
        map.put(node.key, node);
    }
    public void removeFromEnd(){

        Node node = tail ;
        Node prev = tail.prev;
        prev.next = null;
        node.prev= null;
        map.remove(node.key);
        tail = prev;
    }

    public static void main(String args[]){
        LRUCache lruCache = new LRUCache();

        for(int x=1; x< 13; x++) {
            lruCache.add(""+x,""+x);
        }
    }
}
