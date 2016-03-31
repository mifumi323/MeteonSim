import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.applet.*;
import java.util.*;

public class GameScene extends Scene
{
	// ��{�X�e�[�^�X
	Planet planet = null;
	Rule rule = null;
	Meteo field[][] = null;
	Set stacks = new HashSet();

	// �Q�[���̏��
	int S_BEGIN=0, S_PLAY=1, S_WIN=2, S_LOSE=3;
	int status = S_BEGIN;

	// ���e�I�ړ�
	Meteo grabbed = null;
	int grabbedX=-1;
	static final int DRAG_NONE=0;
	static final int DRAG_VERTICAL=1;
	static final int DRAG_HORIZONTAL=2;
	int dragMode=DRAG_NONE;

	// ���Ԋ֌W
	static final long wait = 10;
	static final double dt = (double)wait/1000.0;
	long time = 0, delay = 0;
	double t=0;
	boolean timeAccel=false;
	boolean reverseAccel=false;
	boolean last = false;

	// ���e�I
	double fallMeteo = 0.0;
	int meteoCount=0, ashCount=0, blockCount=0;
	double hp;

	// ����
	long score=0;	// �]�̎����Ɂu�J���X�g�v�̕����͂Ȃ�:p
	int gotMeteo[] = new int[Meteo.colorName.length];
	int gotAllMeteo=0;
	int gotAsh=0;
	int clearCount=0;
	boolean clearFlag=true;
	int horizontal=0, vertical=0;
	int maxIgnition=0;

	// �ꎞ�ϐ�
	int awidth, aheight;
	int msize, basex, basey;

	// ��������
	Random random = new Random();

	// �F
	Color backColor;
	Color activeColor;
	Color movableColor;
	Color textColor;
	Color gridColor;
	Color limitColor;
	Color meteoColor;
	Color hpmaxColor;
	Color hpminColor;
	Color frameColor;

	// �T�E���h
	AudioClip bgm = null;
	int bgmNo = -1;
	long bgmChangeTime=-2000;
	AudioClip fanfare = null;
	int fanfareIgniteNo=0;
	LinkedList ignitedStacks = new LinkedList();

	// �J�n���̉��o
	long beginTime=0;

	// �I�����̉��o
	long endTime=0;
	int endHeight[];

	// �G�t�F�N�g
	private class FallMeteoEffect extends Effect
	{
	}
	private class FireEffect extends Effect
	{
	}

	// Scene Method
	public Scene doWork()
	{
		if (planet==null) init();
		awidth = applet.getWidth(); aheight = applet.getHeight();
		msize = Math.min(awidth*17/planet.width/32,aheight*3/planet.height/4);
		basex = (awidth-msize*planet.width)/2; basey = aheight*5/6;
		if (status==S_PLAY) synchronized (this) { onInput(); }
		delay+=applet.diftime;
		if (delay>100) delay=100;
		for (; delay>=wait; delay-=wait) {
			synchronized (this) {
				if (status==S_PLAY) onPlay();
				else if (status==S_BEGIN) onBegin();
				else if (status==S_WIN) onWin();
				else if (status==S_LOSE) onLose();
			}
		}
		synchronized (this) { paint(); }
		synchronized (this) { updateSound(); }
		if (!last) {
			return this;
		}else {
			applet.stopAllAudio();
			return new OptionScene();
		}
	}

	// MouseListener Method
	public void mousePressed(MouseEvent e)
	{
		synchronized (this) {
			if (status==S_PLAY) {
			}else if (status==S_WIN||status==S_LOSE) {
				if (endTime>=1000) last = true;
			}
		}
	}
	public void mouseReleased(MouseEvent e)
	{
		synchronized (this) {
			if (status==S_PLAY) {
			}
		}
	}

	// KeyListener Method
	public void keyPressed(KeyEvent e)
	{
		synchronized (this) {
			reverseAccel ^= e.getKeyCode()==KeyEvent.VK_T;
		}
	}
	public void keyReleased(KeyEvent e)
	{
		synchronized (this) {
		}
	}

	// ����������
	private void init()
	{
		planet = applet.planet;
		rule = applet.rule;
		field = new Meteo[planet.width][planet.height];
		for (int x=0; x<planet.width; x++) {
			for (int y=0; y<planet.height; y++) {
				field[x][y] = new Meteo();
			}
		}
		for (int y=0; y<rule.initialHeight; y++) {
			Stack s=newStack();
			s.velocity = Math.min(-planet.initialVelocity, 1.0-rule.initialHeight);
			s.offset = planet.height-y;
			if (y>0) s.offset-=s.velocity*y/(rule.initialHeight-1);
			s.ignition=-1;
			for (int x=0; x<planet.width; x++) {
				field[x][y].setMeteo(getSafeColor(x, y));
				field[x][y].setStack(s);
			}
		}
		delay=-applet.diftime;
		hp = planet.maxHP;
		boolean h=applet.option.alpha;
		backColor = planet.backColor.toColor(h);
		activeColor = planet.activeColor.toColor(h);
		movableColor = planet.movableColor.toColor(h);
		textColor = planet.textColor.toColor(h);
		gridColor = planet.gridColor.toColor(h);
		limitColor = planet.limitColor.toColor(h);
		meteoColor = planet.meteoColor.toColor(h);
		frameColor = planet.frameColor.toColor(h);
		endHeight = new int[planet.width];
	}

