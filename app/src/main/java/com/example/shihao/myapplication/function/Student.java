package com.example.shihao.myapplication.function;

public interface Student {
    void sex();
}

class MaleStudent implements Student {

    @Override
    public void sex() {
        System.out.println("男学生");
    }
}

class FemaleStudent implements Student {

    @Override
    public void sex() {
        System.out.println("女学生");
    }
}
