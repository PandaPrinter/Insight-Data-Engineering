import java.io.*;

public class Main {

	public static void main(String[] args) throws IOException {
		ParseTweets p = new ParseTweets();
		String input = "../tweet_input/" + args[0]; // input path
		String output = "../tweet_output/output.txt"; // output path
		PrintWriter printWriter = new PrintWriter(output);
		String line = null;
		try {
			FileReader fileReader = new FileReader(input);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
				p.extractInfo(line, printWriter);
			}
			bufferedReader.close();
			printWriter.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + input + "'");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
