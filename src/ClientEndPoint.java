import java.net.InetAddress;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class ClientEndPoint {
	protected String name;
	protected InetAddress address;
	protected int port;
	protected Set<String> groups;
	protected Queue<String> messages;
	protected int numberOfTimesMessageSent;
	
	public ClientEndPoint(String name, InetAddress addr, int port, Set<String> groups) {
		this.name = name;
		this.address = addr;
		this.port = port;
		this.groups = Collections.synchronizedSet(groups);
		this.numberOfTimesMessageSent = 0;
		this.messages = new LinkedList<String>();
	}

	@Override
	public int hashCode() {
		// the hashcode is the exclusive or (XOR) of the port number and the hashcode of the address object
		return this.port ^ this.address.hashCode();
	}

}
