package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.*;
import stuff.Status;

public class RestApiLoadTester {
    public static void main(String[] args) {

        String url = "http://localhost:8000";
        String headers = "content-type: text/plain\n" +
                "user-agent: Mozilla/5.0";
        int threadCount = 1;
        int callCountPerThread = 10;

        String requestBody = "TaskHash mohamad alturky is a good person";
        RestApiLoadTester.switchCase(url, headers, threadCount, callCountPerThread, requestBody);
    }

    public static void switchCase(String url, String headers, int threadCount, int callCountPerThread,
            String requestBody) {
        Scanner scanner = new Scanner(System.in);
        boolean keepRunning = true;
        System.out.println("To run the test Press 1");
        System.out.println("To print the results of the tests Press 2");
        System.out.println("To close the application Press 0");
        while (keepRunning) {
            int code = scanner.nextInt();
            if (code == 0) {
                keepRunning = false;
            }
            if (code == 1) {
                RestApiLoadTester.runLoadTest(url, headers, threadCount, callCountPerThread, requestBody);
            }
            if (code == 2) {
                double allTime = 0;
                double minTime = 100000000;
                double maxTime = 0;
                int numberOfSuccessRequests = 0;
                int numberOfFailureRequests = 0;
                for (ApiResponseReport report : RestApiLoadTester.reports) {
                    allTime += report.executionTime;
                    if (report.executionTime > maxTime) {
                        maxTime = report.executionTime;
                    }
                    if (report.executionTime < minTime) {
                        minTime = report.executionTime;
                    }
                    if (report.status == Status.Success) {
                        numberOfSuccessRequests++;
                    } else {
                        numberOfFailureRequests++;
                    }
                }
                double meanTime = allTime / reports.size();
                System.out.println("mean time to response for all requests = " + meanTime);
                System.out.println("min time to response for all requests = " + minTime);
                System.out.println("max time to response for all requests = " + maxTime);
                System.out.println("number of failure response for all requests = " + numberOfFailureRequests);
                System.out.println("number of success response for all requests = " + numberOfSuccessRequests);

            }
        }
    }

    private static List<ApiResponseReport> reports = new ArrayList<>();

    public static void runLoadTest(String url, String headers, int threadCount, int callCountPerThread,
            String requestBody) {
        OkHttpClient httpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("text/plain");

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < callCountPerThread; j++) {
                    Request.Builder requestBuilder = new Request.Builder()
                            .url(url);

                    // add headers
                    for (String header : headers.split("\n")) {
                        String[] headerParts = header.split(": ");
                        requestBuilder.addHeader(headerParts[0], headerParts[1]);
                    }

                    // add request body
                    RequestBody requestBodyObj = RequestBody.create(requestBody, mediaType);
                    Request request = requestBuilder
                            .post(requestBodyObj)
                            .build();
                    double time = 0;
                    long start = System.currentTimeMillis();

                    try {
                        Response response = httpClient.newCall(request).execute();
                        response.body().close();
                        long end = System.currentTimeMillis();
                        time = (end - start) / 1000.0;
                        System.out.println("OK");
                        reports.add(new ApiResponseReport(Status.Success, time));
                    } catch (IOException e) {
                        long end = System.currentTimeMillis();
                        time = (end - start) / 1000.0;
                        reports.add(new ApiResponseReport(Status.Failure, time));
                        System.out.println(e.getStackTrace());
                        e.printStackTrace();
                    }
                }
            });
        }
        executor.shutdown();
    }
}