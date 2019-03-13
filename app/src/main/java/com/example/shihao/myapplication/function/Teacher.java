package com.example.shihao.myapplication.function;

public interface Teacher {
    void sex();
}

class MaleTeacher implements Teacher {

    @Override
    public void sex() {
        System.out.println("男老师");
    }
}

class FemaleTeacher implements Teacher {

    @Override
    public void sex() {
        System.out.println("女老师");
    }
}
