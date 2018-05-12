import java.awt.image.*;  
import java.awt.Color;  
import java.io.*;
import javax.imageio.ImageIO;

public class CV
{
	public void binaryImage() throws IOException
	{
		File file = new File(System.getProperty("user.dir") + "/src/DrawServlet.jpg");  
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
				binImage.setRGB(i, j, new_gry[i][j]);
		File newFile = new File(System.getProperty("user.dir") + "/src/newit.png");  
		ImageIO.write(binImage, "png", newFile);  
	}  
	
	public static void main(String[] args) throws IOException
	{
		// TODO Auto-generated method stub
		CV demo = new CV();
		demo.binaryImage();
	}

}
