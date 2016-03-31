import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

public class MeteonSee extends MeteonSim
{
	Graphics2D nullGraphics = new NullGraphics();

	// Applet Method
	public void start()
	{
		parseParam();
		running = true;
		loadResource();
		bgImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bgImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g.setBackground(new Color(0x242420));
		g.clearRect(0, 0, bgWidth, bgHeight);
		g.setColor(new Color(0xfbfbfb));
		g.setFont(g.getFont().deriveFont(Font.PLAIN, 100.0f));
		g.drawString("画像生成中", 70, 240);
		g.setFont(g.getFont().deriveFont(Font.PLAIN, 25.0f));
		g.drawString("しばらく待っても表示されない場合", 120, 270);
		g.drawString("リロードすると直る場合があります", 120, 300);
		new Thread(this).start();
	}
	public void stop()
	{ running = false; }
	public AudioClip getAudioClip(String name)
	{ return nullAudio; }

	// Runnable Method
	public void run()
	{
		KeyEvent[] keys = {
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_W, 'W'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_W, 'W'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_W, 'W'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_W, 'W'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_W, 'W'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_W, 'W'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_W, 'W'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_W, 'W'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_W, 'W'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_W, 'W'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_W, 'W'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_W, 'W'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_W, 'W'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_W, 'W'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_W, 'W'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_W, 'W'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_S, 'S'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_S, 'S'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_S, 'S'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_A, 'A'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_A, 'A'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_A, 'A'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_A, 'A'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_A, 'A'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_A, 'A'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_D, 'D'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_D, 'D'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_D, 'D'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_D, 'D'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_D, 'D'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_D, 'D'),
			new KeyEvent(this, 0, 0, 0, KeyEvent.VK_T, 'T'),
			};
		KeyEvent key;
		MouseEvent mouse = new MouseEvent(this, 0, 0, 0, 0, 0, 0, false);
		Random random = new Random();
		(scene = new GameScene()).applet = this;
		diftime = 10;
		int i=(int)(3000+rule.seconds*100);
		if (random.nextBoolean()) i = random.nextInt(i);
		int ox = getWidth()*17/64, oy = getHeight()*5/6-1;
		int rx = getWidth()*17/32, ry = getHeight()*3/4;
		while (running)
		{
			if (--i<=0) running = false;
			mouse.translatePoint(
				ox+random.nextInt(rx)-mouse.getX(),
				oy-sq(random.nextInt(ry))/ry-mouse.getY());
			mouseMoved(mouse);
			keyPressed(key = keys[random.nextInt(keys.length)]);
			updateInput();
			keyReleased(key);
			scene.doWork();
		}
	}
	int sq(int x) { return x*x; }

	// MeteonSim Method

	// リソース
	public void parseParam()
	{
		option = new Option();
		planet = new Planet();
		if (getParameter("planet")!=null) {
			FieldSerializer.Unserialize(getParameter("planet")
				.replaceAll("gravity", "stackGravity")
				.replaceAll("frequency0", "frequency")
				.replaceAll("fallVelocity0", "fallVelocity")
				, planet);
		}
		rule = new Rule();
		rule.seconds = 120.0;
	}
	public void loadResource()
	{
		mediatracker = new MediaTracker(this);
		mediatracker.addImage(
			meteo=getImage(getDocumentBase(), planet.meteo),
			0);
		mediatracker.addImage(
			back=getImage(getDocumentBase(), planet.back),
			1);
		new Thread() { public void run() {
			boolean reload=false;
			try {
				mediatracker.waitForAll();
			} catch (Exception e) {}
			if (mediatracker.isErrorID(0)) {
				mediatracker.removeImage(meteo);
				mediatracker.addImage(
					meteo=getImage(getDocumentBase(), (new Planet()).meteo),
					0);
				reload = true;
			}
			if (mediatracker.isErrorID(1)) {
				mediatracker.removeImage(back);
				mediatracker.addImage(
					back=getImage(getDocumentBase(), (new Planet()).back),
					1);
				reload = true;
			}
			if (reload) {
				try {
					mediatracker.waitForAll();
				} catch (Exception e) {}
			}
		} }.start();
	}

	// 描画
	public Graphics2D beginPaint()
	{
		if (running) return nullGraphics;
		if (bgImage==null || bgWidth!=getWidth() || bgHeight!=getHeight()) {
			bgImage = new BufferedImage(bgWidth=getWidth(), bgHeight=getHeight(), BufferedImage.TYPE_INT_RGB);
		}
		Graphics2D g = bgImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g.setBackground(new Color(0x242420));
		g.clearRect(0, 0, bgWidth, bgHeight);
		return g;
	}
	public void endPaint()
	{
		if (running) return;
		Graphics g=getGraphics();
		if (bgImage!=null&&g!=null) g.drawImage(bgImage,0,0,null);
	}

	// サウンド
	public void playAudio(final AudioClip clip) {}
	public void loopAudio(final AudioClip clip) {}
	public void stopAudio(final AudioClip clip) {}
	public void stopAllAudio() {}
}
