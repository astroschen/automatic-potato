import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BingWallpaper {
    /**
     *  获取window每日更新锁屏壁纸
     *  targetFile 目标文件夹
     *  sourceFile 源文件夹
     */
    private static String targetFile = "F:\\bing壁纸";
    private static String sourceFile = "C:\\Users\\MSI-PC\\AppData\\Local\\Packages\\Microsoft.Windows.ContentDeliveryManager_cw5n1h2txyewy\\LocalState\\Assets";

    public static void main(String[] args) {
        BingWallpaper bingWallpaper = new BingWallpaper();
        bingWallpaper.deWeightingFile(targetFile, sourceFile);
    }

    public void deWeightingFile(String targetFile, String sourceFile){
        try {
            File tfile = new File(targetFile);
            File sfile = new File(sourceFile);
            File[] tfilesArr = tfile.listFiles();
            File[] sfilesArr = sfile.listFiles();
            if (sfilesArr.length > 0) {
                if (tfilesArr.length == 0) {
                    List fileList = new ArrayList();
                    for (int i = 0; i < sfilesArr.length; i++) {
                        if(getFileSize(sfilesArr[i]) > 200) {
                            fileList.add(sfilesArr[i]);
                            System.out.println("新文件："+sfilesArr[i].getName());
                        }
                    }
                    cloneFile(fileList);
                }else{
                    List tlist = new ArrayList();
                    Map<String, File> sfileMap = readFileForMap(sourceFile, "源");
                    Map<String, File> tfileMap = readFileForMap(targetFile, "目标");
                    System.out.println("开始比对...");
                    for(String key : sfileMap.keySet()){
                        File file = tfileMap.get(key+".jpg");
                        if(file == null){
                            tlist.add(sfileMap.get(key));
                            System.out.println("新文件："+sfileMap.get(key).getName());
                        }
                    }
                    if(tlist.size() != 0){
                        System.out.println("发现"+tlist.size()+"个新文件");
                        cloneFile(tlist);
                    }else{
                        System.out.println("无新文件");
                    }
                }
            } else {
                System.out.println("目标文件夹为空");
            }
        }catch (Exception e){
            System.out.println("deWeightingFile()   Exception:" + e.getMessage());
        }
    }

    public boolean cloneFile(List<File> saveList) throws IOException {
        System.out.println("------------- 开始复制 -------------");
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            if (new File(targetFile).exists()) {
                for (File file : saveList) {
                    String targetUrl = targetFile+ "\\"+ file.getName() + ".jpg";
                    fis = new FileInputStream(file.getPath());
                    fos = new FileOutputStream(targetUrl);
                    byte[] bits = new byte[1024];
                    int leng = 0;
                    while ((leng = fis.read(bits)) > 0) {
                        fos.write(bits, 0, leng);
                    }
                }
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
                System.out.println("复制完成");
            } else {
                System.out.println("目标文件夹不存在");
            }

        } catch (Exception e) {
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }
            System.out.println("复制失败");
            System.out.println("cloneFile()   Exception:" + e.getMessage());
        }
        return true;
    }

    public Map readFileForMap(String filepath, String name) {
        System.out.println("------------- 读取"+name+"文件夹 --------------");
        List fileList = new ArrayList();
        Map fileMap = new HashMap();
        try {
            File file = new File(filepath);
            if (!file.isDirectory()) {
                System.out.println("path=" + file.getPath()+"absolutepath=" + file.getAbsolutePath()+"name=" + file.getName());
            } else if (file.isDirectory()) {
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File readfile = new File(filepath + "\\" + filelist[i]);
                    if (getFileSize(readfile) > 200) {
                        fileList.add(readfile);
                        fileMap.put(readfile.getName(), readfile);
                        System.out.println("文件名称：" + readfile.getName() + "  文件大小：" + getFileSize(readfile) + " kb ");
                    }
                }
                System.out.println("共" + filelist.length + "个文件");
            }
        } catch (Exception e) {
            System.out.println("readFile()   Exception:" + e.getMessage());
        }
        return fileMap;
    }

    public float getFileSize(File file) {
        //kb
        float size;
        if (!file.exists() || !file.isFile()) {
            System.out.println("文件不存在");
            return -1;
        } else {
            size = file.length();
            //b转kb
            size /= 1024;
        }
        return size;
    }
}
