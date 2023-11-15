package client;

import stuff.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketLoadTester {
    public static void main(String[] args) {
        String url = "localhost";
        int port = 8000;
        int threadCount = 6;
        int callCountPerThread = 25;
        String requestBody = "TaskHash 45";
        SocketLoadTester.switchCase(url, port, threadCount, callCountPerThread, requestBody);

    }
    private static List<ApiResponseReport> reports = new ArrayList<>();

    public static void switchCase(String url, int port, int threadCount, int callCountPerThread,String requestBody) {
        Scanner scanner = new Scanner(System.in);
        boolean keepRunning = true;
        System.out.println("To run the test Press 1");
        System.out.println("To print the results of the tests Press 2");
        System.out.println("To close the application Press 0");
        while (keepRunning){
            int code = scanner.nextInt();
            if(code == 0){
                keepRunning = false;
            }
            if (code == 1){
                runLoadTest(url,port , threadCount,callCountPerThread,requestBody);
            }
            if(code == 2){
                double allTime = 0;
                double minTime = 100000000;
                double maxTime = 0;
                int numberOfSuccessRequests = 0;
                int numberOfFailureRequests = 0;
                for (ApiResponseReport report :reports){
                    allTime += report.executionTime;
                    if(report.executionTime > maxTime){
                        maxTime = report.executionTime;
                    }
                    if (report.executionTime < minTime){
                        minTime = report.executionTime;
                    }
                    if(report.status == Status.Success){
                        numberOfSuccessRequests ++;
                    }
                    else{
                        numberOfFailureRequests ++;
                    }
                }
                double meanTime = allTime/reports.size();
                System.out.println("mean time to response for all requests = "+ meanTime);
                System.out.println("min time to response for all requests = "+ minTime);
                System.out.println("max time to response for all requests = "+ maxTime);
                System.out.println("number of failure response for all requests = "+ numberOfFailureRequests);
                System.out.println("number of failure success for all requests = "+ numberOfSuccessRequests);

            }
        }
    }
    public static List<String> answers = new ArrayList<>();

    private static void runLoadTest(String url, int port, int threadCount, int callCountPerThread, String requestBody) {

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {

                Socket clientSocket = null;
                BufferedReader is = null;
                PrintStream os = null;
                try {
                    clientSocket = new Socket(url, port); // Create and connect the socket
                    os = new PrintStream(clientSocket.getOutputStream()); // Create an output stream to send data to the server
                    is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }

                try {
                    for (int j = 0; j < callCountPerThread; j++) {
                        double time = 0;
                        long start = System.currentTimeMillis();
                        os.println(requestBody + j);
                        os.flush();

                        String response = is.readLine();
                        System.out.println(response);
                        is.readLine();
                        long end = System.currentTimeMillis();
                        time = (end - start)/1000.0;
                        System.out.println("OK");
                        reports.add(new ApiResponseReport(Status.Success,time));
                        System.out.println("Client received: " + response);
                    }
                    os.close();
                    is.close();
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            });
        }
        executor.shutdown();
    }
}
