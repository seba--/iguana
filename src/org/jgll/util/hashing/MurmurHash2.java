package org.jgll.util.hashing;

public class MurmurHash2 implements HashFunction{

	@Override
	public int hash(int a, int b, int c, int d) {
		final int m = 0x5bd1e995;
		final int r = 24;
		int h = a ^ 4;

		// b
		int k = b;
		k *= m;
		k ^= k >>> r;
		k *= m;

		h *= m;
		h ^= k;
		
		// c
		k = c;
		k *= m;
		k ^= k >>> r;
		k *= m;

		h *= m;
		h ^= k;
		
		// d
		k = d;
		k *= m;
		k ^= k >>> r;
		k *= m;

		h *= m;
		h ^= k;

		// last mix
		h *= m;
		h ^= h >>> 13;
		h *= m;
		h ^= h >>> 15;
		return h;
	}

	@Override
	public int hash(int a, int b, int c) {
		return hash(0, a, b, c);
	}

	@Override
	public int hash(int k) {
		return 0;
	}

	@Override
	public int hash(int k1, int k2) {
		return 0;
	}

}
