import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.applet.*;
import java.util.*;
import java.lang.reflect.*;

public class OptionScene extends Scene implements ItemListener, ActionListener, FocusListener
{
	boolean first = true, last = false;

	ScrollPane scroll = new ScrollPane();
	Panel box = new Panel();
	Choice choice = new Choice();
	TextArea backup;
	Button button = new Button("開始");

	class Property
	{
		public String name;
		public Object target;
		public String field;
		public int index;
		public Component input;

		public Property(String name, Object target, String field, int index)
		{
			this.name = name;
			this.target = target;
			this.field = field;
			this.index = index;
			set();
		}

		private void set()
		{
			try {
				Class c = target.getClass();
				if (c.isArray()) {
					c = c.getComponentType();
					if (c==Boolean.TYPE) {
						input = new Checkbox("", Array.getBoolean(target, index));
					}else if (c==Character.TYPE) {
						input = new TextField(Character.toString(Array.getChar(target, index)));
					}else if (c==Byte.TYPE) {
						input = new TextField(Byte.toString(Array.getByte(target, index)));
					}else if (c==Short.TYPE) {
						input = new TextField(Short.toString(Array.getShort(target, index)));
					}else if (c==Integer.TYPE) {
						input = new TextField(Integer.toString(Array.getInt(target, index)));
					}else if (c==Long.TYPE) {
						input = new TextField(Long.toString(Array.getLong(target, index)));
					}else if (c==Float.TYPE) {
						input = new TextField(Float.toString(Array.getFloat(target, index)));
					}else if (c==Double.TYPE) {
						input = new TextField(Double.toString(Array.getDouble(target, index)));
					}else {
						input = new TextField((String)Array.get(target, index));
					}
				}else {
					Field f = c.getField(field);
					c = f.getType();
					if (c==Boolean.TYPE) {
						input = new Checkbox("", f.getBoolean(target));
					}else if (c==Character.TYPE) {
						input = new TextField(Character.toString(f.getChar(target)));
					}else if (c==Byte.TYPE) {
						input = new TextField(Byte.toString(f.getByte(target)));
					}else if (c==Short.TYPE) {
						input = new TextField(Short.toString(f.getShort(target)));
					}else if (c==Integer.TYPE) {
						input = new TextField(Integer.toString(f.getInt(target)));
					}else if (c==Long.TYPE) {
						input = new TextField(Long.toString(f.getLong(target)));
					}else if (c==Float.TYPE) {
						input = new TextField(Float.toString(f.getFloat(target)));
					}else if (c==Double.TYPE) {
						input = new TextField(Double.toString(f.getDouble(target)));
					}else {
						input = new TextField((String)f.get(target));
					}
				}
			} catch(Exception e) {
				throw new InternalError(e.getMessage());
			}
		}
		public void get() throws Exception
		{
			try {
				Class c = target.getClass();
				if (c.isArray()) {
					c = c.getComponentType();
					if (c==Boolean.TYPE) {
						Array.setBoolean(target, index, ((Checkbox)input).getState());
					}else if (c==Character.TYPE) {
						Array.setChar(target, index, ((TextField)input).getText().charAt(0));
					}else if (c==Byte.TYPE) {
						Array.setByte(target, index, Byte.parseByte(((TextField)input).getText()));
					}else if (c==Short.TYPE) {
						Array.setShort(target, index, Short.parseShort(((TextField)input).getText()));
					}else if (c==Integer.TYPE) {
						Array.setInt(target, index, Integer.parseInt(((TextField)input).getText()));
					}else if (c==Long.TYPE) {
						Array.setLong(target, index, Long.parseLong(((TextField)input).getText()));
					}else if (c==Float.TYPE) {
						Array.setFloat(target, index, Float.parseFloat(((TextField)input).getText()));
					}else if (c==Double.TYPE) {
						Array.setDouble(target, index, Double.parseDouble(((TextField)input).getText()));
					}else {
						Array.set(target, index, ((TextField)input).getText());
					}
				}else {
					Field f = c.getField(field);
					c = f.getType();
					if (c==Boolean.TYPE) {
						f.setBoolean(target, ((Checkbox)input).getState());
					}else if (c==Character.TYPE) {
						f.setChar(target, ((TextField)input).getText().charAt(0));
					}else if (c==Byte.TYPE) {
						f.setByte(target, Byte.parseByte(((TextField)input).getText()));
					}else if (c==Short.TYPE) {
						f.setShort(target, Short.parseShort(((TextField)input).getText()));
					}else if (c==Integer.TYPE) {
						f.setInt(target, Integer.parseInt(((TextField)input).getText()));
					}else if (c==Long.TYPE) {
						f.setLong(target, Long.parseLong(((TextField)input).getText()));
					}else if (c==Float.TYPE) {
						f.setFloat(target, Float.parseFloat(((TextField)input).getText()));
					}else if (c==Double.TYPE) {
						f.setDouble(target, Double.parseDouble(((TextField)input).getText()));
					}else {
						f.set(target, ((TextField)input).getText());
					}
				}
			} catch(Exception e) {
				throw new Exception(name+"の値が読み取れません");
			}
		}
	}
	LinkedList properties = new LinkedList();

