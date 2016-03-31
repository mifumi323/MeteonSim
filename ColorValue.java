import java.awt.*;

class ColorValue
{
	public int r, g, b, a;

	public ColorValue(int r, int g, int b, int a)
	{
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public Color toColor(boolean hasAlpha)
	{
//		if (a==0) return null;
		return new Color(r, g, b, hasAlpha?a:(a<128?0:255));
	}
}
