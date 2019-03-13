package com.example.shihao.myapplication.function;

public class Aschool extends School {
    @Override
    public Teacher findTeacher() {
        return new MaleTeacher();
    }

    @Override
    public Student findStudent() {
        return new FemaleStudent();
    }
}