	public Scene doWork()
	{
		if (first) init();
		synchronized (this) { return last?next():this; }
	}

	void init()
	{
		int i;
		this.applet = applet;
		Planet planet = applet.planet;
		Rule rule = applet.rule;
		Option option = applet.option;
		applet.beginPaint();
		box.setLayout(new javax.swing.BoxLayout(box, javax.swing.BoxLayout.Y_AXIS));
		Panel p;
		if (applet.option.editplanet) {
			p = createTab("全般設定");
			newItem("惑星列幅", p, planet, "width");
			newItem("惑星高さ", p, planet, "height");
			newItem("初期HP", p, planet, "maxHP");
			newItem("初期メテオ配置速度", p, planet, "initialVelocity");
			p = createTab("落下メテオ");
			newItem("メテオ落下頻度", p, planet, "frequency");
			newItem("メテオ降下初速度", p, planet, "fallVelocity");
			newItem("メテオ降下加速度", p, planet, "fallGravity");
			newItem("メテオ降下速度リミッター", p, planet, "meteoFallLimit");
			newItem("落下倍率(５分経過)", p, planet, "timeRate");
			newItem("落下倍率(アクセル)", p, planet, "accelRate");
			newItem("押し下げ量", p, planet, "pushDown");
			newItem("押し下げ速度", p, planet, "pushDownVelocity");
			p = createTab("カタマリ");
			newItem("上昇速度リミッター", p, planet, "boostLimit");
			newItem("落下速度リミッター", p, planet, "stackFallLimit");
			newItem("落下速度リミッター(アクセル)", p, planet, "stackFallLimitT");
			newItem("重力加速度", p, planet, "stackGravity");
			newItem("還元時間(開始時)", p, planet, "reduceTime0");
			newItem("還元時間(５分後)", p, planet, "reduceTime1");
			newItem("メテオの重さ", p, planet, "meteoWeight");
			newItem("燃えカスの重さ", p, planet, "ashWeight");
			newItem("同時点火ボーナス", p, planet, "sameTimeIgnition");
			newItem("最下方点火ボーナス", p, planet, "lowestIgnition");
			p = createTab("シュート");
			newItem("シュート初速度", p, planet.shoot, "initialVelocity");
			newItem("シュート継続推進力", p, planet.shoot, "boostForce");
			newItem("シュート上昇時間", p, planet.shoot, "boostDuration");
			newItem("シュート落下速度リミッター", p, planet, "shootFallLimit");
			newItem("シュート落下加速度", p, planet, "shootGravity");
			p = createTab("メテオ降下割合");
			for (i=0; i<Meteo.colorName.length; i++) {
				newItem(Meteo.colorName[i], p, planet.rate, i);
			}
			p = createTab("打ちあげパラメータ");
			p.setLayout(new GridLayout(0, 7));
			p.add(new Panel());
			p.add(new Panel());
			p.add(new Label("横点火", Label.CENTER));
			p.add(new Panel());
			p.add(new Panel());
			p.add(new Label("縦点火", Label.CENTER));
			p.add(new Panel());
			p.add(new Panel());
			p.add(new Label("初速度", Label.CENTER));
			p.add(new Label("継続推進力", Label.CENTER));
			p.add(new Label("上昇時間", Label.CENTER));
			p.add(new Label("初速度", Label.CENTER));
			p.add(new Label("継続推進力", Label.CENTER));
			p.add(new Label("上昇時間", Label.CENTER));
			for (i=0; i<10; i++) {
				String s = Integer.toString(i+1);
				p.add(new Label(s+"次点火", Label.RIGHT));
				newItem(s+"次横点火初速度", p, planet.horizontal[i], "initialVelocity", false);
				newItem(s+"次横点火継続推進力", p, planet.horizontal[i], "boostForce", false);
				newItem(s+"次横点火上昇時間", p, planet.horizontal[i], "boostDuration", false);
				newItem(s+"次縦点火初速度", p, planet.vertical[i], "initialVelocity", false);
				newItem(s+"次縦点火継続推進力", p, planet.vertical[i], "boostForce", false);
				newItem(s+"次縦点火上昇時間", p, planet.vertical[i], "boostDuration", false);
			}
			p = createTab("色");
			p.setLayout(new GridLayout(0, 5));
			p.add(new Panel());
			p.add(new Label("赤成分(R)", Label.CENTER));
			p.add(new Label("緑成分(G)", Label.CENTER));
			p.add(new Label("青成分(B)", Label.CENTER));
			p.add(new Label("アルファ(A)", Label.CENTER));
			p.add(new Label("カーソル", Label.RIGHT));
			newItem("カーソル色の赤成分", p, planet.activeColor, "r", false);
			newItem("カーソル色の緑成分", p, planet.activeColor, "g", false);
			newItem("カーソル色の青成分", p, planet.activeColor, "b", false);
			newItem("カーソル色のアルファ", p, planet.activeColor, "a", false);
			p.add(new Label("移動可能範囲", Label.RIGHT));
			newItem("移動可能範囲色の赤成分", p, planet.movableColor, "r", false);
			newItem("移動可能範囲色の緑成分", p, planet.movableColor, "g", false);
			newItem("移動可能範囲色の青成分", p, planet.movableColor, "b", false);
			newItem("移動可能範囲色のアルファ", p, planet.movableColor, "a", false);
			p.add(new Label("文字", Label.RIGHT));
			newItem("文字色の赤成分", p, planet.textColor, "r", false);
			newItem("文字色の緑成分", p, planet.textColor, "g", false);
			newItem("文字色の青成分", p, planet.textColor, "b", false);
			newItem("文字色のアルファ", p, planet.textColor, "a", false);
			p.add(new Label("文字の背景", Label.RIGHT));
			newItem("背景色の赤成分", p, planet.backColor, "r", false);
			newItem("背景色の緑成分", p, planet.backColor, "g", false);
			newItem("背景色の青成分", p, planet.backColor, "b", false);
			newItem("背景色のアルファ", p, planet.backColor, "a", false);
			p.add(new Label("枠線", Label.RIGHT));
			newItem("枠線色の赤成分", p, planet.gridColor, "r", false);
			newItem("枠線色の緑成分", p, planet.gridColor, "g", false);
			newItem("枠線色の青成分", p, planet.gridColor, "b", false);
			newItem("枠線色のアルファ", p, planet.gridColor, "a", false);
			p.add(new Label("大気圏", Label.RIGHT));
			newItem("大気圏色の赤成分", p, planet.limitColor, "r", false);
			newItem("大気圏色の緑成分", p, planet.limitColor, "g", false);
			newItem("大気圏色の青成分", p, planet.limitColor, "b", false);
			newItem("大気圏色のアルファ", p, planet.limitColor, "a", false);
			p.add(new Label("メテオ", Label.RIGHT));
			newItem("メテオ色の赤成分", p, planet.meteoColor, "r", false);
			newItem("メテオ色の緑成分", p, planet.meteoColor, "g", false);
			newItem("メテオ色の青成分", p, planet.meteoColor, "b", false);
			newItem("メテオ色のアルファ", p, planet.meteoColor, "a", false);
			p.add(new Label("HP(最大)", Label.RIGHT));
			newItem("HP(最大)色の赤成分", p, planet.hpmaxColor, "r", false);
			newItem("HP(最大)色の緑成分", p, planet.hpmaxColor, "g", false);
			newItem("HP(最大)色の青成分", p, planet.hpmaxColor, "b", false);
			newItem("HP(最大)色のアルファ", p, planet.hpmaxColor, "a", false);
			p.add(new Label("HP(最小)", Label.RIGHT));
			newItem("HP(最小)色の赤成分", p, planet.hpminColor, "r", false);
			newItem("HP(最小)色の緑成分", p, planet.hpminColor, "g", false);
			newItem("HP(最小)色の青成分", p, planet.hpminColor, "b", false);
			newItem("HP(最小)色のアルファ", p, planet.hpminColor, "a", false);
			p.add(new Label("HP枠", Label.RIGHT));
			newItem("HP枠色の赤成分", p, planet.frameColor, "r", false);
			newItem("HP枠色の緑成分", p, planet.frameColor, "g", false);
			newItem("HP枠色の青成分", p, planet.frameColor, "b", false);
			newItem("HP枠色のアルファ", p, planet.frameColor, "a", false);
			p = createTab("画像ファイル");
			newItem("メテオ", p, planet, "meteo");
			newItem("背景", p, planet, "back");
			p = createTab("BGM＆点火音");
			p.setLayout(new GridLayout(0, 4));
			p.add(new Panel());
			p.add(new Label("メテオちょっと", Label.CENTER));
			p.add(new Label("メテオそこそこ", Label.CENTER));
			p.add(new Label("メテオいっぱい", Label.CENTER));
			p.add(new Label("BGM", Label.RIGHT));
			newItem("メテオちょっとのBGM", p, planet.bgm, 0, false);
			newItem("メテオそこそこのBGM", p, planet.bgm, 1, false);
			newItem("メテオいっぱいのBGM", p, planet.bgm, 2, false);
			p.add(new Label("縦点火ME", Label.RIGHT));
			newItem("メテオちょっとの縦点火ME", p, planet.fanfare[0], 0, false);
			newItem("メテオそこそこの縦点火ME", p, planet.fanfare[0], 1, false);
			newItem("メテオいっぱいの縦点火ME", p, planet.fanfare[0], 2, false);
			for (i=0; i<4; i++) {
				String s = Integer.toString(i+1);
				p.add(new Label(s+"次点火ME", Label.RIGHT));
				newItem("メテオちょっとの"+s+"次点火ME", p, planet.fanfare[i+1], 0, false);
				newItem("メテオそこそこの"+s+"次点火ME", p, planet.fanfare[i+1], 1, false);
				newItem("メテオいっぱいの"+s+"次点火ME", p, planet.fanfare[i+1], 2, false);
			}
			p = createTab("その他音声");
			newItem("生存ME", p, planet, "win");
			newItem("滅亡ME", p, planet, "lose");
			p = createTab("バックアップ");
			p.setLayout(new GridLayout(0, 1));
			p.add(backup=new TextArea(applet.getPlanetString(), 10, 10, TextArea.SCROLLBARS_VERTICAL_ONLY));
			backup.setEditable(false);
			backup.addFocusListener(this);
		}
		if (applet.option.changerule) {
			p = createTab("ルール");
			newItem("縦移動", p, rule, "vertical");
			newItem("横移動", p, rule, "horizontal");
			newItem("シュート", p, rule, "shoot");
//			newItem("スライド", p, rule, "slide");
			newItem("初期積み上げ高さ", p, rule, "initialHeight");
			p = createTab("生存条件");
			newItem("目標得点", p, rule, "score");
			newItem("目標時間(秒)", p, rule, "seconds");
			newItem("目標メテオ数(合計)", p, rule, "gotAllMeteo");
			for (i=0; i<Meteo.colorName.length; i++) {
				newItem("目標メテオ数("+Meteo.colorName[i]+")", p, rule.gotMeteo, i);
			}
			newItem("目標総燃えカス数", p, rule, "gotAsh");
			newItem("目標総全消し数", p, rule, "clearCount");
		}
		p = createTab("グラフィックオプション");
		newItem("半透明処理", p, option, "alpha");
		newItem("高品位な拡大縮小", p, option, "interpolation");
		newItem("高品位な拡大縮小(アルファ)", p, option, "alphaInterpolation");
		newItem("画像にアンチエイリアス", p, option, "antialiasing");
		newItem("テキストにアンチエイリアス", p, option, "textAntialiasing");
		newItem("質優先のレンダリング", p, option, "rendering");
		newItem("質優先のカラーレンダリング", p, option, "colorRendering");
		newItem("ディザリング", p, option, "dithering");
		newItem("フォント部分メトリックス", p, option, "fractionalmetrics");
		p = createTab("サウンドオプション");
		newItem("AU形式のサウンドを再生", p, option, "au");
		newItem("Wave形式のサウンドを再生", p, option, "wave");
		newItem("MIDI形式のサウンドを再生", p, option, "midi");
		p = createTab("メテオンもどきについて");
		TextArea ta;
		p.setLayout(new GridLayout(0, 1));
		p.add(ta=new TextArea(
			"メテオンもどきは美文(mifumi323@tgws.fromc.jp http://tgws.fromc.jp/)がメテオスシリーズのメテオスオンラインに似せて作っているJavaゲームです。\nメテオスおよびメテオスオンラインの著作権はQエンターテインメント(http://www.qentertainment.com/)にあります。",
			5, 10, TextArea.SCROLLBARS_VERTICAL_ONLY));
		ta.setEditable(false);
		applet.setLayout(new BorderLayout());
		applet.add(choice, BorderLayout.NORTH);
		applet.add(button, BorderLayout.SOUTH);
		applet.add(scroll, BorderLayout.CENTER);
		choice.select(0);
		applet.validate();
		applet.endPaint();
		choice.addItemListener(this);
		button.addActionListener(this);
		scroll.add(box);
		scroll.validate();
		first = false;
	}

