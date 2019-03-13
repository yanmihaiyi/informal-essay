package com.example.shihao.myapplication.function;


public class Bschool extends School {
    @Override
    public Teacher findTeacher() {
        return new FemaleTeacher();
    }

    @Override
    public Student findStudent() {
        return new MaleStudent();
    }
}
