public class Planet
{
	// 全般設定
	public int width=9;
	public int height=12;
	public double maxHP = 10.0;
	public double initialVelocity=20.0;

	// 落下メテオ
	public double frequency = 1.0;
	public double fallVelocity = 1.0;
	public double fallGravity = 10.0;
	public double meteoFallLimit = 10.0;
	public double timeRate = 10.0;
	public double accelRate = 4.0;
	public double pushDown = 0.1;
	public double pushDownVelocity=5.0;

	// カタマリ
	public double boostLimit = 12.0;
	public double stackFallLimit = 1.0;
	public double stackFallLimitT = 3.0;
	public double reduceTime0 = 3.0;
	public double reduceTime1 = 1.0;
	public double stackGravity = 10.0;
	public double meteoWeight = 0.6;
	public double ashWeight = 0.2;
	public double sameTimeIgnition = 1.0;
	public double lowestIgnition = 1.1;

	// シュート
	public Ignition shoot = new Ignition(10.0, 10.0, 1.0);
	public double shootFallLimit = 10.0;
	public double shootGravity = 10.0;

	// メテオ降下割合
	public int rate[] = new int[] { 100, 100, 100, 100, 100, 100, 100, 100, 10, 10, 10, 1, };

	// 打ちあげパラメータ
	public Ignition horizontal[] = new Ignition[]
	{
		new Ignition(10.0, 10.0, 1.0),
		new Ignition(10.0, 14.1, 1.0),
		new Ignition(10.0, 20.0, 1.0),
		new Ignition(10.0, 28.3, 1.0),
		new Ignition(10.0, 40.0, 1.0),
		new Ignition(10.0, 56.6, 1.0),
		new Ignition(10.0, 80.0, 1.0),
		new Ignition(10.0, 113.1, 1.0),
		new Ignition(10.0, 160.0, 1.0),
		new Ignition(10.0, 226.3, 1.0),
	};
	public Ignition vertical[] = new Ignition[]
	{
		new Ignition(10.0, 20.0, 1.0),
		new Ignition(10.0, 28.3, 1.0),
		new Ignition(10.0, 40.0, 1.0),
		new Ignition(10.0, 56.6, 1.0),
		new Ignition(10.0, 80.0, 1.0),
		new Ignition(10.0, 113.1, 1.0),
		new Ignition(10.0, 160.0, 1.0),
		new Ignition(10.0, 226.3, 1.0),
		new Ignition(10.0, 320.0, 1.0),
		new Ignition(10.0, 452.5, 1.0),
	};

	// 色
	public ColorValue backColor = new ColorValue(0x00, 0x00, 0x00, 0xff);
	public ColorValue activeColor = new ColorValue(0xff, 0x00, 0x00, 0xff);
	public ColorValue movableColor = new ColorValue(0xc0, 0xc0, 0x00, 0xff);
	public ColorValue textColor = new ColorValue(0xff, 0xff, 0xff, 0xff);
	public ColorValue gridColor = new ColorValue(0x40, 0xdf, 0x40, 0xc0);
	public ColorValue limitColor = new ColorValue(0xdf, 0x00, 0x00, 0xc0);
	public ColorValue meteoColor = new ColorValue(0x00, 0x00, 0x00, 0x00);
	public ColorValue hpmaxColor = new ColorValue(0xff, 0xff, 0x00, 0xff);
	public ColorValue hpminColor = new ColorValue(0xff, 0x00, 0x00, 0xff);
	public ColorValue frameColor = new ColorValue(0xff, 0xff, 0xff, 0xff);

	// 画像ファイル
	public String meteo="default/meteo.gif";
	public String back="default/planet.gif";

	// 音楽
	public String bgm[] = new String[3];
	public String fanfare[][] = new String[5][3];
	public String win, lose;
}
