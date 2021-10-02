package io.richardqiao.practice.file;

import java.io.*;
import java.util.Scanner;

public class ConsoleAccess {

    public static void main(String[] args) throws IOException {
        readConsole();
    }

    private static void readConsole(){
        //Creating Scanner instance to scan console for User input
        Scanner console = new Scanner(System.in);
        System.out.println("System is ready to accept input, please enter name : ");
        String name = console.nextLine();
        System.out.println("Hi " + name + ", Can you enter an int number now?");
        int number = console.nextInt();
        System.out.println("You have entered : " + number);
        System.out.println("Thank you");
    }
}
