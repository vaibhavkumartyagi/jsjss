package com.assignments.corejava;

public class UserCB {

    String name;
    int empId;
    Double salary;

    public UserCB(){

    }
        public UserCB(String name, int empId, Double salary){

        this.name=name;
        this.empId=empId;
        this.salary = salary;
    }


    public String toString(){
        return name+" "+empId+" "+salary;
    }
}