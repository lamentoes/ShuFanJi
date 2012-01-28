package net.windward.RoboRally;// code primarily from http://forum.codecall.net/classes-code-snippets/26089-c-example-high-performance-tcp-server-client.html
// Edited by Windward Studios, Inc. (www.windward.net). No copyright claimed by Windward on changes.


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TcpClient implements Runnable
{
	private static final int BUFFER_SIZE = 65536;
	private static final int port = 1707;
	private IPlayerCallback playerCallback;

	private Thread worker;
	private Socket socket;
	private byte[] socketReadBuffer;

	private boolean abortRequested = false;

	// we store up a message here.
	// length: 0  => have less than 4 bytes in (including 0)
	//         >0 => have part/all of a message - length bytes have been removed from the buffer
	private FifoByteBuffer messageBuffer;
	private int messageLength;

	public TcpClient(IPlayerCallback playerCallback, String address) throws IOException {
		this.playerCallback = playerCallback;
		messageBuffer = new FifoByteBuffer(BUFFER_SIZE * 2);
		socketReadBuffer = new byte[BUFFER_SIZE];
		socket = new Socket(address, port);
	}

	public final Socket getSocket()
	{
		return socket;
	}
	public final void setSocket(Socket value)
	{
		socket = value;
	}
	public final byte[] getSocketReadBuffer()
	{
		return socketReadBuffer;
	}

	public final void ReceivedData(int bytesRead)
	{
		messageBuffer.Write(getSocketReadBuffer(), 0, bytesRead);
	}

	public final boolean getHasMessage()
	{
		// we may need to extract the length
		if (messageLength == 0 && messageBuffer.getCount() >= 4)
		{
			ByteBuffer bBuf = ByteBuffer.allocate(4);
			bBuf.order(ByteOrder.LITTLE_ENDIAN);
			bBuf.put(messageBuffer.Read(4));
			bBuf.position(0);
			messageLength = bBuf.getInt();
		}
		return messageLength > 0 && messageLength <= messageBuffer.getCount();
	}

	public final String getMessage() throws UnsupportedEncodingException {
		String rtn = new String (messageBuffer.Read(messageLength), "UTF-8");
		messageLength = 0;
		return rtn;
	}

	public final void Start()
	{
		worker = new Thread (this);
		worker.start();
	}

	/**
	 * When an object implementing interface <code>Runnable</code> is used
	 * to create a thread, starting the thread causes the object's
	 * <code>run</code> method to be called in that separately executing
	 * thread.
	 */
	@Override
	public void run() {

		try
		{
			while (true) {
				int bytesRead = socket.getInputStream().read(socketReadBuffer);
				if (bytesRead == 0) {
					TRAP.trap();
					continue;
				}
				if (bytesRead == -1) {
					TRAP.trap();
					throw new IllegalStateException("read socket returned -1");
				}


			synchronized (this)
			{
				ReceivedData(bytesRead);
			}

			// only way we have multiple messages is an error on the server side - but that could happen.
			while (true)
			{
				String message;
				synchronized (this)
				{
					if (! getHasMessage())
						break;
					message =  getMessage();
				}
				playerCallback.IncomingMessage(message);
			}
			}
		}
		catch (Exception ex)
		{
			if (abortRequested)
				return;

			System.out.println("Socket receive thread threw exception " + ex.getMessage());
			try {
				Close();
				TRAP.trap(); // bugbug - restart?
				playerCallback.ConnectionLost(ex);
			} catch (Exception e) {
				TRAP.trap();
				System.out.println("restart threw exception " + ex.getMessage());
			}
		}
	}

	public final void SendMessage(String msg) throws IOException {

		byte[] bytes = msg.getBytes("UTF-8");
		ByteBuffer bBuf = ByteBuffer.allocate(4);
		bBuf.order(ByteOrder.LITTLE_ENDIAN);
		bBuf.putInt(bytes.length);
		socket.getOutputStream().write(bBuf.array(), 0, 4);
		TRAP.trap(bytes.length > BUFFER_SIZE);
		for (int offset = 0; offset < bytes.length; offset += BUFFER_SIZE)
			socket.getOutputStream().write(bytes, offset, Math.min(bytes.length - offset, BUFFER_SIZE));
	}

	public final void Close() throws InterruptedException, IOException {

		if (socket == null)
			return;

		try
		{
			if (socket.isConnected())
				socket.close();
			else TRAP.trap();
		}

		catch (RuntimeException e)
		{
			// nada
			TRAP.trap();
		}
		socket = null;
		Thread.sleep(50);
	}

	public void abort() {
		abortRequested = true;
		try {
			if ((socket != null) && socket.isConnected())
				Close();
			if (worker != null && worker.isAlive())
				worker.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}