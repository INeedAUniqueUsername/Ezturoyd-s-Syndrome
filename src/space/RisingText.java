package space;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;

public class RisingText implements Effect {
	private Point pos;
	private int lifetime = 45;
	private String text;
	private Color color;
	public RisingText(Point pos, String text, Color color) {
		this.pos = pos;
		this.text = text;
		this.color = color;
	}
	public RisingText(Point pos, String text, Color color, int lifetime) {
		this.pos = pos;
		this.text = text;
		this.color = color;
		this.lifetime = lifetime;
	}
	public boolean getActive() {
		return lifetime > 0;
	}
	public void update() {
		lifetime--;
		pos.y++;
	}
	public void draw(Graphics2D g) {
		g.setColor(color);
		AffineTransform t = new AffineTransform();
		t.scale(1, -1);
		g.setFont(new Font("Consolas", Font.PLAIN, 24).deriveFont(t));
		
		g.drawString(text, pos.x, pos.y);
	}
}