	Scene next()
	{
		applet.removeAll();
		applet.loadResource();
		return new GameScene();
	}

	Panel createTab(String name)
	{
		Panel p = new Panel(new GridLayout(0, 2));
		box.add(new Label(name, Label.CENTER));
		box.add(p);
		choice.add(name);
		return p;
	}

	// にゅ～あいてむ
	void newItem(String name, Container container, Object target, String field, int index, boolean label)
	{
		Property prop = new Property(name, target, field, index);
		if (label) container.add(new Label(name, Label.RIGHT));
		container.add(prop.input);
		properties.add(prop);
	}
	void newItem(String name, Container container, Object target, String field)
	{ newItem(name, container, target, field, 0, true); }
	void newItem(String name, Container container, Object target, int index)
	{ newItem(name, container, target, null, index, true); }
	void newItem(String name, Container container, Object target, String field, boolean label)
	{ newItem(name, container, target, field, 0, label); }
	void newItem(String name, Container container, Object target, int index, boolean label)
	{ newItem(name, container, target, null, index, label); }

	public void itemStateChanged(ItemEvent e)
	{
		scroll.setScrollPosition(0, box.getComponent(choice.getSelectedIndex()*2).getY());
	}

	public void focusGained(FocusEvent e)
	{
		updatePlanet();
		backup.setText(applet.getPlanetString());
		backup.selectAll();
	}
	public void focusLost(FocusEvent e) {}

