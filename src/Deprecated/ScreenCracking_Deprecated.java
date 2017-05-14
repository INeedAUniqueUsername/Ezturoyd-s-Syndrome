package Deprecated;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

import Interfaces.GameObject;
import Space.Helper;

public class ScreenCracking_Deprecated implements GameObject {
	private static final TexturePaint snow;
	static {
		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = ( (DataBufferInt) image.getRaster().getDataBuffer() ).getData();
		for(int i = 0; i < pixels.length; i++) {
			pixels[i] = new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256), 255).getRGB();
		}
		snow = new TexturePaint(image, new Rectangle(0, 0, 16, 16));
	}
	private int pos_x, pos_y;
	private ArrayList<ScreenCrackingBranch> branches;
	private int maxBranches;
	double branchChance;
	public ScreenCracking_Deprecated(int pos_x, int pos_y, int maxBranches, double branchChance) {
		System.out.println(new Point(pos_x, pos_y));
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.maxBranches = maxBranches;
		this.branchChance = branchChance;
		branches = new ArrayList<ScreenCrackingBranch>();
	}
	protected int getPosX() {
		return pos_x;
	}
	protected int getPosY() {
		return pos_y;
	}
	protected int getMaxBranches() {
		return maxBranches;
	}
	protected double getBranchChance() {
		return branchChance;
	}
	public void initializeBranches() {
		System.out.println("Branch update");
		while(branches.size() < maxBranches && Math.random() < branchChance) {
			ScreenCrackingBranch scb = createBranch();
			addBranch(scb);
			scb.initializeBranches();
		}
	}
	protected ScreenCrackingBranch createBranch() {
		System.out.println("Creating branch from " + new Point(pos_x, pos_y));
		Point2D next = Helper.polarOffset(new Point2D.Double(pos_x, pos_y), Helper.random(30)*12, 1);
		return new ScreenCrackingBranch((int) next.getX(), (int) next.getY(), maxBranches-3, branchChance-0.1, pos_x, pos_y);
	}
	protected void addBranch(ScreenCrackingBranch scb) {
		branches.add(scb);
	}
	protected ArrayList<ScreenCrackingBranch> getBranches() {
		return branches;
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub
		System.out.println("Branch update");
		if(getBranches().size() < getMaxBranches() && Math.random() < branchChance) {
			addBranch(createBranch());
		}
		for(ScreenCrackingBranch sc : branches) {
			sc.update();
		}
	}

	@Override
	public void draw(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		// TODO Auto-generated method stub
		for(ScreenCrackingBranch sc : branches) {
			System.out.println("Draw branches");
			System.out.println(pos_x + " " + pos_y);
			g2D.setPaint(snow);
			g2D.drawLine(pos_x, pos_y, sc.getPosX(), sc.getPosY());
			sc.draw(g2D);
		}
	}

}
class ScreenCrackingBranch extends ScreenCracking_Deprecated {

	private int origin_x, origin_y;
	public ScreenCrackingBranch(int pos_x, int pos_y, int maxBranches, double branchChance, int origin_x, int origin_y) {
		super(pos_x, pos_y, maxBranches, branchChance);
		setOrigin(origin_x, origin_y);
		System.out.println("New Branch");
	}
	public void setOrigin(int origin_x, int origin_y) {
		System.out.println("Origin: " + new Point(origin_x, origin_y));
		this.origin_x = origin_x;
		this.origin_y = origin_y;
		System.out.println("Origin: " + new Point(origin_x, origin_y));
	}
	public ScreenCrackingBranch createBranch() {
		//Create a branch that points away from the origin
		double angle = Helper.getAngleFromPos(new Point2D.Double(getPosX(), getPosY()), new Point2D.Double(origin_x, origin_y));
		System.out.println("Creating Branch from " + new Point(origin_x, origin_y));
		System.out.println("Creating Branch to " + new Point(getPosX(), getPosY()));
		Point2D next = Helper.polarOffset(new Point2D.Double(getPosX(), getPosY()), angle + Helper.randomRange(-1, 1) * 15, Helper.randomRange(40, 200));
		return new ScreenCrackingBranch((int) next.getX(), (int) next.getY(), getMaxBranches()-1, getBranchChance(), origin_x, origin_y);
	}
	
}