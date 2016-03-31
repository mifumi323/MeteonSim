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
			g.drawString("本アプレットはメテオス(特にメテオスオンライン)に似せたゲームです。", 10, 20);
			g.drawString("メテオスの楽しさの体験、及び創作惑星で遊ぶことを目的としています。", 10, 40);
			g.drawString("続けるにはクリックしてください。", 10, 80);
			applet.endPaint();
		}
		return applet.getMousePushed(0)?new OptionScene():(Scene)this;
	}
}
