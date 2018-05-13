
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;
import image_processing.trycaptcha;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class WebConnect {

    private static final int CONNECT_LIMIT_TIME = 15000;
    private static final String LoginPage =
            "https://iaaa.pku.edu.cn/iaaa/oauthlogin.do";
    private static final String RedirPage =
            "http://elective.pku.edu.cn/elective2008/ssoLogin.do?";
    private static final String HelperControlPage =
            "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/help/HelpController.jpf";
    private static final String SupplyOnlyPage =
            "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/supplement/SupplyOnly.do";
    private static final String SupplyCancelPage =
            "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/supplement/SupplyCancel.do";
    private static final String ElectiveWorkControllerPage =
            "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/electiveWork/ElectiveWorkController.jpf";
    private static final String CurriculmFormPage =
            "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/courseQuery/getCurriculmByForm.do";
    private static final String freshPage =
            "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/supplement/refreshLimit.do";
    private static final String validtePage =
            "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/supplement/validate.do";

    private String cookieKey = "";
    private Vector<String> websiteQue = new Vector<String>();

    public static void displayConn(HttpURLConnection conn) throws Exception{
        BufferedReader buffR = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
        String line;
        while ((line = buffR.readLine()) != null)
            System.out.println(new String(line.getBytes()));
        buffR.close();
    }

    public String websiteScanning(HttpURLConnection conn) throws Exception{
        BufferedReader buffR = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
        String line;
        String nextPage = "";
        while ((line = buffR.readLine()) != null) {
            if (line.contains("course_seq_no")) {
                String website = line.substring(line.indexOf("elective"), line.indexOf("target") - 2);
                websiteQue.add(website);
                //System.out.println(website);
                //System.exit(0);
            }
            if (line.contains("Next")) {
                line = line.substring(line.indexOf("Previous"), line.indexOf("Next") - 2);
                if (line.contains("elective"))
                    nextPage = line.substring(line.indexOf("elective"), line.length());
            }
        }
        buffR.close();
        return nextPage;
    }

    public static void displayConnToFile(HttpURLConnection conn, String file) throws Exception{
        BufferedReader buffR = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
        String line;

        FileOutputStream fos=new FileOutputStream(file);
        OutputStreamWriter osw=new OutputStreamWriter(fos,"GBK");
        PrintWriter writer = new PrintWriter(osw, true);
        while ((line = buffR.readLine()) != null)
            writer.println(new String(line.getBytes()));
        writer.close();

        buffR.close();
    }

    public static void displayConnNoPrint(HttpURLConnection conn) throws Exception{
        BufferedReader buffR = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
        String line;
        int i = 0;
        while ((line = buffR.readLine()) != null)
            ++i;
            /// /System.out.println(new String(line.getBytes()));
        buffR.close();
    }

    public HttpURLConnection GetConn(String WebPage, String cookie, String method, boolean redirIf) throws Exception{
        URL url = new URL(WebPage);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();//建立链接

        if (cookie != "") {
            connection.setRequestProperty("Cookie", cookie);
        }
        if (!redirIf) connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("Connection","keep-alive");

        connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.117 Safari/537.36");
        connection.setRequestProperty("Pragma","no-cache");
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        //connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setReadTimeout(CONNECT_LIMIT_TIME);
        connection.setConnectTimeout(CONNECT_LIMIT_TIME);
        connection.setRequestMethod(method);
        //if(WebPage == SupplyOnlyPage) System.out.println("here work");

        //displayConn(connection);
        //String str = connection.getHeaderField("location");
        //System.out.println(str);
        return connection;
    }

    public boolean Login(String userName, String passWord) throws Exception{
        //Get(LoginPage, "", true);
        String outputStr = "appid=syllabus"+"&userName="+userName+"&password="+passWord+"&randCode=验证码"+
                "&smsCode=短信验证码"+"&otpCode=动态口令"+"&redirUrl=http://elective.pku.edu.cn:80/elective2008/agent4Iaaa.jsp/../ssoLogin.do";
        //System.out.println(md5Encode(outputStr));
        //System.out.println(Math.random());

        HttpURLConnection conn = GetConn(LoginPage, "", "POST", false);

        conn.connect();
        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(),"utf-8");
        // outputStr = "userName=xxx&password=xxx"
        out.write(outputStr);
        out.flush();
        out.close();
        BufferedReader buffR = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
        String line = buffR.readLine();
        //System.out.println(line);
        if (line.contains("false")) {
            return false;
        }
        String tokenGet = line.substring(line.indexOf("token")+8, line.length()-2);
        //System.out.println(tokenGet);
        String cookieGet = conn.getHeaderField("Set-Cookie");
        //System.out.println(cookieGet);
        conn.disconnect();


        String reDirPageFull = RedirPage+"?rand="+String.valueOf(Math.random())+"&token="+tokenGet;

        conn = GetConn(reDirPageFull, cookieGet, "POST", false);
        conn.connect();
        //displayConn(conn);
        //for (int i = 1; i <15; i++){
        //    System.out.println("name " + conn.getHeaderFieldKey(i));
        //    System.out.println(conn.getHeaderField(conn.getHeaderFieldKey(i)));
        //}
        cookieGet = conn.getHeaderField("Set-Cookie");
        conn.disconnect();

        conn = GetConn(HelperControlPage, cookieGet, "POST", true);
        conn.connect();
        displayConnNoPrint(conn);
        conn.disconnect();

        cookieKey = cookieGet;
        //System.out.println(cookieKey);


        //conn = GetConn(ElectiveWorkControllerPage, cookieGet, "GET", false);
        //
        //conn.setRequestProperty("Referer", "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/help/HelpController.jpf");
        //conn.setRequestProperty("Host", "http://elective.pku.edu.cn");
        //conn.setRequestProperty("Accept-Encoding","gzip, deflate");
        //conn.setRequestProperty("Upgrade-Insecure-Requests","1");
        //conn.connect();
        //displayConn(conn);
        //cookieGet = conn.getHeaderField("Set-Cookie");
        //conn.disconnect();

        /*
        conn = GetConn(ElectiveWorkControllerPage, cookieGet, "GET", true);
        conn.setRequestProperty("Referer", "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/help/HelpController.jpf");
        conn.setRequestProperty("Host", "http://elective.pku.edu.cn");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");
        conn.setRequestProperty("Accept-Language","zh-CN,zh;q=0.9,en;q=0.8,nl;q=0.7");
        */
        //conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");

        //conn.connect();
        //displayConn(conn);
        //conn.disconnect();


        //Get(SupplyOnlyPage, cookieGet, true);
        return true;
    }

    private void printWebsideQue() {
        Iterator it = websiteQue.iterator();
        while(it.hasNext()) {
            System.out.println(it.next());
        }
    }

    public void Crawl2getWeb() throws Exception{
        websiteQue.clear();
        HttpURLConnection conn;

        // ----------------------   crawl education_plan_bk

        System.out.println("education_plan_bk crawling");
        conn = GetConn(CurriculmFormPage, cookieKey, "GET", false);
        conn.setRequestProperty("Referer", "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/help/HelpController.jpf");
        conn.setRequestProperty("Host", "http://elective.pku.edu.cn");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");
        conn.connect();

        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(),"utf-8");
        String requestStr = "wlw-radio_button_group_key%3A%7BactionForm.courseSettingType%7D=education_plan_bk"
                +"&%7BactionForm.courseID%7D="
                +"&%7BactionForm.courseName%7D="
                +"&wlw-select_key%3A%7BactionForm.deptID%7DOldValue=true"
                //+"&wlw-select_key%3A%7BactionForm.deptID%7D=048"
                +"&wlw-select_key%3A%7BactionForm.courseDay%7DOldValue=true"
                +"&wlw-select_key%3A%7BactionForm.courseDay%7D="
                +"&wlw-select_key%3A%7BactionForm.courseTime%7DOldValue=true"
                +"&wlw-select_key%3A%7BactionForm.courseTime%7D="
                +"&wlw-checkbox_key%3A%7BactionForm.queryDateFlag%7DOldValue=false"
                //+"&deptIdHide=048"
                ;
        out.write(requestStr);
        out.flush();
        out.close();

        String nextPage = "";
        String prePage = CurriculmFormPage;
        while((nextPage = websiteScanning(conn)) != "") {
            conn.disconnect();
            nextPage = "http://elective.pku.edu.cn/"+nextPage;
            nextPage = nextPage.replace("amp;", "");

            conn = WebIterator(prePage, nextPage);
            prePage = nextPage;
        }
        //printWebsideQue();
        conn.disconnect();

        // ----------------------   crawl speciality

        System.out.println("speciality crawling");
        conn = GetConn(CurriculmFormPage, cookieKey, "GET", false);
        conn.setRequestProperty("Referer", "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/help/HelpController.jpf");
        conn.setRequestProperty("Host", "http://elective.pku.edu.cn");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");
        conn.connect();

        out = new OutputStreamWriter(conn.getOutputStream(),"utf-8");
        requestStr = "wlw-radio_button_group_key%3A%7BactionForm.courseSettingType%7D=speciality"
                +"&%7BactionForm.courseID%7D="
                +"&%7BactionForm.courseName%7D="
                +"&wlw-select_key%3A%7BactionForm.deptID%7DOldValue=true"
                //+"&wlw-select_key%3A%7BactionForm.deptID%7D=048"
                +"&wlw-select_key%3A%7BactionForm.courseDay%7DOldValue=true"
                +"&wlw-select_key%3A%7BactionForm.courseDay%7D="
                +"&wlw-select_key%3A%7BactionForm.courseTime%7DOldValue=true"
                +"&wlw-select_key%3A%7BactionForm.courseTime%7D="
                +"&wlw-checkbox_key%3A%7BactionForm.queryDateFlag%7DOldValue=false"
                //+"&deptIdHide=048"
        ;
        out.write(requestStr);
        out.flush();
        out.close();

        prePage = CurriculmFormPage;
        while((nextPage = websiteScanning(conn)) != "") {
            conn.disconnect();
            nextPage = "http://elective.pku.edu.cn/"+nextPage;
            nextPage = nextPage.replace("amp;", "");

            conn = WebIterator(prePage, nextPage);
            prePage = nextPage;
        }
        //printWebsideQue();
        conn.disconnect();

        //----------------------- crawl politics

        System.out.println("politics crawling");
        conn = GetConn(CurriculmFormPage, cookieKey, "GET", false);
        conn.setRequestProperty("Referer", "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/help/HelpController.jpf");
        conn.setRequestProperty("Host", "http://elective.pku.edu.cn");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");
        conn.connect();
        out = new OutputStreamWriter(conn.getOutputStream(),"utf-8");
        requestStr = "wlw-radio_button_group_key%3A%7BactionForm.courseSettingType%7D=politics"
                +"&%7BactionForm.courseID%7D="
                +"&%7BactionForm.courseName%7D="
                +"&wlw-select_key%3A%7BactionForm.deptID%7DOldValue=true"
                //+"&wlw-select_key%3A%7BactionForm.deptID%7D=048"
                +"&wlw-select_key%3A%7BactionForm.courseDay%7DOldValue=true"
                +"&wlw-select_key%3A%7BactionForm.courseDay%7D="
                +"&wlw-select_key%3A%7BactionForm.courseTime%7DOldValue=true"
                +"&wlw-select_key%3A%7BactionForm.courseTime%7D="
                +"&wlw-checkbox_key%3A%7BactionForm.queryDateFlag%7DOldValue=false"
        //+"&deptIdHide=048"
        ;
        out.write(requestStr);
        out.flush();
        out.close();
        //displayConnToFile(conn, "nopage.txt");
        prePage = CurriculmFormPage;
        while((nextPage = websiteScanning(conn)) != "") {
            conn.disconnect();
            nextPage = "http://elective.pku.edu.cn/"+nextPage;
            nextPage = nextPage.replace("amp;", "");

            conn = WebIterator(prePage, nextPage);
            prePage = nextPage;
        }
        //displayConn(conn);
        //printWebsideQue();

        //System.out.println("work here");
        //out = new OutputStreamWriter(conn.getOutputStream(),"utf-8");

        conn.disconnect();

        //----------------------- crawl english

        System.out.println("english crawling");
        conn = GetConn(CurriculmFormPage, cookieKey, "GET", false);
        conn.setRequestProperty("Referer", "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/help/HelpController.jpf");
        conn.setRequestProperty("Host", "http://elective.pku.edu.cn");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");
        conn.connect();
        out = new OutputStreamWriter(conn.getOutputStream(),"utf-8");
        requestStr = "wlw-radio_button_group_key%3A%7BactionForm.courseSettingType%7D=english"
                +"&%7BactionForm.courseID%7D="
                +"&%7BactionForm.courseName%7D="
                +"&wlw-select_key%3A%7BactionForm.deptID%7DOldValue=true"
                +"&wlw-select_key%3A%7BactionForm.deptID%7D="
                +"&wlw-select_key%3A%7BactionForm.courseDay%7DOldValue=true"
                +"&wlw-select_key%3A%7BactionForm.courseDay%7D="
                +"&wlw-select_key%3A%7BactionForm.courseTime%7DOldValue=true"
                +"&wlw-select_key%3A%7BactionForm.courseTime%7D="
                +"&wlw-checkbox_key%3A%7BactionForm.queryDateFlag%7DOldValue=false"
                +"&deptIdHide="
        ;
        out.write(requestStr);
        out.flush();
        out.close();

        prePage = CurriculmFormPage;
        while((nextPage = websiteScanning(conn)) != "") {
            conn.disconnect();
            nextPage = "http://elective.pku.edu.cn/"+nextPage;
            nextPage = nextPage.replace("amp;", "");

            conn = WebIterator(prePage, nextPage);
            prePage = nextPage;
        }

        conn.disconnect();



        //----------------------- crawl gym


        System.out.println("gym crawling");
        conn = GetConn(CurriculmFormPage, cookieKey, "GET", false);
        conn.setRequestProperty("Referer", "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/help/HelpController.jpf");
        conn.setRequestProperty("Host", "http://elective.pku.edu.cn");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");
        conn.connect();
        out = new OutputStreamWriter(conn.getOutputStream(),"utf-8");
        requestStr = "wlw-radio_button_group_key%3A%7BactionForm.courseSettingType%7D=gym"
                +"&%7BactionForm.courseID%7D="
                +"&%7BactionForm.courseName%7D="
                +"&wlw-select_key%3A%7BactionForm.deptID%7DOldValue=true"
                +"&wlw-select_key%3A%7BactionForm.deptID%7D="
                +"&wlw-select_key%3A%7BactionForm.courseDay%7DOldValue=true"
                +"&wlw-select_key%3A%7BactionForm.courseDay%7D="
                +"&wlw-select_key%3A%7BactionForm.courseTime%7DOldValue=true"
                +"&wlw-select_key%3A%7BactionForm.courseTime%7D="
                +"&wlw-checkbox_key%3A%7BactionForm.queryDateFlag%7DOldValue=false"
                +"&deptIdHide="
        ;
        out.write(requestStr);
        out.flush();
        out.close();

        prePage = CurriculmFormPage;
        while((nextPage = websiteScanning(conn)) != "") {
            conn.disconnect();
            nextPage = "http://elective.pku.edu.cn/"+nextPage;
            nextPage = nextPage.replace("amp;", "");

            conn = WebIterator(prePage, nextPage);
            prePage = nextPage;
        }

        conn.disconnect();



