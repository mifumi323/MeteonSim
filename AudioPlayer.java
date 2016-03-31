import java.applet.*;
import java.util.*;

class AudioPlayer implements Runnable
{
	HashSet playing = new HashSet();
	LinkedList waiting = new LinkedList();

	static final int PLAY=1;
	static final int LOOP=2;
	static final int STOP=3;

	public boolean isValid=true;

	class PlayMethod
	{
		public int method;
		public AudioClip clip;
		public PlayMethod(int method, AudioClip clip)
		{
			this.method=method;
			this.clip=clip;
		}
	}

	public void run()
	{
		while (isValid) {
			doWork();
			try { Thread.sleep(100); } catch (Exception e) {}
		}
		doEnd();
	}
	synchronized private void doWork()
	{
		while (!waiting.isEmpty()) {
			PlayMethod pm=(PlayMethod)waiting.removeFirst();
			AudioClip clip = pm.clip;
			switch(pm.method) {
			case PLAY: clip.play(); playing.add(clip); break;
			case LOOP: clip.loop(); playing.add(clip); break;
			case STOP: clip.stop(); playing.remove(clip); break;
			}
		}
	}
	synchronized private void doEnd()
	{
		Iterator it=playing.iterator();
		while (it.hasNext()) ((AudioClip)it.next()).stop();
		playing.clear();
		waiting.clear();
	}

	synchronized public void play(AudioClip clip)
	{
		if (clip==null) return;
		waiting.add(new PlayMethod(PLAY, clip));
	}
	synchronized public void loop(AudioClip clip)
	{
		if (clip==null) return;
		waiting.add(new PlayMethod(LOOP, clip));
	}
	synchronized public void stop(AudioClip clip)
	{
		if (clip==null) return;
		ListIterator li=waiting.listIterator();
		while (li.hasPrevious()) {
			PlayMethod pm=(PlayMethod)li.previous();
			if (pm.clip==clip) {
				if (pm.method!=STOP) li.remove();
				return;
			}
		}
		waiting.add(new PlayMethod(STOP, clip));
	}
	synchronized public void stopAll()
	{
		waiting.clear();
		Iterator it=playing.iterator();
		while (it.hasNext()) waiting.add(new PlayMethod(STOP, (AudioClip)it.next()));
	}
}
