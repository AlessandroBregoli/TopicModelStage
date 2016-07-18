package it.unimib.disco.ab.textPreprocessing;

public class Sentence {
	public String text;
	public int articleID;
	
	@Override
	public Object clone(){
		Sentence st = new Sentence();
		st.articleID = this.articleID;
		st.text = new String(this.text);
		return st;
	}
}
