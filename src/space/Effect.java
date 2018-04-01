package space;

import java.awt.Graphics;
import java.awt.Graphics2D;

public interface Effect {
	public boolean getActive();
	public void update();
	public void draw(Graphics2D g);
}
