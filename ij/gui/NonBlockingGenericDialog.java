package ij.gui;
import ij.*;
import java.awt.event.*;
import java.awt.EventQueue;

/** This is an extension of GenericDialog that is non-model.
 *	@author Johannes Schindelin
 */
public class NonBlockingGenericDialog extends GenericDialog {
	public NonBlockingGenericDialog(String title) {
		super(title, null);
		setModal(false);
	}

	public synchronized void showDialog() {
		super.showDialog();
		if (!IJ.macroRunning()) { // add to Window menu on event dispatch thread
			final NonBlockingGenericDialog thisDialog = this;
			try {
				EventQueue.invokeAndWait(new Runnable() {
					public void run() {
						WindowManager.addWindow(thisDialog);
					}
				});
			} catch (Exception e) { }
		}
		try {
			wait();
		} catch (InterruptedException e) { }
	}

	public synchronized void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		if (!isVisible())
			notify();
	}
	
	public synchronized void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		if (wasOKed() || wasCanceled())
			notify();
	}

    public synchronized void windowClosing(WindowEvent e) {
		super.windowClosing(e);
		if (wasOKed() || wasCanceled())
			notify();
    }
    
	public void dispose() {
		super.dispose();
		WindowManager.removeWindow(this);
	}

}
