import Demo.Response;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Client {

     // Contador de solicitudes enviadas
     private static final AtomicLong sentRequestCount = new AtomicLong(0);


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        java.util.List<String> extraArgs = new java.util.ArrayList<>();




        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "config.client",
                extraArgs)) {
            // com.zeroc.Ice.ObjectPrx base =
            // communicator.stringToProxy("SimplePrinter:default -p 10000");
            Response response = null;
            Demo.PrinterPrx service = Demo.PrinterPrx
                    .checkedCast(communicator.propertyToProxy("Printer.Proxy"));

            if (service == null) {
                throw new Error("Invalid proxy");
            }

            String message;
            boolean execute = true;
            String user = getUserHostString();

            System.out.println("Bienvenido"
                    + "\nIngrese:"
                    + "\n-Un numero n, para saber el fibonacci de ese numero y sus factores primos"
                    + "\n-La palabra listifs para ver las interfaces logicas del server"
                    + "\n-La palabra listports junto con una direccion ip para ver los servicios y los puertos en los que se ofrecen estos servicios en la ip dada. Ejemplo de cadena listports192.158.1.38"
                    + "\n-El caracter ! junto con un comando para ver el resultado de correr ese comando en el server. Ejemplo de string !comando"
                    + "\n-Exit para cerrar el programa");

            while (execute) {

                message = scanner.nextLine();
                if (message == "exit") {
                    return;
                }

                // Incrementar el contador de solicitudes enviadas
                sentRequestCount.incrementAndGet();

                response = service.printString(user + message);

                System.out.println("Respuesta del server: " + response.value + ", Tiempo de respuesta: "
                        + response.responseTime + " ms, Latencia: " + response.latency + " ns");
            }
        }
    }

    private static String getUserHostString() {
        String username = System.getProperty("user.name");
        String hostname = getHostname();
        return username + ":" + hostname + " ";
    }

    private static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown-host";
        }
    }
}