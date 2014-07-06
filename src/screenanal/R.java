/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package screenanal;

// Робот - нажатие клавишей и мыши на картинках в любых программах
// Использование:
// R.run("команды через ;");
// команды: код команды пробел параметры через пробел или запятую
// коды команд
// pi - нажать на картинку на экране, в параметре имя файла с картинкой, можно без расширения если png, можно без пути, если записать путь в R.pathI
// wi - ждать появления картинки на экране
// wni- ждать непоявления картинки на экране
// w  - ждать указанное в параметре число секунд или мСекунд, если >=10
// mm - перместить мышь в указанные координаты x,y, если <0 то на край экрана
// s  - нажать указанную далее строку по символам
// k  - нажать клавиши по названиям: down, alt+Tab, shift+F9 и т.д.
// c  - нажать указанную далее строку через clipBoard
// screen2file - записать экран в файл
// clickmouse  - нажать мышь в указанных координатах или там где она сейчас
// Примеры:
// R.run("wi пароль; s 12345; wi войти; k enter;");
// R.run("mm 100,-1; pi tc; k home; k down shift+F9; pi Обновить; w 2; pi x;");

import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.awt.event.InputEvent;
import java.awt.*;
import java.awt.image.BufferedImage;

class R {// Робот
  static int waitI=8000;  // время ожидания появления картинки
  static String pathI=""; // путь к картинкам
  static String error="";
  static Robot robot=null;
  static int[] imageRGB, screenRGB;
  static int w=0,h=0, W=0,H=0, xn=0,yn=0, t;
  static String readI="";   // последняя прочитанная картинка
  static int yi=0;          // место последней найденной картинки

  static void iniRobot() {
    try {
      if (robot==null) {
        GraphicsEnvironment gen=GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice screen=gen.getDefaultScreenDevice();
        robot=new Robot(screen);
      }
    }
    catch (Exception e) {error(e);}
  }

  static boolean clickKey(int k) {
    try {
      iniRobot();
      System.out.println("clickKey k="+k);
      robot.keyPress(k);
      robot.delay(32);
      robot.keyRelease(k);
      robot.delay(32);
    }
    catch (Exception e) {return false;}
    return true;
  }

  static void downKey (int k) {iniRobot(); System.out.println("downKey k="+k); robot.keyPress(k); robot.delay(32);}

  static void upKey   (int k) {iniRobot(); System.out.println("upKey k="+k); robot.keyRelease(k);}
  
  static boolean pressKey(String sk) {// нажатие на клавиши по названию 
    String[] ms=sk.split("[ ,]+");
    
    for (int n=0, t=0, j,k,l; n<ms.length; n++) {
      String s=ms[n];
      String[] sz=s.split("\\*");
      if (sz.length>1) { // многократное нажатие: ...*9
        s="";
        for (j=pI(sz[1]);j-->0;) s+=sz[0]+' ';
        for (j=n+1;j<ms.length;j++) s+=ms[j]+' ';
        ms=s.split(" ");
        s=ms[0];
        n=0;
      } 
      
      if ((l=s.length())==0) continue;
      
      if (s.matches(".+_\\d$")) {// ожидание после нажатия сек
          t=pI(s.substring(l-1))*1000; s=s.substring(0,l-2);
      }
      
// замена названий клавишей кодами
      s=s.toLowerCase()
       .replaceAll("back" , ""+(char)  8)
       .replaceAll("tab"  , ""+(char)  9)
       .replaceAll("enter", ""+(char) 10)
       .replaceAll("esc"  , ""+(char) 27)
       .replaceAll("pgup" , ""+(char) 33)
       .replaceAll("pgdn" , ""+(char) 34)
       .replaceAll("end" ,  ""+(char) 35)
       .replaceAll("left" , ""+(char) 37)
       .replaceAll("top"  , ""+(char) 38)
       .replaceAll("right", ""+(char) 39)
       .replaceAll("down" , ""+(char) 40)
       .replaceAll("ins"  , ""+(char)155);
      s=repl(s,    "home" , ""+(char) 36);
      for (int i=1;i<=9;i++) s=s.replaceAll("f"+i,""+(char)(byte)(112+i-1)); // F1..F9


      if ((l=s.length())==1) clickKey(s.toUpperCase().charAt(0)); else {
        if (s.indexOf("shift" )>=0) downKey(16);
        if (s.indexOf("ctrl"  )>=0) downKey(17);
        if (s.indexOf("alt"   )>=0) downKey(18);
        robot.delay(100);
        k=s.toUpperCase().charAt(l-1);
        if (s.charAt(l-2)=='+')  clickKey(s.toUpperCase().charAt(l-1));
          //clickKey(k<112? s.toUpperCase().charAt(l-1):k);
        sleep(1000);
        if (s.indexOf("alt"   )>=0) upKey(18);
        if (s.indexOf("ctrl"  )>=0) upKey(17);
        if (s.indexOf("shift" )>=0) upKey(16);
      }
      if (t>0) {sleep(t); t=0;}
    }
    return true;
  }

