package crawl;

import java.io.*;
import java.util.regex.*;

public class Crawl2 {
	
	static int num = 0;
	
	public static String readToString(String fileName) {  
        String encoding = "UTF-8";  
        File file = new File(fileName);  
        Long filelength = file.length();  
        byte[] filecontent = new byte[filelength.intValue()];  
        try {  
            FileInputStream in = new FileInputStream(file);  
            in.read(filecontent);  
            in.close();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        try {  
            return new String(filecontent, encoding);  
        } catch (UnsupportedEncodingException e) {  
            System.err.println("The OS does not support " + encoding);  
            e.printStackTrace();  
            return null;  
        }  
    }  
	
	static String RegexString(int opt, String targetStr, String patternStr) {
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(targetStr);
		String result = "";
		while (matcher.find())
		{
			++num;
			if (opt == 0)
				result += matcher.group() + "\n";
			else
				result += matcher.group(1) + "\n";
		}
		if (result.equals(""))
			result = "��\n";
		return result;
	}
	
	static String GetNames(String website)
	{
		String score = RegexString(1, website, "style=\"width: 80\"><span>(.+?)</span></a></td>");
		return score;
	}
	
	static String GetSeqNo(String website)
	{
		String score = RegexString(1, website, "course_seq_no=(.+?)\" ");
		return score;
	}

	static String GetTea(String website)
	{
		String score = RegexString(1, website, "<td class=\"datagrid\"><span style=\"width: 40%\">(.+?)</span></td>");
		return score;
	}

	public static void main(String[] args) throws IOException {
		String website = readToString("src/crawl/PageWeb.txt");
		FileWriter writer = new FileWriter("src/crawl/PageWebInfo.txt", false);
		String names = GetNames(website);
		writer.write(num + "" + "\n");
		writer.write(names);
		writer.write(GetTea(website));
		writer.write(GetSeqNo(website));
		writer.close();
	}
}
