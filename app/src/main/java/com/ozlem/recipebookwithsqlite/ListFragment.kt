package com.ozlem.recipebookwithsqlite

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() {

    var foodNameList = ArrayList<String>()
    var foodIdList = ArrayList<Int>()
    private lateinit var listAdapter : ListRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listAdapter = ListRecyclerAdapter(foodNameList,foodIdList)
        // this diyemeyiz çünkü fragment'tayız bu yüzden context dedik:
        recyclerViewID.layoutManager = LinearLayoutManager(context)
        recyclerViewID.adapter = listAdapter

        SQLTakingData()
    }

    fun SQLTakingData(){

        try {
            activity?.let {
                // Database oluşturalım:
                val database = it.openOrCreateDatabase("Foods" , Context.MODE_PRIVATE , null)
                // cursor oluşturalım:
                val cursor = database.rawQuery("SELECT * FROM foods",null)
                val foodNameIndex = cursor.getColumnIndex("foodName")
                val foodIdIndex = cursor.getColumnIndex("id")

                foodNameList.clear()
                foodIdList.clear()

                while(cursor.moveToNext()){

                    foodNameList.add(cursor.getString(foodNameIndex))
                    foodIdList.add(cursor.getInt(foodIdIndex))

                }

                // Verilerçekildikten sonra gelen yeni verileri gösterebilmek için:
                listAdapter.notifyDataSetChanged()

                cursor.close()
            }
        }catch (e: Exception){
            e.printStackTrace()
        }

    }
}