	// ���C������
	private void onInput()
	{
		timeAccel = applet.getKeyPressed(KeyEvent.VK_SHIFT)||applet.getMousePressed(2);
		if (applet.getKeyPushed(KeyEvent.VK_ESCAPE)) annihilate();
		double mx=getMeteoX(applet.getMouseX()), my=getMeteoY(applet.getMouseY());
		Meteo m = getMeteo(mx, my);
		int imx = (int)mx, imy = (int)my;
		if (mx<0) imx=0; else if (mx>=planet.width) imx = planet.width-1;
		if (!applet.getMousePressed(0)) {
			grabbed=null;
			grabbedX = -1;
			dragMode=DRAG_NONE;
			if (m!=null&&m.isGrabbable()) {
				if (applet.getKeyPushed(KeyEvent.VK_W)) m.fastMove=Meteo.DIR_UP;
				if (applet.getMousePushed(1)||
					applet.getKeyPushed(KeyEvent.VK_S)) m.fastMove=Meteo.DIR_DOWN;
				if (applet.getKeyPushed(KeyEvent.VK_A)) m.fastMove=Meteo.DIR_LEFT;
				if (applet.getKeyPushed(KeyEvent.VK_D)) m.fastMove=Meteo.DIR_RIGHT;
			}
			return;
		}
		if (grabbed!=null&&!grabbed.isGrabbable()) grabbed = null;
		if (m==grabbed) return;
		if (grabbed==null) {
			// ���ꂩ����ނ��[
			if (applet.getMousePushed(0)) {
				if (m!=null) {
					if (m.isGrabbable()) {
						grabbed = m;
						grabbedX = imx;
						dragMode = DRAG_NONE;
					}
				}
			}
		}else {
			// ���񂾂̓��������[
			int grabbedY=0;
			// ���e�I�̌��݈ʒu�𒲂ׂ�
			for (; grabbedY<planet.height; grabbedY++) {
				if (field[grabbedX][grabbedY]==grabbed) break;
			}
			// �ǂ����ɓ����������߂܂��傤
			if (dragMode==DRAG_NONE) {
				if (grabbedX!=imx) {
					// �S�u���Ȃ����ړ�
					dragMode=DRAG_HORIZONTAL;
				}else {
					// �c�ړ��͂��ꂪ���镪���肪�����ʓ|
					double dy = my-grabbedY-grabbed.getOffset();
					if (dy<0||1<=dy) dragMode=DRAG_VERTICAL;
				}
			}
			// ����Ƃ������点�邺
			if (dragMode==DRAG_VERTICAL) {
				int diffy = grabbedY+grabbed.getOffset()<my?1:-1;
				double dy;
				for (; ; grabbedY+=diffy) {
					dy = my-grabbedY-grabbed.getOffset();
					if (0<=dy&&dy<1) break;
					if (grabbedY+diffy<0||planet.height<=grabbedY+diffy) break;
					if (!canMove(grabbedX, grabbedY, grabbedX, grabbedY+diffy)) {
						if (rule.shoot&&diffy==1&&!grabbed.inStack()&&
							(field[grabbedX][grabbedY+1].isNone()||field[grabbedX][grabbedY+1].inStack()))
							shoot(grabbed);
						break;
					}
					Move(grabbedX, grabbedY+diffy, grabbedX, grabbedY);
					if (grabbed==null) break;
					if (!rule.vertical) break;
				}
			}else if (dragMode==DRAG_HORIZONTAL) {
				if (grabbedX!=imx) {
					int diffx = grabbedX<imx?1:-1;
					for (; grabbedX!=imx; grabbedX+=diffx) {
						Move(grabbedX+diffx, grabbedY, grabbedX, grabbedY);
						if (grabbed==null) break;
					}
				}
			}
		}
	}
	private void onPlay()
	{
		int x, y;
		Meteo m, m2;
		Stack s, s2, sOld;
		// �����ړ�
		for (x=0; x<planet.width; x++) {
			for (y=0; y<planet.height; y++) {
				switch ((m=field[x][y]).fastMove) {
				case Meteo.DIR_DOWN: if (!Move(x, y, x, y-1)) m.fastMove=Meteo.DIR_NONE; break;
				case Meteo.DIR_LEFT: if (!Move(x, y, x-1, y)) m.fastMove=Meteo.DIR_NONE; break;
				}
			}
		}
		for (x=planet.width-1; x>=0; x--) {
			for (y=planet.height-1; y>=0; y--) {
				switch ((m=field[x][y]).fastMove) {
				case Meteo.DIR_UP: if (!Move(x, y, x, y+1)) m.fastMove=Meteo.DIR_NONE; break;
				case Meteo.DIR_RIGHT: if (!Move(x, y, x+1, y)) m.fastMove=Meteo.DIR_NONE; break;
				}
			}
		}
		// �������e�I
		fallMeteo+=getTimeParam(planet.frequency)*dt*(accel()?planet.accelRate:1.0);
		if (fallMeteo>planet.width) fallMeteo = planet.width;
		y = planet.height-1;
		for (; fallMeteo>=1.0; fallMeteo-=1.0) {
			int fx=-1, sum=0;
			for (x=0; x<planet.width; x++) {
				if (!field[x][y].isNone()) continue;
				if (random.nextInt(++sum)==0) fx = x;
			}
			if (fx<0) break;
			m = field[fx][y];
			m.setMeteo(getSafeColor(fx, y));
			m.setStack(s=newStack());
			s.offset=1.0;
			s.velocity=-getTimeParam(planet.fallVelocity);
			s.ignition=-1;
			clearFlag = true;
		}
		// �J�^�}���̓���
		Iterator it = stacks.iterator();
		double oMin=0.0, oMax=0.0;
		countMeteos();
		while (it.hasNext()) {
			s = (Stack)it.next();
			if (s.meteoCount==0&&s.ashCount==0) {
				it.remove();
				continue;
			}
			s.difference = 0.0;
			s.velocity -= dt*
				(s.ignition>=1?planet.stackGravity:
				(s.ignition==0?planet.shootGravity:
				(planet.fallGravity*(accel()?planet.accelRate:1.0))));
			double bt = s.boostDuration>dt?dt:s.boostDuration;
			double weight = planet.meteoWeight*s.meteoCount+planet.ashWeight*s.ashCount;
			if (weight>=0.0) weight += 1.0;
			else weight = 1.0/(1.0-weight);
			s.velocity += s.boostForce*bt/weight;
			if (s.ignition>=1) {
				// �ʏ�̃J�^�}��
				if (s.velocity>planet.boostLimit) {
					s.velocity = planet.boostLimit;
				}else if (accel()) {
					s.velocity = Math.max(s.velocity, -planet.stackFallLimitT);
				}else {
					s.velocity = Math.max(s.velocity, -planet.stackFallLimit);
				}
				if (s.onGround) {
					s.reduceTime-=dt;
					if (s.offset>0.0) s.onGround = false;
				}
				if (s.pushDown>0.0) {
					double pushDown = planet.pushDownVelocity*dt;
					if (pushDown>s.pushDown) pushDown = s.pushDown;
					s.pushDown -= pushDown;
					s.difference -= pushDown;
				}
			}else if (s.ignition==0) {
				// �V���[�g�������e�I
				s.velocity = Math.max(s.velocity, -planet.shootFallLimit);
			}else {
				// �������e�I
				s.velocity = Math.max(s.velocity, -getTimeParam(planet.meteoFallLimit)*(accel()?planet.accelRate:1.0));
			}
			s.boostDuration -= bt;
			if (s.boostDuration<=0) s.boostForce=0.0;
			s.difference += s.velocity*dt;
			s.offset += s.difference;
			if (oMin>s.offset) oMin=s.offset;
			if (oMax<s.offset) oMax=s.offset;
		}
		// �J�^�}���O�̔R���J�X�̊Ҍ�
		for (x=0; x<planet.width; x++) {
			for (y=0; y<planet.height; y++) {
				m = field[x][y];
				if (m.isAsh()&&!m.inStack()) {
					m.reduceTime -= dt;
					if (m.reduceTime<=0.0) {
						m.setMeteo(getSafeColor(x, y));
					}
				}
			}
		}
		// �h�b�L���O�ƃ��e�I�ړ�
		do {
			oMin=oMax=0.0;
			// �I�t�Z�b�g�����ƃh�b�L���O(�㏸)
			for (x=0; x<planet.width; x++) {
				for (y=0; y<planet.height; y++) {
					m = field[x][y];
					if ((s=m.getStack())==null) continue;
					if (s.difference>0.0) {
						if (y>=planet.height-1) {
							// ��C��
							if (s.offset>0.0) {
								if (s.ignition<=0) {
									// �V���[�g�ł͑�C���O�܂Ŕ�΂Ȃ�
									s.stop();
									s.boostForce=0.0;
								}else {
									// �܂Ƃ��ȃJ�^�}���Ȃ̂ő�C���˔j�I�I
									if (m.isMeteo()) {
										gotMeteo[m.getColor()]++;
										gotAllMeteo++;
										score+=20;
									}else if (m.isAsh()) {
										gotAsh++;
										score+=10;
									}
									sOld=m.getStack();
									m.setNone();
									if (x<planet.width-1) {
										if (!field[x  ][y-1].inStack(sOld)&&
											field[x+1][y  ].inStack(sOld)&&
											field[x+1][y-1].inStack(sOld)) {
											// ��C�����f�����I
											setStack(x+1, y, newStack(sOld));
										}
									}
								}
							}
						}else {
							// ��C����
							midairDock(x, y);
						}
					}
				}
			}
			// ���e�I�̈ړ�(�㏸)
			for (x=0; x<planet.width; x++) {
				for (y=planet.height-2; y>=0; y--) {
					m = field[x][y];
					if (m.getOffset()>=1.0) {
						field[x][y] = field[x][y+1];
						field[x][y+1] = m;
						y++;
					}
				}
			}
			// �J�^�}���̏����ŐV�ɍX�V(�㏸)
			it = stacks.iterator();
			while (it.hasNext()) {
				s = (Stack)it.next();
				if (s.offset>=1.0) s.offset-=1.0;
				if (oMax<s.offset) oMax=s.offset;
			}
			// �I�t�Z�b�g�����ƃh�b�L���O(���~)
			for (x=0; x<planet.width; x++) {
				for (y=0; y<planet.height; y++) {
					if ((m=field[x][y]).isNone()) continue;
					if ((s=m.getStack())==null) {
						if (y>0) {
							m2 = field[x][y-1];
							if (m2.isNone()) {
								s = newStack();
								setStack(x, y, s);
							}else if (m2.inStack()) {
								setStack(x, y, m2.getStack());
							}
						}
					}else if (s.difference<=0.0) {
						if (y<=0) {
							// �n��
							if (s.offset<=0.0) {
								if (s.ignition<=0) {
									// �V���[�g���������e�I�����Ă���
									m.setStack(null);
								}else {
									if (!s.onGround) {
										// �J�^�}���������Ă���
										s.onGround=true;
										s.reduceTime = getReduceTime();
									}
									s.stop();
									if (s.reduceTime<=0.0) reduce(x, y);
								}
							}
						}else {
							// ��
							midairDock(x, y-1);
						}
					}
				}
			}
			// ���e�I�̈ړ�(���~)
			for (x=0; x<planet.width; x++) {
				for (y=1; y<planet.height; y++) {
					m = field[x][y];
					if (m.getOffset()<0.0) {
						field[x][y] = field[x][y-1];
						field[x][y-1] = m;
					}
				}
			}
			countMeteos();
			// �J�^�}���̏����ŐV�ɍX�V(���~)
			it = stacks.iterator();
			while (it.hasNext()) {
				s = (Stack)it.next();
				if (s.meteoCount==0&&s.ashCount==0) {
					it.remove();
					continue;
				}
				if (s.offset<0.0) s.offset+=1.0;
				if (oMin>s.offset) oMin=s.offset;
			}
		} while (oMin<0.0||1.0<=oMax);
		ignite();
		countMeteos();
		if (clearFlag&&blockCount==0) {
			score+=1000*planet.width;
			clearCount++;
			clearFlag = false;
		}
		y = planet.height-1;
		for (x=0; x<planet.width; x++) {
			if (field[x][y].isGrabbable()&&!field[x][y].inStack()) hp-=dt;
		}
		time+=wait;
		boolean win = true, challenge=true;
		if (rule.score>0) {
			challenge = false;
			if (rule.score>score) win = false;
		}
		if (rule.seconds>0.0) {
			challenge = false;
			if (rule.seconds*1000>time) win = false;
		}
		for (int i=0; i<Meteo.colorName.length; i++) {
			if (rule.gotMeteo[i]>0) {
				challenge = false;
				if (rule.gotMeteo[i]>gotMeteo[i]) win = false;
			}
		}
		if (rule.gotAllMeteo>0) {
			challenge = false;
			if (rule.gotAllMeteo>gotAllMeteo) win = false;
		}
		if (rule.gotAsh>0) {
			challenge = false;
			if (rule.gotAsh>gotAsh) win = false;
		}
		if (rule.clearCount>0) {
			challenge = false;
			if (rule.clearCount>clearCount) win = false;
		}
		if (win&&!challenge) {
			victory();
		}else if (hp<=0.0) {
			hp=0.0;
			annihilate();
		}
	}
	private void onBegin()
	{
		if (beginTime>=2000) { status=S_PLAY; return; }
		for (int y=0; y<rule.initialHeight; y++) {
			Stack s=field[0][y].getStack();
			if (s!=null) {
				s.offset += s.velocity*dt;
				if (s.offset<=0.0) {
					for (int x=0; x<planet.width; x++) field[x][y].setStack(null);
				}
			}
		}
		beginTime += wait;
	}
	private void onWin()
	{ endTime += wait; }
	private void victory()
	{
		status = S_WIN;
		if (bgm!=null) {
			applet.stopAudio(bgm);
			applet.playAudio(applet.meWin);
		}
	}
	private void onLose()
	{
		int fx=-1, sum=0, d;
		for (int x=0; x<planet.width; x++) {
			d = planet.height-endHeight[x];
			if (d<=0) continue;
			sum += d;
			if (random.nextInt(sum)<d) fx = x;
		}
		if (fx>=0) {
			Meteo m=field[fx][endHeight[fx]++];
			if (m.isBlock()) m.setAsh(0.0);
		}
		endTime += wait;
	}
	private void annihilate()
	{
		status = S_LOSE;
		if (bgm!=null) {
			applet.stopAudio(bgm);
			applet.playAudio(applet.meLose);
		}
	}

