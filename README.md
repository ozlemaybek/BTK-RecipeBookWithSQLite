# BTK-RecipeBookWithSQLite

## Kotlin İle Android Mobil Uygulama Geliştirme Eğitimi Temel Seviye (BTK AKADEMİ) Notlarım

## Proje Hakkında

### Kullandığımız Dosyalar

> MainActivity.kt

> activity_main.xml

> ListFragment.kt

> fragment_list.xml

- Bu ekranda eklediğimiz yemek tarifi isimlerinin listesi olacak.

> RecipeFragment.kt

> fragment_recipe.xml

- Burada imageView'un içine "CLICK TO SELECT IMAGE" görselini ekledik. Resmin jpeg ya da png formatında olması farketmez.

![image](https://user-images.githubusercontent.com/109730490/183310707-10673b6f-70b5-41aa-a51a-4325030cee5f.png)

- ImageView'un altına 2 tane plainText ekledik. 

## MOBİL UYGULAMA YAPARKEN KULLANABİLECEĞİMİZ TASARIM ARAÇLARI

- ADOBE XD (ADOBE EXPERİENCE DESİGN): Sadece ücretli versiyon var. 

- SKETCH: Sadece ücretli versiyon var.

## Menü Oluşturma

> Menü oluşturmak için RES klasörü altında bir menu klasörü oluşturup onun içinde işlem yapmalıyız. 

> RES klasörü > new > directory > menu olarak isimlendir.

![image](https://user-images.githubusercontent.com/109730490/183632707-8f845b16-5710-4e10-90d6-5d8e3fdf77aa.png)

> Bu şekilde isimlendirdiğimiz için içerisinde menü olacağı android studio tarafından algılanıyor. 

> menu klasörüne sağ tık > new menu resource file :

![image](https://user-images.githubusercontent.com/109730490/183633032-9643bd3c-c11e-47b2-ad5b-da1bcc70bb5f.png)

> Sonraında menu resource file'ı add_food olarak isimlendirdik ve xml kodlarına bunları ekleyerek menü'yü oluşturduk:

![image](https://user-images.githubusercontent.com/109730490/183633961-4291dd3b-6ca6-4dd8-90a8-f0b317a259dc.png)

> Menüyü eklediğimiz yer fragment'lar değil activity olacak. Bunu yapmak için daha önce hazır olan iki fonksiyonu MainActivity içinde konumlandırmamız gerekiyor. 

![image](https://user-images.githubusercontent.com/109730490/183638895-0aecd068-f3f9-4fe1-9600-fc992d471377.png)

> Eklediğimiz menüye options menu deniyor.

## APP İÇERİSİNDE CİHAZDAN RESİM SEÇME

> Bunun için önce kullanıcının iznini almalıyız. İzin almak için kullanıcının görsellerin kaydedildiği deposuna gideceğiz. Bunun içinde external storage (harici depolama) diye bir kavram var bunu kullanıcının SD kartı gibi düşünebiliriz. 

> Bu izinlerin 2 tipi var. Bazılarında direkt manifeset dosyasında belirtmemiz yeterli oluyor ama bazılarında manifest yetmiyor kullanıcıya açık açık bunu sormamız gerekiyor. 

> Hangi İzin Nasıl Sorulacak

> Bunu developer.android.com'dan bulabiliriz. 

> MAnifest'te hangi izinlerin kullanıldığı uygulama yüklenirken kullanıcıya bildiriliyor. Bazıizinler için kullanıcıya sormaya gerek yok çünkü uygulamyı indiriyorsa bu izinleri kabul ediyor demektir ama bazıları için ekstradan sormamız gerekiyor. 

> Örneğin internet izni için ekstra sormaya gerek yok. Fakat lokasyon ya da galeri için ekstra izne ihtiyacımız var. 

> Android izinleri normal izinler vetehlikeli izinler olarak ikiye ayırır. İstediğimiz iznin hangisi olduğunu bilmek için "protection levels" a bakmalıyız. 

> Manifest.permisson bölümünde tek tek tüm izinler anlatılıyor. 

![image](https://user-images.githubusercontent.com/109730490/183645371-af77fd99-3577-4a6e-a6d3-f07cb41b081b.png)

> Burada protection level : normal olan izinleri direkt manifest'e yazıp geçebiliriz. Eğer dangerous ise kullanıcıya açık açık sormamız gerekir. 

![image](https://user-images.githubusercontent.com/109730490/183645639-ea5a0c47-6f92-4e53-a692-6e95513d2a4e.png)

> Galeri için istenen izin:

![image](https://user-images.githubusercontent.com/109730490/183669618-521e11f3-85b3-47ef-aa93-861b06480b67.png)






## Kaynaklar

- [BTK Akademi](https://www.btkakademi.gov.tr/portal/course/kotlin-ile-android-mobil-uygulama-gelistirme-egitimi-temel-seviye-10274)

- [developer.android.com](https://developer.android.com/reference/android/Manifest.permission#INTERNET)
