package svgview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

class DelayedProgress implements Runnable {
	JProgressBar jpb;
	JStatusBar jsb;
	boolean stop = false;
	boolean waiting = true;
	int count = 8;
	Thread t;
	ProgressUpdater pupd = new ProgressUpdater();
	
	class ProgressUpdater implements Runnable {
		@Override
		public void run() {
			jsb.stopProgress2();
			jsb.setStatusLabel1("");			
		}		
	}

	public DelayedProgress(JStatusBar jsb){
		this.jsb = jsb;		
	}
	
	@Override
	public void run() {
		
		try {
			while (true) {
				Thread.sleep(100);
				if (count==0) {
					
					// aislar con SwingUtilities.invokeLater
					//jsb.stopProgress2();
					//jsb.setStatusLabel1("");
					// fin bloque
					SwingUtilities.invokeLater( pupd );
					
					synchronized(this) {
						waiting = true;
						wait();
					}
					
				} else {
					if (stop) count--;
				}
				synchronized(this) {
					waiting = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	synchronized void setStop(boolean b){
		stop = b;
		if (!b) {
			// Iniciar
			count = 8;
		} 

		if (waiting) {
			notify();				
		}		
	}	
}


class MemoryWatch implements Runnable {
	JLabel lbl;
	int c;
	Thread t;
	LabelUpdater lupd = new LabelUpdater();
	
	public MemoryWatch(JLabel lbl) {
		super();
		this.lbl = lbl;
		
//		t = new Thread(this);
//		t.start();

		
		lbl.addMouseListener(
				new MouseListener() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						System.gc();						
					}

					@Override
					public void mouseEntered(MouseEvent arg0) {
					}

					@Override
					public void mouseExited(MouseEvent arg0) {
					}

					@Override
					public void mousePressed(MouseEvent arg0) {						
					}

					@Override
					public void mouseReleased(MouseEvent arg0) {						
					}
				}
		
			);
	}
	
	@Override
	public void run() {
		while(lbl.isVisible()) {
			// Ejecutar en la hebra de swing
			SwingUtilities.invokeLater( lupd );			   
			try {
				Thread.sleep(5000);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	class LabelUpdater implements Runnable {
	   public void run() {
		   lbl.setText( "" + Runtime.getRuntime().freeMemory()/(1024*1024) + "MB / " 
			+ Runtime.getRuntime().totalMemory()/(1024*1024) + "MB (" +
			100 * (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())
				  / Runtime.getRuntime().totalMemory() + "%)");
	   }		
	}
	
}

class SeparatePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6381454935245655242L;
	private Color leftColor;
	private Color rightColor;
	 
	
	public SeparatePanel(Color left, Color right) {
		this.leftColor = left;
		this.rightColor = right;
		setOpaque(false);
		
//		setPreferredSize(new Dimension(40, 23));
	}
	
	protected void paintComponent(Graphics g) {
//		g.setColor(Color.BLUE);
//		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(leftColor);
		g.drawLine(0, 1, 0, getHeight() -2 );
		g.setColor(rightColor);
		g.drawLine(1, 1, 1, getHeight() -2);
	}
	
//	public Dimension getPreferredSize(){
//		return new Dimension(40, 23);
//	}
//	
//	public Dimension getMinimumSize(){
//		return new Dimension(40, 23);
//	}
//	
//	public Dimension getSize(){
//		return new Dimension(40, 23);
//	}
}

public class JStatusBar extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 662092776931926971L;
	JLabel lbl1;
	JLabel lbl2;
	JProgressBar progress;
	private boolean stopProcess=false;
	private DelayedProgress dpg;
	
	public JStatusBar() {		
		super(new BorderLayout());

		JPanel leftPanel = new JPanel(new FlowLayout());		
		lbl1 = new JLabel("Label1");
		lbl2 = new JLabel("Label2");
		
		progress = new JProgressBar();
		progress.setPreferredSize(new Dimension(50, 14));
		progress.setIndeterminate(false);
		progress.setMaximum(150);
		progress.setValue(50);
		leftPanel.add(progress);
		
		leftPanel.add( new JLabel(" ") ); // Margin
		leftPanel.add(lbl1);

		
		this.add(leftPanel, BorderLayout.WEST);
		SeparatePanel sp1 = new SeparatePanel(Color.WHITE,  Color.GRAY);
		lbl2.setToolTipText("Click to run garbage collector");
		sp1.add(lbl2);
		MemoryWatch mw = new MemoryWatch(lbl2);
		this.add(sp1, BorderLayout.EAST);
		setPreferredSize(new Dimension(getWidth(), 21));
		dpg = new DelayedProgress(this);
		
		try {
			//SwingUtilities.invokeLater(mw);
			new Thread(mw).start();
			//SwingUtilities.invokeLater(dpg);
			new Thread(dpg).start();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		
		
		
		
		
	}
	
	public void setStatusLabel1(String txt){
		lbl1.setText(txt);
	}
	
	public void setStatusLabel2(String txt){
		lbl2.setText(txt);
	}	
	
	
	public void stopProgress() {		
		this.stopProcess = true;
		dpg.setStop(true);
		
	}
	
	protected void stopProgress2() {
		if (stopProcess) {
			progress.setIndeterminate(false);
			progress.setValue(0);
			this.setStatusLabel1("Ready...");
		}		
	}
	
	public void startProgress() {
		this.stopProcess = false;
		dpg.setStop(false);
		progress.setIndeterminate(true);		
	}
}
