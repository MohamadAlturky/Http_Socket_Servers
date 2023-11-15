package client;

import stuff.Status;

public class ApiResponseReport {
    public Status status;
    public double executionTime;
    public ApiResponseReport(Status status, double executionTime) {
        this.status = status;
        this.executionTime = executionTime;
    }
}

