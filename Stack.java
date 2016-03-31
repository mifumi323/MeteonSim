// ÉJÉ^É}Éä
class Stack
{
	public Stack() {}
	public Stack(Stack s)
	{
		onGround=s.onGround;
		ignition=s.ignition;
		meteoCount=s.meteoCount;
		ashCount=s.ashCount;
		offset=s.offset;
		difference=s.difference;
		velocity=s.velocity;
		boostForce=s.boostForce;
		boostDuration=s.boostDuration;
	}

	public void stop()
	{ offset=difference=velocity=pushDown=0.0; }

	public boolean onGround=false;
	public int ignition=0;
	public int meteoCount=0;
	public int ashCount=0;

	public double offset=0.0;
	public double difference=0.0;
	public double velocity=0.0;
	public double boostForce=0.0;
	public double boostDuration=0.0;
	public double reduceTime=0.0;
	public double pushDown=0.0;
}
