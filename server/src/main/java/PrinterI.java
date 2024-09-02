import Demo.Response;
import java.util.ArrayList;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrinterI implements Demo.Printer
{
    private static ArrayList<Integer> fib = new ArrayList<Integer>();

    public Response printString(String s, com.zeroc.Ice.Current current)
    {
        System.out.println(s);

        String[] input = s.split(" ");

        String user = input[0];
        int number;

        try{
            number = Integer.parseInt(input[1]);
            //return new Response(0, user + "\nFibonacci: " + fibonacci(number) + "\nPrime factors: " + primeFactor(number));
            System.out.println(user + "\nFibonacci: " + fibonacci(number) + "\nPrime factors: " + primeFactor(number));
            return new Response(0, "Server response: " + s);

        }catch(NumberFormatException e){

        }

        System.out.println(user);
        if (input[1].contains("listifs")){
            printNetworkInterfaces();
        }else if (input[1].contains("listports")){
            runNmapOnIp(input[1]);
        }else if (input[1].contains("!")){
            executeCommand(input[1]);
        }

        return new Response(0, "Server response: " + s);
    }

    private int fibonacci(int n){
        if (fib.isEmpty()){
            fib.add(1);
            fib.add(1);
        }
        
        if(n <= 1){
            return 1;
        } else if (fib.size() < n){
            
            for (int i = 2; i <= n; i++){
                fib.add(i, fibonacci(i-1) + fibonacci(i-2));
            }

        }
        return fib.get(n);
    }

    private String primeFactor (int n){
        String message = "";

        int pf = 2;
        boolean firstPF = true;

        while (n > 1) {
            if (n%pf == 0){
                if (firstPF){
                    message = pf + "";
                }else {
                    message += " + " + pf;
                }
                n /= pf;
            } else {
                pf++;
            }
        }

        return message;
    }

    private static void printNetworkInterfaces (){
        try{

            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {
                System.out.println(netint.getDisplayName());
            }

            
        }catch (SocketException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static void runNmapOnIp(String input) {
        String ip = extractIp(input);
        if (ip != null) {
            System.out.println("Extracted IP: " + ip);
            runNmap(ip);
        } else {
            System.out.println("Can not extract a valid ip address");
        }
    }

    private static String extractIp(String input) {
        Pattern pattern = Pattern.compile("listports\\s*(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})\\s*");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static void runNmap(String ip) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("nmap", "-sn", ip);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    

    //

    private static void executeCommand(String input) {
        String command = extractCommand(input);
        if (command != null) {
            System.out.println("Extracted command: " + command);
            runCommand(command);
        } else {
            System.out.println("can not extract command.");
        }
    }

    private static String extractCommand(String input) {
        Pattern pattern = Pattern.compile("^\\s*!(\\S.*)\\s*$");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private static void runCommand(String command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                processBuilder.command("cmd.exe", "/c", command);
            } else {
                processBuilder.command("sh", "-c", command);
            }
            
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            System.out.println("Result");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }

        } catch (Exception e) {
            System.out.println("Error");
        }
    }
}