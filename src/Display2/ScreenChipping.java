package display;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

import interfaces.GameObject;

public class ScreenChipping implements GameObject {
	Polygon shape;
	public ScreenChipping() {
		
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		BufferedImage b = new BufferedImage(1, 2, BufferedImage.TYPE_INT_ARGB);
	}

}