//----------------------- crawl trans_choice


        System.out.println("trans_choice crawling");

        conn = GetConn(CurriculmFormPage, cookieKey, "GET", false);
        conn.setRequestProperty("Referer", "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/help/HelpController.jpf");
        conn.setRequestProperty("Host", "http://elective.pku.edu.cn");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");
        conn.connect();
        out = new OutputStreamWriter(conn.getOutputStream(),"utf-8");
        requestStr = "wlw-radio_button_group_key%3A%7BactionForm.courseSettingType%7D=trans_choice"
                +"&%7BactionForm.courseID%7D="
                +"&%7BactionForm.courseName%7D="
                +"&wlw-select_key%3A%7BactionForm.deptID%7DOldValue=true"
                +"&wlw-select_key%3A%7BactionForm.deptID%7D=ALL"
                +"&wlw-select_key%3A%7BactionForm.courseDay%7DOldValue=true"
                +"&wlw-select_key%3A%7BactionForm.courseDay%7D="
                +"&wlw-select_key%3A%7BactionForm.courseTime%7DOldValue=true"
                +"&wlw-select_key%3A%7BactionForm.courseTime%7D="
                +"&wlw-checkbox_key%3A%7BactionForm.queryDateFlag%7DOldValue=false"
                +"&deptIdHide=ALL"
        ;
        out.write(requestStr);
        out.flush();
        out.close();

        prePage = CurriculmFormPage;
        while((nextPage = websiteScanning(conn)) != "") {
            conn.disconnect();
            nextPage = "http://elective.pku.edu.cn/"+nextPage;
            nextPage = nextPage.replace("amp;", "");

            conn = WebIterator(prePage, nextPage);
            prePage = nextPage;
        }

        conn.disconnect();


        //----------------------- crawl pub_choice


        System.out.println("pub_choice crawling");
        conn = GetConn(CurriculmFormPage, cookieKey, "GET", false);
        conn.setRequestProperty("Referer", "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/help/HelpController.jpf");
        conn.setRequestProperty("Host", "http://elective.pku.edu.cn");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");
        conn.connect();
        out = new OutputStreamWriter(conn.getOutputStream(),"utf-8");
        requestStr = "wlw-radio_button_group_key%3A%7BactionForm.courseSettingType%7D=pub_choice"
                +"&%7BactionForm.courseID%7D="
                +"&%7BactionForm.courseName%7D="
                +"&wlw-select_key%3A%7BactionForm.deptID%7DOldValue=true"
                +"&wlw-select_key%3A%7BactionForm.deptID%7D=ALL"
                +"&wlw-select_key%3A%7BactionForm.courseDay%7DOldValue=true"
                +"&wlw-select_key%3A%7BactionForm.courseDay%7D="
                +"&wlw-select_key%3A%7BactionForm.courseTime%7DOldValue=true"
                +"&wlw-select_key%3A%7BactionForm.courseTime%7D="
                +"&wlw-checkbox_key%3A%7BactionForm.queryDateFlag%7DOldValue=false"
                +"&deptIdHide=ALL"
                ;
        out.write(requestStr);
        out.flush();
        out.close();

        prePage = CurriculmFormPage;
        while((nextPage = websiteScanning(conn)) != "") {
            conn.disconnect();
            nextPage = "http://elective.pku.edu.cn/"+nextPage;
            nextPage = nextPage.replace("amp;", "");

            conn = WebIterator(prePage, nextPage);
            prePage = nextPage;
        }

        conn.disconnect();

        //----------------------- crawl liberal_computer


        System.out.println("liberal_computer crawling");
        conn = GetConn(CurriculmFormPage, cookieKey, "GET", false);
        conn.setRequestProperty("Referer", "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/help/HelpController.jpf");
        conn.setRequestProperty("Host", "http://elective.pku.edu.cn");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");
        conn.connect();
        out = new OutputStreamWriter(conn.getOutputStream(),"utf-8");
        requestStr = "wlw-radio_button_group_key%3A%7BactionForm.courseSettingType%7D=liberal_computer"
                +"&%7BactionForm.courseID%7D="
                +"&%7BactionForm.courseName%7D="
                +"&wlw-select_key%3A%7BactionForm.deptID%7DOldValue=true"
                +"&wlw-select_key%3A%7BactionForm.deptID%7D="
                +"&wlw-select_key%3A%7BactionForm.courseDay%7DOldValue=true"
                +"&wlw-select_key%3A%7BactionForm.courseDay%7D="
                +"&wlw-select_key%3A%7BactionForm.courseTime%7DOldValue=true"
                +"&wlw-select_key%3A%7BactionForm.courseTime%7D="
                +"&wlw-checkbox_key%3A%7BactionForm.queryDateFlag%7DOldValue=false"
                +"&deptIdHide="
        ;
        out.write(requestStr);
        out.flush();
        out.close();

        prePage = CurriculmFormPage;
        while((nextPage = websiteScanning(conn)) != "") {
            conn.disconnect();
            nextPage = "http://elective.pku.edu.cn/"+nextPage;
            nextPage = nextPage.replace("amp;", "");

            conn = WebIterator(prePage, nextPage);
            prePage = nextPage;
        }

        conn.disconnect();

        System.out.println(websiteQue.size());
    }

    public HttpURLConnection WebIterator(String prePage,String Page) throws Exception {
        //System.out.println(Page);
        String str = Page;
        HttpURLConnection conn = GetConn(str, cookieKey, "GET", true);
        conn.setRequestProperty("Referer", prePage);
        conn.setRequestProperty("Host", "elective.pku.edu.cn");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");
        conn.connect();
        //OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(),"utf-8");
        //out.write(requestStr);
        //out.flush();
        //out.close();
        return conn;
    }

    public boolean isQueEmpty() {
        return websiteQue.isEmpty();
    }

    public String getFirstWebsite() throws Exception{
        String str = websiteQue.firstElement();

        str = "http://elective.pku.edu.cn/"+str;
        //System.out.println(str);

        HttpURLConnection conn = GetConn(str, cookieKey, "GET", true);
        conn.setRequestProperty("Referer", "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/courseQuery/getCurriculmByForm.do");
        conn.setRequestProperty("Host", "elective.pku.edu.cn");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");
        //System.out.println("work here");
        conn.connect();
        //System.out.println("work here");

        //OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(),"utf-8");


        BufferedReader buffR = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
        String line;
        while ((line = buffR.readLine()) != null)
            str = str + line + "\n";
        System.out.println(str);
        buffR.close();

        conn.disconnect();

        return str;
    }

    public void popFirstWebsite() {
        websiteQue.remove(0);
    }

    public String discernCaptcha() throws Exception {
        String srcPic = "http://elective.pku.edu.cn/elective2008/DrawServlet?Rand="+String.valueOf(Math.random()*10000);
        HttpURLConnection conn = GetConn(srcPic, cookieKey, "GET", false);
        conn.setRequestProperty("Referer", SupplyCancelPage);
        conn.setRequestProperty("Host", "elective.pku.edu.cn");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");
        conn.connect();

        String strCookie = conn.getHeaderField("Set-Cookie");
        //System.out.println(strCookie);

        BufferedImage picR = ImageIO.read(conn.getInputStream());
        File picOut = new File("src/captcha/captcha.jpg");
        ImageIO.write(picR, "jpg", picOut);

        conn.disconnect();

        //System.out.println("please input Captcha answer:");
        //Scanner scan = new Scanner(System.in);
        //String ans = scan.next();


        String ans;
        try {
            ans = trycaptcha.getCaptcha();
        } catch (Exception e) {
            System.out.println("Image wrong");
            throw e;
        }
        System.out.println(ans);
        return ans;
    }

    public boolean haveCourse(String CourseName) throws Exception {
        // get Page Info

        HttpURLConnection conn = GetConn(SupplyCancelPage, cookieKey, "GET", false);
        conn.setRequestProperty("Referer", HelperControlPage);
        conn.setRequestProperty("Host", "elective.pku.edu.cn");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");
        conn.connect();

        BufferedReader buffR = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
        String line;
        Vector<String> supplyPageBody = new Vector<>();
        supplyPageBody.clear();
        while ((line = buffR.readLine()) != null)
            supplyPageBody.add(new String(line.getBytes()));
        buffR.close();

        conn.disconnect();

        // check curriculum

        Iterator it = supplyPageBody.iterator();
        while (it.hasNext()) {
            line = String.valueOf(it.next());
            if (line.contains(CourseName) && (line.contains("刷新") || line.contains("补选"))) {
                return true;
            }
        }
        return false;
    }

    public boolean freshCourse(String CourseName, Vector<String> supplyPageBody) throws Exception {
        Iterator it = supplyPageBody.iterator();
        String seqnum = null;
        boolean flag = false;
        String upLimit = "";
        while (it.hasNext()) {
            String line = String.valueOf(it.next());
            //System.out.println(line);
            if (line.contains(CourseName)) {
                flag = true;
            }
            if (flag && line.contains("electedNum")) {
                //System.out.println(line);
                String numStr = line.substring(line.indexOf("/") + 2, line.length());
                numStr = numStr.substring(0, numStr.indexOf("<"));
                upLimit = numStr;
                flag = false;
            }
            if (line.contains(CourseName) && (line.contains("刷新") || line.contains("补选"))) {
                if (line.contains("补选")) return true;
                seqnum = line.substring(line.indexOf("index=") , line.indexOf("eid="));
                break;
            }
        }
        System.out.println(upLimit);
        if (upLimit == "") {
            System.out.println("upLimit is wrong!");
            return false;
        }
        seqnum = seqnum.replace("amp;", "");
        seqnum = seqnum.substring(0, seqnum.length() - 1);
        System.out.println(seqnum);

        HttpURLConnection conn = GetConn(freshPage, cookieKey, "POST", false);
        conn.setRequestProperty("Referer", SupplyCancelPage);
        conn.setRequestProperty("Host", "elective.pku.edu.cn");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");
        conn.connect();
        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(),"utf-8");
        out.write(seqnum);
        out.flush();
        out.close();

        BufferedReader buffR = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
        String line = buffR.readLine();
        System.out.println(line);
        conn.disconnect();

        //System.out.println(line);
        if (line.contains(upLimit)) return false;
        return true;
    }

    public boolean supplying(String CourseName) throws Exception {

        // get Page Info

        HttpURLConnection conn = GetConn(SupplyCancelPage, cookieKey, "GET", false);
        conn.setRequestProperty("Referer", HelperControlPage);
        conn.setRequestProperty("Host", "elective.pku.edu.cn");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");
        conn.connect();

        BufferedReader buffR = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
        String line;
        Vector<String> supplyPageBody = new Vector<>();
        supplyPageBody.clear();
        while ((line = buffR.readLine()) != null)
            supplyPageBody.add(new String(line.getBytes()));
        buffR.close();

        conn.disconnect();

        // check curriculum

        Iterator it = supplyPageBody.iterator();
        String seqnum = null;
        while (it.hasNext()) {
            line = String.valueOf(it.next());
            if (line.contains(CourseName) && (line.contains("刷新") || line.contains("补选"))) {
                if (line.contains("刷新")) return false;
                seqnum = line.substring(line.indexOf("elective"), line.indexOf("style") - 2);
                break;
            }
        }
        seqnum = "http://elective.pku.edu.cn/" + seqnum;
        seqnum = seqnum.replace("amp;", "");

        // enter captcha
        boolean flag = false;
        while (!flag) {
            String captchaAns = discernCaptcha();

            conn = GetConn(validtePage, cookieKey, "POST", false);
            conn.setRequestProperty("Referer", SupplyCancelPage);
            conn.setRequestProperty("Host", "elective.pku.edu.cn");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
            conn.connect();
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
            out.write("validCode=" + captchaAns);
            out.flush();
            out.close();

            buffR = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            line = buffR.readLine();

            conn.disconnect();

            System.out.println(line);
            if (line.contains("2")) {
                conn = GetConn(seqnum, cookieKey, "GET", false);
                conn.setRequestProperty("Referer", SupplyCancelPage);
                conn.setRequestProperty("Host", "elective.pku.edu.cn");
                conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
                conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
                conn.connect();
                displayConnNoPrint(conn);
                conn.disconnect();
                flag = true;
            } else {
                flag = false;
            }
        }
        if (haveCourse(CourseName)) return false;
        return true;
    }

    public void supply(Vector<String> list) throws Exception {

        // get Page info

        HttpURLConnection conn = GetConn(SupplyCancelPage, cookieKey, "GET", false);
        conn.setRequestProperty("Referer", HelperControlPage);
        conn.setRequestProperty("Host", "elective.pku.edu.cn");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");
        conn.connect();

        BufferedReader buffR = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
        String line;
        Vector<String> supplyPageBody = new Vector<>();
        supplyPageBody.clear();
        while ((line = buffR.readLine()) != null)
            supplyPageBody.add(new String(line.getBytes()));
        buffR.close();

        conn.disconnect();

        boolean flag = false;
        while (!flag) {
            Iterator it = list.iterator();
            while(it.hasNext()) {
                String CourseName = String.valueOf(it.next());
                if (freshCourse(CourseName, supplyPageBody)) {
                    System.out.println(CourseName + " 发现空位");
                    if (supplying(CourseName)) {
                        flag = true;
                        System.out.println(CourseName + " 抢课成功");
                        break;
                    }
                    else {
                        System.out.println(CourseName + " 抢课失败");
                    }
                }
                else {
                    System.out.println(CourseName + " 选课人数没有变化");
                }
                Thread.sleep(3000);
            }
        }
        //supplying("紧凑数据结构与大数据");
        //System.out.println(freshCourse("紧凑数据结构与大数据"));
        //System.out.println(freshCourse("Python编程：从新手到专家之路"));
    }

    public void getPageCode(String PagePath) throws Exception {
        HttpURLConnection conn = GetConn(SupplyCancelPage, cookieKey, "GET", false);
        conn.setRequestProperty("Referer", HelperControlPage);
        conn.setRequestProperty("Host", "elective.pku.edu.cn");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");
        conn.connect();

        displayConnToFile(conn, "PageWeb.txt");

        conn.disconnect();
    }

    public static void main(String[] args) {
        WebConnect X = new WebConnect();
        try {
            if (X.Login("1600012799","2333333")) {
                System.out.println("login successfully!");
                Vector<String> lst = new Vector<>();
                lst.add("Python编程：从新手到专家之路");
                lst.add("生物特征识别");
                lst.add("深度学习");
                //lst.add("计算机视觉中的机器学习");
                lst.add("机器学习与时间序列分析");

                //X.supply(lst);
                //X.Crawl2getWeb();
                //X.getFirstWebsite();
                //X.popFirstWebsite();
                X.getPageCode(SupplyCancelPage);
            }
            else {
                System.out.println("user name or password is wrong!");
            }
        } catch (Exception e) {
            System.out.println("Connect time limit exceeded!");
        }
    }
}
