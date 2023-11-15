package handler;

import singleton.TaskExecutor;
import singleton.TasksList;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class RequestsHandlerSocket implements Runnable {

    private Socket socket;

    public RequestsHandlerSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            handle();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handle() throws IOException {
        // System.out.println("Start handling");
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        InputStream is = socket.getInputStream();
        InputStreamReader isReader = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isReader);

        PrintWriter writer = new PrintWriter(out, true);

        while (true) {

            String request = br.readLine();
            // System.out.println("line " + request);

            String response = TaskExecutor.INSTANCE.run(request);
            writer.println(response);

            if (request.equals("stop")) {
                break;
            }
        }
        writer.close();
        out.close();
        br.close();
        socket.close();
        //
        // byte[] line = is.readAllBytes();
        // System.out.println("line " + " request");
        //
        // String request = new String(line);
        //
        // System.out.println("line " + request);
        //
        // String response = "";
        // response = ""+ request;
        //// String requestType = getRequestType(br);
        //// System.out.println("requestType " + requestType);
        //// switch (requestType) {
        //// case "GET":
        //// response = TasksList.INSTANCE.list();
        //// break;
        //// case "POST":
        //// String params = getParameters(br);
        //// System.out.println("New task requested -> " + params);
        //// response = TaskExecutor.INSTANCE.run(params);
        //// break;
        //// }
        ////
        //// // Send HTTP response Status
        //// String resStatus= "HTTP_STATUS: 200 OK";
        ////
        //// // Send HTTP response headers
        //// String resHeader= "HEADERS: Content-Type: text/plain";
        //
        // // Send HTTP response body
        // PrintWriter writer = new PrintWriter(out, true);
        //// writer.print(resStatus);
        //// writer.print(resHeader);
        // writer.print(response);
        //
        // writer.close();
        // out.close();
        // br.close();
        // socket.close();
    }

    /**
     * Get the type of the request: GET, POST and etc.
     * 
     * @param in
     * @return String
     * @throws IOException
     */
    private String getRequestType(BufferedReader in) throws IOException {
        String request = in.readLine();
        request = request.substring(0, request.indexOf(" ")).trim();
        return request;
    }

    /**
     * Get the POST parameters to be used to create the tasks
     * 
     * @param in
     * @return String
     * @throws IOException
     */
    private String getParameters(BufferedReader in) throws IOException {
        StringBuilder values = new StringBuilder();
        while (in.ready()) {
            values.append((char) in.read());
        }
        List<String> list = Arrays.asList(values.toString().split("\r"));
        String result = list.get(list.size() - 1).trim();
        return result;
    }
}
