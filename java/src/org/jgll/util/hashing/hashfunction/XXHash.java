package org.jgll.util.hashing.hashfunction;

public class XXHash implements HashFunction {
	//private static final int PRIME1 = (int) 2654435761L;
	private static final int PRIME2 = (int) 2246822519L;
	private static final int PRIME3 = (int) 3266489917L;
	private static final int PRIME4 = 668265263;
	private static final int PRIME5 = 0x165667b1;
	private int seed;
	
	public XXHash(int seed) {
		this.seed = seed + PRIME5;
	}

	@Override
	public int hash(int k) {
		return 0;
	}

	@Override
	public int hash(int k1, int k2) {
		return 0;
	}
	

	@Override
	public int hash(int k1, int k2, int k3) {
		int h = seed;
		
		h += k1 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;
		
		h += k2 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;
		
		h += k3 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;

		h ^= h >>> 15;
		h *= PRIME2;
		h ^= h >>> 13;
		h *= PRIME3;
		h ^= h >>> 16;

		return h;
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4) {
		int h = seed;
		
		h += k1 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;
		
		h += k2 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;
		
		h += k3 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;
		
		h += k4 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;

		h ^= h >>> 15;
		h *= PRIME2;
		h ^= h >>> 13;
		h *= PRIME3;
		h ^= h >>> 16;

		return h;
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4, int k5) {
		int h = seed;
		
		h += k1 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;
		
		h += k2 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;
		
		h += k3 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;
		
		h += k4 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;

		h ^= h >>> 15;
		h *= PRIME2;
		h ^= h >>> 13;
		h *= PRIME3;
		h ^= h >>> 16;

		return h;
	}

	@Override
	public int hash(int... keys) {
		// TODO Auto-generated method stub
		return 0;
	}

}
