package dual.samurai;

public enum Language {

	Japanese,
	English;

	public static boolean isJp(Language language){
		return language == Language.Japanese;
	}
}
