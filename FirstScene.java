import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.imageio.event.*;
import java.applet.*;

public class FirstScene extends Scene
{
	boolean first=true;
	public Scene doWork()
	{
		if (first) {
			Graphics2D g = applet.beginPaint();
			g.drawString("�{�A�v���b�g�̓��e�I�X(���Ƀ��e�I�X�I�����C��)�Ɏ������Q�[���ł��B", 10, 20);
			g.drawString("���e�I�X�̊y�����̑̌��A�y�ёn��f���ŗV�Ԃ��Ƃ�ړI�Ƃ��Ă��܂��B", 10, 40);
			g.drawString("������ɂ̓N���b�N���Ă��������B", 10, 80);
			applet.endPaint();
		}
		return applet.getMousePushed(0)?new OptionScene():(Scene)this;
	}
}
