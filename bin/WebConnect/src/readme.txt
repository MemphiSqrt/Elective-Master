
useage:

WebConnect X = new WebConnect();

X.Login(username, password);
    return true
        login successfully
    return false
        username or password is wrong
    throw exception
        time limit exceeded




// 补课
Vector<String> list = new Vector<>();
list.add("深度学习");
list.add("生物特征识别");
list.add("太极拳");
X.supply(list); //开始刷课，刷到课就会退出





// 爬网页源代码
X.Crawl2getWeb()
while(!isQueEmpty()) {
    String code = X.getFirstWebsite()
    X.popFirstWebsite()
}




// 就会在 src/crawl/PageWebInfo.txt 里得到课程信息，把SupplyCancelPage换成别的网站同样work
X.getPageInfo(SupplyCancelPage);
