import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

public class MeteonSim extends Applet implements Runnable, MouseListener, MouseMotionListener, KeyListener
{
	boolean running;

	public Planet planet;
	public Rule rule;
	public Option option;
	public long diftime=0;
	Scene scene;

	// リソース
	public Image meteo, back;
	public AudioClip bgm[] = new AudioClip[3];
	public AudioClip fanfare[][] = new AudioClip[5][3];
	public AudioClip meWin, meLose;
	public AudioClip nullAudio = new AudioClip()
	{ public void play() {} public void loop() {} public void stop() {} };
	MediaTracker mediatracker;
	AudioPlayer audioPlayer = new AudioPlayer();
	Set playingSounds = Collections.synchronizedSet(new HashSet());

	BufferedImage bgImage = null;
	int bgWidth=0, bgHeight=0;

	// ～Pressedはビット0がリアルタイム情報、1が最終更新時、2が一つ前、3以降は使用しない
	int iKeyCode[] = new int[16];
	int iKeyPressed[] = new int[16];
	int iMousePressed[] = new int[3];
	int iMouseX=0, iMouseY=0;

	// Applet Method
	public void init() {}
	public void destroy() {}
	public void start()
	{
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		parseParam();
		running = true;
		loadResource();
		new Thread(audioPlayer).start();
		new Thread(this).start();
	}
	public void stop()
	{
		stopAllAudio();
		audioPlayer.isValid = false;
		running = false;
	}
	public void update(Graphics g)
	{ paint(g); }
	public void paint(Graphics g)
	{
		synchronized (this) {
			if (bgImage!=null) g.drawImage(bgImage,0,0,null);
		}
	}
	public AudioClip getAudioClip(String name)
	{
		if (name==null||name.equals("")) return nullAudio;
		if (!option.au&&name.endsWith(".au")) return nullAudio;
		if (!option.wave&&name.endsWith(".wav")) return nullAudio;
		if (!option.midi&&name.endsWith(".mid")) return nullAudio;
		return getAudioClip(getDocumentBase(), name);
	}

	// Runnable Method
	public void run()
	{
		long time;
		scene = new FirstScene();
		while (running)
		{
			time = System.currentTimeMillis();
			updateInput();
			scene.applet = this;
			scene = scene.doWork();
			try { Thread.sleep(1); } catch (Exception e) {}
			diftime = System.currentTimeMillis()-time;
		}
	}

	// MouseListener Method
	public void mouseClicked(MouseEvent e) { scene.mouseClicked(e); }
	public void mouseEntered(MouseEvent e) { scene.mouseEntered(e); }
	public void mouseExited(MouseEvent e) { scene.mouseExited(e); }
	public void mousePressed(MouseEvent e)
	{
		synchronized (this) {
			switch (e.getButton()) {
			case MouseEvent.BUTTON1: iMousePressed[0] |= 1; break;
			case MouseEvent.BUTTON2: iMousePressed[1] |= 1; break;
			case MouseEvent.BUTTON3: iMousePressed[2] |= 1; break;
			}
		}
		scene.mousePressed(e);
	}
	public void mouseReleased(MouseEvent e)
	{
		synchronized (this) {
			switch (e.getButton()) {
			case MouseEvent.BUTTON1: iMousePressed[0] &= ~1; break;
			case MouseEvent.BUTTON2: iMousePressed[1] &= ~1; break;
			case MouseEvent.BUTTON3: iMousePressed[2] &= ~1; break;
			}
		}
		scene.mouseReleased(e);
	}

	// MouseMotionListener Method
	public void mouseDragged(MouseEvent e)
	{ synchronized (this) { iMouseX=e.getX(); iMouseY=e.getY(); } }
	public void mouseMoved(MouseEvent e)
	{ synchronized (this) { iMouseX=e.getX(); iMouseY=e.getY(); } }

	// KeyListener Method
	public void keyPressed(KeyEvent e)
	{
		scene.keyPressed(e);
		synchronized (this) {
			int n = -1, k = e.getKeyCode();
			for (int i=0; i<16; i++) {
				if (iKeyCode[i]==k) {
					iKeyPressed[i] |= 1;
					return;
				}else if (iKeyPressed[i]==0) {
					if (n==-1||iKeyCode[i]==0) n = i;
				}
			}
			iKeyCode[n]=k;
			iKeyPressed[n] |= 1;
		}
	}
	public void keyReleased(KeyEvent e)
	{
		scene.keyReleased(e);
		synchronized (this) {
			int k = e.getKeyCode();
			for (int i=0; i<16; i++) {
				if (iKeyCode[i]==k) {
					iKeyPressed[i] &= ~1;
					return;
				}
			}
		}
	}
	public void keyTyped(KeyEvent e) { scene.keyTyped(e); }

