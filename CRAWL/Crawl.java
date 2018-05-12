import java.io.*;
import java.net.*;
import java.util.regex.*;

public class Crawl {
	static String sendGet(String url) {
		String charset = "UTF-8"; 
		// 定义一个字符串用来存储网页内容
		String result = "";
		// 定义一个缓冲字符输入流
		BufferedReader in = null;

		try {
			// 将string转成url对象
			URL realUrl = new URL(url);
			// 初始化一个链接到那个url的连接
			URLConnection connection = realUrl.openConnection();
			// 开始实际的连接
			connection.connect();
			// 初始化 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), charset));
			// 用来临时存储抓取到的每一行的数据
			String line;
			while ((line = in.readLine()) != null) {
				// 遍历抓取到的每一行并将其存储到result里面
				result += line + "\n";
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	} 

//	String url = "https://bbs.pku.edu.cn/v2/home.php";
//	String url = "http://summer.pku.edu.cn/";
//	String url = "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/courseDetail/getCourseDetail.do?kclx=BK&course_seq_no=BZ1718200100883_16002";
//	String url = "https://www.baidu.com";
	
	static String RegexString(int opt, String targetStr, String patternStr) {
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(targetStr);
		String result = "";
		while (matcher.find())
		{
			if (opt == 0)
				result += matcher.group() + "\n";
			else
				result += matcher.group(1) + "\n";
		}
		if (result.equals(""))
			result = "无\n";
		return result;
	}
	
	static String GetDescription(String website)
	{
		String description = "中文课程简介：\n" + RegexString(1, website, "中文简介</th>\n					<td colspan = '9'><span>(.+?)</span>") + "\n";		
		return description;
	}
	
	static String GetDescriptionEng(String website)
	{
		String description = "英文课程简介：\n" + RegexString(1, website, "英文简介</th>\n					<td colspan = '9'><span>(.+?)</span>") + "\n";		
		return description;
	}
	
	static String GetScore(String website)
	{
		String score = "课程评分：\n" + RegexString(0, website, "学年度学期：(.+?)；") + "\n";
		return score;
	}
	
	static String GetTeacher(String website)
	{
		String teacher = "授课老师：\n" + RegexString(1, website, "<span>(.+?)：") + "\n";		
		return teacher;
	}
	
	static String GetField(String website)
	{
		String teacher = "通选课领域：\n" + RegexString(1, website, "通选课领域</th>\n					<td colspan = '9'><span>(.+?)</span></td>") + "\n";		
		return teacher;
	}
	
	public static void main(String[] args) throws IOException {
//		String url = "http://www.baidu.com";
		String url = "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/courseDetail/getCourseDetail.do?kclx=BK&course_seq_no=BZ1718201831330_19550";
		String website = sendGet(url);
//		FileWriter writer = new FileWriter("website.html", false);
//		writer.write(website);
//		writer.close();
//		String info = GetTeacher(website) + GetField(website) + GetDescription(website) + GetDescriptionEng(website) + GetScore(website);
		FileWriter writer = new FileWriter("info.txt", false);
		writer.write(GetTeacher(website) + "\n");
		writer.write(GetField(website) + "\n");
		writer.write(GetDescription(website) + "\n");
		writer.write(GetDescriptionEng(website) + "\n");
		writer.write(GetScore(website) + "\n");
		writer.close();
	}
}
