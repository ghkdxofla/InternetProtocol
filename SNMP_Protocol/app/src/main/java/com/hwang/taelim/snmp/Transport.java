//package com.hwang.taelim.snmp;
//
//import java.io.IOException;
//import java.io.InterruptedIOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.PortUnreachableException;
//import java.net.SocketException;
//import java.net.SocketTimeoutException;
//import java.nio.ByteBuffer;
//import java.util.Collections;
//import java.util.Iterator;
//
//import org.snmp4j.log.LogAdapter;
//import org.snmp4j.log.LogFactory;
//import org.snmp4j.log.NoLogger;
//import org.snmp4j.smi.Address;
//import org.snmp4j.smi.TransportIpAddress;
//import org.snmp4j.smi.UdpAddress;
//
//
//
//public class Transport {
//	protected UdpAddress udpAddress;
//	protected DatagramSocket socket = null;
//	protected WorkerTask listener;
//	protected ListenThread listenerThread;
//	private int socketTimeout = 0;
//	private static final LogAdapter logger =
//		      LogFactory.getLogger(Transport.class);
//	  public Transport(UdpAddress udpAddress) {
//	    this.udpAddress = udpAddress;
//	    socket = new DatagramSocket(udpAddress.getPort());
//	  }
//	  
//	  public synchronized void listen() throws IOException {
//	    if (listener != null) {
//	      throw new SocketException("Port already listening");
//	    }
//	    ensureSocket();
//	    listenerThread = new ListenThread();
//	    listener = SNMP4JSettings.getThreadFactory().createWorkerThread(
//	        "DefaultUDPTransportMapping_"+getAddress(), listenerThread, true);
//	    listener.run();
//	  }
//	  
//	  private synchronized DatagramSocket ensureSocket() throws SocketException {
//	    DatagramSocket s = socket;
//	    if (s == null) {
//	      s = new DatagramSocket(udpAddress.getPort());
//	      s.setSoTimeout(socketTimeout);
//	      this.socket = s;
//	    }
//	    return s;
//	  }
//	  
//	  class ListenThread implements WorkerTask {
//
//		    private byte[] buf;
//		    private volatile boolean stop = false;
//
//
//		    public ListenThread() throws SocketException {
//		      buf = new byte[getMaxInboundMessageSize()];
//		    }
//
//		    public void run() {
//		      DatagramSocket socketCopy = socket;
//		      if (socketCopy != null) {
//		        try {
//		          socketCopy.setSoTimeout(getSocketTimeout());
//		          if (receiveBufferSize > 0) {
//		            socketCopy.setReceiveBufferSize(Math.max(receiveBufferSize,
//		                                                      maxInboundMessageSize));
//		          }
//		          if (logger.isDebugEnabled()) {
//		            logger.debug("UDP receive buffer size for socket " +
//		                             getAddress() + " is set to: " +
//		                             socketCopy.getReceiveBufferSize());
//		          }
//		        } catch (SocketException ex) {
//		          logger.error(ex);
//		          setSocketTimeout(0);
//		        }
//		      }
//		      while (!stop) {
//		        DatagramPacket packet = new DatagramPacket(buf, buf.length,
//		                                                   udpAddress.getInetAddress(),
//		                                                   udpAddress.getPort());
//		        try {
//		          socketCopy = socket;
//		          try {
//		            if (socketCopy == null) {
//		              stop = true;
//		              continue;
//		            }
//		            try {
//		                socketCopy.receive(packet);
//		            }
//		            catch (SocketTimeoutException ste) {
//		                continue;
//		            }
//		          }
//		          catch (InterruptedIOException iiox) {
//		            if (iiox.bytesTransferred <= 0) {
//		              continue;
//		            }
//		          }
//		          if (logger.isDebugEnabled()) {
//		            logger.debug("Received message from "+packet.getAddress()+"/"+
//		                         packet.getPort()+
//		                         " with length "+packet.getLength()+": "+
//		                         new OctetString(packet.getData(), 0,
//		                                         packet.getLength()).toHexString());
//		          }
//		          ByteBuffer bis;
//		          // If messages are processed asynchronously (i.e. multi-threaded)
//		          // then we have to copy the buffer's content here!
//		          if (isAsyncMsgProcessingSupported()) {
//		            byte[] bytes = new byte[packet.getLength()];
//		            System.arraycopy(packet.getData(), 0, bytes, 0, bytes.length);
//		            bis = ByteBuffer.wrap(bytes);
//		          }
//		          else {
//		            bis = ByteBuffer.wrap(packet.getData());
//		          }
//		          TransportStateReference stateReference =
//		            new TransportStateReference(DefaultUdpTransportMapping.this, udpAddress, null,
//		                                        SecurityLevel.undefined, SecurityLevel.undefined,
//		                                        false, socketCopy);
//		          fireProcessMessage(new UdpAddress(packet.getAddress(),
//		                                            packet.getPort()), bis, stateReference);
//		        }
//		        catch (SocketTimeoutException stex) {
//		          // ignore
//		        }
//		        catch (PortUnreachableException purex) {
//		          synchronized (DefaultUdpTransportMapping.this) {
//		            listener = null;
//		          }
//		          logger.error(purex);
//		          if (logger.isDebugEnabled()) {
//		            purex.printStackTrace();
//		          }
//		          if (SNMP4JSettings.isForwardRuntimeExceptions()) {
//		            throw new RuntimeException(purex);
//		          }
//		          break;
//		        }
//		        catch (SocketException soex) {
//		          if (!stop) {
//		            logger.warn("Socket for transport mapping " + toString() + " error: " + soex.getMessage());
//		          }
//		          if (SNMP4JSettings.isForwardRuntimeExceptions()) {
//		            stop = true;
//		            throw new RuntimeException(soex);
//		          }
//		          else if (!stop) {
//		            try {
//		              DatagramSocket newSocket = renewSocketAfterException(soex, socketCopy);
//		              if (newSocket == null) {
//		                throw soex;
//		              }
//		              socket = newSocket;
//		            } catch (SocketException e) {
//		              stop = true;
//		              socket = null;
//		              logger.error("Socket renewal for transport mapping " + toString() +
//		                  " failed with: " + e.getMessage(), e);
//
//		            }
//		          }
//		        }
//		        catch (IOException iox) {
//		          logger.warn(iox);
//		          if (logger.isDebugEnabled()) {
//		            iox.printStackTrace();
//		          }
//		          if (SNMP4JSettings.isForwardRuntimeExceptions()) {
//		            throw new RuntimeException(iox);
//		          }
//		        }
//		      }
//		      synchronized (DefaultUdpTransportMapping.this) {
//		        listener = null;
//		        stop = true;
//		        DatagramSocket closingSocket = socket;
//		        if ((closingSocket != null) && (!closingSocket.isClosed())) {
//		          closingSocket.close();
//		        }
//		        socket = null;
//		      }
//		      if (logger.isDebugEnabled()) {
//		        logger.debug("Worker task stopped:" + getClass().getName());
//		      }
//		    }
//
//		    public void close() {
//		      stop = true;
//		    }
//
//		    public void terminate() {
//		      close();
//		      if (logger.isDebugEnabled()) {
//		        logger.debug("Terminated worker task: " + getClass().getName());
//		      }
//		    }
//
//		    public void join() throws InterruptedException {
//		      if (logger.isDebugEnabled()) {
//		        logger.debug("Joining worker task: " + getClass().getName());
//		      }
//		    }
//
//		    public void interrupt() {
//		      if (logger.isDebugEnabled()) {
//		        logger.debug("Interrupting worker task: " + getClass().getName());
//		      }
//		      close();
//		    }
//		  }
//	  
//	  class LogFactory {
//
//		  public static final String SNMP4J_LOG_FACTORY_SYSTEM_PROPERTY =
//		      "snmp4j.LogFactory";
//
//		  private static LogFactory snmp4jLogFactory = null;
//		  private static boolean configChecked = false;
//
//		  /**
//		   * Gets the logger for the supplied class.
//		   *
//		   * @param c
//		   *    the class for which a logger needs to be created.
//		   * @return
//		   *    the <code>LogAdapter</code> instance.
//		   */
//		  public static LogAdapter getLogger(Class c) {
//		    checkConfig();
//		    if (snmp4jLogFactory == null) {
//		      return NoLogger.instance;
//		    }
//		    else {
//		      return snmp4jLogFactory.createLogger(c.getName());
//		    }
//		  }
//
//		  private static void checkConfig() {
//		    if (!configChecked) {
//		      configChecked = true;
//		      getFactoryFromSystemProperty();
//		    }
//		  }
//
//		  @SuppressWarnings("unchecked")
//		  private synchronized static void getFactoryFromSystemProperty() {
//		    try {
//		      String factory =
//		          System.getProperty(SNMP4J_LOG_FACTORY_SYSTEM_PROPERTY, null);
//		      if (factory != null) {
//		        try {
//		          Class<? extends LogFactory> c = (Class<? extends LogFactory>)Class.forName(factory);
//		          snmp4jLogFactory = c.newInstance();
//		        }
//		        catch (ClassNotFoundException ex) {
//		          throw new RuntimeException(ex);
//		        }
//		        catch (IllegalAccessException ex) {
//		          throw new RuntimeException(ex);
//		        }
//		        catch (InstantiationException ex) {
//		          throw new RuntimeException(ex);
//		        }
//		      }
//		    }
//		    catch (SecurityException sec) {
//		      throw new RuntimeException(sec);
//		    }
//		  }
//
//		  /**
//		   * Returns the top level logger.
//		   * @return
//		   *    a LogAdapter instance.
//		   * @since 1.7
//		   */
//		  public LogAdapter getRootLogger() {
//		    return NoLogger.instance;
//		  }
//
//		  /**
//		   * Gets the logger for the supplied class name.
//		   *
//		   * @param className
//		   *    the class name for which a logger needs to be created.
//		   * @return
//		   *    the <code>LogAdapter</code> instance.
//		   * @since 1.7
//		   */
//		  public static LogAdapter getLogger(String className) {
//		    checkConfig();
//		    if (snmp4jLogFactory == null) {
//		      return NoLogger.instance;
//		    }
//		    else {
//		      return snmp4jLogFactory.createLogger(className);
//		    }
//		  }
//
//		  /**
//		   * Creates a Logger for the specified class. This method returns the
//		   * {@link NoLogger} logger instance which disables logging.
//		   * Overwrite this method the return a custom logger to enable logging for
//		   * SNMP4J.
//		   *
//		   * @param c
//		   *    the class for which a logger needs to be created.
//		   * @return
//		   *    the <code>LogAdapter</code> instance.
//		   */
//		  protected LogAdapter createLogger(Class c) {
//		    return NoLogger.instance;
//		  }
//
//		  /**
//		   * Creates a Logger for the specified class. This method returns the
//		   * {@link NoLogger} logger instance which disables logging.
//		   * Overwrite this method the return a custom logger to enable logging for
//		   * SNMP4J.
//		   *
//		   * @param className
//		   *    the class name for which a logger needs to be created.
//		   * @return
//		   *    the <code>LogAdapter</code> instance.
//		   * @since 1.7
//		   */
//		  protected LogAdapter createLogger(String className) {
//		    return NoLogger.instance;
//		  }
//
//		  /**
//		   * Sets the log factory to be used by SNMP4J. Call this method before
//		   * any other SNMP4J class is referenced or created to set and use a custom
//		   * log factory.
//		   *
//		   * @param factory
//		   *    a <code>LogFactory</code> instance.
//		   */
//		  public static void setLogFactory(LogFactory factory) {
//		    configChecked = true;
//		    snmp4jLogFactory = factory;
//		  }
//
//		  /**
//		   * Gets the log factory to be used by SNMP4J. If the log factory has not been
//		   * initialized by {@link #setLogFactory} a new instance of {@link LogFactory}
//		   * is returned.
//		   *
//		   * @return
//		   *    a <code>LogFactory</code> instance.
//		   * @since 1.7
//		   */
//		  public static LogFactory getLogFactory() {
//		    if (snmp4jLogFactory == null) {
//		      return new LogFactory();
//		    }
//		    return snmp4jLogFactory;
//		  }
//
//		  /**
//		   * Returns all available LogAdapters in depth first order.
//		   * @return
//		   *    a read-only Iterator.
//		   * @since 1.7
//		   */
//		  public Iterator loggers() {
//		    return Collections.singletonList(NoLogger.instance).iterator();
//		  }
//
//		}
//	  
//	  static class UdpAddress extends TransportIpAddress {
//
//		  static final long serialVersionUID = -4390734262648716203L;
//
//		  public UdpAddress() {
//		  }
//
//		  public UdpAddress(InetAddress inetAddress, int port) {
//		    setInetAddress(inetAddress);
//		    setPort(port);
//		  }
//
//		  public UdpAddress(int port) {
//		    setPort(port);
//		  }
//
//		  public UdpAddress(String address) {
//		    if (!parseAddress(address)) {
//		      throw new IllegalArgumentException(address);
//		    }
//		  }
//
//		  public static Address parse(String address) {
//		    UdpAddress a = new UdpAddress();
//		    if (a.parseAddress(address)) {
//		      return a;
//		    }
//		    return null;
//		  }
//
//		  public boolean equals(Object o) {
//		    return (o instanceof UdpAddress) && super.equals(o);
//		  }
//
//		}
//}
//
//
