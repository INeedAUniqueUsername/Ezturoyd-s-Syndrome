package Display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import Interfaces.GameObject;
import Space.Helper;

public class ScreenCracking implements GameObject {

	private int pos_x, pos_y;
	private ArrayList<ScreenCrackingBranch> branches;
	private int maxBranches;
	public ScreenCracking(int pos_x, int pos_y, int maxBranches) {
		setPos(pos_x, pos_y);
		setMaxBranches(maxBranches);
		branches = new ArrayList<ScreenCrackingBranch>();
	}
	public void setPos(int pos_x, int pos_y) {
		this.pos_x = pos_x;
		this.pos_y = pos_y;
	}
	public void setMaxBranches(int maxBranches) {
		this.maxBranches = maxBranches;
	}
	public int getPosX() {
		return pos_x;
	}
	public int getPosY() {
		return pos_y;
	}
	public int getMaxBranches() {
		return maxBranches;
	}
	public void createBranch() {
		Point2D next = Helper.polarOffset(new Point2D.Double(pos_x, pos_y), Helper.random(360), 10);
		System.out.println("Create new branch at (" + (int) next.getX() + ", " + (int) next.getY() + ")");
		addBranch(new ScreenCrackingBranch((int) next.getX(), (int) next.getY(), maxBranches-1, pos_x, pos_y));
	}
	public void addBranch(ScreenCrackingBranch scb) {
		branches.add(scb);
	}
	public ArrayList<ScreenCrackingBranch> getBranches() {
		return branches;
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub
		System.out.println("Branch update");
		if(getBranches().size() < getMaxBranches() && Math.random() < 0.5) {
			createBranch();
		}
		updateBranches();
	}
	public void updateBranches() {
		for(ScreenCrackingBranch sc : branches) {
			sc.update();
		}
	}

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		for(ScreenCrackingBranch sc : branches) {
			System.out.println("Draw branches");
			System.out.println(pos_x + " " + pos_y);
			g.setColor(Color.BLACK);
			g.drawLine(pos_x, pos_y, sc.getPosX(), sc.getPosY());
			sc.draw(g);
		}
	}

}
class ScreenCrackingBranch extends ScreenCracking {

	private int origin_x, origin_y;
	public ScreenCrackingBranch(int pos_x, int pos_y, int maxBranches, int origin_x, int origin_y) {
		super(pos_x, pos_y, maxBranches);
		setOrigin(origin_x, origin_y);
	}
	public void setOrigin(int origin_x, int origin_y) {
		this.origin_x = origin_x;
		this.origin_y = origin_y;
	}
	public void update() {
		if(getBranches().size() < getMaxBranches() && Math.random() < 0.5) {
			createBranch();
		}
		updateBranches();
	}
	public void createBranch() {
		double angle = Helper.arctanDegrees(getPosY() - origin_y, getPosX() - origin_x);
		Point2D next = Helper.polarOffset(new Point2D.Double(getPosX(), getPosY()), angle + Helper.random(30) - 15, 10);
		System.out.println("Create new sub-branch at (" + (int) next.getX() + ", " + (int) next.getY() + ")");
		addBranch(new ScreenCrackingBranch((int) next.getX(), (int) next.getY(), getMaxBranches()-1, origin_x, origin_y));
	}
	
}