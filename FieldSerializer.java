// FieldSerializer
//  ������V���A���C�Y�E�A���V���A���C�Y�Ƃ�������
//  �N���X�̃����o�ϐ��𕶎���Ƃ��ĕۑ��ł���悤�ɂ�����A
//  ���̕����񂩂�N���X�̃����o�ϐ��𕜌�������ł��܂��B
//
//  �����o��������ȊO�̃N���X�E�z��ł���΂������
//  �ċA�I�ɃV���A���C�Y�ł��܂��B
//  ���͂ɑ����G���[�������Ă��\�Ȍ���ǂݍ��݂܂��B
//  �܂�A���g�������Ɍ��ď������Ă��Ȃ��̂�
//  �M�����ɂ͌����܂������̑���Ɉ����₷���ł��B
//
// ver1.0 �Ƃ肠�������J
//
// ��ҁF����(http://tgws.fromc.jp/ mifumi323@tgws.fromc.jp)

import java.lang.reflect.*;

class FieldSerializer
{
	// String���܂߂�Subprimitive�ƌĂԂ��Ƃɂ��悤
	public static final Class clsString="".getClass();
	static boolean isSubprimitive(Class c)
	{ return c.isPrimitive()||c==clsString; }

	// �V���A���C�Y
	public static String Serialize(Object in)
	{
		StringBuffer buf=new StringBuffer(4096);
		SerializeClass(in, buf, "");
		return buf.toString();
	}
	private static void SerializeClass(Object in, StringBuffer out, String start)
	{
		Field[] f = in.getClass().getFields();
		for (int i=0; i<f.length; i++) {
			try {
				Class c = f[i].getType();
				String name = f[i].getName();
				if (isSubprimitive(c)) {
					out.append(start);
					out.append(name);
					out.append("=");
					if (c==Boolean.TYPE) {
						out.append(f[i].getBoolean(in));
					}else if (c==Character.TYPE) {
						out.append(f[i].getChar(in));
					}else if (c==Byte.TYPE) {
						out.append(f[i].getByte(in));
					}else if (c==Short.TYPE) {
						out.append(f[i].getShort(in));
					}else if (c==Integer.TYPE) {
						out.append(f[i].getInt(in));
					}else if (c==Long.TYPE) {
						out.append(f[i].getLong(in));
					}else if (c==Float.TYPE) {
						out.append(f[i].getFloat(in));
					}else if (c==Double.TYPE) {
						out.append(f[i].getDouble(in));
					}else if (c.isInstance(start)) {
						out.append((String)f[i].get(in));
					}
					out.append(";");
				}else if (c.isArray()) {
					SerializeArray(f[i].get(in), out, start+name);
				}else {
					SerializeClass(f[i].get(in), out, start+name+".");
				}
			}catch (Exception e) {}
		}
	}
	private static void SerializeArray(Object in, StringBuffer out, String start)
	{
		int length = Array.getLength(in);
		for (int i=0; i<length; i++) {
			try {
				Class c = in.getClass().getComponentType();
				if (isSubprimitive(c)) {
					out.append(start);
					out.append("[");
					out.append(Integer.toString(i));
					out.append("]=");
					if (c==Boolean.TYPE) {
						out.append(Array.getBoolean(in, i));
					}else if (c==Character.TYPE) {
						out.append(Array.getChar(in, i));
					}else if (c==Byte.TYPE) {
						out.append(Array.getByte(in, i));
					}else if (c==Short.TYPE) {
						out.append(Array.getShort(in, i));
					}else if (c==Integer.TYPE) {
						out.append(Array.getInt(in, i));
					}else if (c==Long.TYPE) {
						out.append(Array.getLong(in, i));
					}else if (c==Float.TYPE) {
						out.append(Array.getFloat(in, i));
					}else if (c==Double.TYPE) {
						out.append(Array.getDouble(in, i));
					}else if (c.isInstance(start)) {
						out.append((String)Array.get(in, i));
					}
					out.append(";");
				}else if (c.isArray()) {
					SerializeArray(Array.get(in, i), out, start+"["+Integer.toString(i)+"]");
				}else {
					SerializeClass(Array.get(in, i), out, start+"["+Integer.toString(i)+"].");
				}
			}catch (Exception e) {}
		}
	}

