package mailsender;

import sender.Sender;

public class Main {
	public static void main(String... args){
		Sender.send("821600682@qq.com", "myappKey", "subj001", "txt001");
	}

}