	// �`��֘A
	private void paint()
	{
		Meteo target = grabbed!=null?grabbed:
			getMeteo(getMeteoX(applet.getMouseX()), getMeteoY(applet.getMouseY()));
		if (status!=S_PLAY) target=grabbed=null;
		int x, y, tx=0, ty=0, tvx=0, tvy=0;
		int lineWidth=msize/15+1;
		int i;
		Option o=applet.option;
		Image meteo=applet.meteo;
		int sw=meteo.getWidth(null)/13, sh=meteo.getHeight(null);
		Graphics2D g = applet.beginPaint();
		g.drawImage(applet.back, 0, 0, awidth, aheight, null);
		// �g��`�����I
		g.setColor(gridColor);
		for (x=0; x<=planet.width; x++) {
			g.drawLine(getPixelX(x), getPixelY(0), getPixelX(x), getPixelY(planet.height));
		}
		for (y=0; y<planet.height; y++) {
			g.drawLine(getPixelX(0), getPixelY(y), getPixelX(planet.width), getPixelY(y));
		}
		g.setColor(limitColor);
		g.drawLine(getPixelX(0), getPixelY(planet.height), getPixelX(planet.width), getPixelY(planet.height));
		// ���e�I�`��
		g.setColor(meteoColor);
		for (x=0; x<planet.width; x++) {
			for (y=planet.height-1; y>=0; y--) {
				Meteo m = field[x][y];
				if (!(0.0<=m.getOffset()&&m.getOffset()<planet.height)) continue;
				int vx = getPixelX(x);
				int vy = getPixelY(m.getOffset()+y+1);
				if (m.isMeteo()) {
					g.fillRect(vx, vy, msize, msize);
					g.drawImage(meteo, vx, vy, vx+msize, vy+msize, sw*m.getColor(), 0, sw*(m.getColor()+1), sh, null);
				}else if (m.isAsh()) {
					g.fillRect(vx, vy, msize, msize);
					g.drawImage(meteo, vx, vy, vx+msize, vy+msize, sw*Meteo.colorName.length, 0, sw*(Meteo.colorName.length+1), sh, null);
				}
				if (m==target) {
					tx = x; ty = y;
					tvx = vx; tvy = vy;
				}
			}
		}
		if (grabbed!=null) {
			g.setColor(movableColor);
			if (dragMode==DRAG_VERTICAL||dragMode==DRAG_NONE) {
				int h;
				for (y=ty; canMove(tx, y, tx, y+1, true); y++);
				for (h=0; canMove(tx, y-h, tx, y-h-1, true); h++);
				h++;
				for (i=0; i<lineWidth; i++) {
					g.drawRect(tvx-i-1, getPixelY(grabbed.getOffset()+y+1)-i-1, msize+i*2+1, h*msize+i*2+1);
				}
			}
			if (dragMode==DRAG_HORIZONTAL||dragMode==DRAG_NONE) {
				int w;
				for (x=tx; canMove(x, ty, x-1, ty, true); x--);
				for (w=0; canMove(x+w, ty, x+w+1, ty, true); w++);
				w++;
				for (i=0; i<lineWidth; i++) {
					g.drawRect(getPixelX(x)-i-1, tvy-i-1, w*msize+i*2+1, msize+i*2+1);
				}
			}
		}
		if (target!=null&&target.isGrabbable()) {
			g.setColor(activeColor);
			for (i=0; i<lineWidth; i++) {
				g.drawRect(tvx-i-1, tvy-i-1, msize+i*2+1, msize+i*2+1);
			}
		}
		// HP���[�^
		if (hp>0.0) {
			if (hp!=Double.POSITIVE_INFINITY) {
				g.setColor(new Color(
					(int)(planet.hpmaxColor.r*hp/planet.maxHP+planet.hpminColor.r*(1.0-hp/planet.maxHP)),
					(int)(planet.hpmaxColor.g*hp/planet.maxHP+planet.hpminColor.g*(1.0-hp/planet.maxHP)),
					(int)(planet.hpmaxColor.b*hp/planet.maxHP+planet.hpminColor.b*(1.0-hp/planet.maxHP)),
					!o.alpha?planet.hpmaxColor.a:
					(int)(planet.hpmaxColor.a*hp/planet.maxHP+planet.hpminColor.a*(1.0-hp/planet.maxHP))
				));
				g.fillArc(25, 30, 100, 100, 90, (int)(-360*hp/planet.maxHP));
			}else {
				g.setColor(planet.hpmaxColor.toColor(o.alpha));
				g.fillOval(25, 30, 100, 100);
			}
		}
		g.setColor(frameColor);
		g.drawOval(25, 30, 100, 100);
		g.drawLine(75, 30, 75, 80);
		// �e�L�X�g�\��
		float fontSize = 15.0f;
		int lh = 20;
		g.setFont(g.getFont().deriveFont(Font.PLAIN, fontSize));
		t = t*0.99+applet.diftime*0.01;
		int left=5, top=5;
		textOut(g, Long.toString(time/60000)+"'"+Long.toString(time/10000%6)+Long.toString(time/1000%10)+"\""+Long.toString(time/100%10)+Long.toString(time/10%10), left, top); top += lh;
		top=140;
		textOut(g, "�����݂̏󋵁�", left, top); top += lh;
		textOut(g, "���e�I�F"+Integer.toString(meteoCount)+"��", left, top); top += lh;
		textOut(g, "�R���J�X�F"+Integer.toString(ashCount)+"��", left, top); top += lh;
		textOut(g, "�u���b�N�F"+Integer.toString(blockCount)+"��", left, top); top += lh;
		textOut(g, "�J�^�}���F"+Integer.toString(stacks.size())+"��", left, top); top += lh;
		textOut(g, "�A�N�Z���F"+(accel()?"ON":"OFF"), left, top); top += lh;
		textOut(g, "FPS�F"+Integer.toString((int)(1000.0/t))+
			(t<20.0?"(�y��)":(t<50.0?"(���d)":
			(t<100.0?"(�d)":"(��������)"))), left, top); top += lh;
		top += lh;
		textOut(g, "�S�����F"+Integer.toString(clearCount)+"��", left, top); top += lh;
		textOut(g, "�_�΁F"+Integer.toString(horizontal+vertical)+"��", left, top); top += lh;
		textOut(g, "���_�΁F"+Integer.toString(horizontal)+"��", left, top); top += lh;
		textOut(g, "�c�_�΁F"+Integer.toString(vertical)+"��", left, top); top += lh;
		textOut(g, "�ő�_�΁F"+Integer.toString(maxIgnition)+"��", left, top); top += lh;
		left=awidth-150; top=5;
		textOut(g, "���_�F"+Long.toString(score)+"�_", left, top); top += lh;
		top += lh;
		textOut(g, "���ł��グ������", left, top); top += lh;
		textOut(g, "���e�I�F"+Integer.toString(gotAllMeteo)+"��", left, top); top += lh;
		textOut(g, "�R���J�X�F"+Integer.toString(gotAsh)+"��", left, top); top += lh;
		textOut(g, "�u���b�N�F"+Integer.toString(gotAllMeteo+gotAsh)+"��", left, top); top += lh;
		top += lh;
		textOut(g, "���f�ޕʁ�", left, top); top += lh;
		for (i=0; i<Meteo.colorName.length; i++) {
			textOut(g, Meteo.colorName[i]+"�F"+Integer.toString(gotMeteo[i])+"��", left, top); top += lh;
		}
		if (status==S_WIN) {
			g.setFont(g.getFont().deriveFont(Font.PLAIN, 100.0f));
			textOut(g, "����", 220, 180);
		}else if (status==S_LOSE) {
			g.setFont(g.getFont().deriveFont(Font.ITALIC, 100.0f));
			textOut(g, "�ŖS", 220, 180);
		}
		applet.endPaint();
	}
	void textOut(Graphics2D g, String text, int left, int top)
	{
		int y=top+g.getFont().getSize();
		g.setColor(backColor);
		g.drawString(text, left-1, y);
		g.drawString(text, left+1, y);
		g.drawString(text, left, y-1);
		g.drawString(text, left, y+1);
		if (applet.option.textAntialiasing) g.drawString(text, left, y);
		g.setColor(textColor);
		g.drawString(text, left, y);
	}