	// ����
	public static void Unserialize(String in, Object out)
	{
		char chars[]=in.toCharArray();
		StringBuffer buf=new StringBuffer(4096);
		Object o=out;
		Class c=null;
		Field f=null;
		int index=0;
		boolean isValue=false;
		boolean isArray=false;
		boolean isError=false;
		for (int i=0; i<chars.length; i++) {
			try {
				// �����ȋL�@�����邯�ǎ��͍l�����ĂȂ�
				char ch=chars[i];
				if ((!isValue||buf.length()<=0)&&(ch=='.'||ch=='['||ch==']'||ch=='=')) {
					if (isError) {
						buf.setLength(0);
					}else if (buf.length()>0) {
						c = o.getClass();
						if (c.isArray()) {
							index = Integer.parseInt(buf.toString());
							if (isSubprimitive(c.getComponentType())) {
								isValue = true;
								isArray = true;
							}else {
								o = Array.get(o, index);
							}
						}else {
							f = c.getField(buf.toString());
							if (!(isValue=isSubprimitive(f.getType()))) o = f.get(o);
						}
						buf.setLength(0);
					}
				}else if (ch=='\r'||ch=='\n'||ch==';') {
					if (isError) {
						buf.setLength(0);
					}else if (isValue&&buf.length()>0) {
						if (isArray) {
							c = c.getComponentType();
							if (c==Boolean.TYPE) {
								Array.setBoolean(o, index, "true".equalsIgnoreCase(buf.toString()));
							}else if (c==Character.TYPE) {
								Array.setChar(o, index, buf.charAt(0));
							}else if (c==Byte.TYPE) {
								Array.setByte(o, index, Byte.parseByte(buf.toString()));
							}else if (c==Short.TYPE) {
								Array.setShort(o, index, Short.parseShort(buf.toString()));
							}else if (c==Integer.TYPE) {
								Array.setInt(o, index, Integer.parseInt(buf.toString()));
							}else if (c==Long.TYPE) {
								Array.setLong(o, index, Long.parseLong(buf.toString()));
							}else if (c==Float.TYPE) {
								Array.setFloat(o, index, Float.parseFloat(buf.toString()));
							}else if (c==Double.TYPE) {
								Array.setDouble(o, index, Double.parseDouble(buf.toString()));
							}else if (c.isInstance(in)) {
								Array.set(o, index, buf.toString());
							}
						}else {
							c = f.getType();
							if (c==Boolean.TYPE) {
								f.setBoolean(o, "true".equalsIgnoreCase(buf.toString()));
							}else if (c==Character.TYPE) {
								f.setChar(o, buf.charAt(0));
							}else if (c==Byte.TYPE) {
								f.setByte(o, Byte.parseByte(buf.toString()));
							}else if (c==Short.TYPE) {
								f.setShort(o, Short.parseShort(buf.toString()));
							}else if (c==Integer.TYPE) {
								f.setInt(o, Integer.parseInt(buf.toString()));
							}else if (c==Long.TYPE) {
								f.setLong(o, Long.parseLong(buf.toString()));
							}else if (c==Float.TYPE) {
								f.setFloat(o, Float.parseFloat(buf.toString()));
							}else if (c==Double.TYPE) {
								f.setDouble(o, Double.parseDouble(buf.toString()));
							}else if (c.isInstance(in)) {
								f.set(o, buf.toString());
							}
						}
						buf.setLength(0);
					}
					o = out;
					isValue = false;
					isArray = false;
					isError = false;
				}else {
					buf.append(ch);
				}
			}catch (Exception e) {
				isError = true;
			}
		}
	}
}
