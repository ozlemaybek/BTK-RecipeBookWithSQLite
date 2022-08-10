package com.ozlem.recipebookwithsqlite

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
// Fragment ve XML arasındaki senkronizasyonu sağlamak için:
import kotlinx.android.synthetic.main.fragment_recipe.*
import java.io.ByteArrayOutputStream
import kotlin.Exception


class RecipeFragment : Fragment() {

    var choosenImage : Uri? = null
    var choosenBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe, container, false)
    }

    // Bu fonksiyon görünümler oluşturulduktan sonra çağrılıyordu.
    // Eğer görünümlerle ilgili bir işlem yapcaksak bu fonksiyonun içinde yapmak daha güvenliydi.
    // Fragment'lara Özel Durum: Eğer fragment'ta bir butona fonksiyon atıyorsak bunu setOnClickListener ile yapmamız gerekiyor.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // SAVE butonuna tıklandığında
        buttonID.setOnClickListener {
            onClickSave(it)
        }
        // Görsel seç imageView'u için:
        imageViewID.setOnClickListener {
            onClickChooseImage(it)
        }

        arguments?.let {
            // gelen bilgi
            var incomingInformation = RecipeFragmentArgs.fromBundle(it).info
            if(incomingInformation.equals("I came from menu")){
                // Yeni bir yemek eklemeye geldi.
                // Aşağıdaki 2 bölümü boş bırakalım:
                plainTextFoodNameID.setText("")
                plainTextFoodMaterialID.setText("")
                // butonu görünür olarak ayarlayalım:
                buttonID.visibility = View.VISIBLE
                //görsel seçme arkaplanı:
                val chooseImageBackround = BitmapFactory.decodeResource(context?.resources , R.drawable.select_image)
                imageViewID.setImageBitmap(chooseImageBackround)
            }else{
                // Daha önce oluşturulan yemeği görmeye geldi.
                // Bu durumda SQLite'dan gelen id'yi çekip göstermem gerekiyor.
                // butonun görünürlüğünü görünmez yapalım:
                buttonID.visibility = View.INVISIBLE

                // Bize yollanan id'yi alalım:
                val choosenId = RecipeFragmentArgs.fromBundle(it).id

                context?.let {

                    try {

                        // Database'i oluşturalım:
                        val db = it.openOrCreateDatabase("Foods",Context.MODE_PRIVATE,null)
                        // cursor'ı oluşturalım:
                        val cursor = db.rawQuery("SELECT * FROM foods WHERE id = ?", arrayOf(choosenId.toString()))

                        // Seçilen yemeğe ait datayı çekelim:
                        val foodNameIndex = cursor.getColumnIndex("foodName")
                        val foodMaterialIndex = cursor.getColumnIndex("foodMaterials")
                        val foodImage = cursor.getColumnIndex("image")

                        // Çektiğimiz dataları ekranda yerlerine atayalım:
                        while(cursor.moveToNext()){
                            plainTextFoodNameID.setText(cursor.getString(foodNameIndex))
                            plainTextFoodMaterialID.setText(cursor.getString(foodMaterialIndex))
                            // Görsel bizebyte dizisi olarak geliyor sonrasında onu Bitmap'e çeviriyoruz.
                            val byteArray = cursor.getBlob(foodImage)
                            // Byte Dizisini bitmap'e çevirelim:
                            val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                            imageViewID.setImageBitmap(bitmap)
                        }
                        // cursor'ı kapatalım:
                        cursor.close()

                    } catch (e: Exception){
                        e.printStackTrace()
                    }

                }
            }
        }

    }


    // Fragment içinde tanımladığımız onClick metodlar activity'de olduğu gibi direkt algılanmaz.
    // Bunun için önce onViewCreated metodunu çağıracağız.
    // Bu yüzden setOnClickListener içinde bu fonksiyonları çağırdık.
    fun onClickSave(view: View) {
        // Bu fonksiyonda SQLite'a kaydetme işlemi yapılacak:

        val foodName = plainTextFoodNameID.text.toString()
        val foodMaterials = plainTextFoodMaterialID.text.toString()

        if(choosenBitmap != null){
            // 300deneyerek bulunmuş bir rakam resimden resime değişir
            val littleBitmap = createLittleBitmap(choosenBitmap!! , 300)
            // Şİmdi resmi kaydetmeden önce veriye çevirmeliyiz.
            // Bir görseli kaydederken aslında Bitmap , jpeg , png olarak değil veriye çevirip kaydederiz.
            // Şimdi görseli veriye çevirelim:
            // ByteArrayOutputStream; veriye çevirme işleminde bize yardımcı olan bir sınıf
            val outputStream = ByteArrayOutputStream()
            // compress sıkıştırmak demektir. Bizden parametre olarak hangi formatta çevirme yapacağını istiyor.
            // 2. parametre quality 0 ile 100 arasında değişen bir rakam. Baskılama işlemi yapıldığı için kalite verebiliyoruz
            littleBitmap.compress(Bitmap.CompressFormat.PNG , 50 , outputStream)
            // Şimdi bunu byte dizisi haline getirelim:
            // database işlemlerini try-catch bloğu içinde yapıyorduk:
            val byteArray = outputStream.toByteArray()

            // Şimdi SQLite'a kaydetme işlemini yapalım:
            try {

                //  Önce database'i oluşturalım:
                context?.let {
                    val database = it.openOrCreateDatabase("Foods" , Context.MODE_PRIVATE , null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS foods (id INTEGER PRIMARY KEY , foodName VARCHAR , foodMaterials VARCHAR , image BLOB)")
                    val sqlString = "INSERT INTO foods (foodName , foodMaterials , image) VALUES (? , ? , ?)"
                    val statement = database.compileStatement(sqlString)
                    // Statement SQLite Statement denen bir yapıdan geliyor. Alınan string'i SQL kodu gibi çalıştırabilir ve
                    // binding yapmaya yani ?'leri yerine değer atamayada olanak sağlıyor.
                    // Bağlama işlemlerini yapalım:
                    statement.bindString(1 , foodName)
                    statement.bindString(2 , foodMaterials)
                    statement.bindBlob(3 , byteArray)
                    // SQLite'ta çalıştırmaya çalışalım:
                    statement.execute()
                }

            }catch (e : Exception){
                e.printStackTrace()
            }
            // Kaydetme işlemi tamamlandıktan sonra listFragment'a geri dönmeliyim.
            // Bunun için bir action tanımladık:
            val action = RecipeFragmentDirections.actionRecipeFragmentToListFragment()
            // action'a gidelim:
            Navigation.findNavController(view).navigate(action)
        }
    }

    fun onClickChooseImage(view: View) {

        activity?.let {
            // Uygulama her açıldığında izinler tekrar istenmeyecek sadecebir kez istenecek.
            // Bu yüzden bu izin önceden verilmişmi diye bir kontrol yapmamız gerekiyor.

            // Önce izin daha önce verilmiş mi diye kontrol edelim:
            // izinler checkSefPermission ile kontrol edilir. Fakat checkSelfPermission Activity'den çağrılır.
            // Fragment içinde direkt çağıramayacağımıziçin context'den çağırmalıyız ama
            // Activity'den değil ContextCompat'ten çağıracağız çünkü daha güvenli
            // API 19 öncesinde mi çalışacak sonrasında mı gibi şeyleri her cihazda kontrol etmemek için ContextCompat'ten çağırıyoruz.
            // Aynı şekilde Activty içindede böyle yapacağız.
            // 1.parametre: context isteniyor , 2: hangi izini kontrol edeceğim soruluyor
            // 2. parametrede android olan manifest'i seçmeye dikkat etmeliyiz:
            // PERMISSION_GRANTED: İZİN VERİLDİ PERMISSION_DENIED: İZİN VERİLMEDİ DEMEK
            if (ContextCompat.checkSelfPermission(it.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Bu bloğa girildiyse izin verilmedi izin istememiz gerekiyor demektir:
                // Şimdi izin isteyelim:
                // İstediğimiz izinleri array formatında vermemiz gerekiyor çünkü requestPermissions metodu bu şekilde tasarlanmış
                // Bu aynı anda birden fazla izin isteyebilmemizi sağlıyor.
                // requestCode: Sonradan bu izini kontrol etmek istediğimizde kullanabileceğimiz bir kod
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE) , 1)

            }else{
                // izin zaten verilmiş, direkt galeriye git
                // Galeriye gitmek için bir intent oluşturmamız gerekiyor.
                // Şimdi öncekilerden farklı olarak intent'in içine bir action koyacağız.
                // Uri : alacağımız görselin nerede durudğunu belirten yapı
                val galleryIntent = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                // Geriye bir şey (bir resim) dödüreceğimiz için startActivity değil startActivityForResult() kullanıyoruz
                // parametre olarak bir intent bir de requestCode isteyecek:
                startActivityForResult(galleryIntent , 2)
            }
        }
    }

    // İstenilen izinlerin sonuçları:
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray  // verilen sonuçlar
    ) {
        if(requestCode == 1){
            // Geriye bir şey döndürüldü mü ve izin verildi mi:
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Bu bloğa girildiyse izni aldık demektir.
                // Galeriye gidelim:
                val galleryIntent = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent , 2)

            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // Galeriye gidince ne yapılacak:
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // resultCode; kullanıcı bir şey seçitimi seçmedimi bunu kontrol eder.
        // RESULT_OK: Kullanıcı galeriye gittikten sonra işleme devam etti
        // RESULT_CANCELLED: Kullanıcı galeriye gittikten sonra işlemden vazgeçti
        // data seçtiğimiz görselin nerede olduğu (Uri'ı)
        // data != null burada geriye bir veri döndümü bunu kontrol ediyoruz.
        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){

            // Seçilen görselin telefonda nerede olduğunu yani Uri'ını almış olduk.
            choosenImage = data.data

            // Resmi aldıktan sonra bir Bitmap'e çevirmemiz lazım ki imageView'un içine koyabilelim.
            // Resmin konumunu aldık. Konumdan dosyaya çevirmeye çalışırken bir çok hata alabiliriz bu yüzden try-catch ile devam ediyoruz.
            try {

                //createSource API28 ve altında çalışmaz.
                context?.let {
                    // context'i it içerisine atadık. ve context yerine it yazdık:
                    if(choosenImage != null){

                        // createSource bizim için bir kaynak oluşturacak:
                        // Kullanılan telefonun SDK'sı 28'den büyükmü diye kontrol ettik:
                        if(Build.VERSION.SDK_INT > 28) {
                          val source = ImageDecoder.createSource(it.contentResolver , choosenImage!!)
                            // Bitmap oluşturalım:
                            // source'dan bitmap oluşturmak için ImageDecoder kullanalım:
                            // decodeBitmap benden bir kaynak istiyor:
                            choosenBitmap = ImageDecoder.decodeBitmap(source)
                            // Seçilen görseli imageView'un içine atadık:
                            imageViewID.setImageBitmap(choosenBitmap)
                        }else{
                            // SDK 28'den büyük değilse Bitmap'i başkabir şekilde alacağız:
                            // Seçilen görseli MediaStore'dan alalım:
                            choosenBitmap = MediaStore.Images.Media.getBitmap(it.contentResolver , choosenImage)
                            // Üstü çizili şeyler artık bunun kullanılmadığını bir üst versiyonu çıktığını ifade eder ama çalışır.
                            imageViewID.setImageBitmap(choosenBitmap)

                        }

                    }
                }

            }catch (e : Exception){
                e.printStackTrace()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }


    // Bu fonksiyonu her app'te görsel küçültmek için kullanabiliriz:
    fun createLittleBitmap(bitmapSelectedByTheUser : Bitmap , maxSize : Int) : Bitmap {

        // Güncel boyutları alalım:
        var width = bitmapSelectedByTheUser.width
        var height = bitmapSelectedByTheUser.height

        // Genişliği ve uzunluğu birbirine oransal olarak azaltmak istiyoruz.
        // Yani biri %30 azaltılacaksa diğerideöyle olmalı.
        //Bu yüzden width ve height'ın birbirine oranını bulup kendimize ideal bir oran çıkartmalıyız

        // Ratio: oran
        // bitmapRatio 1'den büyük çıkıyorsa görsel yatay demektir, 1'den küçükse görsel dikey demektir.
        val bitmapRatio : Double = width.toDouble() / height.toDouble()

        if(bitmapRatio > 1){
            // Görsel yatay
            width = maxSize
            // kısaltılmış boy:
            val shortenedHeight = width / bitmapRatio
            height = shortenedHeight.toInt()
        }else{
            // Görsel dikey
            height = maxSize
            val shortenedHeight = height * bitmapRatio
            width = shortenedHeight.toInt()

        }

        // Genişliği ve uzunluğu bu fonksiyonu bundan sonra her yerde kullanabilelim diye sabit bir rakam olarak vermiyoruz.
        // createScaledBitmap bitmap'i küçültmek için kullanılır
        return Bitmap.createScaledBitmap(bitmapSelectedByTheUser , width , height ,true)

    }

}