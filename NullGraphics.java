import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.image.renderable.*;
import java.text.*;
import java.util.*;

public class NullGraphics extends Graphics2D
{
	public void clearRect(int x, int y, int width, int height) {}
	public void clipRect(int x, int y, int width, int height) {}
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {}
	public Graphics create() { return this; }
	public Graphics create(int x, int y, int width, int height) { return this; }
	public void dispose() {}
	public void draw3DRect(int x, int y, int width, int height, boolean raised) {}
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {}
	public void drawBytes(byte[] data, int offset, int length, int x, int y) {}
	public void drawChars(char[] data, int offset, int length, int x, int y) {}
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) { return true; }
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) { return true; }
	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) { return true; }
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) { return true; }
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) { return true; }
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) { return true; }
	public void drawLine(int x1, int y1, int x2, int y2) {}
	public void drawOval(int x, int y, int width, int height) {}
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {}
	public void drawPolygon(Polygon p) {}
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {}
	public void drawRect(int x, int y, int width, int height) {}
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {}
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {}
	public void drawString(String str, int x, int y) {}
	public void fill3DRect(int x, int y, int width, int height, boolean raised) {}
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {}
	public void fillOval(int x, int y, int width, int height) {}
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {}
	public void fillPolygon(Polygon p) {}
	public void fillRect(int x, int y, int width, int height) {}
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {}
	public void finalize() {}
	public Shape getClip() { return null; }
	public Rectangle getClipBounds() { return null; }
	public Rectangle getClipBounds(Rectangle r) { return r; }
	public Color getColor() { return new Color(0); }
	public Font getFont() { return new Font(null, Font.PLAIN, 10); }
	public FontMetrics getFontMetrics() { return null; }
	public FontMetrics getFontMetrics(Font f) { return null; }
	public boolean hitClip(int x, int y, int width, int height) { return false; }
	public void setClip(int x, int y, int width, int height) {}
	public void setClip(Shape clip) {}
	public void setColor(Color c) {}
	public void setFont(Font font) {}
	public void setPaintMode() {}
	public void setXORMode(Color c1) {}
	public String toString() { return ""; }
	public void translate(int x, int y) {}
	public void addRenderingHints(Map hints) {}
	public void clip(Shape s) {}
	public void draw(Shape s) {}
	public void drawGlyphVector(GlyphVector g, float x, float y) {}
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {}
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) { return true; }
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {}
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {}
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {}
	public void drawString(String s, float x, float y) {}
	public void fill(Shape s) {}
	public Color getBackground() { return new Color(0); }
	public Composite getComposite() { return AlphaComposite.Clear; }
	public GraphicsConfiguration getDeviceConfiguration() {return null; }
	public FontRenderContext getFontRenderContext() { return null; }
	public Paint getPaint() { return null; }
	public Object getRenderingHint(RenderingHints.Key hintKey) { return null; }
	public RenderingHints getRenderingHints() { return null; }
	public Stroke getStroke() { return null; }
	public AffineTransform getTransform() { return null; }
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) { return false; }
	public void rotate(double theta) {}
	public void rotate(double theta, double x, double y) {}
	public void scale(double sx, double sy) {}
	public void setBackground(Color color) {}
	public void setComposite(Composite comp) {}
	public void setPaint(Paint paint) {}
	public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {}
	public void setRenderingHints(Map hints) {}
	public void setStroke(Stroke s) {}
	public void setTransform(AffineTransform Tx) {}
	public void shear(double shx, double shy) {}
	public void transform(AffineTransform Tx) {}
	public void translate(double tx, double ty) {}
}