	// ���֘A
	private void updateSound()
	{
		// �Q�b�Ԃ�BGM�ύX���Ȃ�
		int newBGMNo = (blockCount-1)*4/(planet.width*planet.height);
		if (bgmNo!=newBGMNo&&time-bgmChangeTime>2000) {
			AudioClip newBGM=applet.bgm[Math.max(0,Math.min(2,bgmNo>newBGMNo?newBGMNo:newBGMNo-1))];
			if (newBGM!=null&&newBGM!=bgm) {
				applet.stopAudio(bgm);
				applet.loopAudio(bgm=newBGM);
				bgmChangeTime = time;
			}
			bgmNo = newBGMNo;
		}
		if (!ignitedStacks.isEmpty()) {
			if (fanfareIgniteNo>4) fanfareIgniteNo=4;
			int fanfareWeight=0;
			while (!ignitedStacks.isEmpty()) {
				Stack s=(Stack)ignitedStacks.removeFirst();
				fanfareWeight = Math.max(fanfareWeight, s.meteoCount+s.ashCount/3);
			}
			int stackWidth = Math.min(fanfareIgniteNo*2+1, planet.width);
			fanfareWeight = fanfareWeight*3/stackWidth/applet.planet.height;
			if (fanfareWeight>2) fanfareWeight=2;
			if (applet.fanfare[fanfareIgniteNo][fanfareWeight]!=null) {
				applet.stopAudio(fanfare);
				applet.playAudio(fanfare=applet.fanfare[fanfareIgniteNo][fanfareWeight]);
			}
		}
		fanfareIgniteNo=0;
	}

