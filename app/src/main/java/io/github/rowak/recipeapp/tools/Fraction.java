package io.github.rowak.recipeapp.tools;

public class Fraction {
	private int num;
	private int denom;
	
	public Fraction(int num, int denom) {
		if (denom == 0) {
			throw new IllegalArgumentException(
					"Denominator cannot be zero");
		} else if (denom < 0) {
			num *= -1;
			denom *= -1;
		}
		this.num = num;
		this.denom = denom;
	}
	
	public static Fraction fromString(String str) {
		String[] data = str.split("/");
		if (data.length == 0 || data.length > 2) {
			return null;
		}
		try {
			int denom = data.length > 1 ? Integer.parseInt(data[1]) : 1;
			return new Fraction(Integer.parseInt(data[0]), denom);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	public void add(Fraction frac) {
		int lcd = lcd(denom, frac.denom);
		num = num * (lcd / denom) + frac.num * (lcd / frac.denom);
		denom = denom * (lcd / denom);
	}
	
	public void subtract(Fraction frac) {
		int lcd = lcd(denom, frac.denom);
		num = num * (lcd / denom) - frac.num * (lcd / frac.denom);
		denom = denom * (lcd / denom);
	}
	
	public void multiply(Fraction frac) {
		num *= frac.num;
		denom *= frac.denom;
	}
	
	public void divide(Fraction frac) {
		num *= frac.denom;
		denom *= frac.num;
	}
	
	public double toDecimal() {
		return num / (double)denom;
	}
	
	public String toMixedStr() {
		Fraction simple = simplify();
		int whole = (int)(simple.num / (float)simple.denom);
		int leftover = simple.num - (whole * simple.denom);
		StringBuilder sb = new StringBuilder();
		if (whole != 0) {
			sb.append(whole);
			if (leftover != 0) {
				sb.append(" ");
			}
		}
		if (leftover != 0) {
			sb.append(leftover + "/" + simple.denom);
		}
		else if (simple.num == 0) {
			sb.append(simple.denom);
		}
		return sb.toString();
	}
	
	@Override
	public String toString() {
		Fraction simple = simplify();
		return simple.num + "/" + simple.denom;
	}
	
	public Fraction simplify() {
		Fraction simple = new Fraction(num, denom);
		int gcd = gcd(simple.num, simple.denom);
		simple.num /= gcd;
		simple.denom /= gcd;
		return simple;
	}
	
	private int lcd(int a, int b) {
		return (a * b)/gcd(a, b);
	}
	
	private int gcd(int a, int b) {
		return a % b == 0 ? b : gcd(b, a % b);
	}
}
