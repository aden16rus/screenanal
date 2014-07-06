/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package screenanal;

public class FindImg {
      static boolean findImage(String i){// поиск картинки из файла i в картинке screenRGB, xn yn - место найденного
    if (i.indexOf(".")<0) i+=".png";  //если изображение без расширения то добавить расширение
    readImage(pathI+i); //считать изображение из файла патч+имя файла
    if (imageRGB==null) return false;   //если массив картинки пуст то ошибка
    capScreen();  // поместить скриншот экрана в массив screenRGB
    int c=imageRGB[0];  //переменная С содержит первый пиксель картинки из файла
    boolean e=false;  //

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

    
}