	// ���e�I�z�u�֘A
	private int getSafeColor(int x, int y)
	{
		int[] rate = (int[])planet.rate.clone();
		int c1, c2, cy, fy=y;
		Meteo m;
		// ���ɓ��
		c1 = -1;
		for (cy=y-1; cy>=0; cy--) {
			m = field[x][cy];
			if (m.isAsh()) break;
			if (m.isMeteo()) {
				if (c1<0) {
					c1 = m.getColor();
					fy = cy+1;	// �����\��n�_
				}else {
					if (c1==m.getColor()) rate[c1]=0;
					break;
				}
			}
		}
		// ��ɓ��(�ƁA���łɂ������̂ƍ��킹�ď㉺�����)
		c2 = -1;
		for (cy=y+1; cy<planet.height; cy++) {
			m = field[x][cy];
			if (m.isAsh()) break;
			if (m.isMeteo()) {
				if (c2<0) {
					c2 = m.getColor();
					if (c1==c2) {
						rate[c2]=0;
						break;
					}
				}else {
					if (c2==m.getColor()) rate[c2]=0;
					break;
				}
			}
		}
		// ���ɓ��(�����\��n�_�̍��E�𒲂ׂ�)
		c1 = -1;
		if (x>0) {
			m = field[x-1][fy];
			if (m.isMeteo()) {
				c1 = m.getColor();
				if (x>1) {
					m = field[x-1][fy];
					if (m.isMeteo()&&c1==m.getColor()) rate[c1]=0;
				}
			}
		}
		// �E�ɓ��(��ɂ���č��E�����)
		c2 = -1;
		if (x<planet.width-1) {
			m = field[x+1][fy];
			if (m.isMeteo()) {
				c2 = m.getColor();
				if (c1==c2) {
					rate[c2]=0;
				}else if (x<planet.width-2) {
					m = field[x+2][fy];
					if (m.isMeteo()&&c2==m.getColor()) rate[c2]=0;
				}
			}
		}
		/*
		if (x>=2&&field[x-1][y].isMeteo()&&field[x-2][y].isMeteo()&&
			(c=field[x-1][y].getColor())==field[x-2][y].getColor()) rate[c]=0;
		if (y>=2&&field[x][y-1].isMeteo()&&field[x][y-2].isMeteo()&&
			(c=field[x][y-1].getColor())==field[x][y-2].getColor()) rate[c]=0;
		if (x<planet.width-2&&field[x+1][y].isMeteo()&&field[x+2][y].isMeteo()&&
			(c=field[x+1][y].getColor())==field[x+2][y].getColor()) rate[c]=0;
		if (y<planet.height-2&&field[x][y+1].isMeteo()&&field[x][y+2].isMeteo()&&
			(c=field[x][y+1].getColor())==field[x][y+2].getColor()) rate[c]=0;
		*/
		return getRandomColor(rate);
	}
	private int getRandomColor(int[] rate)
	{
		int sum=0,c=0;
		for (int i=0; i<Meteo.colorName.length; i++) {
			sum+=rate[i];
			if (sum>0&&random.nextInt(sum)<rate[i]) c = i;
		}
		return c;
	}

