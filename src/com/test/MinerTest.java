package com.test;

public class MinerTest {

	public static void main(String[] args) {
		String str = "Hello World!";
		long i = 0;
		String sha256;
		long start = System.currentTimeMillis();
		long end;
		while(true) {
			sha256 = SecurityUtils.getSHA256(str + i);
			if(sha256.startsWith("000000")) {
				System.out.println(sha256);
				System.out.println(i);
				end = System.currentTimeMillis();
				System.out.println("Spend " + (end - start) + "ms");
				break;
			}
			i++;
		}
	}

}
