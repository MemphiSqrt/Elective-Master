package com.example.mrmrfan.hello_app.WebConnect.image_processing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class trycaptcha {


    static int len = -1;
    static int white = -1;//255 white
    static int black = -16777216;// 0 black
    static boolean gry[][][] = new boolean[65][17][22];
    static char ys[] = new char[100];
    static int ks[] = new int[65], js[] = new int[65];
    static int hks[] = new int[65], hjs[] = new int[65];
    static int tn[] = new int[65], sz[] = new int[65];


    public static void binaryImage(Bitmap image) throws IOException
    {
        int i, j, k, l;
        int width = image.getWidth();
        int height = image.getHeight();
        int gry[][] = new int[width][height], new_gry[][] = new int[width][height];
        int white = -1;
        int black = -16777216;

        for(i= 0 ; i < width ; i++)
        {
            for(j = 0 ; j < height; j++)
            {
                int rgb = image.getPixel(i, j);
                int r = Color.red(rgb);
                int g = Color.green(rgb);
                int b = Color.blue(rgb);
                int sm = r + g + b;

                if(sm > 600)
                    gry[i][j] = white;
                else
                    gry[i][j] = black;
            }
        }
        Bitmap im2=Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        System.out.println("work1\n");
//        System.out.println(Color.BLACK);
//        System.out.println(Color.WHITE);
//        im2.setPixel(1, 1, Color.WHITE);
        for(i = 0; i < width; ++i)
            for(j = 0; j < height; ++j)
            {
                int sum = 0;
                for(k = -1; k < 2; ++k)
                    for(l = -1; l < 2; ++l)
                        if(i + k > -1 && i + k < width && j + l > -1 && j + l < height && gry[i + k][j + l] == black)
                            ++sum;
                if(sum <= 3)
                    im2.setPixel(i, j, Color.WHITE);
                else
                    im2.setPixel(i, j, Color.BLACK);
                if(gry[i][j] == white)
                    im2.setPixel(i, j, Color.WHITE);
            }
        System.out.println("work2\n");

        File file = new File("mnt/shared/Image/captcha.png");
        if(file.exists()){
            file.delete();
        }
        FileOutputStream out;
        try{
            out = new FileOutputStream(file);
            if(im2.compress(Bitmap.CompressFormat.PNG, 100, out))
            {
                out.flush();
                out.close();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void prework() throws IOException
    {
        len = -1;
        char ch;
        char st[] = new char[62];
        for(ch = '0'; ch <= '9'; ++ch)
            st[ch - '0'] = ch;
        for(ch = 'a'; ch <= 'z'; ++ch)
            st[ch - 'a' + 10] = ch;
        for(ch = 'A'; ch <= 'Z'; ++ch)
            st[ch - 'A' + 36] = ch;
        int forr;
        for(forr = 0; forr < 10; ++forr)
        {
            ch = st[forr];
            String filename = "nn" + ch + ".png";

            try
            {
                Bitmap image = BitmapFactory.decodeFile("mnt/shared/Image/" + filename);

                int i, j;
                int width = image.getWidth();
                int height = image.getHeight();
                ++len;
                ys[len] = ch;
                ks[len] = width - 1;
                hks[len] = height - 1;
                js[len] = hjs[len] = 0;
                tn[len] = 0;
                for(i = 0; i < width; ++i)
                    for(j = 0; j < height; ++j)
                    {
                        int rgb = image.getPixel(i, j);
                        if(rgb != Color.BLACK)
                            gry[len][i][j] = false;
                        else
                        {
                            gry[len][i][j] = true;
                            if(i < ks[len])
                                ks[len] = i;
                            if(i > js[len])
                                js[len] = i;
                            if(j < hks[len])
                                hks[len] = j;
                            if(j > hjs[len])
                                hjs[len] = j;
                            ++tn[len];
                        }
                    }
                sz[len] = (js[len] - ks[len] + 1) * (hjs[len] - hks[len] + 1);
            } catch(Exception e){ System.out.println(forr);}
        }
        for(forr = 10; forr < 36; ++forr)
        {
            ch = st[forr];
            String filename = ch + "0.png";

            try
            {
                Bitmap image = BitmapFactory.decodeFile("mnt/shared/Image/" + filename);

                int i, j;
                int width = image.getWidth();
                int height = image.getHeight();
                ++len;
                ys[len] = st[forr + 26];
                ks[len] = width - 1;
                hks[len] = height - 1;
                js[len] = hjs[len] = 0;
                tn[len] = 0;
                for(i = 0; i < width; ++i)
                    for(j = 0; j < height; ++j)
                    {
                        int rgb = image.getPixel(i, j);
                        if(rgb != Color.BLACK)
                            gry[len][i][j] = false;
                        else
                        {
                            gry[len][i][j] = true;
                            if(i < ks[len])
                                ks[len] = i;
                            if(i > js[len])
                                js[len] = i;
                            if(j < hks[len])
                                hks[len] = j;
                            if(j > hjs[len])
                                hjs[len] = j;
                            ++tn[len];
                        }
                    }
                sz[len] = (js[len] - ks[len] + 1) * (hjs[len] - hks[len] + 1);
            } catch(Exception e){ System.out.println(forr);}
        }
        for(forr = 10; forr < 36; ++forr)
        {
            ch = st[forr];
            String filename = ch + "1.png";

            try
            {
                Bitmap image = BitmapFactory.decodeFile("mnt/shared/Image/" + filename);

                int i, j;
                int width = image.getWidth();
                int height = image.getHeight();
                ++len;
                ys[len] = ch;
                ks[len] = width - 1;
                hks[len] = height - 1;
                js[len] = hjs[len] = 0;
                tn[len] = 0;
                for(i = 0; i < width; ++i)
                    for(j = 0; j < height; ++j)
                    {
                        int rgb = image.getPixel(i, j);
                        if(rgb == Color.WHITE)
                            gry[len][i][j] = false;
                        else
                        {
                            gry[len][i][j] = true;
                            if(i < ks[len])
                                ks[len] = i;
                            if(i > js[len])
                                js[len] = i;
                            if(j < hks[len])
                                hks[len] = j;
                            if(j > hjs[len])
                                hjs[len] = j;
                            ++tn[len];
                        }
                    }
                sz[len] = (js[len] - ks[len] + 1) * (hjs[len] - hks[len] + 1);
            } catch(Exception e){ System.out.println(forr);}
        }

        System.out.println(len + "" + "len =\n");
    }

    public static String getCaptcha() throws IOException {

        prework();

//		File file = new File("mnt/shared/Image/captcha.jpg");

        Bitmap image = BitmapFactory.decodeFile("mnt/shared/Image/captcha11.png");

        binaryImage(image);

//		file = new File("src/captcha/" + "captcha" + ".png");
        image = BitmapFactory.decodeFile("mnt/shared/Image/captcha.png");

        int width = image.getWidth();
        int height = image.getHeight();
        System.out.println(width);
        System.out.println(height);
        boolean pic[][] = new boolean[width][height];
        int i, j, num;
        for(i = 0; i < width; ++i)
            for(j = 0; j < height; ++j)
            {
                int rgb = image.getPixel(i, j);
                if(rgb == Color.WHITE)
                    pic[i][j] = false;
                else
                    pic[i][j] = true;
            }
        int po[] = new int[4], an[] = new int[4];
        double mx[] = new double[4];
        int po2, i1, j1, tot;
        double mx2;
        for(i = 0; i < 4; ++i)
        {
            po[i] = an[i] = 1000000;
            mx[i] = 1000000.0;
        }

        for(num = 0; num <= len; ++num)
        {

            po2 = 1000000;
            mx2 = 1000000.0;
            int wd = js[num] - ks[num] + 1, he = hjs[num] - hks[num] + 1;
            for(i = 0; i <= width - wd; ++i)
                for(j = 0; j <= height - he; ++j)
                {
                    tot = 0;
                    for(i1 = 0; i1 < wd; ++i1)
                        for(j1 = 0; j1 < he; ++j1)
                            if(pic[i + i1][j + j1] != gry[num][ks[num] + i1][hks[num] + j1])
                            {
                                if(gry[num][ks[num] + i1][hks[num] + j1] == true)
                                    tot += 1;
                                else tot += 2;
                            }
                    for(i1 = -2; i1 < wd + 2; ++i1)
                        for(j1 = 0; j1 < height; ++j1)
                        {
                            if(i + i1 < 0 || i + i1 >= width)
                                continue;
                            if(j1 >= j && j1 < j + he && i1 >= 0 && i1 <= wd)
                                continue;
                            if(pic[i + i1][j1] == true)
                                tot += 1;
                        }
                    double txans = (double)tot / (double)sz[num];

                    if(txans < mx2)
                    {
                        mx2 = txans;
                        po2 = i;
                    }
                }

            for(i = 0; i < 4; ++i)
                if(mx2 < mx[i])
                {
                    for(j = 3; j > i; --j)
                    {
                        mx[j] = mx[j - 1];
                        po[j] = po[j - 1];
                        an[j] = an[j - 1];
                    }
                    mx[i] = mx2;
                    po[i] = po2;
                    an[i] = num;
                    break;
                }
        }
        int tmpi;
        double tmpm;
        for(i = 0; i < 4; ++i)
            for(j = i + 1; j < 4; ++j)
                if(po[i] > po[j])
                {
                    tmpi = po[i]; po[i] = po[j]; po[j] = tmpi;
                    tmpm = mx[i]; mx[i] = mx[j]; mx[j] = tmpm;
                    tmpi = an[i]; an[i] = an[j]; an[j] = tmpi;
                }

        String ans = String.format("%c%c%c%c", ys[an[0]], ys[an[1]], ys[an[2]], ys[an[3]]);
        //System.out.println("work here");
        System.out.println(ans);
        return ans;
    }

    public static void main(String[] args) throws IOException {
        prework();

        try
        {
            Bitmap image = BitmapFactory.decodeFile("mnt/shared/Image/captcha.jpg");
            System.out.println("222");
            binaryImage(image);
            System.out.println("222");
        } catch(Exception e){System.out.println(e);}

    }
}