	// ���W�ϊ�(���e�I���W�����X�N���[�����W)
	private int getPixelX(double meteoX)
	{ return basex+(int)(meteoX*msize); }
	private int getPixelY(double meteoY)
	{ return basey-(int)(meteoY*msize); }
	private double getMeteoX(int pixelX)
	{ return (double)(pixelX-basex)/msize; }
	private double getMeteoY(int pixelY)
	{ return (double)(basey-pixelY)/msize; }

	// �t�B�[���h��̃��e�I����
	private Meteo getMeteo(double x, double y)
	{
		if (x<0||planet.width<=x||y<0||planet.height<=y) return null;
		int cx = (int)x, cy = (int)y;
		double offset = y-(double)cy;
		Meteo m = field[cx][cy];
		if (offset>m.getOffset()&&!m.isNone()) return m;
		if (cy==0) return null;
		m = field[cx][cy-1];
		if (offset<=m.getOffset()&&!m.isNone()) return m;
		return null;
	}
	private boolean canMove(int fromX, int fromY, int toX, int toY, boolean strict)
	{
		if (fromX<0||planet.width<=fromX) return false;
		if (fromY<0||planet.height<=fromY) return false;
		if (toX<0||planet.width<=toX) return false;
		if (toY<0||planet.height<=toY) return false;
		if (fromX==toX) {
			if (strict&&!rule.vertical) return false;
			if (fromY+1!=toY&&fromY-1!=toY) return false;
		}else if (fromY==toY) {
			if (strict&&!rule.horizontal) return false;
			if (fromX+1!=toX&&fromX-1!=toX) return false;
		}else return false;
		Meteo from=field[fromX][fromY], to=field[toX][toY];
		if (from.isNone()||to.isNone()) return false;
		if (from.getOffset()!=to.getOffset()) return false;
		if (!from.inSameStack(to)) {
			if (!from.inStack()&&to.isOnGround()) return true;
			if (from.isOnGround()&&!to.inStack()) return true;
			if (from.isOnGround()&&to.isOnGround()) return true;
			return false;
		}
		else return true;
	}
	private boolean canMove(int fromX, int fromY, int toX, int toY)
	{ return canMove(fromX, fromY, toX, toY, false); }
	private boolean Move(int fromX, int fromY, int toX, int toY)
	{
		if (!canMove(fromX, fromY, toX, toY, true)) return false;
		Meteo m = field[fromX][fromY];
		Stack s1 = m.getStack(), s2 = field[toX][toY].getStack();
		field[fromX][fromY] = field[toX][toY];
		field[toX][toY] = m;
		field[toX][toY].setStack(s2);
		field[fromX][fromY].setStack(s1);
		if (s1==null&&s2!=null) field[fromX][fromY].reduceTime = s2.reduceTime;
		else if (s2==null&&s1!=null) field[toX][toY].reduceTime = s1.reduceTime;
		ignite();
		return true;
	}
	private void countMeteos()
	{
		Iterator it = stacks.iterator();
		double oMin=0.0, oMax=0.0;
		while (it.hasNext()) {
			Stack s = (Stack)it.next();
			s.meteoCount=0;
			s.ashCount=0;
		}
		meteoCount=ashCount=0;
		for (int x=0; x<planet.width; x++) {
			for (int y=0; y<planet.height; y++) {
				Meteo m = field[x][y];
				if (m.isMeteo()) {
					if (m.inStack()) m.getStack().meteoCount++;
					meteoCount++;
				}
				if (m.isAsh()) {
					if (m.inStack()) m.getStack().ashCount++;
					ashCount++;
				}
			}
		}
		blockCount = meteoCount+ashCount;
	}

