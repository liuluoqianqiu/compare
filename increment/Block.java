package increment;

import java.util.ArrayList;
import java.util.List;

import core.Coord;

public class Block {

	private static List<Block> AllBlocks = new ArrayList<Block>();
	private static double length = 250;
	private Coord blockId;
	
	public Block(Coord blockId) {
		this.blockId = blockId;
	}
	
	//根据轨迹坐标计算块ID，左上角坐标为(1,1)
	public static Coord calBlockId(Coord coord) {
		int xNum = (int)Math.ceil(coord.getX()/Block.length);
		int yNum = (int)Math.ceil(coord.getY()/Block.length); 
	
		return new Coord(xNum, yNum);
	}
	
	public static Block getBlockById(Coord id) {
		for(Block b : AllBlocks) {
			if(id.equals(b.getBlockId()))
				return b;
		}
		Block block = new Block(id);
		AllBlocks.add(block);
		return block;
	}
	
	public static double getLength() {
		return length;
	}
	public static void setLength(double length) {
		Block.length = length;
	}
	public Coord getBlockId() {
		return blockId;
	}
	public void setBlockId(Coord blockId) {
		this.blockId = blockId;
	}
	@Override
	public String toString() {
		return blockId.toString();
	}
}
