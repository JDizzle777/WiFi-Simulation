public enum Websites {
	facebook,
	google,
	youtube,
	yahoo,
	amazon,
	cnn,
	msnbc,
	reddit,
	ebay,
	wikipedia;
	
	public static Websites getRandom(){
		return values()[(int) (Math.random() * values().length)];
	}
}