	// ���e�I�̓���ȋ���
	private void ignite()
	{
		int x, y, c, start, end, lowest;
		Meteo m, m2;
		Stack s, sOld, sOld2;
		Ignition ig;
		double rt = getReduceTime();
		// ���_�΂̔���
		for (y=0; y<planet.height; y++) {
			for (x=0; x<planet.width-2; x++) {
				if (!field[x][y].isMeteo()) continue;
				c = field[x][y].getColor();
				if (field[x+1][y].isMeteo()&&
					field[x+2][y].isMeteo()&&
					c==field[x+1][y].getColor()&&
					c==field[x+2][y].getColor()&&
					canMove(x  , y, x+1, y)&&
					canMove(x+1, y, x+2, y)) {
					do {
						field[x][y].horizontal = true;
						x++;
					}while (x<planet.width&&field[x][y].isMeteo()&&field[x][y].getColor()==c&&canMove(x-1, y, x, y));
					x--;
				}
			}
		}
		// �c�_�΂̔���
		for (x=0; x<planet.width; x++) {
			for (y=0; y<planet.height-2; y++) {
				if (!field[x][y].isMeteo()) continue;
				c = field[x][y].getColor();
				if (field[x][y+1].isMeteo()&&
					field[x][y+2].isMeteo()&&
					c==field[x][y+1].getColor()&&
					c==field[x][y+2].getColor()&&
					canMove(x, y  , x, y+1)&&
					canMove(x, y+1, x, y+2)) {
					do {
						field[x][y].vertical = true;
						y++;
					}while (y<planet.height&&field[x][y].isMeteo()&&field[x][y].getColor()==c&&canMove(x, y-1, x, y));
					y--;
				}
			}
		}
		// ���_��
		for (y=planet.height-1; y>=0; y--) {
			for (x=0; x<planet.width-2; x++) {
				if (field[x][y].horizontal) {
					c = (m=field[x][y]).getColor();
					sOld = sOld2 = m.getStack();
					(s=newStack()).offset=m.getOffset();
					start=x;
					for (; x<planet.width&&field[x][y].horizontal&&field[x][y].getColor()==c; x++) {
						m=field[x][y];
						if (grabbed==m) grabbed = null;
						if (y<planet.height-1) {
							if ((m2=field[x][y+1]).isBlock()&&s.offset==m2.getOffset()) {
								// ��ƃh�b�L���O
								setStack(x, y+1, s);
							}
						}
						if (m.inStack()) {
							if (s.ignition<m.getStack().ignition) s.ignition = m.getStack().ignition;
							if (s.velocity<m.getStack().velocity) s.velocity = m.getStack().velocity;
							if (s.velocity>0.0) {
								// �㏸���͐؂藣���_�΂��N����Ȃ��I�I
								if (y>0&&(m2=field[x][y-1]).isBlock()&&m2.inStack(sOld)) {
									setStack(x, y-1, s);
								}
							}else {
								// �؂藣���ł͉��̃J�^�}���̊Ҍ����Ԃ����Z�b�g����
								if (y>0&&(m2=field[x][y-1]).isBlock()&&m2.inStack()) {
									m2.getStack().reduceTime = rt;
								}
							}
						}
						m.setAsh(rt);
						m.fastMove = Meteo.DIR_NONE;
						sOld2 = m.getStack();
						m.setStack(s);
						m.horizontal = false;
					}
					end=x;
					if (s.velocity>0.0) {
						if (start>0&&field[start-1][y].inStack(sOld)) setStack(start-1, y, s);
						if (end<planet.width&&field[end][y].inStack(sOld)) setStack(end, y, s);
					}else {
						// �؂藣���_�΂̓���
						if (y<planet.height-1) {
							if (start>0&&sOld!=null&&!field[start][y+1].vertical&&
								field[start-1][y].inStack(sOld)&&field[start-1][y+1].inStack(sOld)) {
								setStack(start-1, y, s);
							}
							if (end<planet.width&&sOld2!=null&&!field[end-1][y+1].vertical&&
								field[end][y].inStack(sOld2)&&field[end][y+1].inStack(sOld2)) {
								setStack(end, y, s);
							}
						}
					}
					// �ŉ����_�΃{�[�i�X
					lowest=0;
					for (x=start; x<end; x++) {
						if (y<=0||!field[x][y-1].inStack(s)) lowest++;
					}
					double bonus = getLowestBonus(planet.lowestIgnition, lowest, end-start);
					// �J�^�}���ɑ��x��^����
					s.ignition++;
					if (s.ignition<1) s.ignition = 1;
					if (s.ignition>maxIgnition) maxIgnition = s.ignition;
					ig = planet.horizontal[s.ignition>=10?9:s.ignition-1];
					if (s.velocity<ig.initialVelocity*bonus) s.velocity = ig.initialVelocity*bonus;
					s.boostForce = ig.boostForce*(Math.pow(end-start, planet.sameTimeIgnition))*bonus;
					s.boostDuration = ig.boostDuration;
					score+=(end-start)*100*s.ignition;
					horizontal++;
					// �t�@���t�@�[��
					if (s.ignition>fanfareIgniteNo) {
						fanfareIgniteNo=s.ignition;
						ignitedStacks.add(s);
					}
					x--;
				}
			}
		}
		// �c�_��
		for (x=0; x<planet.width; x++) {
			for (y=planet.height-1; y>=2; y--) {
				if (field[x][y].vertical) {
					c = (m=field[x][y]).getColor();
					sOld = sOld2 = m.getStack();
					(s=newStack()).offset=m.getOffset();
					start=y;
					for (; y>=0&&field[x][y].vertical&&field[x][y].getColor()==c; y--) {
						m=field[x][y];
						if (grabbed==m) grabbed = null;
						if (m.inStack()) {
							if (s.ignition<m.getStack().ignition) s.ignition = m.getStack().ignition;
							if (s.velocity<m.getStack().velocity) s.velocity = m.getStack().velocity;
							if (s.velocity>0.0) {
								// �㏸���͐؂藣���_�΂��N����Ȃ��I�I
								if (x>0&&(m2=field[x-1][y]).isBlock()&&m2.inStack(sOld)) {
									setStack(x-1, y, s);
								}
								if (x<planet.width-1&&(m2=field[x+1][y]).isBlock()&&m2.inStack(sOld)) {
									setStack(x+1, y, s);
								}
							}
						}
						if (!m.isAsh()) {
							m.setAsh(rt);
							m.fastMove = Meteo.DIR_NONE;
						}else {
							// �k���_��
							if (x>0&&field[x-1][y].inStack(sOld)) setStack(x-1, y, s);
							if (x<planet.width-1&&field[x+1][y].inSameStack(m)) setStack(x+1, y, s);
						}
						sOld2 = m.getStack();
						m.setStack(s);
						m.vertical = false;
					}
					end=y;
					if (start<planet.height-1&&(m2=field[x][start+1]).isBlock()&&s.offset==m2.getOffset()) {
						// ��ƃh�b�L���O
						setStack(x, start+1, s);
					}
					// �㏸���͐؂藣���_�΂��N����Ȃ��I�I
					if (end>=0&&s.velocity>0.0) {
						if ((m2=field[x][end]).isBlock()&&m2.inStack(sOld2)) {
							setStack(x, end, s);
						}
					}else {
						// �؂藣���_�΂̓���
						if (sOld!=null&&start<planet.height-1) {
							if (x>0&&field[x-1][start].inStack(sOld)&&field[x-1][start+1].inStack(sOld)) {
								setStack(x-1, start, s);
							}
							if (x<planet.width-1&&field[x+1][start].inStack(sOld)&&field[x+1][start+1].inStack(sOld)) {
								setStack(x+1, start, s);
							}
						}
						if (end>=0&&(m2=field[x][end]).isBlock()&&m2.inStack()) {
							m2.getStack().reduceTime = rt;
						}
					}
					// �ŉ����_�΃{�[�i�X
					double bonus = end+1<=0||!field[x][end].inStack(s)?planet.lowestIgnition:1.0;
					// �J�^�}���ɑ��x��^����
					s.ignition++;
					if (s.ignition<1) s.ignition = 1;
					if (s.ignition>maxIgnition) maxIgnition = s.ignition;
					ig = planet.vertical[s.ignition>=10?9:s.ignition-1];
					if (s.velocity<ig.initialVelocity*bonus) s.velocity = ig.initialVelocity*bonus;
					s.boostForce = ig.boostForce*(Math.pow(start-end, planet.sameTimeIgnition))*bonus;
					s.boostDuration = ig.boostDuration;
					score+=(start-end)*100*s.ignition;
					vertical++;
					// �t�@���t�@�[��
					if (s.ignition>fanfareIgniteNo) {
						if (s.ignition==1) {
							fanfareIgniteNo=0;
						}else {
							fanfareIgniteNo=s.ignition;
						}
						ignitedStacks.add(s);
					}
					y--;
				}
			}
		}
	}
	private void shoot(Meteo m)
	{
		Stack s;
		m.setStack(s=newStack());
		s.velocity=planet.shoot.initialVelocity;
		s.boostForce=planet.shoot.boostForce;
		s.boostDuration=planet.shoot.boostDuration;
	}
	private boolean midairDock(int x, int y)
	{
		Meteo m=field[x][y], m2=field[x][y+1];
		// ���炩�Ƀh�b�L���O���Ă��Ȃ��V�`���G�[�V����
		if (m.isNone()||m2.isNone()) return false;
		if (m.inSameStack(m2)) return false;
		if (m2.getOffset()>m.getOffset()) return false;
		// ���Ԃ�h�b�L���O���Ă�炵��
		Stack s=m.getStack(), s2=m2.getStack();
		if (s==null) {
			// ���n�I�I(��̏������s2!=null)
			if (s2.ignition<=0) {
				// �V���[�g���������e�I�����Ă���
				m2.setStack(null);
				return true;
			}else {
				boolean ret;
				if (ret=!s2.onGround) {
					// �J�^�}���������Ă���
					s2.onGround=true;
					s2.reduceTime = getReduceTime();
				}
				s2.stop();
				if (s2.reduceTime<=0.0) reduce(x, y+1);
				return ret;
			}
		}else {
			// �󒆃h�b�L���O
			if (s.ignition>0) {
				// �m�[�}���ȃh�b�L���O
				if (s2!=null) {
					if (s2.ignition>0) {
						score+=400;
					}else if (s2.ignition<0) {
						s.pushDown += planet.pushDown;
					}
				}
			}else {
				// �V���[�g���������e�I
				if (s2==null) {
					// �P���ɏ�ƃh�b�L���O
				}else {
					if (s2.ignition>0) {
						// �V���[�g��������
						m.setStack(s2);
						for (int sy=y; ; sy++) {
							if (sy==planet.height-1) {
								if (m.isMeteo()) {
									gotMeteo[m.getColor()]++;
									gotAllMeteo++;
									score+=20;
								}else if (m.isAsh()) {
									gotAsh++;
									score+=10;
								}
								m.setNone();
								break;
							}
							if (m.isNone()) break;
							m2 = field[x][sy+1];
							field[x][sy+1] = m;
							m = m2;
						}
						field[x][y] = m;
						return true;
					}else {
						if (s.difference>0&&s2.difference<0&&
							m.getColor()==m2.getColor()&&
							m.isMeteo()&&m2.isMeteo()) {
							// �V���[�g���E(�h�b�L���O����Ȃ��ď���)
							s2.velocity = s.velocity;
							m.setNone();
							m2.setNone();
							score+=300;
							return false;
						}
					}
				}
			}
			setStack(x, y+1, s);
			return true;
		}
	}

