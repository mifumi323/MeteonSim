public class Rule
{
	public boolean vertical = true;
	public boolean horizontal = true;
	public boolean shoot = true;
	public boolean slide = false;
	public int initialHeight = 3;

	public int score=0;
	public double seconds = 0.0;
	public int gotMeteo[] = new int[Meteo.colorName.length];
	public int gotAllMeteo=0;
	public int gotAsh=0;
	public int clearCount=0;
}
