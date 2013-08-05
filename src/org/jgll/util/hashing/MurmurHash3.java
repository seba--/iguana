package org.jgll.util.hashing;

public class MurmurHash3 implements HashFunction {

	private static final long serialVersionUID = 1L;
	
	private final static int C1 = 0xcc9e2d51;
	private final static int C2 = 0x1b873593;
	private final static int M = 5;
	private final static int N = 0xe6546b64;

	private int seed;

	public MurmurHash3(int seed) {
		this.seed = seed;
	}

	public MurmurHash3() {
		this(0);
	}
	
	@Override
	public int hash(int...keys) {
		int h = seed;
		
		int k = 0;
		for(int i = 0; i < keys.length; i++) {
			k = keys[i];
			k = mixK(k);
			h = mixH(h, k);
		}
		
		h ^= keys.length;

		h ^= h >>> 16;
		h *= 0x85ebca6b;
		h ^= h >>> 13;
		h *= 0xc2b2ae35;
		h ^= h >>> 16;
		
		return h;
	}
	
	@Override
	public int hash(int a) {
		int h = seed;

		int k = a;
		k = mixK(k);
		h = mixH(h, k);

		// finalizing
		h ^= 1;

		h ^= h >>> 16;
		h *= 0x85ebca6b;
		h ^= h >>> 13;
		h *= 0xc2b2ae35;
		h ^= h >>> 16;

		return h;
	}

	@Override
	public int hash(int a, int b) {
		int h = seed;

		int k = a;
		k = mixK(k);
		h = mixH(h, k);

		k = b;
		k = mixK(k);
		h = mixH(h, k);

		// finalizing
		h ^= 2;

		h ^= h >>> 16;
		h *= 0x85ebca6b;
		h ^= h >>> 13;
		h *= 0xc2b2ae35;
		h ^= h >>> 16;
		
		return h;
	}

	@Override
	public int hash(int a, int b, int c) {
		int h = seed;

		int k = a;
		k = mixK(k);
		h = mixH(h, k);

		k = b;
		k = mixK(k);
		h = mixH(h, k);

		k = c;
		k = mixK(k);
		h = mixH(h, k);

		// finalizing
		h ^= 3;

		h ^= h >>> 16;
		h *= 0x85ebca6b;
		h ^= h >>> 13;
		h *= 0xc2b2ae35;
		h ^= h >>> 16;
		
		return h;
	}
	
	public int hash(int a, int b, int c, int d, int e) {

		int h = seed;

		int k = a;
		k = mixK(k);
		h = mixH(h, k);

		k = b;
		k = mixK(k);
		h = mixH(h, k);

		k = c;
		k = mixK(k);
		h = mixH(h, k);

		k = d;
		k = mixK(k);
		h = mixH(h, k);
		
		k = e;
		k = mixK(k);
		h = mixH(h, k);

		// finalizing
		h ^= 5;

		h ^= h >>> 16;
		h *= 0x85ebca6b;
		h ^= h >>> 13;
		h *= 0xc2b2ae35;
		h ^= h >>> 16;

		return h;
	}


	public int hash(int a, int b, int c, int d) {

		int h = seed;

		int k = a;
		k = mixK(k);
		h = mixH(h, k);

		k = b;
		k = mixK(k);
		h = mixH(h, k);

		k = c;
		k = mixK(k);
		h = mixH(h, k);

		k = d;
		k = mixK(k);
		h = mixH(h, k);

		// finalizing
		h ^= 4;

		h ^= h >>> 16;
		h *= 0x85ebca6b;
		h ^= h >>> 13;
		h *= 0xc2b2ae35;
		h ^= h >>> 16;

		return h;
	}

	private final static int mixK(int k) {
		k *= C1;
		k = Integer.rotateLeft(k, 15);
		k = k * C2;
		return k;
	}

	private final static int mixH(int h, int k) {
		h ^= k;
		h = Integer.rotateLeft(h, 13);
		h = h * M + N;
		return h;
	}
}
