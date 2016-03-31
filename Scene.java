import java.awt.*;
import java.awt.event.*;
import java.applet.*;

public abstract class Scene implements MouseListener, KeyListener
{
	public MeteonSim applet;

	public abstract Scene doWork();
	public void updatePlanet() {}

	// MouseListener Method
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}

	// KeyListener Method
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
}
