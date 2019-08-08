package House_Committee.Server;

import House_Committee.db.sqlHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    public static final String SPACIALLINEBREAK = "#$";
    public static final Object waitObject = Server.class;
    public static int connected = 0;
    public static void main(String argv[]) throws Exception
    {
        ServerSocket s = null;
        sqlHandler sql = new sqlHandler();
        int port = 10000;
        try {
            s = new ServerSocket(port);
            sqlHandler.ConnectingToSQL();
            System.out.println("Server is on, Port is: "+ port);
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        while (true) {
            Socket incoming = null;

            try {
                incoming = s.accept();
                synchronized (Server.waitObject)
                {
                    connected++;
                }

                System.out.println("num of connected clients "+ connected);
            } catch(IOException e) {
                System.out.println(e);
                continue;
            }
            new socketHandler(incoming, sql,port).start();


        }
    }
}
