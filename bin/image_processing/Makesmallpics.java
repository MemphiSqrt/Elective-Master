import java.awt.image.*;  
import java.awt.Color;  
import java.io.*;
import javax.imageio.ImageIO;

public class Makesmallpics
{
	
	public void binaryImage() throws IOException
	{
		int forr;
		char st[] = new char[62];
		char ch;
		for(ch = '0'; ch <= '9'; ++ch)
			st[ch - '0'] = ch;
		for(ch = 'A'; ch <= 'Z'; ++ch)
			st[ch - 'A' + 10] = ch;
		for(ch = 'a'; ch <= 'z'; ++ch)
			st[ch - 'a' + 36] = ch;
		for(forr = 0; forr < 62; ++forr)
		{
			ch = st[forr];
			File file;
			if(ch >= 'a')
				file = new File(System.getProperty("user.dir") + "/src/" + ch + "1.jpg"); 
			else
				file = new File(System.getProperty("user.dir") + "/src/" + ch + ".jpg");
			if(file.exists() == false)
				continue;
			BufferedImage image = ImageIO.read(file);  
		
			int i, j, k, l;
			int width = image.getWidth();  
			int height = image.getHeight();  
			int gry[][] = new int[width][height], new_gry[][] = new int[width][height];
        	int white = new Color(255, 255, 255).getRGB(); 
        	int black = new Color(0, 0, 0).getRGB();  
        
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
					binImage.setRGB(i, j, gry[i][j]);
			File newFile;
			if(ch >= 'a')
				newFile = new File(System.getProperty("user.dir") + "/src/" + ch + "1.png");
			else
				newFile = new File(System.getProperty("user.dir") + "/src/" + ch + ".png");
			ImageIO.write(binImage, "png", newFile);
		}
	}  
	
	public static void main(String[] args) throws IOException
	{
		// TODO Auto-generated method stub
		Makesmallpics demo = new Makesmallpics();
		demo.binaryImage();
	}

}