	// 入力フォームゲット
	public void actionPerformed(ActionEvent e)
	{
		synchronized (this) {
			last = true;
			updatePlanet();
		}
	}
	public void updatePlanet()
	{
		Planet planet = applet.planet;
		Rule rule = applet.rule;
		try {
			Iterator it = properties.iterator();
			while (it.hasNext()) {
				((Property)it.next()).get();
			}
			if (planet.width<1) error("幅は1以上必要です");
			if (planet.height<3) error("高さは3以上必要です");
			if (planet.maxHP<=0.0) error("初期HPは0.0より大きくなくてはいけません");
			if (planet.initialVelocity<=0.0) error("初期メテオ配置速度は0.0より大きくなくてはいけません");
			int i, j=0;
			for (i=0; i<Meteo.colorName.length; i++) {
				if (planet.rate[i]>0) j++;
			}
			if (j<5) error("メテオは5種類以上必要です");
			if (rule.initialHeight<0) error("初期積み上げ高さは0以上必要です");
			if (planet.height<rule.initialHeight) error("初期積み上げ高さは惑星の高さまでです");
		} catch (Exception ex) {
			error(ex.getMessage());
		}
	}
	void error(String s)
	{
		if (last) {
			button.setLabel("開始("+s+")");
			last = false;
		}
	}
}
