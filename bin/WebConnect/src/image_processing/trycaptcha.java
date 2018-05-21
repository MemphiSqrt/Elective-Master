package image_processing;


import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

public class trycaptcha {

    static int len = -1;
	static int white = -1; 
	static int black = -16777216; 
	static boolean gry[][][] = new boolean[65][17][22];
	static char ys[] = new char[100];
	static int ks[] = new int[65], js[] = new int[65];
	static int hks[] = new int[65], hjs[] = new int[65];
	static int tn[] = new int[65], sz[] = new int[65];

	public static void binaryImage(BufferedImage image) throws IOException
	{
		int i, j, k, l;
		int width = image.getWidth();  
		int height = image.getHeight();  
		int gry[][] = new int[width][height], new_gry[][] = new int[width][height];
        int white = -1; 
        int black = -16777216;  
		
		BufferedImage binImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		for(i= 0 ; i < width ; i++)
		{  
			for(j = 0 ; j < height; j++)
			{
				int rgb = image.getRGB(i, j);
				int r = (rgb & 16711680) >> 16, g = (rgb & 65280) >> 8, b = rgb & 255;
				int ave = (r + g + b) / 3;
				
				if(ave > 200)
					gry[i][j] = white;
				else
					gry[i][j] = black; 
			}
		}
		for(i = 0; i < width; ++i)
			for(j = 0; j < height; ++j)
				new_gry[i][j] = white;
		for(i = 0; i < width; ++i)
			for(j = 0; j < height; ++j)
			{
				int sum = 0;
				for(k = -1; k < 2; ++k)
					for(l = -1; l < 2; ++l)
						if(i + k > -1 && i + k < width && j + l > -1 && j + l < height && gry[i + k][j + l] == black)
							++sum;
				if(sum <= 3)
					new_gry[i][j] = white;
				else
					new_gry[i][j] = black;
				if(gry[i][j] == white)
					new_gry[i][j] = white;
			}
		for(i = 0; i < width; ++i)
			for(j = 0; j < height; ++j)
				binImage.setRGB(i, j, new_gry[i][j]);
		File newFile = new File(System.getProperty("user.dir") + "/src/captcha/captcha.png");  
		ImageIO.write(binImage, "png", newFile); 
		
	}  
	
	public static void prework() throws IOException
	{
	    len = -1;
		char ch;
		char st[] = new char[62];
		for(ch = '0'; ch <= '9'; ++ch)
			st[ch - '0'] = ch;
		for(ch = 'A'; ch <= 'Z'; ++ch)
			st[ch - 'A' + 10] = ch;
		for(ch = 'a'; ch <= 'z'; ++ch)
			st[ch - 'a' + 36] = ch;
		int forr;
		for(forr = 0; forr < 62; ++forr)
		{
			ch = st[forr];
			File file;
			if(ch >= 'a')
				file = new File("src/model_image/" + ch + "1.png");
			else
				file = new File("src/model_image/" + ch + ".png");
			if(file.exists() == false)
				continue;
			++len;
			
			BufferedImage image = ImageIO.read(file); 
			
			int i, j;
			int width = image.getWidth();  
			int height = image.getHeight();
			ys[len] = ch;
			ks[len] = width - 1;
			hks[len] = height - 1;
			js[len] = hjs[len] = 0;
			tn[len] = 0;
			for(i = 0; i < width; ++i)
				for(j = 0; j < height; ++j)
				{
					int rgb = image.getRGB(i, j); 
					if(rgb == white)
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
		}
        //System.out.println(len);
	}
	
	public static String getCaptcha() throws IOException {
		// TODO Auto-generated method stub
		prework();
		
		File file = new File("src/captcha/" + "captcha" + ".jpg");
		BufferedImage image = ImageIO.read(file);
		binaryImage(image);
		file = new File("src/captcha/" + "captcha" + ".png");
		image = ImageIO.read(file);
		int width = image.getWidth();
		int height = image.getHeight();
		boolean pic[][] = new boolean[width][height];
		int i, j, num;
		for(i = 0; i < width; ++i)
			for(j = 0; j < height; ++j)
			{
				int rgb = image.getRGB(i, j); 
				if(rgb == white)
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
//		for(num = 0; num <= len; ++num)
//			System.out.printf("%d %c %d\n", num, ys[num], tn[num]);
            //System.out.println(len);
		for(num = 0; num <= len; ++num)
		{
            //System.out.println(num);
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
//				if(ys[num] == 'n' && j == 8 && i == 39)
//					System.out.printf("mx = %f i = %d j = %d he = %d tot = %d\n", txans, i, j, he, tot);
				if(txans < mx2)
				{
					mx2 = txans;
					po2 = i;
				}
			}
//			if(ys[num] == 'E')
//				System.out.printf("mx2 = %f po2 = %d\n", mx2, po2);
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
//		System.out.printf("%d %d %d %d\n", an[0], an[1], an[2], an[3]);
//		System.out.printf("%f %f %f %f\n", mx[0], mx[1], mx[2], mx[3]);
//		System.out.printf("%d %d %d %d\n", po[0], po[1], po[2], po[3]);
        //System.out.println(an[0]);
        //System.out.println(an[1]);
        //System.out.println(an[2]);
        //System.out.println(an[3]);

        String ans = String.format("%c%c%c%c", ys[an[0]], ys[an[1]], ys[an[2]], ys[an[3]]);
        //System.out.println("work here");
		return ans;
	}

}
