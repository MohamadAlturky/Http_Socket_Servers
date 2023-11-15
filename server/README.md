# Multi-threaded WebServers tasks executor using WebHttpServer and WebServerSocket

This project aims to create two kinds of multi-threaded web servers (with thread-pooling) for executing simple tasks.
There are three tasks implemented and the user can add more (see details below).
The web servers receive the command and the parameter(s) for running a task through terminal command `curl`.

## WebServers
* **WebServerHttp** is developed using *HttpServer* [[javadoc](https://docs.oracle.com/javase/8/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/HttpServer.html)].

* **WebServerSocket** is developed using *ServerSocket* [[javadoc](https://docs.oracle.com/javase/8/docs/api/java/net/ServerSocket.html)].

Both web servers use *ExecutorService* [[javadoc](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html)] that executes each submitted task in the pooled thread, more specifically, `Executors.newCachedThreadPool()` which is an unbounded thread pool with automatic thread reclamation [[javadoc](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html#newCachedThreadPool--)].

P.S. There is another project where I used *ServerSocker* in GitHub: [localhost-chat-socket](https://github.com/wagnerjfr/localhost-chat-socket)

## Tasks
The idea for the tasks comes from another GitHub project [TaskScheduler](https://github.com/wagnerjfr/Java-TaskScheduler).

The tasks currently implement are:
* **TaskBubbleSort**: sorts a list of numbers [[wikipedia](https://en.wikipedia.org/wiki/Bubble_sort)]
* **TaskFiboRecursive**: calculates the fibonacci of a number recursively [[wikipedia](https://en.wikipedia.org/wiki/Fibonacci_number)]
* **TaskBitcoin**: grabs the last price(s) of the bitcoin from [BitStamp](https://www.bitstamp.net/).

## How to add tasks

The steps to create a new task are:
* Create a new class inside the package `task`, for example, `TaskHello.java`;
* Make sure your class extends the abstract class `TaskImpl.java`;
* Create a constructor and override the required method `execute()`;
* Add your logic and attribute a `String` response to the `response` instance variable.

Example:
```java
package task;

import task.TaskImpl;

public class TaskHello extends TaskImpl {

    @Override
    public void execute() {
        result = "Hello " + input;
    }
}
```
* When the project is compiled, up and running, the task can be called like:
`curl -d TaskHello -d World http://localhost:8000/`

Something like this should be expected as output:
```console
wfranchi@computer:~$ curl -d TaskHello -d World http://localhost:8000
Result: Hello World; Executed in: 0,00s
```
Just two parameters are allowed: `-d <task name> -d <parameter values>`.

If you want to pass more parameter values, one suggestion is to use key delimiter like comma `,`.

## Running the web servers in Docker containers 

### 1. Download the project
```
git clone https://github.com/wagnerjfr/web-http-socket-server-task-manager-docker.git
cd web-http-socket-server-task-manager-docker
```
### 2. Build the project image
```
$ docker build -t taskwebserver:1.0 .
```
### 3. Running the web servers in different containers
Let's create two containers. First start the `WebServerSocket` container that can be accessed through the port `8000`:
```
$ docker run -d --rm --name webserversocket -e WEBSERVER=WebServerSocket -p 8000:8000 taskwebserver:1.0
```
And then start the `WebServerHttp` container that can be accessed through the port `8001`:
```
$ docker run -d --rm --name webserverhttp -e WEBSERVER=WebServerHttp -p 8001:8000 taskwebserver:1.0
```
Check whether the containers are up and running:
```
$ docker ps -a
```
Sample output:
```console
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
5f3564b1e3bd        taskwebserver:1.0   "/bin/sh -c 'java $W…"   3 seconds ago       Up 2 seconds        0.0.0.0:8001->8000/tcp   webserverhttp
83a07b12b36c        taskwebserver:1.0   "/bin/sh -c 'java $W…"   18 seconds ago      Up 17 seconds       0.0.0.0:8000->8000/tcp   webserversocket
```

## Running tasks separately

You can replace the port from `:8000/` to `:8001/` to run the same task but in a different web server if you have launched two containers as described before.

#### Bubble Sort
```
$ curl -d TaskBubbleSort -d 20,10,15,7,33,31,25,10,16,9,28,77,1,4,3,5,6,2,22,13 http://localhost:8000/
```
This is a POST request to run the task `TaskBubbleSort` and sort the numbers `20,10,15,7,33,31,25,10,16,9,28,77,1,4,3,5,6,2,22,13`.

#### Recursive Fibonacci
```
$ curl -d TaskFiboRecursive -d 46 http://localhost:8000/
```
This is a POST request to run the task `TaskFiboRecursive` and calculate the fibonacci value of `46`.

#### Bitcoin last price list
```
$ curl -d TaskBitcoin -d 5 http://localhost:8000/
```
This is a POST request to run the task `TaskBitcoin` and it will bring the last values of the bitcoin request `5` times with 1s interval.

#### Get executed task list
```
$ curl http://localhost:8000/
```
This is a GET request to list all the tasks already executed by the web server and its results.

## Running the tasks in parallel in UNIX

To check whether the web servers can handle more than one request in parallel, you can run the command list below or create a new one.

In order to the command line to run in background we append it with `&` at the end.
```
curl -d TaskFiboRecursive -d 46 http://localhost:8000/ & \
curl -d TaskFiboRecursive -d 45 http://localhost:8000/ & \
curl -d TaskFiboRecursive -d 46 http://localhost:8000/ & \
curl -d TaskFiboRecursive -d 45 http://localhost:8000/ & \
curl -d TaskBitcoin -d 5 http://localhost:8000/ & \
curl -d TaskBubbleSort -d 10,9,8,7,6,5,4,3,2,1 http://localhost:8000/ & \
curl -d TaskBubbleSort -d 20,9,18,7,36,5,44,3,22,1 http://localhost:8000/ &
```

## Cleanup

Stopping the containers:
```
$ docker stop webserverhttp webserversocket
```
Removing the docker images:
```
$ docker rmi taskwebserver:1.0 openjdk:8-jdk-alpine
```
