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
	Button button = new Button("�J�n");

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
				throw new Exception(name+"�̒l���ǂݎ��܂���");
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
			p = createTab("�S�ʐݒ�");
			newItem("�f����", p, planet, "width");
			newItem("�f������", p, planet, "height");
			newItem("����HP", p, planet, "maxHP");
			newItem("�������e�I�z�u���x", p, planet, "initialVelocity");
			p = createTab("�������e�I");
			newItem("���e�I�����p�x", p, planet, "frequency");
			newItem("���e�I�~�������x", p, planet, "fallVelocity");
			newItem("���e�I�~�������x", p, planet, "fallGravity");
			newItem("���e�I�~�����x���~�b�^�[", p, planet, "meteoFallLimit");
			newItem("�����{��(�T���o��)", p, planet, "timeRate");
			newItem("�����{��(�A�N�Z��)", p, planet, "accelRate");
			newItem("����������", p, planet, "pushDown");
			newItem("�����������x", p, planet, "pushDownVelocity");
			p = createTab("�J�^�}��");
			newItem("�㏸���x���~�b�^�[", p, planet, "boostLimit");
			newItem("�������x���~�b�^�[", p, planet, "stackFallLimit");
			newItem("�������x���~�b�^�[(�A�N�Z��)", p, planet, "stackFallLimitT");
			newItem("�d�͉����x", p, planet, "stackGravity");
			newItem("�Ҍ�����(�J�n��)", p, planet, "reduceTime0");
			newItem("�Ҍ�����(�T����)", p, planet, "reduceTime1");
			newItem("���e�I�̏d��", p, planet, "meteoWeight");
			newItem("�R���J�X�̏d��", p, planet, "ashWeight");
			newItem("�����_�΃{�[�i�X", p, planet, "sameTimeIgnition");
			newItem("�ŉ����_�΃{�[�i�X", p, planet, "lowestIgnition");
			p = createTab("�V���[�g");
			newItem("�V���[�g�����x", p, planet.shoot, "initialVelocity");
			newItem("�V���[�g�p�����i��", p, planet.shoot, "boostForce");
			newItem("�V���[�g�㏸����", p, planet.shoot, "boostDuration");
			newItem("�V���[�g�������x���~�b�^�[", p, planet, "shootFallLimit");
			newItem("�V���[�g���������x", p, planet, "shootGravity");
			p = createTab("���e�I�~������");
			for (i=0; i<Meteo.colorName.length; i++) {
				newItem(Meteo.colorName[i], p, planet.rate, i);
			}
			p = createTab("�ł������p�����[�^");
			p.setLayout(new GridLayout(0, 7));
			p.add(new Panel());
			p.add(new Panel());
			p.add(new Label("���_��", Label.CENTER));
			p.add(new Panel());
			p.add(new Panel());
			p.add(new Label("�c�_��", Label.CENTER));
			p.add(new Panel());
			p.add(new Panel());
			p.add(new Label("�����x", Label.CENTER));
			p.add(new Label("�p�����i��", Label.CENTER));
			p.add(new Label("�㏸����", Label.CENTER));
			p.add(new Label("�����x", Label.CENTER));
			p.add(new Label("�p�����i��", Label.CENTER));
			p.add(new Label("�㏸����", Label.CENTER));
			for (i=0; i<10; i++) {
				String s = Integer.toString(i+1);
				p.add(new Label(s+"���_��", Label.RIGHT));
				newItem(s+"�����_�Ώ����x", p, planet.horizontal[i], "initialVelocity", false);
				newItem(s+"�����_�Όp�����i��", p, planet.horizontal[i], "boostForce", false);
				newItem(s+"�����_�Ώ㏸����", p, planet.horizontal[i], "boostDuration", false);
				newItem(s+"���c�_�Ώ����x", p, planet.vertical[i], "initialVelocity", false);
				newItem(s+"���c�_�Όp�����i��", p, planet.vertical[i], "boostForce", false);
				newItem(s+"���c�_�Ώ㏸����", p, planet.vertical[i], "boostDuration", false);
			}
			p = createTab("�F");
			p.setLayout(new GridLayout(0, 5));
			p.add(new Panel());
			p.add(new Label("�Ԑ���(R)", Label.CENTER));
			p.add(new Label("�ΐ���(G)", Label.CENTER));
			p.add(new Label("����(B)", Label.CENTER));
			p.add(new Label("�A���t�@(A)", Label.CENTER));
			p.add(new Label("�J�[�\��", Label.RIGHT));
			newItem("�J�[�\���F�̐Ԑ���", p, planet.activeColor, "r", false);
			newItem("�J�[�\���F�̗ΐ���", p, planet.activeColor, "g", false);
			newItem("�J�[�\���F�̐���", p, planet.activeColor, "b", false);
			newItem("�J�[�\���F�̃A���t�@", p, planet.activeColor, "a", false);
			p.add(new Label("�ړ��\�͈�", Label.RIGHT));
			newItem("�ړ��\�͈͐F�̐Ԑ���", p, planet.movableColor, "r", false);
			newItem("�ړ��\�͈͐F�̗ΐ���", p, planet.movableColor, "g", false);
			newItem("�ړ��\�͈͐F�̐���", p, planet.movableColor, "b", false);
			newItem("�ړ��\�͈͐F�̃A���t�@", p, planet.movableColor, "a", false);
			p.add(new Label("����", Label.RIGHT));
			newItem("�����F�̐Ԑ���", p, planet.textColor, "r", false);
			newItem("�����F�̗ΐ���", p, planet.textColor, "g", false);
			newItem("�����F�̐���", p, planet.textColor, "b", false);
			newItem("�����F�̃A���t�@", p, planet.textColor, "a", false);
			p.add(new Label("�����̔w�i", Label.RIGHT));
			newItem("�w�i�F�̐Ԑ���", p, planet.backColor, "r", false);
			newItem("�w�i�F�̗ΐ���", p, planet.backColor, "g", false);
			newItem("�w�i�F�̐���", p, planet.backColor, "b", false);
			newItem("�w�i�F�̃A���t�@", p, planet.backColor, "a", false);
			p.add(new Label("�g��", Label.RIGHT));
			newItem("�g���F�̐Ԑ���", p, planet.gridColor, "r", false);
			newItem("�g���F�̗ΐ���", p, planet.gridColor, "g", false);
			newItem("�g���F�̐���", p, planet.gridColor, "b", false);
			newItem("�g���F�̃A���t�@", p, planet.gridColor, "a", false);
			p.add(new Label("��C��", Label.RIGHT));
			newItem("��C���F�̐Ԑ���", p, planet.limitColor, "r", false);
			newItem("��C���F�̗ΐ���", p, planet.limitColor, "g", false);
			newItem("��C���F�̐���", p, planet.limitColor, "b", false);
			newItem("��C���F�̃A���t�@", p, planet.limitColor, "a", false);
			p.add(new Label("���e�I", Label.RIGHT));
			newItem("���e�I�F�̐Ԑ���", p, planet.meteoColor, "r", false);
			newItem("���e�I�F�̗ΐ���", p, planet.meteoColor, "g", false);
			newItem("���e�I�F�̐���", p, planet.meteoColor, "b", false);
			newItem("���e�I�F�̃A���t�@", p, planet.meteoColor, "a", false);
			p.add(new Label("HP(�ő�)", Label.RIGHT));
			newItem("HP(�ő�)�F�̐Ԑ���", p, planet.hpmaxColor, "r", false);
			newItem("HP(�ő�)�F�̗ΐ���", p, planet.hpmaxColor, "g", false);
			newItem("HP(�ő�)�F�̐���", p, planet.hpmaxColor, "b", false);
			newItem("HP(�ő�)�F�̃A���t�@", p, planet.hpmaxColor, "a", false);
			p.add(new Label("HP(�ŏ�)", Label.RIGHT));
			newItem("HP(�ŏ�)�F�̐Ԑ���", p, planet.hpminColor, "r", false);
			newItem("HP(�ŏ�)�F�̗ΐ���", p, planet.hpminColor, "g", false);
			newItem("HP(�ŏ�)�F�̐���", p, planet.hpminColor, "b", false);
			newItem("HP(�ŏ�)�F�̃A���t�@", p, planet.hpminColor, "a", false);
			p.add(new Label("HP�g", Label.RIGHT));
			newItem("HP�g�F�̐Ԑ���", p, planet.frameColor, "r", false);
			newItem("HP�g�F�̗ΐ���", p, planet.frameColor, "g", false);
			newItem("HP�g�F�̐���", p, planet.frameColor, "b", false);
			newItem("HP�g�F�̃A���t�@", p, planet.frameColor, "a", false);
			p = createTab("�摜�t�@�C��");
			newItem("���e�I", p, planet, "meteo");
			newItem("�w�i", p, planet, "back");
			p = createTab("BGM���_�Ή�");
			p.setLayout(new GridLayout(0, 4));
			p.add(new Panel());
			p.add(new Label("���e�I�������", Label.CENTER));
			p.add(new Label("���e�I��������", Label.CENTER));
			p.add(new Label("���e�I�����ς�", Label.CENTER));
			p.add(new Label("BGM", Label.RIGHT));
			newItem("���e�I������Ƃ�BGM", p, planet.bgm, 0, false);
			newItem("���e�I����������BGM", p, planet.bgm, 1, false);
			newItem("���e�I�����ς���BGM", p, planet.bgm, 2, false);
			p.add(new Label("�c�_��ME", Label.RIGHT));
			newItem("���e�I������Ƃ̏c�_��ME", p, planet.fanfare[0], 0, false);
			newItem("���e�I���������̏c�_��ME", p, planet.fanfare[0], 1, false);
			newItem("���e�I�����ς��̏c�_��ME", p, planet.fanfare[0], 2, false);
			for (i=0; i<4; i++) {
				String s = Integer.toString(i+1);
				p.add(new Label(s+"���_��ME", Label.RIGHT));
				newItem("���e�I������Ƃ�"+s+"���_��ME", p, planet.fanfare[i+1], 0, false);
				newItem("���e�I����������"+s+"���_��ME", p, planet.fanfare[i+1], 1, false);
				newItem("���e�I�����ς���"+s+"���_��ME", p, planet.fanfare[i+1], 2, false);
			}
			p = createTab("���̑�����");
			newItem("����ME", p, planet, "win");
			newItem("�ŖSME", p, planet, "lose");
			p = createTab("�o�b�N�A�b�v");
			p.setLayout(new GridLayout(0, 1));
			p.add(backup=new TextArea(applet.getPlanetString(), 10, 10, TextArea.SCROLLBARS_VERTICAL_ONLY));
			backup.setEditable(false);
			backup.addFocusListener(this);
		}
		if (applet.option.changerule) {
			p = createTab("���[��");
			newItem("�c�ړ�", p, rule, "vertical");
			newItem("���ړ�", p, rule, "horizontal");
			newItem("�V���[�g", p, rule, "shoot");
//			newItem("�X���C�h", p, rule, "slide");
			newItem("�����ςݏグ����", p, rule, "initialHeight");
			p = createTab("��������");
			newItem("�ڕW���_", p, rule, "score");
			newItem("�ڕW����(�b)", p, rule, "seconds");
			newItem("�ڕW���e�I��(���v)", p, rule, "gotAllMeteo");
			for (i=0; i<Meteo.colorName.length; i++) {
				newItem("�ڕW���e�I��("+Meteo.colorName[i]+")", p, rule.gotMeteo, i);
			}
			newItem("�ڕW���R���J�X��", p, rule, "gotAsh");
			newItem("�ڕW���S������", p, rule, "clearCount");
		}
		p = createTab("�O���t�B�b�N�I�v�V����");
		newItem("����������", p, option, "alpha");
		newItem("���i�ʂȊg��k��", p, option, "interpolation");
		newItem("���i�ʂȊg��k��(�A���t�@)", p, option, "alphaInterpolation");
		newItem("�摜�ɃA���`�G�C���A�X", p, option, "antialiasing");
		newItem("�e�L�X�g�ɃA���`�G�C���A�X", p, option, "textAntialiasing");
		newItem("���D��̃����_�����O", p, option, "rendering");
		newItem("���D��̃J���[�����_�����O", p, option, "colorRendering");
		newItem("�f�B�U�����O", p, option, "dithering");
		newItem("�t�H���g�������g���b�N�X", p, option, "fractionalmetrics");
		p = createTab("�T�E���h�I�v�V����");
		newItem("AU�`���̃T�E���h���Đ�", p, option, "au");
		newItem("Wave�`���̃T�E���h���Đ�", p, option, "wave");
		newItem("MIDI�`���̃T�E���h���Đ�", p, option, "midi");
		p = createTab("���e�I�����ǂ��ɂ���");
		TextArea ta;
		p.setLayout(new GridLayout(0, 1));
		p.add(ta=new TextArea(
			"���e�I�����ǂ��͔���(mifumi323@tgws.fromc.jp http://tgws.fromc.jp/)�����e�I�X�V���[�Y�̃��e�I�X�I�����C���Ɏ����č���Ă���Java�Q�[���ł��B\n���e�I�X����у��e�I�X�I�����C���̒��쌠��Q�G���^�[�e�C�������g(http://www.qentertainment.com/)�ɂ���܂��B",
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

	// �ɂ�`�����Ă�
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

	// ���̓t�H�[���Q�b�g
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
			if (planet.width<1) error("����1�ȏ�K�v�ł�");
			if (planet.height<3) error("������3�ȏ�K�v�ł�");
			if (planet.maxHP<=0.0) error("����HP��0.0���傫���Ȃ��Ă͂����܂���");
			if (planet.initialVelocity<=0.0) error("�������e�I�z�u���x��0.0���傫���Ȃ��Ă͂����܂���");
			int i, j=0;
			for (i=0; i<Meteo.colorName.length; i++) {
				if (planet.rate[i]>0) j++;
			}
			if (j<5) error("���e�I��5��ވȏ�K�v�ł�");
			if (rule.initialHeight<0) error("�����ςݏグ������0�ȏ�K�v�ł�");
			if (planet.height<rule.initialHeight) error("�����ςݏグ�����͘f���̍����܂łł�");
		} catch (Exception ex) {
			error(ex.getMessage());
		}
	}
	void error(String s)
	{
		if (last) {
			button.setLabel("�J�n("+s+")");
			last = false;
		}
	}
}