  static boolean pressString(String s) {// вставка строки через clipBoard
    ClipBoard.setText(s);
    sleep(1000);
    return
      pressKey("ctrl+V");
  }
    
  static void mouseMove(int x, int y) {
    iniRobot();
    if (y<0) {getWH(); y=h-1;}
    robot.mouseMove(x,y);
  }

  static void clickMouse() { // нажатие мыши
    robot.mousePress  (InputEvent.BUTTON1_MASK);
    robot.mouseRelease(InputEvent.BUTTON1_MASK);
  }

  static void clickMouse(int x, int y) { // нажатие мыши на координаты x,y
    mouseMove(x,y);
    clickMouse();
  }

  static boolean screen2file(String f) { // захват экрана в файл f
    BufferedImage bi=new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    bi.setRGB(0,0, w,h, screenRGB,0,w);
    try {
      ImageIO.write(bi,"png",new File(f));
      return true;
    } catch (Exception e) {return false;}
  }

  static void capScreen() { // захват экрана в массив screenRGB
    getWH();
    screenRGB=new int[w*h];
    iniRobot();
    robot.createScreenCapture(new Rectangle(0,0,w-1,h-1)).getRGB(0,0, w-1,h-1, screenRGB, 0,w);
  }
  
  static void getWH() {
    Dimension size=Toolkit.getDefaultToolkit().getScreenSize();
    w=(int)size.getWidth();
    h=(int)size.getHeight();
  }
  
  static void readImage(String fi) { // чтение картинки из файла fi в массив imageRGB
    if (!fi.equals(readI))
      try {
        if (!new File(fi).exists()) {imageRGB=null; return;}
        Image im=Toolkit.getDefaultToolkit().getImage(fi);   //получаем изображение из файла
        MediaTracker mt=new MediaTracker (new JFrame());
        mt.addImage(im,0);
        mt.waitForID(0);
        BufferedImage i=new BufferedImage(W=im.getWidth(null),H=im.getHeight(null),BufferedImage.TYPE_INT_RGB);
        i.getGraphics().drawImage(im,0,0,new JFrame());
        imageRGB=new int[W*H];
        i.getRGB(0,0, W,H, imageRGB, 0,W);
        readI=fi;
        yi=0;
      }
      catch (Exception e) {error(e); readI="";}
  }
  
  static void error (Exception e) {
    String s=e.getMessage();
    alert(s);
  }

  static boolean findImage(String i){// поиск картинки из файла i в картинке screenRGB, xn yn - место найденного
    if (i.indexOf(".")<0) i+=".png";
    readImage(pathI+i);
    if (imageRGB==null) return false;
    capScreen();
    int c=imageRGB[0];
    boolean e=false;

    if (screenRGB[yi]!=c && yi>0) yi=0;

//  for (int q=1000; q-->0;) { e=false; yi=0; // проверка на скорость
    for (int y=yi; y<screenRGB.length && !e; y+=w) {
      for (int x=y, k=y+w; x<k; x++)
        if (screenRGB[x]==c) {
          e=true;

          for (int Y=0, xx=x, nx=x; Y<imageRGB.length && e; Y+=W, nx+=w, xx=nx)
            for (int X=Y, K=Y+W; X<K; X++,xx++)
              if (screenRGB[xx]!=imageRGB[X]) {e=false; break;}

          if (e) {xn=x%w+W/2; yn=x/w+H/2; yi=y; break;} else
          if (yi>0) {y=-w; yi=0; break;} // попытка найти картинку на прежнем месте не удалась, ищем с начала
        }
    }
//  }
    if (!e) yi=0;
    return e;
  }

