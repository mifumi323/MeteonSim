// ���e�I
public class Meteo
{
	public static final int TYPE_NONE = 0;
	public static final int TYPE_METEO = 1;
	public static final int TYPE_ASH = 2;

	public static final int DIR_NONE = 0;
	public static final int DIR_UP = 1;
	public static final int DIR_DOWN = 2;
	public static final int DIR_LEFT = 3;
	public static final int DIR_RIGHT = 4;

	public static final String colorName[] = new String[] {
		"��",	"��",	"��",	"��n",	"�S",	"�d�C",
		"�A��",	"����",	"�X",	"��",	"��",	"��",
	};

	// ������́u��Ȃ��v�ϐ��Ȃ̂Ń����o�֐��o�R�ŃA�N�Z�X������
	private int color=0;
	private int type=TYPE_NONE;
	private Stack stack=null;
	private Effect effect = null;

	// ������͂ނ��냁�e�I�Ƃ͊֌W�Ȃ��p�����[�^�Ȃ̂łǂ��ł���������
	public int fastMove=DIR_NONE;
	public boolean horizontal = false;
	public boolean vertical = false;
	public double reduceTime = 0.0;

	public boolean isNone()
	{ return type==TYPE_NONE; }
	public boolean isMeteo()
	{ return type==TYPE_METEO; }
	public boolean isAsh()
	{ return type==TYPE_ASH; }
	public boolean isBlock()
	{ return type!=TYPE_NONE; }
	public boolean isGrabbable()
	{ return type!=TYPE_NONE&&(stack==null||stack.ignition>0); }
	public boolean inStack()
	{ return stack!=null; }
	public boolean inStack(Stack s)
	{ return stack==s; }
	public boolean inSameStack(Meteo m)
	{ return stack==m.getStack(); }
	public boolean isOnGround()
	{ return stack==null||stack.onGround; }

	public int getColor()
	{
		if (isNone()) throw new InternalError("�����Ȃ��Ƃ���ɐF�Ȃ�ĂȂ���I");
		return color;
	}
	public Stack getStack()
	{ return stack; }

	public void setNone()
	{ type = TYPE_NONE; stack = null; }
	public int setMeteo(int c)
	{ type = TYPE_METEO; return color=c; }
	public void setAsh(double r)
	{ type = TYPE_ASH; reduceTime = r; }
	public Stack setStack(Stack s)
	{
		if (type==TYPE_NONE) throw new InternalError("�����Ȃ��Ƃ���ɃJ�^�}���͂Ȃ���I");
		return stack=s;
	}

	public double getOffset()
	{ return stack!=null?stack.offset:0.0; }

	public void setEffect(Effect e)
	{ if ((effect=e)!=null) e.meteo=this; }
	public Effect getEffect()
	{ return effect; }
}
