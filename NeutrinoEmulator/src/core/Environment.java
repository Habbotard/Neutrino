package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.imageio.spi.ServiceRegistry;

import core.packetlog.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.sulake.habbo.communication.messages.incoming.handshake.HelloMessageEvent;

import core.data.habbo.Habbo;
import core.messages.MessageEvent;
import core.network.Socket;
import core.storage.DatabaseManager;

public class Environment {
	// general controller of Neutrino, init all idiot things.
	public static MessageEvent[] Messages;
	private static String CurrentTime()
	{
		 SimpleDateFormat formattime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss ('ms' ms)", new Locale("es_ES"));
		 Date D = new Date();
		 return formattime.format(D);
	}
	
	public static void WritePacket(String Line, boolean IncomingMessage)
	{
		System.out.println("[" + CurrentTime() + "][" + ((IncomingMessage) ? "RECEIVED FROM" : "SENDED TO") + " CLIENT]" + ((IncomingMessage) ? "<-" : "->") + " " + Line);
	}
	
	public static void WriteDebug(String Line)
	{
		System.out.println("[" + CurrentTime() + "][DEBUG] " + Line);
	}
	
	public static void WriteStartup(String Line)
	{
		System.out.println("[" + CurrentTime() + "][STARTUP] " + Line);
	}
	
	public static void WriteStatus(String Line)
	{
		System.out.println("[" + CurrentTime() + "][STATE] " + Line);
	}
	
	public static void WriteLnStatus(String Line)
	{
		System.out.print("[" + CurrentTime() + "][STATE] " + Line);
	}
	
	public static void WriteLine(String Ln)
	{
		System.out.println(Ln);
	}
	
	public static void WriteLn(String Ln)
	{
		System.out.print(Ln);
	}
	
	public static void WriteException(String Line)
	{
		System.out.println("[" + CurrentTime() + "][EXCEPTION] " + Line);
	}
	
	public static void WriteException(Exception e)
	{
		System.out.println("[" + CurrentTime() + "][EXCEPTION] " + e.toString());
	}
	
	// All vars
	public static SessionFactory sessionFactory; // Hibernate sessionFactory
	private static DatabaseManager sessionManager;
	public static DatabaseManager GetDatabase()
	{
		return sessionManager;
	}
	public static Map<String, String> Properties;
	public static void Init() throws Exception
	{
		WriteStartup("Starting...");
		
		// Load properties
		WriteStatus("Loading properties from 'neutrino.properties'");
		Properties = new HashMap<String, String>();
        File propers = new File("neutrino.properties");
        BufferedReader propersreader = new BufferedReader(new FileReader(propers));
        while(propersreader.ready())
        {
        	String NextLine = propersreader.readLine();
        	if(!NextLine.startsWith("#") && NextLine.length() > 0)
        	{
        		String du = NextLine;
        		String[] d = du.split("=");
        		if(Properties.containsKey(d[0]))
        			break;
        		Properties.put(d[0], du.replace(d[0] + "=", ""));
        	}
        }
        WriteStatus("Loaded " + Properties.size() + " propertie(s) from 'neutrino.properties'");
        
        // Start Hibernate
        WriteStatus("Starting Hibernate...");
        sessionFactory = (new org.hibernate.cfg.Configuration()).configure().buildSessionFactory();
        sessionManager = new DatabaseManager();
        
        // Init cache
        WriteStatus("Loading all database information... (no much queries after that...?)");
        Habbo.init();
        // Set requests
        WriteLnStatus("Adding all requests to MessageEvent general list...");
        InitRequests();
        WriteLine("OK!");
        
        // Start Netty
        WriteStatus("Starting netty NIO sockets...");
        Socket.StartSocket(Integer.parseInt(Properties.get("tcp.portbase")));
        WriteStatus("Listening sockets on " + Properties.get("tcp.ipbase") + ":" + Properties.get("tcp.portbase"));
	}
	
	public static void InitRequests() throws Exception
    {
        Messages = new MessageEvent[20000];
        SetMessage(ClientEvents.HelloMessageEvent, new HelloMessageEvent());
    }
	
	private static void SetMessage(int i, MessageEvent e) throws Exception
    {
		Messages[i] = e;
    }
}
