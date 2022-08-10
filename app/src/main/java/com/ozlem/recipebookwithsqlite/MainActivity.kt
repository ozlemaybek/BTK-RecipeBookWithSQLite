package com.ozlem.recipebookwithsqlite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // recyclerView'da bir şey görebilmek için SQLite'a veri eklememiz gerekiyor.
    }

    // Options menu'nun onCreate'i:
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Bu fonksiyon içerisinde menüyü bağlama işlemi yapacağız.
        // Bunun için menuInflater kullanacağız
        // menuInflater kullanarak oluşturduğumuz menüleri app içinde her yere bağlayabiliriz.
        val menuInflater = menuInflater
        // İki parametre istiyor 1: menu resources (oluşturduğumuz menü) , 2: Hangi menü ile bağlayacağımız
        menuInflater.inflate(R.menu.add_food , menu)

        return super.onCreateOptionsMenu(menu)
    }

    // Options menu'den bir şey seçilirse ne yapayım:
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Birden fazla item olabilir bu yüzden item'ı if ile kontrol etmeye dikkat etmeliyiz.
        if(item.itemId == R.id.add_food_item){
            val action = ListFragmentDirections.actionListFragmentToRecipeFragment("I came from menu" , 0)
            // .navigate diyerek istediğimiz yere gidebiliyoruz:
            Navigation.findNavController(this , R.id.NavHostFragmentID).navigate(action)
        }
        return super.onOptionsItemSelected(item)
    }
}