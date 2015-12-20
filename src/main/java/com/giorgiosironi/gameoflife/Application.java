package com.giorgiosironi.gameoflife;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class Application implements Runnable {

	private Server server;
	private boolean started;
	private Object startedNotification;
	
	public Application() {
		startedNotification = new Object();
	}

	public void run() {	
		try {
			synchronized (startedNotification) {
				started = false;
				server = new Server(8080);
				ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
				context.setContextPath("/");context.addServlet(GameOfLifeServlet.class, "/");
				server.setHandler(context);
				server.start();
				started = true;	
				startedNotification.notify();
			}
				//server.dumpStdErr();
			server.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void waitForStartup() throws InterruptedException {
		synchronized (startedNotification) {
			while (!started) {
				this.startedNotification.wait();
			}
		}
	}
	
	public void stop() {
		try {
			server.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		new Application().run();
	}
}