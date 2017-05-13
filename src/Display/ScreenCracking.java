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
	double branchChance;
	public ScreenCracking(int pos_x, int pos_y, int maxBranches, double branchChance) {
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.maxBranches = maxBranches;
		this.branchChance = branchChance;
		branches = new ArrayList<ScreenCrackingBranch>();
		initializeBranches();
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
	private void initializeBranches() {
		System.out.println("Branch update");
		while(branches.size() < maxBranches && Math.random() < branchChance) {
			addBranch(createBranch());
		}
	}
	protected ScreenCrackingBranch createBranch() {
		Point2D next = Helper.polarOffset(new Point2D.Double(pos_x, pos_y), Helper.random(30)*12, Helper.randomRange(20, 100));
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
		// TODO Auto-generated method stub
		for(ScreenCrackingBranch sc : branches) {
			System.out.println("Draw branches");
			System.out.println(pos_x + " " + pos_y);
			g.setColor(Color.RED);
			g.drawLine(pos_x, pos_y, sc.getPosX(), sc.getPosY());
			sc.draw(g);
		}
	}

}
class ScreenCrackingBranch extends ScreenCracking {

	private int origin_x, origin_y;
	public ScreenCrackingBranch(int pos_x, int pos_y, int maxBranches, double branchChance, int origin_x, int origin_y) {
		super(pos_x, pos_y, maxBranches, branchChance);
		setOrigin(origin_x, origin_y);
	}
	public void setOrigin(int origin_x, int origin_y) {
		this.origin_x = origin_x;
		this.origin_y = origin_y;
	}
	public ScreenCrackingBranch createBranch() {
		//Create a branch that points away from the origin
		double angle = Helper.arctanDegrees(getPosY() - origin_y, getPosX() - origin_x);
		Point2D next = Helper.polarOffset(new Point2D.Double(getPosX(), getPosY()), angle + Helper.randomRange(-6, 6) * 15, Helper.randomRange(10, 50));
		return new ScreenCrackingBranch((int) next.getX(), (int) next.getY(), getMaxBranches()-1, getBranchChance(), origin_x, origin_y);
	}
	
}