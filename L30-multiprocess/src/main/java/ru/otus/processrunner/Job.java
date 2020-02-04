package ru.otus.processrunner;

import java.util.stream.IntStream;

/*
from location: src/main/java
javac ru/otus/processrunner/Job.java
java ru.otus.processrunner.Job
 */
public class Job {
  public static void main(String[] args) {
    var propVal = System.getenv("propName");
    System.out.println("do job, propVal:" +  propVal);
    IntStream.range(1, 100).forEach(System.out::println);
  }
}
