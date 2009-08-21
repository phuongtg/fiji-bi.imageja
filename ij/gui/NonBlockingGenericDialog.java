package ij.gui;
import ij.IJ;
import java.awt.event.ActionEvent;

public class NonBlockingGenericDialog extends GenericDialog {
	public NonBlockingGenericDialog(String title) {
		super(title, null);
		setModal(false);
	}

	public synchronized void showDialog() {
		super.showDialog();
		try {
			wait();
		} catch (InterruptedException e) { }
	}

	public synchronized void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		if (wasOKed() || wasCanceled())
			notify();
	}
}
