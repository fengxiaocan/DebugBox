package com.box.libs.web;

import java.util.regex.Pattern;

class HtmlParser {
    static final char START_NODE = '<';//开始节点
    static final char END_NODE = '>';//结束节点
    static final String nodeRegex = "<[-A-Za-z0-9+&@#/%?=~_|!:,.;'\"]+[^>]*>";//结束节点
    static final String LINE = "\r\n";//换行
    static final String BASE64 = "base64img...";//换行
    static final int LINE_LENGHT = LINE.length();//换行
    static final int BASE64_LENGHT = BASE64.length();//换行

    private final String htmlContent;
    private final StringBuilder htmlBuilder;
    private final char[] chars;
    private int vernier = 0;
    private boolean isIgnoreBase64 = true;//是否忽略base64

    public HtmlParser setIgnoreBase64(boolean ignoreBase64) {
        isIgnoreBase64 = ignoreBase64;
        return this;
    }

    public HtmlParser(String htmlContent) {
        this.htmlContent = htmlContent;
        chars = htmlContent.toCharArray();
        htmlBuilder = new StringBuilder(htmlContent);
    }

    /**
     * 把html 格式化输出
     *
     * @return
     */
    public String parser() {
        vernier = 0;
        int startIndex = -1;
        int endIndex = -1;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            switch (c) {
                case START_NODE:
                    startIndex = i;
                    break;
                case END_NODE:
                    endIndex = i;
                    break;
            }
            if (startIndex >= 0 && endIndex > 0 && endIndex > startIndex) {
                String str = htmlContent.substring(startIndex, endIndex + 1);
                if (Pattern.matches(nodeRegex, str)) {
                    htmlBuilder.insert(vernier + startIndex, LINE);
                    vernier += LINE_LENGHT;
                    if (isIgnoreBase64 && str.startsWith("<img")) {
                        replace(startIndex + vernier, endIndex + vernier + 1, str);
                    }
                    htmlBuilder.insert(vernier + endIndex + 1, LINE);
                    vernier += LINE_LENGHT;
                }
                startIndex = -1;
                endIndex = -1;
            }
        }
        return htmlBuilder.toString();
    }

    private void replace(int startIndex, int endIndex, final String str) {
        String srcData = str;
        srcData = srcData.replaceAll("data:image[^\"|']*", BASE64);
        htmlBuilder.replace(startIndex, endIndex, srcData);
        vernier -= (str.length() - srcData.length());
    }
}