	// MeteonSim Method

	// リソース
	public void parseParam()
	{
		option = new Option();
		if (getParameter("option")!=null) {
			FieldSerializer.Unserialize(getParameter("option")
				, option);
		}
		planet = new Planet();
		if (getParameter("planet")!=null) {
			FieldSerializer.Unserialize(getParameter("planet")
				.replaceAll("gravity", "stackGravity")
				.replaceAll("frequency0", "frequency")
				.replaceAll("fallVelocity0", "fallVelocity")
				, planet);
		}
		rule = new Rule();
		if (getParameter("rule")!=null) {
			FieldSerializer.Unserialize(getParameter("rule")
				, rule);
		}
	}
	public String getPlanetString()
	{
		scene.updatePlanet();
		return FieldSerializer.Serialize(planet);
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
			for (int i=0; i<3; i++) {
				bgm[i] = getAudioClip(planet.bgm[i]);
				for (int j=0; j<5; j++) {
					fanfare[j][i] = getAudioClip(planet.fanfare[j][i]);
				}
			}
			meWin = getAudioClip(planet.win);
			meLose = getAudioClip(planet.lose);
		} }.start();
	}

	// 描画
	public Graphics2D beginPaint()
	{
		if (bgImage==null || bgWidth!=getWidth() || bgHeight!=getHeight()) {
			bgImage = new BufferedImage(bgWidth=getWidth(), bgHeight=getHeight(), BufferedImage.TYPE_INT_RGB);
		}
		Graphics2D g = bgImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, option.antialiasing?
			RenderingHints.VALUE_ANTIALIAS_ON:RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, option.rendering?
			RenderingHints.VALUE_RENDER_QUALITY:RenderingHints.VALUE_RENDER_SPEED);
		g.setRenderingHint(RenderingHints.KEY_DITHERING, option.dithering?
			RenderingHints.VALUE_DITHER_ENABLE:RenderingHints.VALUE_DITHER_DISABLE);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, option.textAntialiasing?
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON:RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, option.fractionalmetrics?
			RenderingHints.VALUE_FRACTIONALMETRICS_ON:RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, option.interpolation?
			RenderingHints.VALUE_INTERPOLATION_BILINEAR:RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, option.alphaInterpolation?
			RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY:RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, option.colorRendering?
			RenderingHints.VALUE_COLOR_RENDER_QUALITY:RenderingHints.VALUE_COLOR_RENDER_SPEED);
		g.setBackground(new Color(0x242420));
		g.clearRect(0, 0, bgWidth, bgHeight);
		return g;
	}
	public void endPaint()
	{
		Graphics g=getGraphics();
		if (bgImage!=null&&g!=null) g.drawImage(bgImage,0,0,null);
	}

	// サウンド
	public void playAudio(final AudioClip clip)
	{
		if (clip==null||clip==nullAudio) return;
		playingSounds.add(clip);
		audioPlayer.play(clip);
	}
	public void loopAudio(final AudioClip clip)
	{
		if (clip==null||clip==nullAudio) return;
		playingSounds.add(clip);
		audioPlayer.loop(clip);
	}
	public void stopAudio(final AudioClip clip)
	{
		if (clip==null||clip==nullAudio) return;
		playingSounds.remove(clip);
		audioPlayer.stop(clip);
	}
	public void stopAllAudio()
	{
		audioPlayer.stopAll();
	}

	// 入力処理
	void updateInput()
	{
		synchronized (this) {
			for (int i=0; i<16; i++) iKeyPressed[i]=updateInput(iKeyPressed[i]);
			for (int i=0; i<3; i++) iMousePressed[i]=updateInput(iMousePressed[i]);
		}
	}
	int updateInput(int i)
	{ return ((i<<1)|(i&1))&7; }
	public int getMouseX()
	{ return iMouseX; }
	public int getMouseY()
	{ return iMouseY; }
	public boolean getMousePressed(int k)
	{ return (iMousePressed[k]&2)!=0; }
	public boolean getMousePushed(int k)
	{ return (iMousePressed[k]&6)==2; }
	public boolean getKeyPressed(int k)
	{
		for (int i=0; i<16; i++) if (iKeyCode[i]==k) return (iKeyPressed[i]&2)!=0;
		return false;
	}
	public boolean getKeyPushed(int k)
	{
		for (int i=0; i<16; i++) if (iKeyCode[i]==k) return (iKeyPressed[i]&6)==2;
		return false;
	}
}