	// �J�^�}���֘A
	private Stack newStack()
	{
		Stack s = new Stack();
		stacks.add(s);
		return s;
	}
	private Stack newStack(Stack o)
	{
		Stack s = new Stack(o);
		stacks.add(s);
		return s;
	}
	private void setStack(int x, int y, Stack s)
	{
		Meteo m=field[x][y], m2;
		if (m.horizontal||m.vertical) return;
		Stack sOld = m.getStack();
		if (sOld==s) return;
		m.setStack(s);
		if (sOld!=null) {
			if (s!=null) {
				if (s.ignition<sOld.ignition) s.ignition = sOld.ignition;
				if (s.reduceTime<sOld.reduceTime) s.reduceTime = sOld.reduceTime;
			}
			if (y<planet.height-1&&!(m2=field[x][y+1]).isNone()&&m2.inStack(sOld)) {
				setStack(x, y+1, s);
			}
			if (y>0&&!(m2=field[x][y-1]).isNone()&&m2.inStack(sOld)) {
				setStack(x, y-1, s);
			}
			if (x<planet.width-1&&!(m2=field[x+1][y]).isNone()&&m2.inStack(sOld)) {
				setStack(x+1, y, s);
			}
			if (x>0&&!(m2=field[x-1][y]).isNone()&&m2.inStack(sOld)) {
				setStack(x-1, y, s);
			}
		}else {
			if (y<planet.height-1&&!(m2=field[x][y+1]).isNone()&&!m2.inStack()) {
				setStack(x, y+1, s);
			}
		}
	}
	private void reduce(int x, int y)
	{
		Meteo m=field[x][y], m2;
		Stack sOld = m.getStack();
		if (m.isAsh()) m.setMeteo(getSafeColor(x, y));
		m.setStack(null);
		if (y<planet.height-1&&!(m2=field[x][y+1]).isNone()&&m2.inStack(sOld)) {
			reduce(x, y+1);
		}
		if (y>0&&!(m2=field[x][y-1]).isNone()&&m2.inStack(sOld)) {
			reduce(x, y-1);
		}
		if (x<planet.width-1&&!(m2=field[x+1][y]).isNone()&&m2.inStack(sOld)) {
			reduce(x+1, y);
		}
		if (x>0&&!(m2=field[x-1][y]).isNone()&&m2.inStack(sOld)) {
			reduce(x-1, y);
		}
	}

	// �p�����[�^�v�Z
	private double getTimeParam(double base)
	{
		return base*Math.exp(Math.log(planet.timeRate)*time/(5*60*1000));
	}
	private double getTimeParam(double first, double last)
	{
		if (first<=0.0||last<=0.0) return 0.0;
		return first*Math.exp((Math.log(last)-Math.log(first))*time/(5*60*1000));
	}
	private double getLowestBonus(double bonus, int lowest, int total)
	{ return (bonus*lowest+total-lowest)/total; }
	private double getReduceTime()
	{ return getTimeParam(planet.reduceTime0, planet.reduceTime1); }
	private boolean accel()
	{ return timeAccel^reverseAccel; }
}