  static void prImage() {R.clickMouse(xn,yn); robot.delay(32);}
  
  static boolean pressImage(String i) {
    if (R.findImage(i)) {prImage(); return true;}
    return false;
  }

  static String robot(String d) {
    String[] p=d.split(" +|, *");
    String p0=p[0], p1=p[1], n=p0.toLowerCase();
    
    if (n.equals("k")  )  {// нажать клавиши
      for (int i=1;i<p.length;i++) pressKey(p[i]);  
      return "";
    }
    if (n.equals("s")  )  {// нажать строку
      for (int i=2;i<d.length();i++) pressKey(d.substring(i,i+1));
      return "";
    }
    if (p.length>1)
    if (n.equals("pressimage") ) return pressImage (p1)+"";  else
    if (n.equals("c"))           return pressString(d.substring(2))+"";  else
    if (n.equals("wi"))          return wi(p1,true )+"";     else
    if (n.equals("wni"))         return wi(p1,false)+"";     else
    if (n.equals("w"))           {t=pI(p1); sleep(t<1?1000: t<10? t*1000:t); return "";} else
    if (n.equals("pi"))          {if (wi(p1,true)) prImage(); return  "";} else
    if (n.equals("screen2file")) {capScreen(); return screen2file(p1)+"";} else
    if (n.equals("findimage")  ) {if (findImage(p1)) return xn+","+yn; else return "";}
    if (p.length==3)
    if (n.equals("mm"))          {mouseMove(pI(p1),pI(p[2]));  return "true";}
    if (n.equals("clickmouse"))  {
      if (p.length==1) clickMouse(); 
      if (p.length==3) clickMouse(pI(p1),pI(p[2]));
      return "true";
    }
    return "false";
  }
  
  static boolean wi(String n, boolean e) {// ожидание появления/пропадания картинки
    for (int t=0;;t+=100)  {
      sleep(100);
      if (findImage(n)==e) return true;
      if (t>waitI)      {
        error+=(e?"Не найдена картинка\n":"Не дождалась пропадания\n")+pathI+n; return false;
      }
    }
  }
  
  static String repl(String S, String s, String n) {// замена в строке S строки s на n
    if (s.equals("")) return S;
    StringBuilder b=new StringBuilder();
    for (int i,p=0;;p=i+s.length()) {
      if (p>S.length()) return b.toString();
      if ((i=S.indexOf(s,p))<0) return b.append(S.substring(p)).toString();
      b.append(S.substring(p, i)).append(n);
    }
  }
  
  static int  pI(String s)    {try {return Integer.parseInt(s);} catch (Exception e) {return 0;}}
  static void sleep(int t)    {try {Thread.sleep(t);} catch(Exception e){}}
  static void wR()            {for (;Run.r;) sleep(1000);}// ожидание остановки робота
  static void alert(String s) {JOptionPane.showMessageDialog(null, s,"Робот:", 1);}
  static void run(String s)   {new Run(s);}
}

class Run extends Thread { // Запуск робота в другом потоке
  static boolean r=false;
  static String a;
  
  Run(String ac)  {
      if (r) return;
      r=true;
      a=ac;
      setDaemon(true);
      start(); 
  }
  
  public void run() {// Выполнение команд из строки a
    String[] m=a.split(" *; *");
    R.error="";

    for (int i=0,p;i<m.length;i++) {
      R.robot(m[i]);
      if (R.error.length()>0) {R.alert(R.error); break;}
    }
    r=false;
  }
}