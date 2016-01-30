
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

public class Main {

    public static boolean isWhite(int colorInt) {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() == 765) {
            return true;
        }
        return false;
    }

    public static Integer similar(Map<Integer, Integer> map,int key) {
        for (Map.Entry<Integer,Integer> entry:map.entrySet()) {
            if (cmpColor(entry.getValue(),key)<10)return entry.getKey();
        }
        return -1;
    }

    public static BufferedImage removeBackgroud(BufferedImage img)
            throws Exception {
        int width = img.getWidth();
        int height = img.getHeight();
//		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
//		for (int x = 0; x < width; ++x) {
//			for (int y = 0; y < height; ++y) {
//				if (isWhite(img.getRGB(x, y)))
//					continue;
//				Integer c;
//				if ((c=(similar(map,img.getRGB(x, y))))!=-1) {
//					map.put(c, map.get(c) + 1);
//				} else {
//					map.put(img.getRGB(x, y), 1);
//				}
//			}
//		}
//
//		int max = 0;
//		int colorMax = 0;
//		for (Integer color : map.keySet()) {
//			if (max < map.get(color)) {
//				max = map.get(color);
//				colorMax = color;
//			}
//		}
//
//		for (int x = 0; x < width; ++x) {
//			for (int y = 0; y < height; ++y) {
//				if (cmpColor(img.getRGB(x, y) , colorMax) < 10) {
//					img.setRGB(x, y, Color.WHITE.getRGB());
//				} else {
//					img.setRGB(x, y, Color.BLACK.getRGB());
//				}
//			}
//		}
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if (isWhite(img.getRGB(x,y))) {
                    img.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    img.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
        return img;
    }

    public static int cmpColor(int src,int tar) {
        Color s = new Color(src);
        Color t = new Color(tar);
        int r = Math.abs(s.getRed()-t.getRed());
        int g = Math.abs(s.getGreen()-t.getGreen());
        int b = Math.abs(s.getBlue()-t.getBlue());
        return r+g+b;
    }

    public static BufferedImage removeBlank(BufferedImage img) throws Exception {
        int width = img.getWidth();
        int height = img.getHeight();

        //纵向扫描
        int left = width/2-1;
        int right = width/2;
        for (int i=0;i<14;i++){
            int leftCount = 0;
            int rightCount = 0;
            for (int y = 0; y < height; ++y) {
                if (!isWhite(img.getRGB(left,y))) {
                    leftCount++;
                }
            }
            for (int y = 0; y < height; ++y) {
                if (!isWhite(img.getRGB(right,y))) {
                    rightCount++;
                }
            }
            if (leftCount<rightCount&&right!=width-1||left==0) {
                right++;
            } else {
                left--;
            }
        }

        //横向扫描
        int start = height/2-1;
        int end = height/2;
        for (int i=0;i<20;i++){
            int headCount = 0;
            int footCount = 0;
            for (int x = 0; x < width; ++x) {
                if (!isWhite(img.getRGB(x,start))) {
                    headCount++;
                }
            }
            for (int x = 0; x < width; ++x) {
                if (!isWhite(img.getRGB(x,end))) {
                    footCount++;
                }
            }
            if (headCount<footCount&&end!=height-1||start==0) {
                end++;
            } else {
                start--;
            }
        }
        return img.getSubimage(left, start, right - left +1, end - start + 1);
    }

    public static List<BufferedImage> splitImage(BufferedImage img)
            throws Exception {
        List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
        BufferedImage img1 = img.getSubimage(0, 0, 24, 41);
        img1 = removeBlank(img1);
        subImgs.add(img1);
        BufferedImage img2 = img.getSubimage(34, 0, 24, 41);
        img2 = removeBlank(img2);
        subImgs.add(img2);
//		ImageIO.write(img1,"PNG",new File("temp/"+Math.random()));//写入temp
        return subImgs;
    }

    public static Map<BufferedImage, String> loadTrainData() throws Exception {
        Map<BufferedImage, String> map = new HashMap<BufferedImage, String>();
        File dir = new File("train");
        File[] files = dir.listFiles();
        for (File file : files) {
            map.put(ImageIO.read(file), file.getName().charAt(0) + "");
        }
        return map;
    }

    public static String getSingleCharOcr(BufferedImage img,
                                          Map<BufferedImage, String> map) {
        String result = "";
        int width = img.getWidth();
        int height = img.getHeight();
        int min = width * height;
        for (Map.Entry<BufferedImage, String> entry : map.entrySet()) {
            int count = 0;
            Label1: for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    if (isWhite(img.getRGB(x, y)) != isWhite(entry.getKey().getRGB(x, y))) {
                        count++;
                        if (count >= min)
                            break Label1;
                    }
                }
            }
            if (count < min) {
                min = count;
                result = entry.getValue();
            }
        }
        return result;
    }

    public static BufferedImage removeGanRaoLine(BufferedImage img){
        int width = img.getWidth();
        int height = img.getHeight();
        for (int x = 1; x < width-1; ++x) {
            for (int y = 1; y < height-1; ++y) {
                if (isWhite(img.getRGB(x, y)))continue;
                int center = img.getRGB(x,y);
                int l = img.getRGB(x-1,y);
                int r = img.getRGB(x+1,y);
                int u = img.getRGB(x,y-1);
                int d = img.getRGB(x,y+1);
                if ((!isWhite(l)&&l!=center)||(!isWhite(r)&&r!=center)||(!isWhite(u)&&u!=center)||(!isWhite(d)&&d!=center)){

                } else if (isWhite(l)||isWhite(r)||isWhite(u)||isWhite(d)) {
                    img.setRGB(x,y,Color.WHITE.getRGB());
                }
            }
        }
        return img.getSubimage(1,1,width-1,height-1);
    }

    public static String getAllOcr(int index) throws Exception {

        String filePath = "img/"+index+".png";
        BufferedImage img = ImageIO.read(new File(filePath));
        img = removeGanRaoLine(img);//去掉干扰线
        img = removeBackgroud(img);//黑白化
        List<BufferedImage> listImg	 = splitImage(img);//纵横向扫描
//		return "";
        Map<BufferedImage, String> map = loadTrainData();
        String result = "";
        for (BufferedImage bi : listImg) {
            String singleChar = getSingleCharOcr(bi, map);
//			ImageIO.write(bi, "PNG", new File("temp/"+getSingleCharNum(singleChar)));//添加对比源
            result += singleChar;
        }
        String outputPath = "result/"+index+"=>"+result+".png";
        ImageIO.write(ImageIO.read(new File(filePath)), "PNG", new File(outputPath));
        return outputPath;
    }

    public static String getSingleCharNum(String singleChar){
        int c = 1;
        while (new File("fen10/"+singleChar+'-'+(++c)).exists()||new File("temp/"+singleChar+'-'+(c)).exists());
        return singleChar+'-'+c;
    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 700; ++i) {
            String text = getAllOcr(i);
            System.out.println(i + ".png = " + text);
        }
        System.out.println(System.currentTimeMillis()-start);
    }
}
